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
package com.spar.hcl.core.constants;

import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;



/**
 * Global class for all SparCore constants. You can add global constants for your extension into this class.
 */
public final class SparCoreConstants extends GeneratedSparCoreConstants
{
	public static final String EXTENSIONNAME = "sparcore";

	private SparCoreConstants()
	{
		//empty
	}

	/**
	 * Payment Gateway properties for Credit/Debit Cards
	 *
	 * @author rohan_c
	 *
	 */
	public interface IPGProperties extends PaymentConstants.PaymentProperties
	{
		public final String IPG_POST_URL = "ipg.post.url";
		public final String IPG_SUCCESS_RESPONSE_URL = "ipg.response.success.url";
		public final String IPG_FAILURE_RESPONSE_URL = "ipg.response.failure.url";
		public final String IPG_CURRENCY = "ipg.currency";
		public final String MERCHANT_ID = "icici.store.id";
		public final String SHARED_SECRET = "icici.store.password";
		public final String TIME_ZONE = "ipg.timezone";
		public final String TXN_TYPE = "ipg.txntype";
		public final String MODE = "ipg.mode";
		public final String LANGUAGE = "ipg.language.iso";
		public final String HASH_ALGORITHM = "ipg.hash_algorithm.sha";
	}

	/**
	 * Payment Gateway constants for Credit/Debit Cards
	 *
	 * @author rohan_c
	 *
	 */
	public interface IPGPaymentConstants extends PaymentConstants
	{
		public interface PaymentProperties
		{
			public final String IPG_POST_URL = "ipg.post.url";
			public final String IPG_RESPONSE_SUCCESS_URL = "ipg.response.success.url";
			public final String IPG_RESPONSE_FAILURE_URL = "ipg.response.success.url";
			public final String IPG_DEBUG_MODE = "ipg.debug.mode";
		}
	}

	/**
	 * This interface is used to hold constants for value providers
	 * 
	 * @author rohan_c
	 *
	 */
	public interface SparValueProviderConstants
	{
		public static final String POS_LEVEL_DELIMETER = "@";
		public static final String FIELD_VALUE_DELIMETER = ":";

	}

}
