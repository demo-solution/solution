/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */
package com.spar.hcl.actions.order;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.util.Config;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.SourcingService;

import com.spar.hcl.constants.SparordermanagementConstants;
import com.spar.hcl.core.blowhorn.SparBlowhornService;
import com.spar.hcl.core.model.process.SparBlowhornFailedRequestOrderEmailProcessModel;
import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataResponse;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.plms.webservices.utility.SparPLMSServerConnectionUtility;

import java.util.Collection;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
/**
 * Action node responsible for sourcing the order and allocating the consignments. After the consignments are created,
 * the consignment sub-process is started for every consignment.
 */
public class SourceOrderAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOGGER = Logger.getLogger(SourceOrderAction.class);
	private static final String INSERT = "I";
	private SourcingService sourcingService;
	private AllocationService allocationService;
	private BusinessProcessService businessProcessService;
	private SparPlmsService sparPlmsService;
	private String warehouseCity;
	private Double fraudThreshold;
	private SparBlowhornService sparBlowhornService;
	private ConfigurationService configurationService;
	private static final String BLOWHORN_SYSTEM_ACTIVE = "spar.blowhorn.isActive";

	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		LOGGER.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final OrderModel order = process.getOrder();

		final SourcingResults results = getSourcingService().sourceOrder(order);
		if (results != null)
		{
			results.getResults().forEach(result -> logSourcingInfo(result));
		}

		if (results == null || !results.isComplete())
		{
			LOGGER.info("Order failed to be sourced");
			order.setStatus(OrderStatus.ON_HOLD);
		}
		else
		{
			LOGGER.info("Order was successfully sourced");

			final Collection<ConsignmentModel> consignments = getAllocationService().createConsignments(process.getOrder(),
					"cons" + process.getOrder().getCode(), results);
			LOGGER.debug("Number of consignments created during allocation: " + consignments.size());
			startConsignmentSubProcess(consignments, process);
			order.setStatus(OrderStatus.READY);
		}
		getModelService().save(order);
		sendOrderDataToPLMS(order);
		sendOderDataToBlowHorn(order);
	}

	
	/**
	 * This method id used to send the OMS Data to PLMS server for inserted order in OMS
	 * 
	 * @param orderModel
	 */	

	private void sendOrderDataToPLMS(final OrderModel orderModel)
	{
		LOGGER.info("Entred into sendOrderDataToPLMS method");
		ResponseEntity<SparPlmsOrderDataResponse> response=getSparPlmsService().plmsInsertOrder(orderModel);
		String status =null;
		if(null != response)
		{
			status = response.getBody().getResult().getStatus();
			LOGGER.info("status::"+status+" ErrorCode::"+response.getBody().getResult().getError_code()+" ErrorFiledValue:: "+response.getBody().getResult().getError_Field_Value());
			LOGGER.info(" error reason::"+response.getBody().getResult().getError_Reason()+" error type::"+response.getBody().getResult().getError_Type()+" message:::"+response.getBody().getResult().getMessage());
		}
		if(null == response || !status.equals("true"))
		{
			LOGGER.info("inside the if condition to send the data in table");
			getSparPlmsService().saveOrder(orderModel.getCode(), INSERT);
		}
		LOGGER.info("Exited from sendOrderDataToPLMS method");
	}
	
	
	
	/**
	 * This method id used to send the address and order Data to Blowhorn server for inserted order in OMS
	 * 
	 * @param orderModel
	 */	

	private void sendOderDataToBlowHorn(final OrderModel orderModel)
	{
		LOGGER.info("Entred into sendOderDataToBlowHorn method");
		boolean activeFlage = Config.getBoolean(BLOWHORN_SYSTEM_ACTIVE, false);
		if(activeFlage){
		try{
			ResponseEntity<SparBlowhornShipAddressOrderDataResponse> response=getSparBlowhornService().addOrderToBlowhorn(orderModel);
			String status = null;
			String awb_number = null;
			if(null != response)
			{
				awb_number = response.getBody().getMessage().getAwb_number();
				status = response.getBody().getStatus();
				orderModel.setAwb_number(awb_number);
				getModelService().save(orderModel);
				LOGGER.info("status::::::::::"+status);
				LOGGER.info("awb_number::::::::::"+awb_number);
				LOGGER.info("Order successfully shared with blowhorn ::::::::::"+orderModel.getCode());
			}
			if(null == response || response.getStatusCode().value() != 200)
			{
				LOGGER.info("inside the if condition to send the data in table");
				//Send email on failure case
				sparBlowhornService.sendEmailBlowhornSendDataFailure(orderModel);
			}
		}
		catch(Exception e){
			LOGGER.error("inside catch block of sendOderDataToBlowHorn :::::" + e.getMessage());
			e.printStackTrace();
		}
		}
		LOGGER.info("Exited from sendOderDataToBlowHorn method");
		
	}
	
	

	/**
	 * Create and start a consignment process for each consignment in the collection.
	 *
	 * @param consignments
	 *           - list of consignments; never <tt>null</tt>
	 * @param process
	 *           - order process model
	 */
	protected void startConsignmentSubProcess(final Collection<ConsignmentModel> consignments, final OrderProcessModel process)
	{
		for (final ConsignmentModel consignment : consignments)
		{
			final ConsignmentProcessModel subProcess = getBusinessProcessService().<ConsignmentProcessModel> createProcess(
					consignment.getCode() + "_ordermanagement", SparordermanagementConstants.CONSIGNMENT_SUBPROCESS_NAME);
			subProcess.setParentProcess(process);
			subProcess.setConsignment(consignment);
			save(subProcess);
			LOGGER.info("Start Consignment sub-process: '" + subProcess.getCode() + "'");
			getBusinessProcessService().startProcess(subProcess);
		}
	}

	private void logSourcingInfo(final SourcingResult result)
	{
		if (result != null)
		{
			LOGGER.info("Sourcing from Location: '" + result.getWarehouse().getCode() + "'");
			result.getAllocation().forEach(
					(product, qty) -> LOGGER.info("\tProduct [" + product.getProduct().getCode() + "]: '"
							+ product.getProduct().getName(getSessionLocale()) + "'\tQuantity: '" + qty + "'"));
		}
		else
		{
			LOGGER.info("The sourcing result is null");
		}
	}

	protected Locale getSessionLocale()
	{
		return JaloSession.getCurrentSession().getSessionContext().getLocale();
	}

	protected SourcingService getSourcingService()
	{
		return sourcingService;
	}

	@Required
	public void setSourcingService(final SourcingService sourcingService)
	{
		this.sourcingService = sourcingService;
	}

	protected AllocationService getAllocationService()
	{
		return allocationService;
	}

	@Required
	public void setAllocationService(final AllocationService allocationService)
	{
		this.allocationService = allocationService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
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
	
	public Double getConfig(final String property) {
		final String value = Config
				.getParameter(SparordermanagementConstants.EXTENSIONNAME
						+ ".fraud." + property);
		return Double.valueOf(value);
	}
	
	/**
	 * @return the warehouseCity
	 */
	public String getWarehouseCity() {
		if (warehouseCity == null) {
			warehouseCity = Config
					.getParameter(SparordermanagementConstants.EXTENSIONNAME
							+ ".fraud." + warehouseCity);
		}
		return warehouseCity;
	}

	/**
	 * @param warehouseCity
	 *            the warehouseCity to set
	 */
	public void setWarehouseCity(String warehouseCity) {
		this.warehouseCity = warehouseCity;
	}

	/**
	 * @return the fraudThreshold
	 */
	public Double getFraudThreshold() {
		if (fraudThreshold == null) {
			fraudThreshold = getConfig("fraudThreshold");
		}
		return fraudThreshold;
	}

	/**
	 * @param fraudThreshold
	 *            the fraudThreshold to set
	 */
	public void setFraudThreshold(Double fraudThreshold) {
		this.fraudThreshold = fraudThreshold;
	}
	
	
	/**
	 * @return the sparBlowhornService
	 */
	public SparBlowhornService getSparBlowhornService() {
		return sparBlowhornService;
	}


	/**
	 * @param sparBlowhornService the sparBlowhornService to set
	 */
	public void setSparBlowhornService(SparBlowhornService sparBlowhornService) {
		this.sparBlowhornService = sparBlowhornService;
	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}


	/**
	 * @param configurationService the configurationService to set
	 */
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
