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

import de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl.DefaultPaymentFormActionUrlStrategy;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spar.hcl.core.constants.SparCoreConstants.IPGPaymentConstants;
import com.spar.hcl.core.payment.ipg.strategies.SparPaymentFormActionUrlStrategy;


/**
 * This class is used to get the IPG URLs for post
 * 
 * @author rohan_c
 *
 */
public class SparDefaultPaymentFormActionUrlStrategy extends DefaultPaymentFormActionUrlStrategy implements
		SparPaymentFormActionUrlStrategy
{

	@Override
	public String getIPGRequestUrl(final String clientRef)
	{
		final String urlStr = getSiteConfigService().getProperty(IPGPaymentConstants.PaymentProperties.IPG_POST_URL);
		if (StringUtils.isNotEmpty(urlStr))
		{
			return String.valueOf(getAdjustRequestURI(urlStr));
		}
		return null;
	}

	/**
	 * Required to initiate a subscription facade call.
	 *
	 * @return String
	 */
	@Override
	public String getCurrentClientIpAddress()
	{
		final ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		final HttpServletRequest request = sra.getRequest();
		return request.getRemoteAddr();
	}

}
