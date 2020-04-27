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

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateEmailForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdatePasswordForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.EmailValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparLRUserDetailDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.forms.SparPasswordValidator;
import com.spar.hcl.storefront.forms.SparUpdateProfileForm;
import com.spar.hcl.storefront.validation.forms.SparAddressValidator;
import com.spar.hcl.storefront.validation.forms.SparProfileValidator;


/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-account")
public class AccountPageController extends AbstractSearchPageController
{
	// Internal Redirects
	private static final String REDIRECT_TO_ADDRESS_BOOK_PAGE = REDIRECT_PREFIX + "/my-account/address-book";
	private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/payment-details";
	private static final String REDIRECT_TO_EDIT_ADDRESS_PAGE = REDIRECT_PREFIX + "/my-account/edit-address/";
	private static final String REDIRECT_TO_UPDATE_EMAIL_PAGE = REDIRECT_PREFIX + "/my-account/update-email";
	private static final String REDIRECT_TO_UPDATE_PROFILE = REDIRECT_PREFIX + "/my-account/update-profile";
	private static final String REDIRECT_TO_PASSWORD_UPDATE_PAGE = REDIRECT_PREFIX + "/my-account/update-password";
	private static final String REDIRECT_TO_ORDER_HISTORY_PAGE = REDIRECT_PREFIX + "/my-account/orders";
	private static final String REDIRECT_TO_ACCOUNT_PROFILE_PAGE = REDIRECT_PREFIX + "/my-account/profile";
	public static final String OTP_KEY_STRING = "OTPKey";
	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";

	// CMS Pages
	private static final String ACCOUNT_CMS_PAGE = "/account";
	private static final String PROFILE_CMS_PAGE = "profile";
	private static final String UPDATE_PASSWORD_CMS_PAGE = "updatePassword";
	private static final String UPDATE_PROFILE_CMS_PAGE = "update-profile";
	private static final String UPDATE_EMAIL_CMS_PAGE = "update-email";
	private static final String ADDRESS_BOOK_CMS_PAGE = "address-book";
	private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
	private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";
	private static final String ORDER_HISTORY_CMS_PAGE = "orders";
	private static final String ORDER_DETAIL_CMS_PAGE = "order";
	private static final String WALLET_CMS_PAGE = "wallet";
	private static final String LANDMARKREWARD_CMS_PAGE = "landmarkReward";
	private static final Logger LOG = Logger.getLogger(AccountPageController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "acceleratorCheckoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "sparPasswordValidator")
	private SparPasswordValidator SparPasswordValidator;

	@Resource(name = "sparAddressValidator")
	private SparAddressValidator sparAddressValidator;

	@Autowired
	private UserService userService;


	/**
	 * @return the sparAddressValidator
	 */
	public SparAddressValidator getSparAddressValidator()
	{
		return sparAddressValidator;
	}

	@Resource(name = "profileValidator")
	private ProfileValidator profileValidator;

	@Resource(name = "emailValidator")
	private EmailValidator emailValidator;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;

	@Resource(name = "addressVerificationResultHandler")
	private AddressVerificationResultHandler addressVerificationResultHandler;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "sparProfileValidator")
	private SparProfileValidator sparProfileValidator;

	/*
	 * public SparProfileValidator getSparProfileValidator() { return sparProfileValidator; }
	 */

	protected SparPasswordValidator getSparPasswordValidator()
	{
		return SparPasswordValidator;
	}



	protected ProfileValidator getProfileValidator()
	{
		return profileValidator;
	}

	protected EmailValidator getEmailValidator()
	{
		return emailValidator;
	}

	protected I18NFacade getI18NFacade()
	{
		return i18NFacade;
	}

	protected AddressVerificationFacade getAddressVerificationFacade()
	{
		return addressVerificationFacade;
	}

	protected AddressVerificationResultHandler getAddressVerificationResultHandler()
	{
		return addressVerificationResultHandler;
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return checkoutFacade.getDeliveryCountries();
	}

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return userFacade.getTitles();
	}

	@ModelAttribute("countryDataMap")
	public Map<String, CountryData> getCountryDataMap()
	{
		final Map<String, CountryData> countryDataMap = new HashMap<>();
		for (final CountryData countryData : getCountries())
		{
			countryDataMap.put(countryData.getIsocode(), countryData);
		}
		return countryDataMap;
	}


	@RequestMapping(value = "/addressform", method = RequestMethod.GET)
	public String getCountryAddressForm(@RequestParam("addressCode") final String addressCode,
			@RequestParam("countryIsoCode") final String countryIsoCode, final Model model)
	{
		model.addAttribute("supportedCountries", getCountries());
		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
		model.addAttribute("country", countryIsoCode);

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}

		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());

		final AddressForm addressForm = new AddressForm();
		model.addAttribute("addressForm", addressForm);
		for (final AddressData addressData : userFacade.getAddressBook())
		{
			if (addressData.getId() != null && addressData.getId().equals(addressCode)
					&& countryIsoCode.equals(addressData.getCountry().getIsocode()))
			{
				model.addAttribute("addressData", addressData);
				addressForm.setAddressId(addressData.getId());
				addressForm.setTitleCode(addressData.getTitleCode());
				addressForm.setFirstName(addressData.getFirstName());
				addressForm.setLastName(addressData.getLastName());
				addressForm.setLine1(addressData.getLine1());
				addressForm.setLine2(addressData.getLine2());
				addressForm.setTownCity(addressData.getTown());
				addressForm.setPostcode(addressData.getPostalCode());
				addressForm.setCountryIso(addressData.getCountry().getIsocode());
				addressForm.setPhone(addressData.getPhone());

				if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
				{
					addressForm.setRegionIso(addressData.getRegion().getIsocode());
				}

				break;
			}
		}
		return ControllerConstants.Views.Fragments.Account.CountryAddressForm;
	}

	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String account(final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		/*
		 * if (ResponsiveUtils.isResponsive()) { GlobalMessages.addFlashMessage(redirectModel,
		 * GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null); return REDIRECT_PREFIX + "/"; }
		 */
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	@RequireHardLogIn
	public String orders(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		// Handle paged search results
		final PageableData pageableData = createPageableData(page, 20, sortCode, showMode);
		final SearchPageData<OrderHistoryData> searchPageData = orderFacade.getPagedOrderHistoryForStatuses(pageableData);
		populateModel(model, searchPageData, showMode);

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/order/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String order(@PathVariable("orderCode") final String orderCode, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try
		{
			final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
			model.addAttribute("orderData", orderDetails);

			final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
			breadcrumbs.add(new Breadcrumb("/my-account/orders", getMessageSource().getMessage("text.account.orderHistory", null,
					getI18nService().getCurrentLocale()), null));
			breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[]
			{ orderDetails.getCode() }, "Order {0}", getI18nService().getCurrentLocale()), null));
			model.addAttribute("breadcrumbs", breadcrumbs);

			model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
			model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
			final HomePageForm homePageForm = new HomePageForm();
			if (null != sessionService.getAttribute("selectedStore"))
			{
				model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
				homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

			}

			if (null != sessionService.getAttribute("cityName"))
			{
				model.addAttribute("cityName", sessionService.getAttribute("cityName"));
				homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
			}
			if (null != sessionService.getAttribute("selectedDeliveryType"))
			{
				model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
				homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
				model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
			}
			model.addAttribute("homePageForm", homePageForm);
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a order that does not exist or is not visible", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_TO_ORDER_HISTORY_PAGE;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
		return getViewForPage(model);
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	@RequireHardLogIn
	public String profile(final Model model) throws CMSItemNotFoundException
	{
		final List<TitleData> titles = userFacade.getTitles();
		/*
		 * if (userFacade.isAnonymousUser()) { return REDIRECT_PREFIX + "/login"; }
		 */
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		if (customerData.getTitleCode() != null)
		{
			model.addAttribute("title", findTitleForCode(titles, customerData.getTitleCode()));
		}

		model.addAttribute("customerData", customerData);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PROFILE_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	protected TitleData findTitleForCode(final List<TitleData> titles, final String code)
	{
		if (code != null && !code.isEmpty() && titles != null && !titles.isEmpty())
		{
			for (final TitleData title : titles)
			{
				if (code.equals(title.getCode()))
				{
					return title;
				}
			}
		}
		return null;
	}

	@RequestMapping(value = "/update-email", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editEmail(final Model model) throws CMSItemNotFoundException
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UpdateEmailForm updateEmailForm = new UpdateEmailForm();

		updateEmailForm.setEmail(customerData.getDisplayUid());

		model.addAttribute("updateEmailForm", updateEmailForm);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_EMAIL_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/update-email", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updateEmail(final UpdateEmailForm updateEmailForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getEmailValidator().validate(updateEmailForm, bindingResult);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		String returnAction = REDIRECT_TO_UPDATE_EMAIL_PAGE;

		if (!bindingResult.hasErrors() && !updateEmailForm.getEmail().equals(updateEmailForm.getChkEmail()))
		{
			bindingResult.rejectValue("chkEmail", "validation.checkEmail.equals", new Object[] {}, "validation.checkEmail.equals");
		}

		if (bindingResult.hasErrors())
		{
			returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
		}
		else
		{
			try
			{
				customerFacade.changeUid(updateEmailForm.getEmail(), updateEmailForm.getPassword());
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
						"text.account.profile.confirmationUpdated", null);

				// Replace the spring security authentication with the new UID
				final String newUid = customerFacade.getCurrentCustomer().getUid().toLowerCase();
				final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
				final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
						oldAuthentication.getAuthorities());
				newAuthentication.setDetails(oldAuthentication.getDetails());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}
			catch (final DuplicateUidException e)
			{
				bindingResult.rejectValue("email", "profile.email.unique");
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
			}
			catch (final PasswordMismatchException passwordMismatchException)
			{
				bindingResult.rejectValue("password", "profile.currentPassword.invalid");
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_EMAIL_CMS_PAGE);
			}
		}

		return returnAction;
	}

	protected String setErrorMessagesAndCMSPage(final Model model, final String cmsPageLabelOrId) throws CMSItemNotFoundException
	{
		GlobalMessages.addErrorMessage(model, "form.global.error");
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		storeCmsPageInModel(model, getContentPageForLabelOrId(cmsPageLabelOrId));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(cmsPageLabelOrId));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
		return getViewForPage(model);
	}


	@RequestMapping(value = "/update-profile", method = RequestMethod.GET)
	public String editProfile(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("titleData", userFacade.getTitles());

		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final SparUpdateProfileForm sparUpdateProfileForm = new SparUpdateProfileForm();
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());

		sparUpdateProfileForm.setTitleCode(customerData.getTitleCode());
		sparUpdateProfileForm.setFirstName(customerData.getFirstName());
		sparUpdateProfileForm.setLastName(customerData.getLastName());
		sparUpdateProfileForm.setWhetherEmployee(customerData.getWhetherEmployee());
		sparUpdateProfileForm.setEmployeeCode(customerData.getEmployeeCode());
		sparUpdateProfileForm.setDateOfJoining(customerData.getDateOfJoining());
		sparUpdateProfileForm.setDateOfBirth(customerData.getDateOfBirth());

		model.addAttribute("sparUpdateProfileForm", sparUpdateProfileForm);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/update-profile", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updateProfile(final SparUpdateProfileForm sparUpdateProfileForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		/* getProfileValidator().validate(updateProfileForm, bindingResult); */
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		sparProfileValidator.validate(sparUpdateProfileForm, bindingResult);
		String returnAction = REDIRECT_TO_UPDATE_PROFILE;
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final CustomerData customerData = new CustomerData();
		customerData.setTitleCode(sparUpdateProfileForm.getTitleCode());
		customerData.setFirstName(sparUpdateProfileForm.getFirstName());
		customerData.setLastName(sparUpdateProfileForm.getLastName());
		customerData.setUid(currentCustomerData.getUid());
		customerData.setDisplayUid(currentCustomerData.getDisplayUid());
		if (sparUpdateProfileForm.getDateOfBirth() != null)
		{
			customerData.setDateOfBirth(sparUpdateProfileForm.getDateOfBirth());
		}
		else
		{
			customerData.setDateOfBirth(currentCustomerData.getDateOfBirth());
		}

		if (sparUpdateProfileForm.getWhetherEmployee().booleanValue())
		{
			customerData.setWhetherEmployee(sparUpdateProfileForm.getWhetherEmployee());
			customerData.setEmployeeCode(sparUpdateProfileForm.getEmployeeCode());
			customerData.setDateOfJoining(sparUpdateProfileForm.getDateOfJoining());
		}
		else
		{
			customerData.setWhetherEmployee(sparUpdateProfileForm.getWhetherEmployee());
		}
		model.addAttribute("titleData", userFacade.getTitles());
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PROFILE_CMS_PAGE));

		if (bindingResult.hasErrors())
		{
			returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
		}
		else
		{
			try
			{
				/* customerFacade.updateProfile(customerData); */
				sparCustomerFacade.updateCustomerProfile(customerData);
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
						"text.account.profile.confirmationUpdated", null);

			}
			catch (final DuplicateUidException e)
			{
				bindingResult.rejectValue("email", "registration.error.account.exists.title");
				returnAction = setErrorMessagesAndCMSPage(model, UPDATE_PROFILE_CMS_PAGE);
			}
		}


		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile"));
		return returnAction;
	}

	@RequestMapping(value = "/update-password", method = RequestMethod.GET)
	@RequireHardLogIn
	public String updatePassword(final Model model) throws CMSItemNotFoundException
	{
		final UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();

		model.addAttribute("updatePasswordForm", updatePasswordForm);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/update-password", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updatePassword(final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, IOException, DuplicateUidException
	{
		getSparPasswordValidator().validate(updatePasswordForm, bindingResult);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		if (!bindingResult.hasErrors())
		{
			if (updatePasswordForm.getNewPassword().equals(updatePasswordForm.getCheckNewPassword()))
			{
				try
				{
					customerFacade.changePassword(updatePasswordForm.getCurrentPassword(), updatePasswordForm.getNewPassword());
					//Initiate SMS to User
					if (null != currentCustomerData.getCustPrimaryMobNumber())
					{
						sparCustomerFacade.sendSMSToUser(currentCustomerData.getCustPrimaryMobNumber(), Config.getString("mGage.user.changePassword", "mGage.user.changePassword"));
					}
				}
				catch (final PasswordMismatchException localException)
				{
					bindingResult.rejectValue("currentPassword", "profile.currentPassword.invalid", new Object[] {},
							"profile.currentPassword.invalid");
				}
			}
			else
			{
				bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {},
						"validation.checkPwd.equals");
			}
		}

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");

			storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PASSWORD_CMS_PAGE));

			model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.profile.updatePasswordForm"));
			return getViewForPage(model);
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.confirmation.password.updated", null);
			return REDIRECT_TO_ACCOUNT_PROFILE_PAGE;
		}
	}

	@RequestMapping(value = "/address-book", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getAddressBook(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("addressData", userFacade.getAddressBook());
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADDRESS_BOOK_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.addressBook"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addAddress(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", userFacade.getTitles());
		final AddressForm addressForm = getPreparedAddressForm();
		model.addAttribute("addressForm", addressForm);
		model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute("isDefaultAddress", Boolean.FALSE);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage("text.account.addressBook", null,
				getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.addressBook.addEditAddress", null,
				getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	protected AddressForm getPreparedAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final AddressForm addressForm = new AddressForm();
		addressForm.setFirstName(currentCustomerData.getFirstName());
		addressForm.setLastName(currentCustomerData.getLastName());
		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return addressForm;
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String addAddress(final AddressForm addressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		getSparAddressValidator().validate(addressForm, bindingResult);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpAddressFormAfterError(addressForm, model);
			return getViewForPage(model);
		}

		final AddressData newAddress = new AddressData();
		newAddress.setTitleCode(addressForm.getTitleCode());
		newAddress.setFirstName(addressForm.getFirstName());
		newAddress.setLastName(addressForm.getLastName());
		newAddress.setLine1(addressForm.getLine1());
		newAddress.setLine2(addressForm.getLine2());
		newAddress.setTown(addressForm.getTownCity());
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		newAddress.setVisibleInAddressBook(true);
		newAddress.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
		newAddress.setPhone(addressForm.getPhone());

		if (addressForm.getRegionIso() != null && !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			newAddress.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
		}

		if (userFacade.isAddressBookEmpty())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null && addressForm.getDefaultAddress().booleanValue());
		}

		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.added");

		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
		model.addAttribute("country", addressForm.getCountryIso());
		model.addAttribute("edit", Boolean.TRUE);
		model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));

		if (addressRequiresReview)
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			return getViewForPage(model);
		}

		userFacade.addAddress(newAddress);


		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added",
				null);

		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	protected void setUpAddressFormAfterError(final AddressForm addressForm, final Model model)
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", userFacade.getTitles());
		model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));
		if (addressForm.getCountryIso() != null)
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
			model.addAttribute("country", addressForm.getCountryIso());
		}
	}

	@RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String editAddress(@PathVariable("addressCode") final String addressCode, final Model model)
			throws CMSItemNotFoundException
	{
		final SparAddressForm sparAddressForm = new SparAddressForm();
		model.addAttribute("countryData", checkoutFacade.getDeliveryCountries());
		model.addAttribute("titleData", userFacade.getTitles());
		model.addAttribute("sparAddressForm", sparAddressForm);
		model.addAttribute("addressBookEmpty", Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		for (final AddressData addressData : userFacade.getAddressBook())
		{
			if (addressData.getId() != null && addressData.getId().equals(addressCode))
			{
				model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressData.getCountry().getIsocode()));
				model.addAttribute("country", addressData.getCountry().getIsocode());
				model.addAttribute("addressData", addressData);
				sparAddressForm.setAddressId(addressData.getId());
				sparAddressForm.setTitleCode(addressData.getTitleCode());
				sparAddressForm.setFirstName(addressData.getFirstName());
				sparAddressForm.setLastName(addressData.getLastName());
				sparAddressForm.setLine1(addressData.getLine1());
				sparAddressForm.setLine2(addressData.getLine2());
				sparAddressForm.setTownCity(addressData.getTown());
				sparAddressForm.setPostcode(addressData.getPostalCode());
				sparAddressForm.setCountryIso(addressData.getCountry().getIsocode());
				sparAddressForm.setPhone(addressData.getPhone());
				sparAddressForm.setSaveInAddressBook(Boolean.valueOf(addressData.isVisibleInAddressBook()));
				sparAddressForm.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
				sparAddressForm.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
				sparAddressForm.setEmail(addressData.getEmail());
				sparAddressForm.setArea(addressData.getArea());
				sparAddressForm.setBuildingName(addressData.getBuildingName());
				sparAddressForm.setLandmark(addressData.getLandmark());
				sparAddressForm.setLongAddress(addressData.getLongAddress());
				/* sparAddressForm.setDateOfBirth(addressData.getDateOfBirth()); */

				if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
				{
					sparAddressForm.setRegionIso(addressData.getRegion().getIsocode());
				}

				if (isDefaultAddress(addressData.getId()))
				{
					sparAddressForm.setDefaultAddress(Boolean.TRUE);
					model.addAttribute("isDefaultAddress", Boolean.TRUE);
				}
				else
				{
					sparAddressForm.setDefaultAddress(Boolean.FALSE);
					model.addAttribute("isDefaultAddress", Boolean.FALSE);
				}


				model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
				model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());

				break;
			}
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));




		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-account/address-book", getMessageSource().getMessage("text.account.addressBook", null,
				getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.addressBook.addEditAddress", null,
				getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("edit", Boolean.TRUE);
		return getViewForPage(model);
	}

	/**
	 * Method checks if address is set as default
	 *
	 * @param addressId
	 *           - identifier for address to check
	 * @return true if address is default, false if address is not default
	 */
	protected boolean isDefaultAddress(final String addressId)
	{
		final AddressData defaultAddress = userFacade.getDefaultAddress();
		return (defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId));
	}

	@RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
	@RequireHardLogIn
	public String editAddress(final SparAddressForm sparAddressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		getSparAddressValidator().validate(sparAddressForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "form.global.error");
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpAddressFormAfterError(sparAddressForm, model);
			return getViewForPage(model);
		}

		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		final AddressData newAddress = new AddressData();
		newAddress.setId(sparAddressForm.getAddressId());
		newAddress.setTitleCode(sparAddressForm.getTitleCode());
		newAddress.setFirstName(sparAddressForm.getFirstName());
		newAddress.setLastName(sparAddressForm.getLastName());
		newAddress.setLine1(sparAddressForm.getLine1());
		newAddress.setLine2(sparAddressForm.getLine2());
		newAddress.setTown(sparAddressForm.getTownCity());
		newAddress.setPostalCode(sparAddressForm.getPostcode());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		newAddress.setPhone(sparAddressForm.getPhone());

		newAddress.setVisibleInAddressBook(true);

		newAddress.setEmail(sparAddressForm.getEmail());
		newAddress.setDateOfBirth(sparAddressForm.getDateOfBirth());
		newAddress.setArea(sparAddressForm.getArea());
		newAddress.setBuildingName(sparAddressForm.getBuildingName());
		newAddress.setLandmark(sparAddressForm.getLandmark());
		newAddress.setLongAddress(sparAddressForm.getLongAddress());


		newAddress.setCountry(getI18NFacade().getCountryForIsocode(sparAddressForm.getCountryIso()));
		if (sparAddressForm.getDateOfBirth() != null)
		{
			final CustomerModel customer = getCurrentCustomerModel();
			customer.setDateOfBirth(sparAddressForm.getDateOfBirth());
		}
		if (sparAddressForm.getRegionIso() != null && !StringUtils.isEmpty(sparAddressForm.getRegionIso()))
		{
			newAddress.setRegion(getI18NFacade().getRegion(sparAddressForm.getCountryIso(), sparAddressForm.getRegionIso()));
		}

		if (Boolean.TRUE.equals(sparAddressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1)
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sparAddressForm.getCountryIso()));
		model.addAttribute("country", sparAddressForm.getCountryIso());
		model.addAttribute("edit", Boolean.TRUE);
		model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(sparAddressForm.getAddressId())));

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		if (addressRequiresReview)
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
			return getViewForPage(model);
		}

		userFacade.editAddress(newAddress);

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.updated",
				null);
		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	@RequestMapping(value = "/select-suggested-address", method = RequestMethod.POST)
	public String doSelectSuggestedAddress(final AddressForm addressForm, final RedirectAttributes redirectModel)
	{
		final Set<String> resolveCountryRegions = org.springframework.util.StringUtils.commaDelimitedListToSet(Config
				.getParameter("resolve.country.regions"));

		final AddressData selectedAddress = new AddressData();
		selectedAddress.setId(addressForm.getAddressId());
		selectedAddress.setTitleCode(addressForm.getTitleCode());
		selectedAddress.setFirstName(addressForm.getFirstName());
		selectedAddress.setLastName(addressForm.getLastName());
		selectedAddress.setLine1(addressForm.getLine1());
		selectedAddress.setLine2(addressForm.getLine2());
		selectedAddress.setTown(addressForm.getTownCity());
		selectedAddress.setPostalCode(addressForm.getPostcode());
		selectedAddress.setBillingAddress(false);
		selectedAddress.setShippingAddress(true);
		selectedAddress.setVisibleInAddressBook(true);
		selectedAddress.setPhone(addressForm.getPhone());

		final CountryData countryData = i18NFacade.getCountryForIsocode(addressForm.getCountryIso());
		selectedAddress.setCountry(countryData);

		if (resolveCountryRegions.contains(countryData.getIsocode()) && addressForm.getRegionIso() != null
				&& !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
			selectedAddress.setRegion(regionData);
		}

		if (resolveCountryRegions.contains(countryData.getIsocode()) && addressForm.getRegionIso() != null
				&& !StringUtils.isEmpty(addressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso());
			selectedAddress.setRegion(regionData);
		}

		if (Boolean.TRUE.equals(addressForm.getEditAddress()))
		{
			selectedAddress.setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress())
					|| userFacade.getAddressBook().size() <= 1);
			userFacade.editAddress(selectedAddress);
		}
		else
		{
			selectedAddress.setDefaultAddress(Boolean.TRUE.equals(addressForm.getDefaultAddress())
					|| userFacade.isAddressBookEmpty());
			userFacade.addAddress(selectedAddress);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added");

		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@RequestMapping(value = "/remove-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel)
	{
		final AddressData addressData = new AddressData();
		addressData.setId(addressCode);
		userFacade.removeAddress(addressData);

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.removed");
		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@RequestMapping(value = "/set-default-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String setDefaultAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel)
	{
		final AddressData addressData = new AddressData();
		addressData.setDefaultAddress(true);
		addressData.setVisibleInAddressBook(true);
		addressData.setId(addressCode);
		userFacade.setDefaultAddress(addressData);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
				"account.confirmation.default.address.changed");
		return REDIRECT_TO_ADDRESS_BOOK_PAGE;
	}

	@RequestMapping(value = "/payment-details", method = RequestMethod.GET)
	@RequireHardLogIn
	public String paymentDetails(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("customerData", customerFacade.getCurrentCustomer());
		model.addAttribute("paymentInfoData", userFacade.getCCPaymentInfos(true));
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/set-default-payment-details", method = RequestMethod.POST)
	@RequireHardLogIn
	public String setDefaultPaymentDetails(@RequestParam final String paymentInfoId)
	{
		CCPaymentInfoData paymentInfoData = null;
		if (StringUtils.isNotBlank(paymentInfoId))
		{
			paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);
		}
		userFacade.setDefaultPaymentInfo(paymentInfoData);
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@RequestMapping(value = "/remove-payment-method", method = RequestMethod.POST)
	@RequireHardLogIn
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		userFacade.unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@RequestMapping(value = "/wallet", method = RequestMethod.GET)
	@RequireHardLogIn
	public String wallet(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<OrderHistoryData> searchPageData = orderFacade.getPagedOrderHistoryForStatuses(pageableData);
		populateModel(model, searchPageData, showMode);

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		final CustomerData customerData = sparCustomerFacade.getCustomerWalletDetails();
		model.addAttribute("customerData", customerData);
		storeCmsPageInModel(model, getContentPageForLabelOrId(WALLET_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(WALLET_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.wallet"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);

	}

	@RequestMapping(value = "/landmarkReward", method = RequestMethod.GET)
	@RequireHardLogIn
	public String landmarkRewardDetail(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		//final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		//final SearchPageData<OrderHistoryData> searchPageData = orderFacade.getPagedOrderHistoryForStatuses(pageableData);
		//populateModel(model, searchPageData, showMode);

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));
		}

		if (null != sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		final CustomerData customerData = sparCustomerFacade.getCustomerWalletDetails();
		model.addAttribute("customerData", customerData);

		if (customerData.getLrOptStatus().booleanValue() && customerData.getIsEnrolledToLR().booleanValue())
		{
			final SparLRUserDetailDataResult sparLRUserDetailDataResult = sparLandmarkRewardFacade.getMemberForLMS();
			model.addAttribute("sparLRUserDetailDataResult", sparLRUserDetailDataResult);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(LANDMARKREWARD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(LANDMARKREWARD_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.landmarkreward"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);

	}

	@SuppressWarnings("boxing")
	@RequestMapping(value = "/registeroptional/landmarkRewardRegistration", method = RequestMethod.GET)
	@ResponseBody
	public void landmarkRewardRegistration(@RequestParam(value = "lrOptStatus") final String lrOptStatus,
			@RequestParam(value = "disableLRPrompt") final boolean disableLRPrompt,
			@RequestParam(value = "userenteredotp") final String userenteredotp, final Model model) throws DuplicateUidException
	{
		if (StringUtils.isNotEmpty(userenteredotp))
		{
			if (sessionService.getAttribute(OTP_KEY_STRING).equals(userenteredotp))
			{
				sessionService.removeAttribute("regOTPFlagVal");
				sessionService.setAttribute("regOTPFlagVal", "true");
				if (lrOptStatus.equals("true"))
				{
					sparLandmarkRewardFacade.changeLROptStatus();
					final CustomerData customerData = sparCustomerFacade.getCustomerWalletDetails();
					if (null != customerData.getDefaultBillingAddress() && null != customerData.getDateOfBirth())
					{
						sparLandmarkRewardFacade.enrollMember();
					}
				}
				sessionService.setAttribute("myAccountLROtp", null);
				sessionService.removeAttribute("myAccountLROtp");
			}
		}
	}

	public CustomerModel getCurrentCustomerModel()
	{
		return (CustomerModel) getCurrentUser();
	}

	private UserModel getCurrentUser()
	{
		return userService.getCurrentUser();
	}


}
