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
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.spar.hcl.core.employee.discount.ws.EmployeeDetails;
import com.spar.hcl.core.employee.discount.ws.MAXEmployeeDiscountLimitIfc;
import com.spar.hcl.core.employee.discount.ws.MAXEmployeeDiscountLimitService;


/**
 * Default implementation of {@link AuthenticationSuccessHandler}
 */
public class GUIDAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	protected static final Logger LOG = Logger.getLogger(GUIDAuthenticationSuccessHandler.class);
	public static final String PORT = "employeediscount.port";
	public static final String TIMEOUT = "employeediscount.timeout";
	public static final String IP = "employeediscount.ip";
	public static final String ZERO = "0";
	private GUIDCookieStrategy guidCookieStrategy;
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Resource(name = "modelService")
	ModelService modelService;


	@Autowired
	private FlexibleSearchService flexibleSearchService;


	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{
		getEmployeeDiscountData(authentication.getName());
		getGuidCookieStrategy().setCookie(request, response);
		getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);

	}

	void getEmployeeDiscountData(final String id)
	{
		final boolean isemployeedsicountenabled = Config.getBoolean("enableEmployeeDiscount", false);
		LOG.info("In getEmployeeDiscountData : " + isemployeedsicountenabled + " ID :" + id);
		if (isemployeedsicountenabled == true)
		{
			final CustomerModel customerModel = new CustomerModel();
			customerModel.setUid(id);
			final CustomerModel customer = flexibleSearchService.getModelByExample(customerModel);

			if (null != customer && customer.getWhetherEmployee() != null && customer.getWhetherEmployee().booleanValue()
					&& customer.getEmployeeCode() != null)
			{
				boolean isemployeediscountapplicable = false;
				long duration = 0;
				final int port = Config.getInt(PORT, 7001);
				final int connTimeout = Config.getInt(TIMEOUT, 2000);
				final String employeediscountip = Config.getParameter(IP);
				final EmployeeDetails employeeDetails;
				// Setting the default value for CO Employee Discount Amount
				customer.setCOemployeediscountamount(Double.valueOf(ZERO));
				try
				{
					duration = System.currentTimeMillis();
					new Socket().connect(new InetSocketAddress(employeediscountip, port), connTimeout);
					LOG.info("Connected!!");
					isemployeediscountapplicable = true;
				}
				catch (final Exception e)
				{
					duration = System.currentTimeMillis() - duration;
					LOG.info(e.getMessage());
					return;
				}

				try
				{
					if (isemployeediscountapplicable)
					{
						final MAXEmployeeDiscountLimitService service = new MAXEmployeeDiscountLimitService();
						final MAXEmployeeDiscountLimitIfc service1 = service.getMAXEmployeeDiscountLimitPort();
						LOG.info("Connection Established ");
						final JAXBContext jaxbContext = JAXBContext.newInstance(EmployeeDetails.class);
						final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						if (service1.getEmployeeDetails(customer.getEmployeeCode()) != null)
						{
							employeeDetails = (EmployeeDetails) jaxbUnmarshaller.unmarshal(new StringReader(service1.getEmployeeDetails(
									customer.getEmployeeCode()).toString()));
							if (employeeDetails != null)
							{
								if (employeeDetails.getStatus_code().equals(ZERO))
								{
									customer.setWhetherEmployee(Boolean.FALSE);
									modelService.save(customer);
									return;
								}
								customer.setCOemployeediscountamount(employeeDetails.getAvailable_Discount_Amount());
								modelService.save(customer);
								return;
							}
						}
					}
				}

				catch (final ModelNotFoundException me)
				{
					LOG.info("Exception While Parsing");

				}
				catch (final JAXBException e)
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

	protected final GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	/**
	 * @param guidCookieStrategy
	 *           the guidCookieStrategy to set
	 */
	@Required
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected final AuthenticationSuccessHandler getAuthenticationSuccessHandler()
	{
		return authenticationSuccessHandler;
	}

	/**
	 * @param authenticationSuccessHandler
	 *           the authenticationSuccessHandler to set
	 */
	@Required
	public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler)
	{
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}
}
