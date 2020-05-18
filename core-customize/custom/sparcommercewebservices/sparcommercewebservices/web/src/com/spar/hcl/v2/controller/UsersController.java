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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spar.hcl.constants.YcommercewebservicesConstants;
import com.spar.hcl.core.mobileverification.service.SparMobileVerificationService;
import com.spar.hcl.dto.OtpWsDTO;
import com.spar.hcl.facades.constants.SparFacadesConstants;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.populator.HttpRequestCustomerDataPopulator;
import com.spar.hcl.populator.options.PaymentInfoOption;
import com.spar.hcl.user.data.AddressDataList;
import com.spar.hcl.validation.data.AddressValidationData;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;



/**
 * Main Controller for Users
 *
 * @pathparam userId User identifier or one of the literals below :
 *            <ul>
 *            <li>'current' for currently authenticated user</li>
 *            <li>'anonymous' for anonymous user</li>
 *            </ul>
 * @pathparam addressId Address identifier
 * @pathparam paymentDetailsId - Payment details identifier
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@CacheControl(directive = CacheControlDirective.PRIVATE)
public class UsersController extends BaseCommerceController
{
	private static final Logger LOG = Logger.getLogger(UsersController.class);
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "modelService")
	private ModelService modelService;
	@Resource(name = "customerGroupFacade")
	private CustomerGroupFacade customerGroupFacade;
	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;
	@Resource(name = "httpRequestCustomerDataPopulator")
	private HttpRequestCustomerDataPopulator httpRequestCustomerDataPopulator;
	@Resource(name = "httpRequestAddressDataPopulator")
	private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "HttpRequestUserSignUpDTOPopulator")
	private Populator<HttpServletRequest, UserSignUpWsDTO> httpRequestUserSignUpDTOPopulator;
	@Resource(name = "addressValidator")
	private Validator addressValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	private ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "addressDataErrorsPopulator")
	private Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> addressDataErrorsPopulator;
	@Resource(name = "validationErrorConverter")
	private Converter<Object, List<ErrorWsDTO>> validationErrorConverter;
	@Resource(name = "ccPaymentInfoValidator")
	private Validator ccPaymentInfoValidator;
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "putUserDTOValidator")
	private Validator putUserDTOValidator;
	@Resource(name = "userSignUpDTOValidator")
	private Validator userSignUpDTOValidator;
	@Resource(name = "guestConvertingDTOValidator")
	private Validator guestConvertingDTOValidator;
	@Resource(name = "passwordStrengthValidator")
	private Validator passwordStrengthValidator;
	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "sparAddressDataValidator")
	private Validator sparAddressDataValidator;

	@Resource(name = "sparMobileVerificationService")
	private SparMobileVerificationService sparMobileVerificationService;
	
	@Resource(name = "socialUserSignUpDTOValidator")
	private Validator socialUserSignUpDTOValidator;

	private String sysGenOTPKey;
	private String mobilenum;

	/**
	 * Registers a customer. The following two sets of parameters are available:
	 * <ul>
	 * <li>First set is used to register a customer. In this case the required parameters are: login, password,
	 * firstName, lastName, titleCode.</li>
	 * <li>Second set is used to convert a guest to a customer. In this case the required parameters are: guid, password.
	 * </li>
	 * <ul>
	 *
	 * @formparam login Customer's login. Customer login is case insensitive.
	 * @formparam password Customer's password.
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam guid Guest order's guid.
	 * @throws de.hybris.platform.commerceservices.customer.DuplicateUidException
	 *            in case the requested login already exists
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException
	 *            in case given parameters are invalid
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void registerUser(@RequestParam(required = false) final String login, @RequestParam final String password,
			@RequestParam(required = false) final String titleCode, @RequestParam(required = false) final String firstName,
			@RequestParam(required = false) final String lastName, @RequestParam(required = false) final String guid,
			final HttpServletRequest request) throws DuplicateUidException, RequestParameterException, WebserviceValidationException
	{
		final UserSignUpWsDTO user = new UserSignUpWsDTO();
		httpRequestUserSignUpDTOPopulator.populate(request, user);

		if (guid != null)
		{
			validate(user, "user", guestConvertingDTOValidator);
			convertToCustomer(login, password, titleCode, firstName, lastName, guid);
		}
		else
		{
			validate(user, "user", userSignUpDTOValidator);
			registerNewUser(login, password, titleCode, firstName, lastName);
		}
	}

	/**
	 * Registers a customer.
	 *
	 * @param user
	 *           User's object
	 * @bodyparams uid,password,titleCode,firstName,lastName
	 * @throws de.hybris.platform.commerceservices.customer.DuplicateUidException
	 *            in case the requested login already exists
	 * @throws UnknownIdentifierException
	 *            if the title code is invalid
	 * @throws WebserviceValidationException
	 *            if any filed is invalid
	 * @throws ParseException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public void registerUser(@RequestBody final UserSignUpWsDTO user) throws DuplicateUidException, UnknownIdentifierException,
			IllegalArgumentException, WebserviceValidationException, ParseException
	{
		RegisterData registration = new RegisterData();
		if (!user.getLoginVia().isEmpty() && null != user.getLoginVia() 
				&& (user.getLoginVia().equalsIgnoreCase(SparFacadesConstants.FACEBOOK) || user.getLoginVia().equalsIgnoreCase(SparFacadesConstants.GOOGLE)))
      {
			validate(user, "user", socialUserSignUpDTOValidator);
			registration = dataMapper.map(user, RegisterData.class,"login,password,firstName,loginVia");
			registration.setWhetherSubscribedToLandmark(Boolean.FALSE);
			registration.setWhetherSubscribedToPromotion(Boolean.FALSE);
			if(null == registration.getPassword() || !registration.getPassword().equalsIgnoreCase("test1234"))
			{
				String pwd = Config.getString("social.media.auto.password", "test1234");
				registration.setPassword(pwd);
			}
			if(null == registration.getFirstName() || registration.getFirstName().isEmpty())
			{
				registration.setFirstName(registration.getLogin().substring(0, registration.getLogin().indexOf("@")));
			}
			registration.setLastName(registration.getLogin().substring(0, registration.getLogin().indexOf("@")));
      }
		else	{
			validate(user, "user", userSignUpDTOValidator);
			registration = dataMapper.map(user, RegisterData.class,
					"login,password,titleCode,firstName,lastName,dateOfJoining,dateOfBirth,employeeCode,whetherEmployee,loginVia");
			registration.setWhetherSubscribedToLandmark(Boolean.FALSE);
			registration.setWhetherSubscribedToPromotion(Boolean.FALSE);
		}
		sparCustomerFacade.register(registration);
	}

	private void registerNewUser(final String login, final String password, final String titleCode, final String firstName,
			final String lastName) throws RequestParameterException, DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registerUser: login=" + sanitize(login));
		}

		if (!EmailValidator.getInstance().isValid(login))
		{
			throw new RequestParameterException("Login [" + sanitize(login) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "login");
		}

		final RegisterData registration = new RegisterData();
		registration.setFirstName(firstName);
		registration.setLastName(lastName);
		registration.setLogin(login);
		registration.setPassword(password);
		registration.setTitleCode(titleCode);
		customerFacade.register(registration);
	}


	private void convertToCustomer(final String login, final String password, final String titleCode, final String firstName,
			final String lastName, final String guid) throws RequestParameterException, DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registerUser: guid=" + sanitize(guid));
		}

		try
		{
			customerFacade.changeGuestToCustomer(password, guid);
		}
		catch (final UnknownIdentifierException ex)
		{
			throw new RequestParameterException("Order with guid " + sanitize(guid) + " not found in current BaseStore",
					RequestParameterException.UNKNOWN_IDENTIFIER, "guid", ex);
		}
		catch (final IllegalArgumentException ex)
		{
			// Occurs when order does not belong to guest user.
			// For security reasons it's better to treat it as "unknown identifier" error
			throw new RequestParameterException("Order with guid " + sanitize(guid) + " not found in current BaseStore",
					RequestParameterException.UNKNOWN_IDENTIFIER, "guid", ex);
		}
	}

	/**
	 * Returns customer profile.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 *
	 * @return Customer profile.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public UserWsDTO getUser(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UserWsDTO dto = dataMapper.map(customerData, UserWsDTO.class, fields);
		return dto;
	}

	/**
	 * Updates customer profile. Attributes not provided in the request body will be defined again (set to null or
	 * default).
	 *
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam language Customer's language.
	 * @formparam currency Customer's currency.
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putUser(@RequestParam final String firstName, @RequestParam final String lastName,
			@RequestParam(required = false) final String titleCode,
			@RequestParam(defaultValue = "false") final Boolean whetherEmployee,
			@RequestParam(required = false) final Date dateOfJoining, @RequestParam(required = false) final Date dateOfBirth,
			@RequestParam(required = false) final String employeeCode, final HttpServletRequest request)
			throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("putCustomer: userId=" + customer.getUid());
		}
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setTitleCode(titleCode);
		if (whetherEmployee.booleanValue())
		{
			customer.setWhetherEmployee(whetherEmployee);
			customer.setEmployeeCode(employeeCode);
			customer.setDateOfJoining(dateOfJoining);

		}
		else
		{
			customer.setWhetherEmployee(whetherEmployee);
		}
		customer.setDateOfBirth(dateOfBirth);
		customer.setLanguage(null);
		customer.setCurrency(null);
		//httpRequestCustomerDataPopulator.populate(request, customer);

		//customerFacade.updateFullProfile(customer);
		sparCustomerFacade.updateCustomerProfile(customer);
	}

	/**
	 * Updates customer profile. Attributes not provided in the request body will be defined again (set to null or
	 * default).
	 *
	 * @param user
	 *           User's object
	 * @bodyparams firstName,lastName,titleCode,currency(isocode),language(isocode)
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putUser(@RequestBody final UserWsDTO user) throws DuplicateUidException
	{
		validate(user, "user", putUserDTOValidator);

		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("putCustomer: userId=" + customer.getUid());
		}

		dataMapper.map(user, customer, "firstName,lastName,titleCode,currency(isocode),language(isocode)", true);
		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Updates customer profile. Only attributes provided in the request body will be changed.
	 *
	 * @formparam firstName Customer's first name.
	 * @formparam lastName Customer's last name.
	 * @formparam titleCode Customer's title code. For a list of codes, see /{baseSiteId}/titles resource
	 * @formparam language Customer's language.
	 * @formparam currency Customer's currency.
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void updateUser(final HttpServletRequest request) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateUser: userId=" + customer.getUid());
		}
		httpRequestCustomerDataPopulator.populate(request, customer);
		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Updates customer profile. Only attributes provided in the request body will be changed.
	 *
	 * @param user
	 *           User's object
	 * @bodyparams firstName,lastName,titleCode,currency(isocode),language(isocode)
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void updateUser(@RequestBody final UserWsDTO user) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateUser: userId=" + customer.getUid());
		}

		dataMapper.map(user, customer, "firstName,lastName,titleCode,currency(isocode),language(isocode)", false);
		customerFacade.updateFullProfile(customer);
	}

	/**
	 * Removes customer profile.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deactivateUser()
	{
		final UserModel userModel = userService.getCurrentUser();
		if (!userModel.isLoginDisabled())
		{
			userModel.setLoginDisabled(true);
			modelService.save(userModel);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deactivateUser: userId=" + userModel.getUid());
		}
	}

	/**
	 * Changes customer's login.
	 *
	 * @formparam newLogin Customer's new login. Customer login is case insensitive.
	 * @formparam password Customer's current password.
	 * @throws DuplicateUidException
	 * @throws PasswordMismatchException
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/login", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void changeLogin(@RequestParam final String newLogin, @RequestParam final String password)
			throws DuplicateUidException, PasswordMismatchException, RequestParameterException
	{
		if (!EmailValidator.getInstance().isValid(newLogin))
		{
			throw new RequestParameterException("Login [" + newLogin + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "newLogin");
		}
		customerFacade.changeUid(newLogin, password);
	}

	/**
	 * Changes customer's password.
	 *
	 * @formparam new New password
	 * @formparam old Old password. Required only for ROLE_CUSTOMERGROUP
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/password", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void changePassword(@PathVariable final String userId, @RequestParam(required = false) final String oldPassword,
			@RequestParam(value = "newPassword") final String newPassword) throws IOException, DuplicateUidException
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final UserSignUpWsDTO customer = new UserSignUpWsDTO();
		customer.setPassword(newPassword);
		validate(customer, "password", passwordStrengthValidator);
		if (containsRole(auth, "ROLE_TRUSTED_CLIENT") || containsRole(auth, "ROLE_CUSTOMERMANAGERGROUP"))
		{
			userService.setPassword(userId, newPassword);
		}
		else
		{
			final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
			if (StringUtils.isEmpty(oldPassword))
			{
				throw new RequestParameterException("Request parameter 'old password' is missing.",
						RequestParameterException.MISSING, "old");
			}
			customerFacade.changePassword(oldPassword, newPassword);
			sparCustomerFacade.sendSMSToUser(currentCustomerData.getCustPrimaryMobNumber(), Config.getString("mGage.user.changePassword", "mGage.user.changePassword"));
		}
	}

	private boolean containsRole(final Authentication auth, final String role)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns customer's addresses.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return List of customer's addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.GET)
	@ResponseBody
	public AddressListWsDTO getAddresses(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<AddressData> addressList = userFacade.getAddressBook();
		final AddressDataList addressDataList = new AddressDataList();
		addressDataList.setAddresses(addressList);
		final AddressListWsDTO dto = dataMapper.map(addressDataList, AddressListWsDTO.class, fields);
		if (addressList.isEmpty())
		{
			dto.setMessage("No Address found");
		}
		else
		{
			dto.setMessage("success");
		}

		return dto;
	}

	/**
	 * Creates a new address.
	 *
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode Isocode for region. If this parameter is required depends on country.
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Created address
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public AddressWsDTO createAddress(@RequestParam(required = false) final String titleCode,
			@RequestParam(required = false) final String firstName, @RequestParam(required = false) final String lastName,
			@RequestParam(required = false) final String line1, @RequestParam(required = false) final String line2,
			@RequestParam(required = false) final String town, @RequestParam(required = false) final String postalCode,
			@RequestParam(required = true) final String country,
			@RequestParam(required = false, defaultValue = "false") final boolean defaultAddress,
			@RequestParam(required = false) final String phone, @RequestParam(required = false) final String email,
			@RequestParam(required = false) final String area, @RequestParam(required = false) final String buildingName,
			@RequestParam(required = false) final String landmark, final HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		//final AddressData addressData = super.createAddressInternal(request);
		final AddressData addressData = new AddressData();
		addressData.setTitleCode(titleCode);
		addressData.setFirstName(firstName);
		addressData.setLastName(lastName);
		addressData.setLine1(line1);
		addressData.setLine2(line2);
		addressData.setTown(town);
		addressData.setPostalCode(postalCode);
		addressData.setBillingAddress(false);
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);
		addressData.setLandmark(landmark);
		if (country != null && !StringUtils.isEmpty(country))
		{
			addressData.setCountry(i18NFacade.getCountryForIsocode(country));
		}
		addressData.setPhone(phone);
		addressData.setEmail(email);
		addressData.setArea(area);
		addressData.setBuildingName(buildingName);
		if (userFacade.isAddressBookEmpty())
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		else
		{
			addressData.setDefaultAddress(defaultAddress);
		}

		validate(addressData, "address", sparAddressDataValidator);
		userFacade.addAddress(addressData);
		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		dto.setMessage("success");
		
		try
		{
			LOG.info("Sending sms to user" + addressData.getEmail());
			sparCustomerFacade.sendSMSToUser(addressData.getPhone(), Config.getString("mGage.user.register.message", "mGage.user.register.message"));
		}
		catch (final Exception e)
		{
			LOG.debug("Exception occured during sending SMS to user " + addressData.getEmail());
			e.printStackTrace();
		}
		
		return dto;
	}

	/**
	 * Created a new address.
	 *
	 * @param address
	 *           Address object
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress
	 * @return Created address
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public AddressWsDTO createAddress(@RequestBody final AddressWsDTO address,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		validate(address, "address", addressDTOValidator);

		final AddressData addressData = dataMapper
				.map(address,
						AddressData.class,
						"firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress,phone,email,buildingName,area,landmark");

		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);

		userFacade.addAddress(addressData);
		if (addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}

		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		return dto;
	}

	/**
	 * Returns detailed information about address with a given id.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Address data
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.GET)
	@ResponseBody
	public AddressWsDTO getAddress(@PathVariable final String addressId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getAddress: id=" + sanitize(addressId));
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}

		final AddressWsDTO dto = dataMapper.map(addressData, AddressWsDTO.class, fields);
		return dto;
	}

	/**
	 * Updates the address. Attributes not provided in the request will be defined again (set to null or default).
	 *
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country .isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 *            restparam region .isocode Isocode for region. If this parameter is required depends on country.
	 * @formparam defaultAddress Parameter specifies if address should be default for customer
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putAddress(@PathVariable final String addressId, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("editAddress: id=" + sanitize(addressId));
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFirstName(null);
		addressData.setLastName(null);
		addressData.setCountry(null);
		addressData.setLine1(null);
		addressData.setLine2(null);
		addressData.setPostalCode(null);
		addressData.setRegion(null);
		addressData.setTitle(null);
		addressData.setTown(null);
		addressData.setDefaultAddress(false);
		addressData.setFormattedAddress(null);
		addressData.setLandmark(null);
		httpRequestAddressDataPopulator.populate(request, addressData);

		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
		userFacade.editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
	}

	/**
	 * Updates the address. Attributes not provided in the request will be defined again (set to null or default).
	 *
	 * @param address
	 *           Address object
	 * @bodyparams
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putAddress(@PathVariable final String addressId, @RequestBody final AddressWsDTO address)
			throws WebserviceValidationException
	{
		validate(address, "address", addressDTOValidator);
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		dataMapper.map(address, addressData,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress,landmark",
				true);

		userFacade.editAddress(addressData);

		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
	}

	/**
	 * Updates the address. Only attributes provided in the request body will be changed.
	 *
	 * @formparam firstName Customer's first name. This parameter is required.
	 * @formparam lastName Customer's last name. This parameter is required.
	 * @formparam titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode ISO code for region. If this parameter is required depends on country.
	 * @formparam defaultAddress Parameter specifies if address should be default for customer
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void patchAddress(@PathVariable final String addressId, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("editAddress: id=" + sanitize(addressId));
		}
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");

		httpRequestAddressDataPopulator.populate(request, addressData);
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (addressData.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		userFacade.editAddress(addressData);
	}

	/**
	 * Updates the address. Only attributes provided in the request body will be changed.
	 *
	 * @param address
	 *           address object
	 * @bodyparams
	 *             firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void patchAddress(@PathVariable final String addressId, @RequestBody final AddressWsDTO address)
			throws WebserviceValidationException
	{
		final AddressData addressData = userFacade.getAddressForCode(addressId);
		if (addressData == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}
		final boolean isAlreadyDefaultAddress = addressData.isDefaultAddress();
		addressData.setFormattedAddress(null);

		dataMapper.map(address, addressData,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress,landmark",
				false);
		validate(addressData, "address", addressValidator);

		if (addressData.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			addressData.setDefaultAddress(true);
			addressData.setVisibleInAddressBook(true);
		}
		if (!isAlreadyDefaultAddress && addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		userFacade.editAddress(addressData);
	}

	/**
	 * Removes customer's address.
	 *
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/{addressId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteAddress(@PathVariable final String addressId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deleteAddress: id=" + sanitize(addressId));
		}
		final AddressData address = userFacade.getAddressForCode(addressId);
		if (address == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(addressId)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}
		userFacade.removeAddress(address);
	}

	/**
	 * Verifies the address.
	 *
	 * @formparam country.isocode Country isocode. This parameter is required and have influence on how rest of
	 *            parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)
	 * @formparam line1 First part of address. If this parameter is required depends on country (usually it is required).
	 * @formparam line2 Second part of address. If this parameter is required depends on country (usually it is not
	 *            required)
	 * @formparam town Town name. If this parameter is required depends on country (usually it is required)
	 * @formparam postalCode Postal code. If this parameter is required depends on country (usually it is required)
	 * @formparam region.isocode Isocode for region. If this parameter is required depends on country.
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return verification results. If address is incorrect there are information about errors and suggested addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/verification", method = RequestMethod.POST)
	@ResponseBody
	public AddressValidationWsDTO verifyAddress(final HttpServletRequest request,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AddressData addressData = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		AddressValidationData validationData = new AddressValidationData();

		httpRequestAddressDataPopulator.populate(request, addressData);
		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		final AddressValidationWsDTO dto = dataMapper.map(validationData, AddressValidationWsDTO.class, fields);
		return dto;
	}

	/**
	 * Verifies address
	 *
	 * @param address
	 *           address object
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @bodyparams firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode)
	 * @return verification results. If address is incorrect there are information about errors and suggested addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/addresses/verification", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public AddressValidationWsDTO verifyAddress(@RequestBody final AddressWsDTO address,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		// validation is a bit different here
		final AddressData addressData = dataMapper.map(address, AddressData.class,
				"firstName,lastName,titleCode,line1,line2,town,postalCode,country(isocode),region(isocode),landmark");
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		AddressValidationData validationData = new AddressValidationData();

		if (isAddressValid(addressData, errors, validationData))
		{
			validationData = verifyAddresByService(addressData, errors, validationData);
		}
		final AddressValidationWsDTO dto = dataMapper.map(validationData, AddressValidationWsDTO.class, fields);
		return dto;
	}

	/**
	 * Checks if address is valid by a validators
	 *
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 * @return true - adress is valid , false - address is invalid
	 */
	private boolean isAddressValid(final AddressData addressData, final Errors errors, final AddressValidationData validationData)
	{
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			validationData.setDecision(AddressVerificationDecision.REJECT.toString());
			validationData.setErrors(createResponseErrors(errors));
			return false;
		}
		return true;
	}

	/**
	 * Verifies address by commerce service
	 *
	 * @formparam addressData
	 * @formparam errors
	 * @formparam validationData
	 * @return object with verification errors and suggested addresses list
	 */
	private AddressValidationData verifyAddresByService(final AddressData addressData, final Errors errors,
			final AddressValidationData validationData)
	{
		final AddressVerificationResult<AddressVerificationDecision> verificationDecision = addressVerificationFacade
				.verifyAddressData(addressData);
		if (verificationDecision.getErrors() != null && !verificationDecision.getErrors().isEmpty())
		{
			populateErrors(errors, verificationDecision);
			validationData.setErrors(createResponseErrors(errors));
		}

		validationData.setDecision(verificationDecision.getDecision().toString());

		if (verificationDecision.getSuggestedAddresses() != null && verificationDecision.getSuggestedAddresses().size() > 0)
		{
			final AddressDataList addressDataList = new AddressDataList();
			addressDataList.setAddresses(verificationDecision.getSuggestedAddresses());
			validationData.setSuggestedAddressesList(addressDataList);
		}

		return validationData;
	}

	private ErrorListWsDTO createResponseErrors(final Errors errors)
	{
		final List<ErrorWsDTO> webserviceErrorDto = new ArrayList<>();
		validationErrorConverter.convert(errors, webserviceErrorDto);
		final ErrorListWsDTO webserviceErrorList = new ErrorListWsDTO();
		webserviceErrorList.setErrors(webserviceErrorDto);
		return webserviceErrorList;
	}

	/**
	 * Populates Errors object
	 *
	 * @param errors
	 * @param addressVerificationResult
	 */
	private void populateErrors(final Errors errors,
			final AddressVerificationResult<AddressVerificationDecision> addressVerificationResult)
	{
		addressDataErrorsPopulator.populate(addressVerificationResult, errors);
	}

	/**
	 * Return customer's credit card payment details list.
	 *
	 * @queryparam saved Type of payment details
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Customer's payment details list
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails", method = RequestMethod.GET)
	@ResponseBody
	public PaymentDetailsListWsDTO getPaymentInfos(@RequestParam(required = false, defaultValue = "false") final boolean saved,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPaymentInfos");
		}

		final CCPaymentInfoDatas paymentInfoDataList = new CCPaymentInfoDatas();
		paymentInfoDataList.setPaymentInfos(userFacade.getCCPaymentInfos(saved));

		return dataMapper.map(paymentInfoDataList, PaymentDetailsListWsDTO.class, fields);
	}

	/**
	 * Returns customer's credit card payment details for a given id.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return Customer's credit card payment details
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.GET)
	@ResponseBody
	public PaymentDetailsWsDTO getPaymentDetails(@PathVariable final String paymentDetailsId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PaymentDetailsWsDTO dto = dataMapper.map(getPaymentInfo(paymentDetailsId), PaymentDetailsWsDTO.class, fields);
		return dto;
	}

	public CCPaymentInfoData getPaymentInfo(final String paymentDetailsId)
	{
		LOG.debug("getPaymentInfo : id = " + sanitize(paymentDetailsId));
		try
		{
			final CCPaymentInfoData paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentDetailsId);
			if (paymentInfoData == null)
			{
				throw new RequestParameterException("Payment details [" + sanitize(paymentDetailsId) + "] not found.",
						RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId");
			}
			return paymentInfoData;
		}
		catch (final PKException e)
		{
			throw new RequestParameterException("Payment details [" + sanitize(paymentDetailsId) + "] not found.",
					RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId", e);
		}
	}

	/**
	 * Removes customer's credit card payment details based on its ID.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deletePaymentInfo(@PathVariable final String paymentDetailsId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deletePaymentInfo: id = " + sanitize(paymentDetailsId));
		}
		getPaymentInfo(paymentDetailsId);
		userFacade.removeCCPaymentInfo(paymentDetailsId);
	}

	/**
	 * Updates existing customer's credit card payment details based on its ID. Only attributes given in request will be
	 * changed.
	 *
	 * @formparam accountHolderName Name on card. This parameter is required.
	 * @formparam cardNumber Card number. This parameter is required.
	 * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
	 *            card types are supported
	 * @formparam expiryMonth Month of expiry date. This parameter is required.
	 * @formparam expiryYear Year of expiry date. This parameter is required.
	 * @formparam issueNumber
	 * @formparam startMonth
	 * @formparam startYear
	 * @formparam subscriptionId
	 * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
	 *            reused for future orders.
	 * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
	 * @formparam billingAddress.firstName Customer's first name. This parameter is required.
	 * @formparam billingAddress.lastName Customer's last name. This parameter is required.
	 * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
	 *            rest of address parameters are validated (e.g. if parameters are required :
	 *            line1,line2,town,postalCode,region.isocode)
	 * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
	 *            it is required).
	 * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
	 *            it is not required)
	 * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	public void updatePaymentInfo(@PathVariable final String paymentDetailsId, final HttpServletRequest request)
			throws RequestParameterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfo: id = " + sanitize(paymentDetailsId));
		}

		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);

		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();
		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Updates existing customer's credit card payment details based on its ID. Only attributes given in request will be
	 * changed.
	 *
	 * @param paymentDetails
	 *           payment details object
	 * @bodyparams
	 *             accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId
	 *             ,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,
	 *             region(isocode),country(isocode),defaultAddress)
	 * @throws RequestParameterException
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PATCH, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void updatePaymentInfo(@PathVariable final String paymentDetailsId,
			@RequestBody final PaymentDetailsWsDTO paymentDetails) throws RequestParameterException
	{
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

		dataMapper
				.map(paymentDetails,
						paymentInfoData,
						"accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
						false);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}

	}

	/**
	 * Updates existing customer's credit card payment info based on the payment info ID. Attributes not given in request
	 * will be defined again (set to null or default).
	 *
	 * @formparam accountHolderName Name on card. This parameter is required.
	 * @formparam cardNumber Card number. This parameter is required.
	 * @formparam cardType Card type. This parameter is required. Call GET /{baseSiteId}/cardtypes beforehand to see what
	 *            card types are supported
	 * @formparam expiryMonth Month of expiry date. This parameter is required.
	 * @formparam expiryYear Year of expiry date. This parameter is required.
	 * @formparam issueNumber
	 * @formparam startMonth
	 * @formparam startYear
	 * @formparam subscriptionId
	 * @formparam saved Parameter defines if the payment details should be saved for the customer and than could be
	 *            reused for future orders.
	 * @formparam defaultPaymentInfo Parameter defines if the payment details should be used as default for customer.
	 * @formparam billingAddress.firstName Customer's first name. This parameter is required.
	 * @formparam billingAddress.lastName Customer's last name. This parameter is required.
	 * @formparam billingAddress.titleCode Customer's title code. This parameter is required. For a list of codes, see
	 *            /{baseSiteId}/titles resource
	 * @formparam billingAddress.country.isocode Country isocode. This parameter is required and have influence on how
	 *            rest of address parameters are validated (e.g. if parameters are required :
	 *            line1,line2,town,postalCode,region.isocode)
	 * @formparam billingAddress.line1 First part of address. If this parameter is required depends on country (usually
	 *            it is required).
	 * @formparam billingAddress.line2 Second part of address. If this parameter is required depends on country (usually
	 *            it is not required)
	 * @formparam billingAddress.town Town name. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.postalCode Postal code. If this parameter is required depends on country (usually it is
	 *            required)
	 * @formparam billingAddress.region.isocode Isocode for region. If this parameter is required depends on country.
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void putPaymentInfo(@PathVariable final String paymentDetailsId, final HttpServletRequest request)
			throws RequestParameterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfo: id = " + sanitize(paymentDetailsId));
		}

		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);

		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();
		paymentInfoData.setAccountHolderName(null);
		paymentInfoData.setCardNumber(null);
		paymentInfoData.setCardType(null);
		paymentInfoData.setExpiryMonth(null);
		paymentInfoData.setExpiryYear(null);
		paymentInfoData.setDefaultPaymentInfo(false);
		paymentInfoData.setSaved(false);

		paymentInfoData.setIssueNumber(null);
		paymentInfoData.setStartMonth(null);
		paymentInfoData.setStartYear(null);
		paymentInfoData.setSubscriptionId(null);

		final AddressData address = paymentInfoData.getBillingAddress();
		address.setFirstName(null);
		address.setLastName(null);
		address.setCountry(null);
		address.setLine1(null);
		address.setLine2(null);
		address.setPostalCode(null);
		address.setRegion(null);
		address.setTitle(null);
		address.setTown(null);

		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		validate(paymentInfoData, "paymentDetails", ccPaymentInfoValidator);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Updates existing customer's credit card payment info based on the payment info ID. Attributes not given in request
	 * will be defined again (set to null or default).
	 *
	 * @param paymentDetails
	 *           payment details object
	 * @bodyparams
	 *             accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId
	 *             ,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,
	 *             region(isocode),country(isocode),defaultAddress)
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/paymentdetails/{paymentDetailsId}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public void putPaymentInfo(@PathVariable final String paymentDetailsId, @RequestBody final PaymentDetailsWsDTO paymentDetails)
			throws RequestParameterException
	{
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
		final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

		validate(paymentDetails, "paymentDetails", paymentDetailsDTOValidator);
		dataMapper
				.map(paymentDetails,
						paymentInfoData,
						"accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
						true);

		userFacade.updateCCPaymentInfo(paymentInfoData);
		if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
	}

	/**
	 * Returns all customer groups of a customer.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return All customer groups of a customer.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/customergroups", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupListWsDTO getAllCustomerGroupsForCustomer(@PathVariable final String userId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final UserGroupDataList userGroupDataList = new UserGroupDataList();
		userGroupDataList.setUserGroups(customerGroupFacade.getCustomerGroupsForUser(userId));
		return dataMapper.map(userGroupDataList, UserGroupListWsDTO.class, fields);
	}

	protected Set<OrderStatus> extractOrderStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<OrderStatus> statusesEnum = new HashSet<>();
		for (final String status : statusesStrings)
		{
			statusesEnum.add(OrderStatus.valueOf(status));
		}
		return statusesEnum;
	}

	protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result)
	{
		final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

		orderHistoriesData.setOrders(result.getResults());
		orderHistoriesData.setSorts(result.getSorts());
		orderHistoriesData.setPagination(result.getPagination());

		return orderHistoriesData;
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/OTP/{mobilenum}", method = RequestMethod.GET)
	@ResponseBody
	public OtpWsDTO generateOTP(@PathVariable final String mobilenum) throws IOException, DuplicateUidException
	{
		final OtpWsDTO otpWsDTO = new OtpWsDTO();
		final String msg = "Not Valid Number";
		final Boolean isAvailable = sparMobileVerificationService.countMobileNo(mobilenum);
		if (validateMobileNum(mobilenum, 10))
		{
			if (isAvailable.booleanValue())
			{
				sysGenOTPKey = RandomStringUtils.randomNumeric(6);
				getmGageOTPResponse(sysGenOTPKey, mobilenum);
				otpWsDTO.setIsAvailable(isAvailable);
				otpWsDTO.setMobileNumber(mobilenum);
				otpWsDTO.setOTP(sysGenOTPKey);
			}
			else
			{
				otpWsDTO.setMobileNumber(mobilenum);
				otpWsDTO.setIsAvailable(isAvailable);
			}
			return otpWsDTO;
		}
		otpWsDTO.setMessage(msg);
		return otpWsDTO;
	}

	@SuppressWarnings("boxing")
	public void getmGageOTPResponse(final String sysGenOTPKey, final String mobilenum) throws IOException, DuplicateUidException
	{
		this.sysGenOTPKey = sysGenOTPKey;
		URL url = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		final String msg = Config.getString("mGage.user.message", "mGage.user.message");
		final String OTP = sysGenOTPKey;
		LOG.info("OTP generated " + OTP);
		//	model.addAttribute("otpgen", sessionService.getAttribute("sysGenOTPKey"));
		final String OTPmsg = msg.replaceAll("XXXXXX", OTP);

		final String destmobnum = mobilenum;
		final String USER_AGENT = "Mozilla/5.0";
		if (StringUtils.isNotBlank(mobilenum) || null != mobilenum)
		{
			final AddressModel newAddress = new AddressModel();
			newAddress.setPhone1(mobilenum);
			newAddress.setIsOTPValidate(Boolean.FALSE);
		}
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");
		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(OTPmsg, "UTF-8");

		LOG.info(" OTP Message *********** " + mGageURL);
		/*
		 * if (sessionService.getAttribute("OTPKey") != null) { url = new URL(mGageURL); } else {
		 * sessionService.setAttribute("OTPKey", sysGenOTPKey);
		 */
		url = new URL(mGageURL);
		//}
		try
		{
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			//urlConnection.connect();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			final int resCode = urlConnection.getResponseCode();
			final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			final StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			LOG.info("response messege for OTP Request" + response);
			LOG.info(" resCode Message *********** " + resCode + "Message" + urlConnection.getResponseMessage());
			urlConnection.disconnect();
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
			LOG.error("Connection Failed", ex);
		}
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{userId}/OTP/{mobilenum}", method = RequestMethod.POST)
	@ResponseBody
	public OtpWsDTO validateUserOTP(@RequestParam(required = true) final String userEnteredOTP,
			@RequestParam(required = true) final String sysGenOTP, @PathVariable final String mobilenum)
			throws DuplicateUidException
	{
		final OtpWsDTO otpWsDTO = new OtpWsDTO();
		String msg = "Not Valid Number";
		if (validateMobileNum(mobilenum, 10))
		{
			msg = "false";
			if (userEnteredOTP.equals(sysGenOTP))
			{
				sparCustomerFacade.commitOTPData(mobilenum, new Boolean("true"), Boolean.TRUE, Boolean.FALSE);
				msg = "true";
			}
		}
		otpWsDTO.setMessage(msg);
		otpWsDTO.setMobileNumber(mobilenum);
		return otpWsDTO;
	}

	protected boolean validateMobileNum(final String mobilenum, final int maxFieldLength)
	{
		if (mobilenum == null || StringUtils.isEmpty(mobilenum) || (StringUtils.length(mobilenum) > maxFieldLength)
				|| (StringUtils.length(mobilenum) < maxFieldLength) || !containsOnlyNumbers(mobilenum))
		{
			return false;
		}
		return true;
	}

	protected boolean containsOnlyNumbers(final String str)
	{
		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
}
