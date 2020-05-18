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
package com.spar.hcl.core.payment.ipg.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.cybersource.enums.SubscriptionFrequencyEnum;
import de.hybris.platform.acceleratorservices.payment.cybersource.enums.TransactionTypeEnum;
import de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl.DefaultCreateSubscriptionRequestStrategy;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.spar.hcl.core.constants.SparCoreConstants.IPGProperties;
import com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy;


/**
 * This class is an extension for DefaultCreateSubscriptionRequestStrategy to create CreateSubscriptionRequest with IPG
 * parameters
 *
 * @author rohan_c
 *
 */
public class SparDefaultCreateSubscriptionRequestStrategy extends DefaultCreateSubscriptionRequestStrategy implements
		SparCreateSubscriptionRequestStrategy
{
	/**
	 * This method is used to create a subscription request for IPG
	 */
	@Override
	@SuppressWarnings("unused")
	public CreateSubscriptionRequest createSubscriptionRequest(final String siteName, final String requestUrl,
			final String responseUrl, final String merchantCallbackUrl, final CustomerModel customerModel,
			final CreditCardPaymentInfoModel cardInfo, final AddressModel paymentAddress) throws IllegalArgumentException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel == null)
		{
			return null;
		}

		final CreateSubscriptionRequest request = new CreateSubscriptionRequest();
		//Common Data
		request.setRequestId(UUID.randomUUID().toString());
		request.setSiteName(siteName);
		request.setRequestUrl(requestUrl);

		//Version Specific Data using converters
		request.setCustomerBillToData(getCustomerBillToDataConverter().convert(paymentAddress));
		this.setEmailAddress(request.getCustomerBillToData(), customerModel);
		request.setCustomerShipToData(getCustomerShipToDataConverter().convert(cartModel));
		request.setPaymentInfoData(getPaymentInfoDataConverter().convert(cardInfo));

		//In-line Version Specific Data
		request.setOrderInfoData(getRequestOrderInfoData(TransactionTypeEnum.subscription));
		request.setSignatureData(getRequestSignatureData());
		request.setSubscriptionSignatureData(getRequestSubscriptionSignatureData(SubscriptionFrequencyEnum.ON_DEMAND));
		request.setOrderPageAppearanceData(getHostedOrderPageAppearanceConfiguration());
		request.setOrderPageConfirmationData(getOrderPageConfirmationData(responseUrl, merchantCallbackUrl));

		return request;
	}

	/**
	 * This method is overridden to set signature data with IPG request parameters.
	 */
	@Override
	protected SignatureData getRequestSignatureData()
	{
		final SignatureData data = new SignatureData();

		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel == null)
		{
			return null;
		}

		if (StringUtils.isNotEmpty(getHostedOrderPageTestCurrency()))
		{
			data.setCurrency(getHostedOrderPageTestCurrency());
		}
		else
		{
			data.setCurrency(cartModel.getCurrency().getIsocode());

		}
		final BigDecimal amount = new BigDecimal(cartModel.getTotalPrice().doubleValue());
		data.setAmount(amount);
		data.setMerchantID(getMerchantId());
		data.setOrderPageSerialNumber(getSerialNumber());
		data.setOrderPageVersion(getHostedOrderPageVersion());
		data.setSharedSecret(getSharedSecret());
		data.setTimezone(getTimeZone());
		data.setTxntype(getTxnType());
		data.setLanguage(getLanguage());
		data.setHash_algorithm(getHashAlgorithm());
		data.setMode(getMode());
		data.setOid(cartModel.getCode());
		return data;
	}


	/**
	 * This method is used to get the ISO currency code configured for the CyberSource Test Hosted Order Page configured
	 * in the Business Centre. This may be different to the currency being used for the live account.
	 *
	 * @return a three character representing the currency ISO code.
	 */
	@Override
	protected String getHostedOrderPageTestCurrency()
	{
		return getSiteConfigProperty(IPGProperties.IPG_CURRENCY);
	}

	@Override
	protected String getSiteConfigProperty(final String key)
	{
		return getSiteConfigService().getString(key, "");
	}

	/**
	 * Gets the IPG merchant ID.
	 *
	 * @return the IPG merchant ID
	 */
	@Override
	protected String getMerchantId()
	{
		return getSiteConfigProperty(IPGProperties.MERCHANT_ID);
	}

	/**
	 * Gets the IPG merchant's shared secret that is used to encrypt and validate connections.
	 *
	 * @return the shared secret downloaded from the IPG Business Centre.
	 */
	@Override
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(IPGProperties.SHARED_SECRET);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getTimeZone()
	 */
	@Override
	public String getTimeZone()
	{
		return getSiteConfigProperty(IPGProperties.TIME_ZONE);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getTxnType()
	 */
	@Override
	public String getTxnType()
	{
		return getSiteConfigProperty(IPGProperties.TXN_TYPE);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getLanguage()
	 */
	@Override
	public String getLanguage()
	{
		return getSiteConfigProperty(IPGProperties.LANGUAGE);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getHashAlgorithm()
	 */
	@Override
	public String getHashAlgorithm()
	{
		return getSiteConfigProperty(IPGProperties.HASH_ALGORITHM);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getMode()
	 */
	@Override
	public String getMode()
	{
		return getSiteConfigProperty(IPGProperties.MODE);
	}

}
