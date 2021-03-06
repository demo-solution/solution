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
package com.spar.hcl.v2.controller;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spar.hcl.facades.customer.SparCustomerFacade;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;


@Controller
@RequestMapping(value = "/{baseSiteId}/forgottenpasswordtokens")
@CacheControl(directive = CacheControlDirective.NO_STORE)
public class ForgottenPasswordsController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(UsersController.class);
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	
	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	/**
	 * Generates a token to restore customer's forgotten password.
	 *
	 * @formparam userId Customer's user id. Customer user id is case insensitive.
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void restorePassword(@RequestParam final String userId) throws IOException, DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("restorePassword: uid=" + sanitize(userId));
		}
		customerFacade.forgottenPassword(userId);
		CustomerData customerData = sparCustomerFacade.getUserForUID(userId);
		if(null != customerData && StringUtils.isNotEmpty(customerData.getCustPrimaryMobNumber())){
			sparCustomerFacade.sendForgetPasswordSMSToUser(customerData.getCustPrimaryMobNumber(), userId, Config.getString("mGage.user.forgetPassword", "mGage.user.forgetPassword"));
		}
	}
}
