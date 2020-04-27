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
package com.spar.hcl.core.payment.ipg.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * A strategy for creating a {@link CreateSubscriptionRequest} for IPG transaction
 *
 */
public interface SparCreateSubscriptionRequestStrategy
{
	/**
	 * This method is used to create the subscription request for IPG transavtion
	 *
	 * @param siteName
	 * @param requestUrl
	 * @param responseUrl
	 * @param merchantCallbackUrl
	 * @param customerModel
	 * @param cardInfo
	 * @param paymentAddress
	 * @return CreateSubscriptionRequest
	 * @throws IllegalArgumentException
	 */
	CreateSubscriptionRequest createSubscriptionRequest(final String siteName, final String requestUrl, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customerModel, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress) throws IllegalArgumentException;

	/**
	 * Getter for timeZone
	 * 
	 * @return String
	 */
	String getTimeZone();

	/**
	 * Getter for txnType
	 * 
	 * @return String
	 */
	String getTxnType();

	/**
	 * Getter for language
	 * 
	 * @return String
	 */
	String getLanguage();

	/**
	 * Getter for hash algorithm
	 * 
	 * @return String
	 */
	String getHashAlgorithm();

	/**
	 * Getter for Mode(payment IPG)
	 * 
	 * @return String
	 */
	String getMode();

}
