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
package com.spar.hcl.core.customeraccount.service.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;

import org.apache.log4j.Logger;


/**
 * @author ravindra.kr
 *
 */

/**
 * Default implementation of {@link CustomerAccountService}
 */
public class SparDefaultCustomerAccountService extends DefaultCustomerAccountService
{
	private static final Logger LOG = Logger.getLogger(SparDefaultCustomerAccountService.class);

	@Override
	public void register(final CustomerModel customerModel, final String password) throws DuplicateUidException
	{
		LOG.info("In class SparDefaultCustomerAccountService, method : register");
		registerCustomer(customerModel, password);
		//getEventService().publishEvent(initializeEvent(new RegisterEvent(), customerModel));
	}
}
