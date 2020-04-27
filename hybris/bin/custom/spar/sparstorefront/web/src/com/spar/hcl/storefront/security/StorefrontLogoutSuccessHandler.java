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
package com.spar.hcl.storefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;


public class StorefrontLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
{
	protected static final Logger LOG = Logger.getLogger(GUIDAuthenticationSuccessHandler.class);
	private GUIDCookieStrategy guidCookieStrategy;
	private List<String> restrictedPages;

	public static final String ZERO = "0";
	@Resource(name = "modelService")
	ModelService modelService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected List<String> getRestrictedPages()
	{
		return restrictedPages;
	}

	public void setRestrictedPages(final List<String> restrictedPages)
	{
		this.restrictedPages = restrictedPages;
	}

	@Override
	public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{
		getGuidCookieStrategy().deleteCookie(request, response);

		// Delegate to default redirect behaviour
		super.onLogoutSuccess(request, response, authentication);
		if (authentication != null)
		{
			setEmployeediscountamount(authentication.getName());
		}
	}

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response)
	{
		String targetUrl = super.determineTargetUrl(request, response);

		for (final String restrictedPage : getRestrictedPages())
		{
			// When logging out from a restricted page, return user to homepage.
			if (targetUrl.contains(restrictedPage))
			{
				targetUrl = super.getDefaultTargetUrl();
			}
		}

		return targetUrl;
	}

	void setEmployeediscountamount(final String id)
	{
		if (StringUtils.isNotEmpty(id))
		{
			final CustomerModel customerModel = new CustomerModel();
			customerModel.setUid(id);
			try
			{
				final CustomerModel customer = flexibleSearchService.getModelByExample(customerModel);
				if (null != customer && customer.getWhetherEmployee() != null && customer.getWhetherEmployee().booleanValue()
						&& customer.getEmployeeCode() != null && customer.getCOemployeediscountamount() != null)
				{
					customer.setCOemployeediscountamount(Double.valueOf(ZERO));
					modelService.save(customer);
					return;
				}
			}
			catch (final ModelNotFoundException m)
			{
				LOG.info("Exception While Parsing");
				return;
			}
			catch (final WebServiceException w)
			{
				LOG.info("Exception While Accessing WebService");
				return;
			}
		}
	}

}
