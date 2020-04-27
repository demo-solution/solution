package com.spar.hcl.plms.webservices.jobs;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.spar.hcl.core.model.process.SparPLMSManualpickOrderEmailProcessModel;
import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;
import com.spar.hcl.plms.webservices.utility.SparPLMSServerConnectionUtility;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;


/**
 * This job is used to send the available orders from PlmsRetryOrdersModel to plms
 * 
 * @author kumarchoubeys
 *
 */
public class SparPLMSConnectivityjob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOGGER = Logger.getLogger(SparPLMSConnectivityjob.class);
	private static final String PLMS_SYSTEM_ACTIVE = "spar.plms.isActive";
	private static final String INSERT = "I";
	private static final String CANCEL = "C";
	private static final String DATE_FORMAT="EEE MMM dd yyyy HH:mm:ss";
	private static final String SPAR_PLMS_MANUAL_PICKTIME_INTERVAL="spar.plms.manualpick.time.timeInterval";
	private static final String BASESITE = "basesite.uid";
	private static final String MANUAL_PICK_ORDER_EMAIL_PROCESS = "SparPLMSManualpickOrderEmailProcess";
	private SparPlmsService sparPlmsService;
	private ModelService modelService;

	@Autowired
	private BusinessProcessService businessProcessService;
	
	@Autowired
	private BaseSiteService baseSiteService;
	
	@Autowired
	private ConfigurationService configurationService;
	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		boolean plmsActive = Config.getBoolean(PLMS_SYSTEM_ACTIVE, false);
		if(plmsActive)
      	{
			LOGGER.info("Entered in cronjob  process for Spar PLMS::::::");
			final Calendar cal = Calendar.getInstance();
			final SimpleDateFormat formatter=new SimpleDateFormat(DATE_FORMAT);
			final SimpleDateFormat time12Hours = new SimpleDateFormat("hh:mm aa");
			final SimpleDateFormat time24Hours = new SimpleDateFormat("HH:mm:ss");
			final List<PlmsRetryOrdersModel> orders = sparPlmsService.readOrders();
			Date orderDelDate=null;
			ResponseEntity<SparPlmsOrderDataResponse> response=null;
			for (final PlmsRetryOrdersModel order : orders)
				{			
					final String orderCode = order.getOrderCode();
					final OrderModel orderModel=sparPlmsService.findOrderData(orderCode);			
					if(null != orderModel)
						{
							try 
							{	
								final Date currTime=new Date();
								final String delimeter = "To";
								final String deliveryDate = orderModel.getDeliveryDate();
								final String deliverySlot = orderModel.getDeliverySlot();
								String[] deliveryTimeArray = deliverySlot.split(delimeter);
								if(null != deliveryTimeArray && deliveryTimeArray.length < 2)
								{
									deliveryTimeArray = deliverySlot.split("to");
								}
								
								final String deliveryTime24Hours = time24Hours.format(time12Hours.parse(deliveryTimeArray[0].trim()));
								final String delDate = deliveryDate+" "+deliveryTime24Hours;
								orderDelDate = formatter.parse(delDate);
								cal.setTime(orderDelDate);
								cal.add(cal.HOUR, Config.getInt(SPAR_PLMS_MANUAL_PICKTIME_INTERVAL, -4));
								
								LOGGER.info("calculated time after deducting given interval time "+ cal.getTime());
								LOGGER.info("currTime time "+ currTime);
								LOGGER.info("compare calculated time and curr time "+ cal.getTime().compareTo(currTime));
								
								if(cal.getTime().compareTo(currTime) > 0)
								{				
									if(SparPLMSServerConnectionUtility.plmsServerAvailability())
									{
										LOGGER.info("Connection is available to send data to plms server through plms cronjob::::");
										if(order.getActionType().equals(INSERT))
										{
											response= sparPlmsService.plmsInsertOrder(orderModel);	
										}
										else if(order.getActionType().equals(CANCEL))
										{
											response= sparPlmsService.plmsCancelOrder(orderModel);
										}
										else
										{
											response= sparPlmsService.plmsUpdateOrder(orderModel);
										}
														
										if(null != response && response.getBody().getResult().getStatus().equals("true"))
										{
											sparPlmsService.deleteOrders(orderCode);
										}
									}
									else
									{
										LOGGER.info("Unable to conect with Plms server:::");
									}
								}
								else
								{
									if(orderModel.getStatus().toString().equals(OrderStatus.READY.toString()))
									{
										sendManulPickOrderEmail(orderModel);
										orderModel.setManualPick(true);
										getModelService().save(orderModel);
										getModelService().refresh(orderModel);
									}
									sparPlmsService.deleteOrders(orderCode);
									LOGGER.info("Order number "+orderCode+" marked for manual pick::::");
								}		
							} 
							catch (ParseException msg) 
							{
								LOGGER.error("Error in parsing the date format in plms cron job::::"+msg);
							}
						}
					}
		
			LOGGER.info("Exited from cronjob process for Spar PLMS::::::");
      	}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
	
	private void sendManulPickOrderEmail(OrderModel orderModel)
	{
		final SparPLMSManualpickOrderEmailProcessModel process=(SparPLMSManualpickOrderEmailProcessModel)businessProcessService.createProcess
				(MANUAL_PICK_ORDER_EMAIL_PROCESS + "-" + "-" + System.currentTimeMillis(), MANUAL_PICK_ORDER_EMAIL_PROCESS);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE));
		process.setOrder(orderModel);
		process.setSite(baseSite);
		process.setUser((CustomerModel)orderModel.getUser());
		process.setStore(baseSite.getStores().get(0));
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOGGER.debug("SparPLMSConnectivityjob :: SparPLMSManualpickOrderEmail :: finished");
	}

	/**
	 * @return the sparPlmsService
	 */
	public SparPlmsService getSparPlmsService() {
		return sparPlmsService;
	}

	/**
	 * @param sparPlmsService the sparPlmsService to set
	 */
	public void setSparPlmsService(SparPlmsService sparPlmsService) {
		this.sparPlmsService = sparPlmsService;
	}
	
	/**
	 * @return the modelService
	 */
	public ModelService getModelService() {
		return modelService;
	}


	/**
	 * @param modelService the modelService to set
	 */
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
