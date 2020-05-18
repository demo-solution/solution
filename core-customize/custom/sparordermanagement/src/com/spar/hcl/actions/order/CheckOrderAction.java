/*
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
 *
 */
package com.spar.hcl.actions.order;

import javax.annotation.Resource;

import de.hybris.platform.catalog.impl.DefaultCatalogVersionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import com.spar.hcl.CheckOrderService;
import com.spar.hcl.core.enums.PaymentModeEnum;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This example action checks the order for required data in the business process. Skipping this action may result in
 * failure in one of the subsequent steps of the process. The relation between the order and the business process is
 * defined in basecommerce extension through item OrderProcess. Therefore if your business process has to access the
 * order (a typical case), it is recommended to use the OrderProcess as a parentClass instead of the plain
 * BusinessProcess.
 */
public class CheckOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(CheckOrderAction.class);
	private CheckOrderService checkOrderService;
	@Resource
	private DefaultCatalogVersionService catalogVersionService;
	
	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		final OrderModel order = process.getOrder();

		if (order == null)
		{
			LOG.info("Missing the order, exiting the process");
			return Transition.NOK;
		}
		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
		boolean isCashOnDeliveryPaymentMode=order.getPaymentMode()!=null?PaymentModeEnum.CASHONDELIVERY.getCode().equals(order.getPaymentMode().getCode()):false;
		
		//if PaymentMode is COD then order status is set to Checked Valid
		if (isCashOnDeliveryPaymentMode || getCheckOrderService().check(order) || order.getPaymentMode().getCode().equals(PaymentModeEnum.VOUCHERPAYMENTMODE.getCode()))
		{
			setOrderStatus(order, OrderStatus.CHECKED_VALID);
			return Transition.OK;
		}
		else
		{
			setOrderStatus(order, OrderStatus.CHECKED_INVALID);
			return Transition.NOK;
		}
	}

	protected CheckOrderService getCheckOrderService()
	{
		return checkOrderService;
	}

	@Required
	public void setCheckOrderService(final CheckOrderService checkOrderService)
	{
		this.checkOrderService = checkOrderService;
	}
}
