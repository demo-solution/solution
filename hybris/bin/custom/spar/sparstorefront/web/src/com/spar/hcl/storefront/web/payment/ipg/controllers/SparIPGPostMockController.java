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
package com.spar.hcl.storefront.web.payment.ipg.controllers;

import de.hybris.platform.util.Config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * This class is a mock controller for IPG for developers.
 *
 * @author rohan_c
 *
 */
@Controller
@RequestMapping("/ipg-mock")
public class SparIPGPostMockController
{
	protected static final Logger LOG = Logger.getLogger(SparIPGPostMockController.class);

	protected static final String IPG_REDIRECT_POST_PAGE = "ipg/redirectPost";

	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String doValidateAndPost(final HttpServletRequest request, final Model model)
	{
		final Map<String, String> params = cloneRequestParameters(request);
		final String postUrl = params.get("responseSuccessURL");
		initializeSuccessResponse(params);
		processTransactionDecision(request, null, params, false);

		model.addAttribute("postParams", params);
		model.addAttribute("postUrl", postUrl);

		return IPG_REDIRECT_POST_PAGE;
	}

	protected Map<String, String> cloneRequestParameters(final HttpServletRequest request)
	{
		final Enumeration<String> paramNames = request.getParameterNames();
		final Map<String, String> params = new HashMap<String, String>();
		while (paramNames.hasMoreElements())
		{
			final String paramName = paramNames.nextElement();
			params.put(paramName, request.getParameter(paramName));
		}
		return params;
	}

	protected void processTransactionDecision(final HttpServletRequest request, final String reasonCode,
			final Map<String, String> params, final boolean error)
	{
		if (params == null || request == null)
		{
			return;
		}

		final String decision = error ? "DECLINED" : "APPROVED";

		final String modReasonCode;
		if (StringUtils.isBlank(reasonCode))
		{
			if ("APPROVED".equalsIgnoreCase(decision))
			{
				modReasonCode = "00";
			}
			else
			{
				// General error
				modReasonCode = "150";
			}
		}
		else
		{
			modReasonCode = reasonCode;
		}

		params.put("decision", decision);
		params.put("reasonCode", modReasonCode);
	}

	/**
	 * Method to mock success response
	 *
	 * @param resultMap
	 */
	protected void initializeSuccessResponse(final Map<String, String> resultMap)
	{
		final boolean isSuccessResponse = Config.getBoolean("ipg.mockIPGSuccessResponse", true);
		resultMap.put("paymentMethod", "M");
		resultMap.put("txndate_processed", "04/07/16 08:26:38");// server not
		resultMap.put("txntype", "sale");
		resultMap.put("ccbrand", "MASTERCARD");
		resultMap.put("expyear", "2024");
		resultMap.put("expmonth", "12");
		resultMap.put("processor_response_code", "00");
		resultMap.put("cardnumber", "(MASTERCARD) ... 4979");
		resultMap.put("txndatetime", "2016:07:04-11:53:51"); // server in india
		resultMap.put("response_hash", "ec066d0705c21dbb8b8bad43460cbb6be4a6b75a");
		resultMap.put("tdate", "1467613598");
		resultMap.put("ccbin", "542606");
		resultMap.put("oid", "00014001");
		resultMap.put("cccountry", "BEL");
		resultMap.put("timezone", "IST");
		resultMap.put("chargetotal", "129.00");
		resultMap.put("currency", "356");
		resultMap.put("ipgTransactionId", "17852595");//auth code
		resultMap.put("terminal_id", "44000054");
		resultMap.put("endpointTransactionId", "070511153199");
		resultMap.put("hash_algorithm", "SHA1");

		if (isSuccessResponse)
		{
			resultMap.put("status", "APPROVED");
			resultMap.put("approval_code", "Y:005648:0017859712:PPX :070511153199");
			resultMap.put("fail_rc", null);
			resultMap.put("fail_reason", null);
			resultMap.put("response_code_3dsecure", "1");
		}
		else
		{
			if (Config.getBoolean("ipg.mockIPGFailureResponseDecline", true))
			{
				resultMap.put("status", "DECLINED");
			}
			else
			{
				resultMap.put("status", "FAILED");
			}
			resultMap.put("approval_code", "N:-5005:FRAUD - Card's country blocked ");
			resultMap.put("fail_rc", "5005");
			resultMap.put("fail_reason", "The country of the credit card has been blocked.");
			resultMap.put("response_code_3dsecure", "8");
		}


	}
}
