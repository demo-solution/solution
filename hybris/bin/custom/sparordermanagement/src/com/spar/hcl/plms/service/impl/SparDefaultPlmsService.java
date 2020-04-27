/**
 *
 */
package com.spar.hcl.plms.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.core.plms.dao.SparPlmsDao;
import com.spar.hcl.facades.plms.SparOMSOrderData;
import com.spar.hcl.facades.plms.SparPLMSCancelOrderData;
import com.spar.hcl.facades.plms.SparPLMSCancelOrderDataHeader;
import com.spar.hcl.facades.plms.SparPLMSOrderData;
import com.spar.hcl.facades.plms.SparPLMSOrderDataHeader;
import com.spar.hcl.facades.plms.SparPLMSUpdateOrderData;
import com.spar.hcl.facades.plms.SparPLMSUpdateOrderDataHeader;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;
import com.spar.hcl.plms.webservices.utility.SparPLMSOrderTransferUtility;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;


/**
 *This class is implemented for PLMS CRUD operation 
 * 
 * @author ravindra
 *
 */
public class SparDefaultPlmsService implements SparPlmsService
{
	
	private static final Logger LOGGER = Logger.getLogger(SparDefaultPlmsService.class);
	private static final String SPAR_PLMS_USER = "spar.plms.user";
	private static final String SPAR_PLMS_PASS = "spar.plms.pass";
	private static final String SPAR_PLMS_INSERT_METHOD = "spar.plms.insert.method";
	private static final String SPAR_PLMS_UPDATE_METHOD = "spar.plms.update.method";
	private static final String SPAR_PLMS_CANCEL_METHOD = "spar.plms.cancel.method";
	
	private ModelService modelService;
	private SparPlmsDao sparPlmsDao;
	private Converter<AbstractOrderEntryModel, SparOMSOrderData> sparPlmsConverter; 
	
	@Autowired
	private PromotionsService promotionsService;
	
	/**
	 * This method is used save the order data in PlmsRetryOrdersModel whenever plms server is unreachable
	 *
	 * @param orderModel
	 * @param actionType
	 * @return boolean
	 */
	@Override
	public boolean saveOrder(final String orderCode, final String actionType)
	{
		final PlmsRetryOrdersModel model = getModelService().create(PlmsRetryOrdersModel.class);
		model.setActionType(actionType);
		model.setOrderCode(orderCode);
		try
		{
			getModelService().save(model);
		}
		catch(ModelSavingException e)
		{
			LOGGER.warn("ambiguous unique keys OrderCode : "+orderCode +" for model PlmsRetryOrdersModel (<unsaved>) - found 1 item(s) using the same keys");
		}
		return true;
	}


	
	/**
	 * This method is used to update the plms orders
	 *
	 * @param orderModel
	 * @param actionType
	 */
	@Override
	public void updatePlmsOrders(final String orderCode,final String actionType)
	{
		 PlmsRetryOrdersModel  plmsRetryOrdersModel=getSparPlmsDao().findPlmsOrders(orderCode);
		 if(null != plmsRetryOrdersModel)
		 {
			 plmsRetryOrdersModel.setActionType(actionType);
			 getModelService().save(plmsRetryOrdersModel);
			 getModelService().refresh(plmsRetryOrdersModel);
		 }
	}

	
	/**
	 * This method is used read the order's to resend the order detail to plms server
	 *
	 * @return List<PlmsRetryOrdersModel>
	 */
	@Override
	public List<PlmsRetryOrdersModel> readOrders()
	{
		return getSparPlmsDao().readOrders();
	}
	
	
	/**
	 * This method is used to create the orderModel for an orderCode
	 *
	 * @param orderCode
	 * @return List<OrderModel>
	 */
	@Override
	public OrderModel findOrderData(final String orderCode)
	{
		return getSparPlmsDao().findOrderData(orderCode);
	}


	
	/**
	 * This method is used to delete the order details from customize table on completed of manual pick
	 *
	 * @param orderCode
	 * @return boolean
	 */
	@Override
	public boolean deleteOrders(final String orderCode)
	
	{
		LOGGER.info("Entered in deleteOrders to markDelete ::::::");
		PlmsRetryOrdersModel plmsRetryOrdersModel=getSparPlmsDao().findPlmsOrders(orderCode);
		if(null != plmsRetryOrdersModel){
			plmsRetryOrdersModel.setMarkDeleted(Boolean.TRUE);
			getModelService().save(plmsRetryOrdersModel);
			getModelService().refresh(plmsRetryOrdersModel);
			//getModelService().remove(plmsRetryOrdersModel);
		}
		LOGGER.info("Exit in deleteOrders to markDelete ::::::");
		return true;
	}
	
	
	/**
	 * This method is used to push the OMS data to PLMS server for inserted Order in OMS
	 *
	 * @param orderModel
	 * @return ResponseEntity<String>
	 */
	@Override
	public ResponseEntity<SparPlmsOrderDataResponse> plmsInsertOrder(final OrderModel orderModel)
	{
		LOGGER.info("Entered in plmsInsertOrder method");
		SparPLMSOrderDataHeader sparPLMSOrderDataHeader=new SparPLMSOrderDataHeader();
		final SparPLMSOrderData sparPLMSOrderData = new SparPLMSOrderData();
		final List<SparOMSOrderData> sparOrderDataList =new ArrayList<SparOMSOrderData>();
		ResponseEntity<SparPlmsOrderDataResponse> plmsResponse=null;
		long freeGiftQty = 0;
		OrderModel tempOrderModel = modelService.create(OrderModel.class);
		List<AbstractOrderEntryModel> finalEntryList = new ArrayList<AbstractOrderEntryModel>();
		List<String> productList = new ArrayList<String>();
		Iterator<AbstractOrderEntryModel> itr = orderModel.getEntries().iterator();
		while(itr.hasNext())
		{
			AbstractOrderEntryModel entryModel = itr.next();
			for(AbstractOrderEntryModel entry : orderModel.getEntries())
			{
				if(entryModel.getProduct().getCode().equalsIgnoreCase(entry.getProduct().getCode()) 
						&& (null != entry.getBasePrice() && 0 == entry.getBasePrice()))
				{
					if(null != entryModel.getBasePrice() && 0 != entryModel.getBasePrice())
					{
						entryModel.setQuantity(entryModel.getQuantity() + entry.getQuantity());
					}
					else
					{
						entryModel.setQuantity(++freeGiftQty);
					}
				}
			}
			if(!productList.contains(entryModel.getProduct().getCode()))
			{
				finalEntryList.add(entryModel);
				productList.add(entryModel.getProduct().getCode());
				freeGiftQty = 0;
			}
		}
		tempOrderModel.setEntries(finalEntryList);
		
		for(AbstractOrderEntryModel entry : tempOrderModel.getEntries())
		{
			final SparOMSOrderData sparOMSOrderData = getSparPlmsConverter().convert(entry);
			sparOrderDataList.add(sparOMSOrderData);
		}
		final String userName=Config.getString(SPAR_PLMS_USER,SPAR_PLMS_USER);
		final String password=Config.getString(SPAR_PLMS_PASS,SPAR_PLMS_PASS);
		final String methodName=Config.getString(SPAR_PLMS_INSERT_METHOD,SPAR_PLMS_INSERT_METHOD);
	
		
		sparPLMSOrderData.setUser(userName);
		sparPLMSOrderData.setPass(password);
		sparPLMSOrderData.setOmsdata(sparOrderDataList);
		sparPLMSOrderDataHeader.setParams(sparPLMSOrderData);
		sparPLMSOrderDataHeader.setMethod(methodName);
		final String omsJsonData = SparPLMSOrderTransferUtility.createOMSDataAsJson(sparPLMSOrderDataHeader);		
		LOGGER.debug("Json Data for OrderId " + orderModel.getCode() + "::::::" + omsJsonData);
		
		if (StringUtils.isNotBlank(omsJsonData))
		{
			plmsResponse = SparPLMSOrderTransferUtility.sendOMSDataToPLMS(omsJsonData);
			LOGGER.info("Exited from plmsInsertOrder method : "+plmsResponse);
			return plmsResponse;
		}	
		
		LOGGER.info("Exited from plmsInsertOrder method");
		return plmsResponse;
	}

	
	/**
	 * This method is used to push the OMS data to PLMS server for Updated Order in OMS
	 *
	 * @param orderModel
	 * @return ResponseEntity
	 */
	@Override
	public ResponseEntity<SparPlmsOrderDataResponse> plmsUpdateOrder(final OrderModel orderModel)
	{
		LOGGER.info("Entered in plmsUpdateOrder method");
		ResponseEntity<SparPlmsOrderDataResponse> plmsResponse=null;
		SparPLMSUpdateOrderData sparPLMSUpdateOrderData=new SparPLMSUpdateOrderData();
		SparPLMSUpdateOrderDataHeader sparPLMSUpdateOrderDataHeader=new SparPLMSUpdateOrderDataHeader();
		final String userName=Config.getString(SPAR_PLMS_USER,SPAR_PLMS_USER);
		final String password=Config.getString(SPAR_PLMS_PASS,SPAR_PLMS_PASS);
		final String methodName=Config.getString(SPAR_PLMS_UPDATE_METHOD,SPAR_PLMS_UPDATE_METHOD);
		
		sparPLMSUpdateOrderData.setUser(userName);
		sparPLMSUpdateOrderData.setPass(password);
		
		final DateFormat format = new SimpleDateFormat("EEE MMM dd yyy");
		try
		{
			Date date = format.parse(orderModel.getDeliveryDate());
			final DateFormat ddformat = new SimpleDateFormat("MMM dd,yyy");
			sparPLMSUpdateOrderData.setDeliveryDate(ddformat.format(date));
		}
		catch (ParseException e)
		{
			LOGGER.error("Error in parsing the date format populateOrderLevelDetails::::"+ e);
		}
		
		sparPLMSUpdateOrderData.setDeliverySlot(orderModel.getDeliverySlot());
		sparPLMSUpdateOrderData.setOrderNo(orderModel.getCode());
		
		if(null != orderModel.getOderRetryNoShow())
		{
			sparPLMSUpdateOrderData.setOrderNoShow(orderModel.getOderRetryNoShow());
		}
		
		sparPLMSUpdateOrderDataHeader.setParams(sparPLMSUpdateOrderData);
		sparPLMSUpdateOrderDataHeader.setMethod(methodName);
		
		final String plmsUpdateOrderJsonData=SparPLMSOrderTransferUtility.createOMSDataAsJson(sparPLMSUpdateOrderDataHeader);
		LOGGER.debug("PLMS Updated Order Json Data::::::::"+plmsUpdateOrderJsonData);
		
		if(StringUtils.isNotBlank(plmsUpdateOrderJsonData))
		{
			plmsResponse=SparPLMSOrderTransferUtility.sendOMSDataToPLMS(plmsUpdateOrderJsonData);		
		}
		LOGGER.info("Exited from plmsUpdateOrder method : "+plmsResponse);
		return plmsResponse;
	}


	/**
	 * This method is used to push the OMS data to PLMS server for Cancelled Order in OMS
	 *
	 * @param orderModel
	 * @return ResponseEntity<String>
	 */
	@Override
	public ResponseEntity<SparPlmsOrderDataResponse> plmsCancelOrder(final OrderModel orderModel)
	{
		LOGGER.info("Entered in plmsCancelOrder method");
		ResponseEntity<SparPlmsOrderDataResponse> plmsResponse=null;
		SparPLMSCancelOrderData sparPLMSCancelOrderData=new SparPLMSCancelOrderData();
		SparPLMSCancelOrderDataHeader sparPLMSCancelOrderDataHeader = new SparPLMSCancelOrderDataHeader();
		final String userName=Config.getString(SPAR_PLMS_USER,SPAR_PLMS_USER);
		final String password=Config.getString(SPAR_PLMS_PASS,SPAR_PLMS_PASS);
		final String methodName=Config.getString(SPAR_PLMS_CANCEL_METHOD,SPAR_PLMS_CANCEL_METHOD);
		
		sparPLMSCancelOrderData.setUser(userName);
		sparPLMSCancelOrderData.setPass(password);
		
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String cancelDateTime = df.format(orderModel.getModifiedtime());
		sparPLMSCancelOrderData.setCancelDateTime(cancelDateTime);
		
		sparPLMSCancelOrderData.setOrderNo(orderModel.getCode());
		
		sparPLMSCancelOrderDataHeader.setParams(sparPLMSCancelOrderData);
		sparPLMSCancelOrderDataHeader.setMethod(methodName);
		
		final String plmsCancelOrderJsonData = SparPLMSOrderTransferUtility.createOMSDataAsJson(sparPLMSCancelOrderDataHeader);
		LOGGER.debug("PLMS Cancel Order Json Data::::"+plmsCancelOrderJsonData);
		
		if(StringUtils.isNotBlank(plmsCancelOrderJsonData))
		{
			plmsResponse = SparPLMSOrderTransferUtility.sendOMSDataToPLMS(plmsCancelOrderJsonData);
			LOGGER.info("Exited from plmsCancelOrder method : "+plmsResponse);
			return plmsResponse;
		}
		LOGGER.info("Exited from plmsCancelOrder method : "+plmsResponse);
		return plmsResponse;
	}

	/**
	 * @return the sparPlmsConverter
	 */
	public Converter<AbstractOrderEntryModel, SparOMSOrderData> getSparPlmsConverter() {
		return sparPlmsConverter;
	}


	/**
	 * @param sparPlmsConverter the sparPlmsConverter to set
	 */
	public void setSparPlmsConverter(
			Converter<AbstractOrderEntryModel, SparOMSOrderData> sparPlmsConverter) {
		this.sparPlmsConverter = sparPlmsConverter;
	}
	
	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService the modelService to set
	 */
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the sparPlmsDao
	 */
	public SparPlmsDao getSparPlmsDao()
	{
		return sparPlmsDao;
	}

	/**
	 * @param sparPlmsDao the sparPlmsDao to set
	 */
	public void setSparPlmsDao(SparPlmsDao sparPlmsDao)
	{
		this.sparPlmsDao = sparPlmsDao;
	}

}
