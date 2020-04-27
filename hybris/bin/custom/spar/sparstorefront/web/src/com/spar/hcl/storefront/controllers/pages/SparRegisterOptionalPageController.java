/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.mobileverification.service.SparMobileVerificationService;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparLRUserEnrollDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.validation.forms.SparOptionalRegistrationValidator;


/**
 * This Class is an extension of SparAbstractRegisterPageController to handle registration optionhal page.
 *
 * @author kumari-p
 *
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/registeroptional")
public class SparRegisterOptionalPageController extends SparAbstractRegisterPageController
{
	protected static final Logger LOG = Logger.getLogger(SparRegisterOptionalPageController.class);
	public static final String OTP_KEY_STRING = "OTPKey";
	
	private HttpSessionRequestCache httpSessionRequestCache;
	private String sysGenOTPKey;
	private String mobilenum;
	private Boolean regOTPFlagVal = Boolean.FALSE;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;
	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sparOptionalRegistrationValidator")
	private SparOptionalRegistrationValidator sparOptionalRegistrationValidator;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Autowired
	private UserService userService;

	@Resource(name = "sparMobileVerificationService")
	private SparMobileVerificationService sparMobileVerificationService;

	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	protected AcceleratorCheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	@RequestMapping(value = "/optionalDetails", method = RequestMethod.GET)
	public String doOptionalLogin(final Model model) throws CMSItemNotFoundException
	{
		setModelAttributesFromSession(model);
		return getDefaultOptionalRegistrationPage(model);
	}

	/**
	 * @param model
	 */
	private void setModelAttributesFromSession(final Model model)
	{
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("SysGenOTP", sysGenOTPKey);

	}

	protected String getDefaultOptionalRegistrationPage(final Model model) throws CMSItemNotFoundException
	{

		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("header.link.login", null,
				getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
		model.addAttribute(new SparAddressForm());
		return getView();
	}

	/*
	 * public String processRegisterOptionalRequest(final Integer sparServiceAreaId, final String defaultStore, final
	 * String defaultCncCenter, final SparAddressForm sparAddressForm, final BindingResult bindingResult,
	 */
	@SuppressWarnings("boxing")
	public String processRegisterOptionalRequest(final SparAddressForm sparAddressForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		//SparServiceAreaData sparServiceArea = null;
		final HomePageForm homePageForm = new HomePageForm();
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("SysGenOTP", sysGenOTPKey);
		//sparAddressForm.setSparserviceareaid(sparServiceAreaId);

		getSparOptionalRegistrationValidator().validate(sparAddressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			model.addAttribute(sparAddressForm);
			//model.addAttribute("sparServiceAreaId", sparAddressForm.getSparserviceareaid());
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			return handleRegistrationError(model);
		}

		final AddressData newAddress = new AddressData();
		final CustomerModel customer = getCurrentCustomer();
		if (sparAddressForm.getDateOfBirth() != null)
		{
			customer.setDateOfBirth(sparAddressForm.getDateOfBirth());
		}
		newAddress.setTitleCode(sparCustomerFacade.findCustomerTitle());
		newAddress.setFirstName(sparCustomerFacade.findFirstName());
		newAddress.setLastName(sparCustomerFacade.findLastName());
		newAddress.setEmail(sparCustomerFacade.findUserEmailId());
		newAddress.setPhone(sparAddressForm.getPhone());
		newAddress.setDateOfBirth(sparAddressForm.getDateOfBirth());
		newAddress.setTown(sparAddressForm.getTownCity());
		newAddress.setArea(sparAddressForm.getArea());
		newAddress.setPostalCode(sparAddressForm.getPostcode());
		newAddress.setBuildingName(sparAddressForm.getBuildingName());
		newAddress.setLandmark(sparAddressForm.getLandmark());
		newAddress.setLine1(sparAddressForm.getLine1());
		newAddress.setLine2(sparAddressForm.getLine2());
		newAddress.setLongAddress(sparAddressForm.getLongAddress());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		/*
		 * if (StringUtils.isNotEmpty(defaultStore)) { newAddress.setDefaultStore(defaultStore); } if
		 * (StringUtils.isNotEmpty(defaultCncCenter)) { newAddress.setDefaultCncCenter(defaultCncCenter); } if (null !=
		 * sparServiceAreaId) { sparServiceArea = storeFinderFacadeInterface.getAddressServiceArea(sparServiceAreaId);
		 * newAddress.setSparServiceArea(sparServiceArea); }
		 */
		if (sessionService.getAttribute("regOTPFlagVal") == null || sessionService.getAttribute("regOTPFlagVal").equals("false")
				|| !sessionService.getAttribute("mobilenumber").equals(sparAddressForm.getPhone()))
		{
			regOTPFlagVal = Boolean.FALSE;

			sessionService.setAttribute("regOTPFlagVal", sessionService.getAttribute("regOTPFlagVal"));
			bindingResult.rejectValue("phone", "phone.verfity.incomplete");
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			return handleRegistrationError(model);
		}


		if (sparAddressForm.getCountryIso() != null)
		{
			final CountryData countryData = getI18NFacade().getCountryForIsocode(sparAddressForm.getCountryIso());
			newAddress.setCountry(countryData);
		}
		if (sparAddressForm.getRegionIso() != null && !StringUtils.isEmpty(sparAddressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(sparAddressForm.getCountryIso(), sparAddressForm.getRegionIso());
			newAddress.setRegion(regionData);
		}
		sparAddressForm.setSaveInAddressBook(true);
		if (sparAddressForm.getSaveInAddressBook() != null)
		{
			newAddress.setVisibleInAddressBook(sparAddressForm.getSaveInAddressBook().booleanValue());
			if (sparAddressForm.getSaveInAddressBook().booleanValue() && getUserFacade().isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
			}
		}
		getUserFacade().addAddress(newAddress);
		
		try
		{
			sparCustomerFacade.sendSMSToUser(newAddress.getPhone(), Config.getString("mGage.user.register.message", "mGage.user.register.message"));
		}
		catch (final Exception e)
		{
			LOG.debug("Exception occured during sending SMS to user " + newAddress.getEmail());
			e.printStackTrace();
		}

		getCheckoutFacade().setDeliveryAddress(newAddress);
		
		enrollMemberToLandmark(customer.getLrOptStatus(), model);

		return REDIRECT_PREFIX + "/";
	}

	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "mobilenum")
	@ResponseBody
	public Boolean generateOTP(@RequestParam(value = "mobilenum") final String mobilenum, final Model model) throws IOException,
			DuplicateUidException
	{
		final Boolean isAvailable = sparMobileVerificationService.countMobileNo(mobilenum);
		if (isAvailable.booleanValue())
		{
			this.mobilenum = mobilenum;
			sysGenOTPKey = RandomStringUtils.randomNumeric(6);

			sessionService.setAttribute("sysGenOTPKey", sysGenOTPKey);

			//model.addAttribute("SysGenOTP", sysGenOTPKey);
			getmGageOTPResponse(sysGenOTPKey, mobilenum, model);
			model.addAttribute("otpgennum", sysGenOTPKey);
		}


		return isAvailable;

	}

	@SuppressWarnings("boxing")
	public void getmGageOTPResponse(final String sysGenOTPKey, final String mobilenum, final Model model)
	{
		try
		{
			sparCustomerFacade.getmGageOTPResponse(sysGenOTPKey, mobilenum);
		}
		catch (final IOException e)
		{
			LOG.error("Incorrect Input error");
		}
		catch (final DuplicateUidException e)
		{
			LOG.error("Duplicate number error");
		}
		if (sessionService.getAttribute(OTP_KEY_STRING) != null)
		{
			model.addAttribute("otpgen", sessionService.getAttribute(OTP_KEY_STRING));
		}
		else
		{
			model.addAttribute("otpgen", sessionService.getAttribute(OTP_KEY_STRING));
		}
	}

	@SuppressWarnings("boxing")
	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "userenteredotp")
	@ResponseBody
	public void validateUserOTP(@RequestParam(value = "lrOptStatus") final String lrOptStatus,
			@RequestParam(value = "userenteredotp") final String userenteredotp,final Model model) throws DuplicateUidException
	{
		final String regOTPFlagVal = "false";
		sessionService.setAttribute("regOTPFlagVal", regOTPFlagVal);
		if (sessionService.getAttribute(OTP_KEY_STRING).equals(userenteredotp))
		{
			sessionService.removeAttribute("regOTPFlagVal");
			final AddressModel newAddress = new AddressModel();
			sparCustomerFacade.commitOTPData(mobilenum, new Boolean("true"), Boolean.TRUE, Boolean.FALSE);
			newAddress.setPhone1(mobilenum);
			newAddress.setIsOTPValidate(true);
			sessionService.setAttribute("regOTPFlagVal", "true");
			sessionService.setAttribute("mobilenumber", mobilenum);
			if(lrOptStatus.equals("true"))
			{
				sparLandmarkRewardFacade.changeLROptStatus();
			}
		}
		else
		{
			sessionService.setAttribute("regOTPFlagVal", regOTPFlagVal);
		}
	}

	@RequestMapping(value = "/landmarkMyaccountOTPGenerator", method = RequestMethod.GET)
	@ResponseBody
	public String landmarkMyaccountOTPGenerator(final Model model) throws IOException, DuplicateUidException
	{
		final CustomerModel customer = getCurrentCustomer();
		String response = null;
		if(null != customer.getCustPrimaryMobNumber())
		{
			String sysGenOTPKey = RandomStringUtils.randomNumeric(6);
			try
			{
				sparCustomerFacade.getmGageOTPResponse(sysGenOTPKey, customer.getCustPrimaryMobNumber());
				}
				catch (IOException e)
				{
					LOG.error("Incorrect Input error :" + e.getMessage());
				}
				catch (DuplicateUidException e)
				{
					LOG.error("Duplicate number error : "+ e.getMessage());
				}
				//sessionService.setAttribute("myAccountLROtp", sysGenOTPKey);
				response = customer.getCustPrimaryMobNumber()+","+sysGenOTPKey;
		}
		return response;

	}
	
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/landmarkRewardRegistration", method = RequestMethod.GET)
	@ResponseBody
	public void landmarkRewardRegistration(@RequestParam(value = "lrOptStatus") final String lrOptStatus,
			@RequestParam(value = "disableLRPrompt") final boolean disableLRPrompt,
			@RequestParam(value = "userenteredotp") final String userenteredotp,final Model model) throws DuplicateUidException
	{
		if (StringUtils.isNotEmpty(userenteredotp))
		{
			if (sessionService.getAttribute(OTP_KEY_STRING).equals(userenteredotp))
			{
				sessionService.removeAttribute("regOTPFlagVal");
				sessionService.setAttribute("regOTPFlagVal", "true");
				if(lrOptStatus.equals("true"))
				{
					sparLandmarkRewardFacade.changeLROptStatus();
					final CustomerData customerData = sparCustomerFacade.getCustomerWalletDetails();
					if(null != customerData.getDefaultBillingAddress() && null != customerData.getDateOfBirth())
					{
						sparLandmarkRewardFacade.enrollMember();
					}
				}
				sessionService.setAttribute("otpLR", null);
				sessionService.removeAttribute("otpLR");
				sessionService.setAttribute("mobNumberForLRPopup", null);
				sessionService.removeAttribute("mobNumberForLRPopup");
			}
		}
		else if(disableLRPrompt)
		{
			sparLandmarkRewardFacade.disableLRPrompt();
		}
	}

	private void enrollMemberToLandmark(final boolean lrOptStatus, final Model model)
	{
		if (lrOptStatus)
		{
			sparLandmarkRewardFacade.enrollMember();
			/*SparLRUserEnrollDataResult sparLRUserEnrollDataResult = sparLandmarkRewardFacade.enrollMember();
			if(null != sparLRUserEnrollDataResult && sparLRUserEnrollDataResult.isResult())
			{
				sparLandmarkRewardFacade.updateUserEnrolledToLRStatus();
			}
			else
			{
				model.addAttribute("enrollErrorMessage", Config.getString("spar.landmark.enrollmember.error.message",
						"LR System is down. Due to this enrollment couldn't happen. Please try later."));
			}*/
		}
	}

	/*
	 * public String doRegisterDetails(@RequestParam("sparServiceAreaId") final Integer sparServiceAreaId,
	 *
	 * @RequestParam("defaultStore") final String defaultStore,
	 *
	 * @RequestParam("defaultCncCenter") final String defaultCncCenter, final SparAddressForm sparAddressForm, final
	 * BindingResult bindingResult, final Model model, final HttpServletRequest request, final HttpServletResponse
	 * response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/optionalDetails", method = RequestMethod.POST)
	public String doRegisterDetails(final SparAddressForm sparAddressForm, final BindingResult bindingResult, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		model.addAttribute("sparAddressForm", sparAddressForm);
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		return processRegisterOptionalRequest(sparAddressForm, bindingResult, model);
		/*
		 * return processRegisterOptionalRequest(sparServiceAreaId, defaultStore, defaultCncCenter, sparAddressForm,
		 * bindingResult, model);
		 */
	}

	/**
	 * @return the otpKey
	 */

	public String getSysGenOTPKey()
	{
		return sysGenOTPKey;

	}

	/**
	 * @param sysGenOTPKey
	 *           the otpKey to set
	 */

	public void setSysGenOTPKey(final String sysGenOTPKey)
	{
		this.sysGenOTPKey = sysGenOTPKey;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.storefront.controllers.pages.SparAbstractRegisterPageController#getView()
	 */
	@Override
	protected String getView()
	{
		return ControllerConstants.Views.Pages.Account.AccountRegisterOptionalPage;
	}

	@Override
	protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("registeroptional");
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (httpSessionRequestCache.getRequest(request, response) != null)
		{
			return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
		}
		return "/";
	}

	@Resource(name = "httpSessionRequestCache")
	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}

	protected I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	/**
	 * @param i18nFacade
	 *           the i18NFacade to set
	 */
	public void setI18NFacade(final I18NFacade i18nFacade)
	{
		i18NFacade = i18nFacade;
	}

	/**
	 * @return the sparAddressValidator
	 */
	public SparOptionalRegistrationValidator getSparOptionalRegistrationValidator()
	{
		return sparOptionalRegistrationValidator;
	}

	public CustomerModel getCurrentCustomer()
	{
		return (CustomerModel) getCurrentUser();
	}

	private UserModel getCurrentUser()
	{
		return userService.getCurrentUser();
	}

}
