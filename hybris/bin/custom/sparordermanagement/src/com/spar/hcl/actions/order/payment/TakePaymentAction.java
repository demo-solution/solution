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
package com.spar.hcl.actions.order.payment;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.facades.order.data.PaymentModeData;


/**
 * The TakePayment step captures the payment transaction.
 */
public class TakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(TakePaymentAction.class);

	private PaymentService paymentService;

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final OrderModel order = process.getOrder();
		boolean paymentFailed = false;
		boolean isAuthorizationRequired=isAuthorizationRequired(order);
		if(isAuthorizationRequired){
		for (final PaymentTransactionModel txn : order.getPaymentTransactions())
		{
			final PaymentTransactionEntryModel txnEntry = getPaymentService().capture(txn);

			if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("The payment transaction has been captured. Order: " + order.getCode() + ". Txn: " + txn.getCode());
				}
			}
			else
			{
				paymentFailed = true;
				LOG.info("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
			}
		}
		}
		if (paymentFailed)
		{
			setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
			return Transition.NOK;
		}
		else
		{
			setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
			return Transition.OK;
		}
	}
	
	/**
	 * This method is used to check COD/CreditCard payment mode for which authorization is not required.
	 *
	 * @param order
	 * @return boolean
	 */
	private boolean isAuthorizationRequired( OrderModel order)
	{
		final PaymentModeModel paymentModeData = order.getPaymentMode();
		final boolean isCashOnDelivery = (null != paymentModeData && PaymentModeEnum.CASHONDELIVERY.getCode().equals(
				paymentModeData.getCode()));
		final boolean isIPG = (null != paymentModeData && PaymentModeEnum.CREDITCARD.getCode().equals(paymentModeData.getCode()));
		final boolean isLandmarkRewardPaymentMode = (null != paymentModeData && PaymentModeEnum.LANDMARKREWARD.getCode().equals(
				paymentModeData.getCode()));
		final boolean isMultiPaymentMode = (null != paymentModeData && PaymentModeEnum.MULTIPAYMENTMODE.getCode().equals(
				paymentModeData.getCode()));
		return !(isCashOnDelivery || isIPG || isLandmarkRewardPaymentMode || isMultiPaymentMode);
	}
	
	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
