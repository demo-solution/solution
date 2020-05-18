/**
 *
 */
package com.spar.hcl.core.order.service.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparPaymentAmountEmailProcessModel;
import com.spar.hcl.core.order.SparOrderDao;
import com.spar.hcl.core.order.service.SparOrderService;


/**
 * @author jitendriya.m
 *
 */
public class SparOrderServicempl implements SparOrderService
{
	protected static final Logger LOG = Logger.getLogger(SparOrderServicempl.class);
	private static final String BASESITE = "basesite.uid";
	private static final String SPAR_PAYMENTAMOUNT_REPORT_EMAIL_PROCESS = "sparPaymentAmountEmailProcess";
	
	private SparOrderDao sparOrderDao;
	private ModelService modelService;

	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	private BusinessProcessService businessProcessService;
	
	@Autowired
	private BaseSiteService baseSiteService;
	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the sparOrderDao
	 */
	public SparOrderDao getSparOrderDao()
	{
		return sparOrderDao;
	}

	/**
	 * @param sparOrderDao
	 *           the sparOrderDao to set
	 */
	public void setSparOrderDao(final SparOrderDao sparOrderDao)
	{
		this.sparOrderDao = sparOrderDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.order.service.SparOrderService#updateOrder(java.util.Map)
	 */
	@Override
	public void updateOrder(final Map<String, String> resultMap)
	{

		try
		{
			Thread.sleep(configurationService.getConfiguration().getLong("thread.sleep.time"));
			final OrderModel order = sparOrderDao.findOrderByOid(resultMap.get("oid"));
			if (order != null)
			{
				order.setPaymentCaptureValue(Double.valueOf(resultMap.get("chargetotal")));
				modelService.save(order);
				modelService.refresh(order);
				LOG.info("In Update Order "+order.getCode());
				LOG.info("BalanceDue : " + order.getBalanceDue());
				LOG.info("chargetotal : " + Double.valueOf(resultMap.get("chargetotal")));
				LOG.info("Order Code : " + order.getCode());
				LOG.info("BalanceDue not equals to chargetotal : " + (order.getBalanceDue().doubleValue() != (Double.valueOf(resultMap.get("chargetotal"))).doubleValue()));
				if(order.getBalanceDue().doubleValue() != (Double.valueOf(resultMap.get("chargetotal"))).doubleValue())
				{
					order.setStatus(OrderStatus.FRAUD_CHECKED);
					getModelService().save(order);
					modelService.refresh(order);
					sendPaymentAmountReportEmail(order);
				}
			}
		}
		catch (final InterruptedException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setOrderStatusAsFraudChecked(final Map<String, String> resultMap)
	{
		try
		{
			Thread.sleep(configurationService.getConfiguration().getLong("thread.sleep.time"));
   		final OrderModel order = sparOrderDao.findOrderByOid(resultMap.get("oid"));
   		LOG.info("In setOrderStatusAsFraudChecked method. Order Code is "+ order.getCode());
      	order.setStatus(OrderStatus.FRAUD_CHECKED);
      	getModelService().save(order);
      	modelService.refresh(order);
      	sendPaymentAmountReportEmail(order);
		}
		catch(ModelNotFoundException | InterruptedException ex)
		{
			LOG.error("No order found.");
		}
	}

	private void sendPaymentAmountReportEmail(OrderModel orderModel)
	{
		final SparPaymentAmountEmailProcessModel process=(SparPaymentAmountEmailProcessModel)businessProcessService.createProcess
				(SPAR_PAYMENTAMOUNT_REPORT_EMAIL_PROCESS + "-" + "-" + System.currentTimeMillis(), SPAR_PAYMENTAMOUNT_REPORT_EMAIL_PROCESS);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE));
		process.setOrder(orderModel);
		process.setSite(baseSite);
		process.setUser(orderModel.getUser());
		process.setStore(baseSite.getStores().get(0));
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOG.debug("sendPaymentAmountReportEmail :: SparPaymentAmountEmail :: finished");
	}
}
