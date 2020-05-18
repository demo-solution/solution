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
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractRegisterPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.Config;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.spar.hcl.facades.constants.SparFacadesConstants;
import com.spar.hcl.core.employee.discount.ws.EmployeeDetails;
import com.spar.hcl.core.employee.discount.ws.MAXEmployeeDiscountLimitIfc;
import com.spar.hcl.core.employee.discount.ws.MAXEmployeeDiscountLimitService;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.forms.SparRegisterForm;


/**
 * Register Controller . Handles login and register for the account flow.
 */

public abstract class SparAbstractRegisterPageController extends AbstractRegisterPageController
{
	protected static final Logger LOG = Logger.getLogger(SparAbstractRegisterPageController.class);
	public static final String PORT = "employeediscount.port";
	public static final String TIMEOUT = "employeediscount.timeout";
	public static final String IP = "employeediscount.ip";
	public static final String ZERO = "0";

	@Resource(name = "sparRegistrationValidator")
	private Validator sparRegistrationValidator;
	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;
	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "modelService")
	private ModelService modelService;

	/**
	 * @return the registrationValidator
	 */
	@Override
	protected Validator getRegistrationValidator()
	{
		return sparRegistrationValidator;
	}

	/**
	 * This method takes data from the registration form and create a new customer account and attempts to log in using
	 * the credentials of this new user.
	 *
	 * @return true if there are no binding errors or the account does not already exists.
	 * @throws CMSItemNotFoundException
	 */
	protected String processRegisterUserRequest(final String referer, final SparRegisterForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		//Social Media Integration change start
		final boolean loginViaCheck = form.getLoginVia() != null
				&& (form.getLoginVia().equalsIgnoreCase(SparFacadesConstants.FACEBOOK) || form.getLoginVia().equalsIgnoreCase(SparFacadesConstants.GOOGLE));
		//Social Media Integration change end
		if (bindingResult.hasErrors())
		{
			model.addAttribute(form);
			model.addAttribute(new LoginForm());
			model.addAttribute(new GuestForm());
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			GlobalMessages.addErrorMessage(model, "form.global.error");
			//Social Media Integration change start
			if (loginViaCheck)
			{
				model.addAttribute("loginViaCheck", "true");
			}
			else
			{
				model.addAttribute("loginViaCheck", "false");
			}
			//Social Media Integration change end
			return handleRegistrationError(model);
		}

		final RegisterData data = new RegisterData();

		data.setFirstName(form.getFirstName());
		data.setLastName(form.getLastName());
		data.setLogin(form.getEmail());
		data.setPassword(form.getPwd());
		data.setTitleCode(form.getTitleCode());

		if (null != form.getWhetherEmployee() && form.getWhetherEmployee().booleanValue())
		{
			data.setEmployeeCode(form.getEmployeeCode());
			data.setWhetherEmployee(form.getWhetherEmployee());
			data.setDateOfBirth(form.getDateOfBirth());
			data.setDateOfJoining(form.getDateOfJoining());
		}
		if (null != form.getWhetherSubscribedToPromotion() && form.getWhetherSubscribedToPromotion().booleanValue())
		{
			data.setWhetherSubscribedToPromotion(form.getWhetherSubscribedToPromotion());
		}
		data.setWhetherSubscribedToLandmark(form.getWhetherSubscribedToLandmark());
		//Social Media Integration change start
		if(null != form.getLoginVia())
		{
			data.setLoginVia(form.getLoginVia());
			data.setWhetherSubscribedToLandmark(Boolean.FALSE);
		}
		//Social Media Integration change end
		try
		{
			sparCustomerFacade.register(data);
			getAutoLoginStrategy().login(form.getEmail().toLowerCase(), form.getPwd(), request, response);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"registration.confirmation.message.title");

			/*
			 * GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
			 * "registration.confirmation.message.title");
			 */
			model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		}
		catch (final DuplicateUidException e)
		{
			System.out.println("registration failed" + e);
			LOG.warn("registration failed: " + e);
			model.addAttribute(form);
			model.addAttribute(new LoginForm());
			model.addAttribute(new GuestForm());
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			bindingResult.rejectValue("email", "registration.error.account.exists.title");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return handleRegistrationError(model);
		}
		System.out.println("registration success");
		getEmployeeDiscountData(form.getEmail());
		/*
		 * commented for optional page redirect //return REDIRECT_PREFIX + getSuccessRedirect(request, response);
		 */

		final SparAddressForm addressForm = new SparAddressForm();
		model.addAttribute(addressForm);
		if(loginViaCheck && null != referer && referer.contains("/login/checkout"))
		{
			return REDIRECT_PREFIX + "/cart";
		}
		else
		{
			return REDIRECT_PREFIX + "/registeroptional/optionalDetails";
		}

	}

	/**
	 * Returns default register page on click register
	 */
	@Override
	protected String getDefaultRegistrationPage(final Model model) throws CMSItemNotFoundException
	{
		System.out.println("inside SparabstractPagecontroller");
		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("header.link.login", null,
				getI18nService().getCurrentLocale()), null);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
		model.addAttribute(new SparRegisterForm());
		return getView();
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
				customer.setCOemployeediscountamount(Double.valueOf(0.0));
				final EmployeeDetails employeeDetails;
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
							LOG.info(customer.getUid());
							LOG.info(service1.getEmployeeDetails(customer.getEmployeeCode()).toString());
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
					LOG.info("Model Not Found Exception g");
					return;


				}
				catch (final JAXBException e)
				{
					LOG.info("JAXBException While Parsing");
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
}
