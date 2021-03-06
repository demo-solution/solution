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

package com.spar.hcl.storefront.controllers.pages.checkout.steps;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.constants.SparCoreConstants;
import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparLRUserDetailDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.order.data.PaymentModeData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;
import com.spar.hcl.storefront.form.SparSopPaymentDetailsForm;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;


@Controller
@RequestMapping(value = "/checkout/multi/payment-method")
public class PaymentMethodCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(PaymentMethodCheckoutStepController.class);
	
	protected static final Map<String, String> CYBERSOURCE_SOP_CARD_TYPES = new HashMap<String, String>();

	@Resource(name = "deviceDetectionFacade")
	private DeviceDetectionFacade deviceDetectionFacade;

	public final static String PAYMENT_METHOD = "payment-method";
	public final static String PAYMENT_MODE_COD_ENABLED = "sparstorefront.paymentmode.cod.enabled";
	public final static String PAYMENT_MODE_CC_ENABLED = "sparstorefront.paymentmode.creditcard.enabled";
	public final static String PAYMENT_MODE_WALLET_ENABLED = "sparstorefront.paymentmode.wallet.enabled";
	public final static String PAYMENT_MODE_LANDMARKREWARD_ENABLED = "sparstorefront.paymentmode.landmarkreward.enabled";
	protected static final String IPG_REDIRECT_POST_PAGE = "ipg/redirectPost";

	@Autowired
	private ModelService modelService;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;

	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparPaymentFacade;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;
	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFacade().getBillingCountries();
	}

	@ModelAttribute("cardTypes")
	public Collection<CardTypeData> getCardTypes()
	{
		return getCheckoutFacade().getSupportedCardTypes();
	}

	@ModelAttribute("months")
	public List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();

		months.add(new SelectOption("1", "01"));
		months.add(new SelectOption("2", "02"));
		months.add(new SelectOption("3", "03"));
		months.add(new SelectOption("4", "04"));
		months.add(new SelectOption("5", "05"));
		months.add(new SelectOption("6", "06"));
		months.add(new SelectOption("7", "07"));
		months.add(new SelectOption("8", "08"));
		months.add(new SelectOption("9", "09"));
		months.add(new SelectOption("10", "10"));
		months.add(new SelectOption("11", "11"));
		months.add(new SelectOption("12", "12"));

		return months;
	}

	@ModelAttribute("startYears")
	public List<SelectOption> getStartYears()
	{
		final List<SelectOption> startYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i > (calender.get(Calendar.YEAR) - 6); i--)
		{
			startYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return startYears;
	}

	@ModelAttribute("expiryYears")
	public List<SelectOption> getExpiryYears()
	{
		final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i < (calender.get(Calendar.YEAR) + 11); i++)
		{
			expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return expiryYears;
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		if (validateCart(redirectAttributes))
		{
			final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
			//Validate  if cart is empty
			if (null == cartData || CollectionUtils.isEmpty(cartData.getEntries()))
			{
				// Invalid cart. Bounce back to the cart page.
				return REDIRECT_URL_CART;
			}
			else
			{
				return REDIRECT_URL_SUMMARY;
			}
		}

		// Code to check if any cart entry has 0 quantity for an orders - Saif - Start
		final CartData cartDataEntries = sparCartFacade.getSessionCartWithEntryOrdering(true);
		if (null != cartDataEntries.getEntries() && !cartDataEntries.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartDataEntries.getEntries())
			{
				if (entry.getQuantity().intValue() <= 0)
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.validation.quantity");
					return REDIRECT_URL_CART;
				}
			}
		}

		// Code to check if any cart entry has 0 quantity for an orders - Saif - End

		// adding validation for order thresholds.
		final List<String> statusCodes = sparCartFacade.checkCartQualifyForOrderLimits();
		final Double minOrderLimit = baseStoreService.getCurrentBaseStore().getMinOrderLimit();
		final Double minCCOrderLimit = baseStoreService.getCurrentBaseStore().getMinCCOrderLimit();
		final Double minHDOrderLimit = baseStoreService.getCurrentBaseStore().getMinHDOrderLimit();
		final Double[] limits = new Double[3];
		limits[0] = minOrderLimit;
		limits[1] = minCCOrderLimit;
		limits[2] = minHDOrderLimit;
		//code change for voucher story
		if (!statusCodes.isEmpty() && CollectionUtils.isEmpty(sparVoucherFacade.getSparVouchersForCart()))
		{
			if ("Min_Ord".equals(statusCodes.get(0)))
			{
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.value", limits);


			}
			if ("Min_Ord_CNC".equals(statusCodes.get(0)))
			{
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.CNC.value",
						limits);

			}

			if ("Min_Ord_HD".equals(statusCodes.get(0)))
			{
				GlobalMessages
						.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.HD.value", limits);

			}

			return REDIRECT_URL_SUMMARY;
		}

		setModelAttributesFromSession(model);
		getCheckoutFacade().setDeliveryModeIfAvailable();
		setupAddPaymentPage(model);

		// Use the checkout PCI strategy for getting the URL for creating new subscriptions.
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{
				final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription("/checkout/multi/hop/response",
						"/integration/merchant_callback");
				model.addAttribute("hostedOrderPageData", hostedOrderPageData);

				final boolean hopDebugMode = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
						false);
				model.addAttribute("hopDebugMode", Boolean.valueOf(hopDebugMode));

				return ControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			}
		}
		else if (CheckoutPciOptionEnum.SOP.equals(subscriptionPciOption))
		{
			// Build up the SOP form data and render page containing form
			final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model);
				return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
			}
		}
		//Since SPAR has a requirement for IPG that is neither SOP nor HOP, a new PCI.strategy ="CUSTOM" has been introduced to accomplish IPG and COD Flow.
		else if (CheckoutPciOptionEnum.CUSTOM.equals(subscriptionPciOption))
		{
			// Build up the COD form data and render page containing form
			final SparSopPaymentDetailsForm codPaymentDetailsForm = new SparSopPaymentDetailsForm();
			// Build up the IPG form data and render page containing form
			final SparSopPaymentDetailsForm ccPaymentDetailsForm = new SparSopPaymentDetailsForm();
			// Build up the wallet form data and render page containing form
			final SparSopPaymentDetailsForm walletPaymentDetailsForm = new SparSopPaymentDetailsForm();
		// Build up the Landmark Reward form data and render page containing form
			final SparSopPaymentDetailsForm landmarkRewardPaymentDetailsForm = new SparSopPaymentDetailsForm();
			try
			{
				setModelAttributesFromSession(model);

				//configuration to enable COD on SPAR Payment page
				final Boolean isCODEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_COD_ENABLED, true) ? Boolean.TRUE
						: Boolean.FALSE;
				//configuration to enable IPG on SPAR Payment page
				final Boolean isIPGEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_CC_ENABLED, true) ? Boolean.TRUE
						: Boolean.FALSE;

				final Boolean isWalletEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_WALLET_ENABLED, true) ? Boolean.TRUE
						: Boolean.FALSE;
				
				final Boolean isLandmarkEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_LANDMARKREWARD_ENABLED, true) ? Boolean.TRUE
						: Boolean.FALSE;
				
				if (isCODEnabled.booleanValue())
				{
					//setting up COD Payment Mode
					setupCODPaymentMethodPostPage(codPaymentDetailsForm, model);
				}
				if (isIPGEnabled.booleanValue())
				{
					//setting up CrdeitCard Payment Mode
					setupCCPaymentMethodPostPage(ccPaymentDetailsForm, model);
				}
				if (isWalletEnabled.booleanValue())
				{
					//setting up COD Payment Mode
					setupWalletPaymentMethodPostPage(walletPaymentDetailsForm, model);
				}
				final CustomerData customerData = sparCustomerFacade.getCurrentCustomerWallet();
				walletPaymentDetailsForm.setPaidByWallet(Double.valueOf(customerData.getPaidByWallet().getValue().doubleValue()));
				if (isLandmarkEnabled.booleanValue())
				{
					setupLandmarkRewardPaymentMethodPostPage(landmarkRewardPaymentDetailsForm,customerData.getIsEnrolledToLR(),model);
				}
				model.addAttribute("customerData", customerData);
				sessionService.removeAttribute("calculatedWalletData");
				sessionService.removeAttribute("calculateLRAmountResultData");
				if(null != sessionService.getAttribute("redeemAPIErrorMsg"))
				{
					String redeemAPIErrorMsg = sessionService.getAttribute("redeemAPIErrorMsg").toString();
					GlobalMessages.addErrorMessage(model, redeemAPIErrorMsg);
					sessionService.removeAttribute("redeemAPIErrorMsg");
				}
				return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("codPaymentDetailsForm", codPaymentDetailsForm);
				model.addAttribute("ccPaymentDetailsForm", ccPaymentDetailsForm);
				model.addAttribute("walletPaymentDetailsForm", ccPaymentDetailsForm);
				model.addAttribute("landmarkRewardPaymentDetailsForm", ccPaymentDetailsForm);
			}
		}

		// If not using HOP or SOP we need to build up the payment details form
		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
		final AddressForm addressForm = new AddressForm();
		paymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute(paymentDetailsForm);
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
		model.addAttribute("cartData", cartData);

		return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
	}

	private void setupLandmarkRewardPaymentMethodPostPage(final SparSopPaymentDetailsForm landmarkRewardPaymentDetailsForm ,
			final Boolean isEnrolledToLR, final Model model)
	{
		Collection<PaymentModeData> paymentModeSupported = null;
		try
		{
			paymentModeSupported = getPaymentModeTypes(PaymentModeEnum.LANDMARKREWARD.getCode());
			//walletPaymentDetailsForm.setPaymentMode(paymentModeSupported.iterator().next().getName());
		}
		catch (final IllegalArgumentException e)
		{
			//model.addAttribute("payByWalletUrl", "");
			LOG.warn("Failed to set up Landmark Reward payment page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
		//model.addAttribute("walletPaymentDetailsForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("landmarkRewardPaymentDetailsForm", landmarkRewardPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("paymentLandmarkRewardMode", paymentModeSupported);
		if(isEnrolledToLR.booleanValue())
		{
   		final SparLRUserDetailDataResult sparLRUserDetailDataResult = sparLandmarkRewardFacade.getMemberForLMS();
   		model.addAttribute("sparLRUserDetailDataResult", sparLRUserDetailDataResult);
		}
	}


	@RequestMapping(value =
	{ "/add" }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult)
			throws CMSItemNotFoundException
	{
		setModelAttributesFromSession(model);
		getPaymentDetailsValidator().validate(paymentDetailsForm, bindingResult);
		setupAddPaymentPage(model);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setId(paymentDetailsForm.getPaymentId());
		paymentInfoData.setCardType(paymentDetailsForm.getCardTypeCode());
		paymentInfoData.setAccountHolderName(paymentDetailsForm.getNameOnCard());
		paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());
		paymentInfoData.setStartMonth(paymentDetailsForm.getStartMonth());
		paymentInfoData.setStartYear(paymentDetailsForm.getStartYear());
		paymentInfoData.setExpiryMonth(paymentDetailsForm.getExpiryMonth());
		paymentInfoData.setExpiryYear(paymentDetailsForm.getExpiryYear());
		if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) || getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			paymentInfoData.setSaved(true);
		}
		paymentInfoData.setIssueNumber(paymentDetailsForm.getIssueNumber());

		final AddressData addressData;
		if (Boolean.FALSE.equals(paymentDetailsForm.getNewBillingAddress()))
		{
			addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
			if (addressData == null)
			{
				GlobalMessages.addErrorMessage(model,
						"checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
				return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
			}

			addressData.setBillingAddress(true); // mark this as billing address
		}
		else
		{
			final AddressForm addressForm = paymentDetailsForm.getBillingAddress();
			addressData = new AddressData();
			if (addressForm != null)
			{
				addressData.setId(addressForm.getAddressId());
				addressData.setTitleCode(addressForm.getTitleCode());
				addressData.setFirstName(addressForm.getFirstName());
				addressData.setLastName(addressForm.getLastName());
				addressData.setLine1(addressForm.getLine1());
				addressData.setLine2(addressForm.getLine2());
				addressData.setTown(addressForm.getTownCity());
				addressData.setPostalCode(addressForm.getPostcode());
				addressData.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
				if (addressForm.getRegionIso() != null)
				{
					addressData.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
				}

				addressData.setShippingAddress(Boolean.TRUE.equals(addressForm.getShippingAddress()));
				addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
			}
		}

		getAddressVerificationFacade().verifyAddressData(addressData);
		paymentInfoData.setBillingAddress(addressData);

		final CCPaymentInfoData newPaymentSubscription = getCheckoutFacade().createPaymentSubscription(paymentInfoData);
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) && getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.createSubscription.failedMsg");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		model.addAttribute("paymentId", newPaymentSubscription.getId());
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return getCheckoutStep().nextStep();
	}


	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@RequireHardLogIn
	public String remove(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return getCheckoutStep().currentStep();
	}

	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 *
	 * @param selectedPaymentMethodId
	 *           - the id of the payment method to use.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
	{
		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
		}
		return getCheckoutStep().nextStep();
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	protected CardTypeData createCardTypeData(final String code, final String name)
	{
		final CardTypeData cardTypeData = new CardTypeData();
		cardTypeData.setCode(code);
		cardTypeData.setName(name);
		return cardTypeData;
	}

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		setModelAttributesFromSession(model);
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model)
	{
		try
		{
			final PaymentData silentOrderPageData = getPaymentFacade().beginSopCreateSubscription("/checkout/multi/sop/response",
					"/integration/merchant_callback");
			model.addAttribute("silentOrderPageData", silentOrderPageData);
			sopPaymentDetailsForm.setParameters(silentOrderPageData.getParameters());
			model.addAttribute("paymentFormUrl", silentOrderPageData.getPostUrl());
			setModelAttributesFromSession(model);
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("paymentFormUrl", "");
			model.addAttribute("silentOrderPageData", null);
			LOG.warn("Failed to set up silent order post page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		model.addAttribute("silentOrderPostForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("sopCardTypes", getSopCardTypes());
		if (StringUtils.isNotBlank(sopPaymentDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sopPaymentDetailsForm.getBillTo_country()));
			model.addAttribute("country", sopPaymentDetailsForm.getBillTo_country());
		}
	}

	/**
	 * This method is used to setup payment method page for Cash on Delivery.
	 *
	 * @param codPaymentDetailsForm
	 * @param model
	 */
	@SuppressWarnings("boxing")
	protected void setupCODPaymentMethodPostPage(final SparSopPaymentDetailsForm codPaymentDetailsForm, final Model model)
	{
		Collection<PaymentModeData> paymentModeSupported = null;
		try
		{
			paymentModeSupported = getPaymentModeTypes(PaymentModeEnum.CASHONDELIVERY.getCode());
			codPaymentDetailsForm.setPaymentMode(paymentModeSupported.iterator().next().getName());
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("cashOnDeliveryUrl", "");
			LOG.warn("Failed to set up COD payment page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		CartData cartData = getCheckoutFacade().getCheckoutCart();
		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
		model.addAttribute("codOrderPostForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("codPaymentDetailsForm", codPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("paymentModes", paymentModeSupported);
	}


	/**
	 * This method is used to setup payment method page for Cash on Delivery.
	 *
	 * @param codPaymentDetailsForm
	 * @param model
	 */
	@SuppressWarnings("boxing")
	protected void setupWalletPaymentMethodPostPage(final SparSopPaymentDetailsForm walletPaymentDetailsForm, final Model model)
	{
		Collection<PaymentModeData> paymentModeSupported = null;
		try
		{
			paymentModeSupported = getPaymentModeTypes(PaymentModeEnum.WALLET.getCode());
			walletPaymentDetailsForm.setPaymentMode(paymentModeSupported.iterator().next().getName());
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("payByWalletUrl", "");
			LOG.warn("Failed to set up Wallet payment page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		CartData cartData = getCheckoutFacade().getCheckoutCart();

		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
		model.addAttribute("walletPaymentDetailsForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("walletPaymentDetailsForm", walletPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("paymentWalletMode", paymentModeSupported);
	}

	/**
	 * This method is used to set model attributes from session service.
	 *
	 * @param model
	 */
	protected void setModelAttributesFromSession(final Model model)
	{
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

		if (null != (String) sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}

		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		setModelAttributeWithDeviceType(model);
		setPaymentModesSupported(model);
	}

	/**
	 * This method is used to set payment modes that are enabled in SPAR
	 *
	 * @param model
	 */
	protected void setPaymentModesSupported(final Model model)
	{
		//configuration to enable COD on SPAR Payment page
		final Boolean isCODEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_COD_ENABLED, true) ? Boolean.TRUE
				: Boolean.FALSE;
		//configuration to enable IPG on SPAR Payment page
		final Boolean isIPGEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_CC_ENABLED, true) ? Boolean.TRUE
				: Boolean.FALSE;
		final Boolean isWalletEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_WALLET_ENABLED, true) ? Boolean.TRUE
				: Boolean.FALSE;
		final Boolean isLandmarkEnabled = getSiteConfigService().getBoolean(PAYMENT_MODE_LANDMARKREWARD_ENABLED, true) ? Boolean.TRUE
				: Boolean.FALSE;
		model.addAttribute(PaymentModeEnum.CASHONDELIVERY.getCode(), isCODEnabled);
		model.addAttribute(PaymentModeEnum.CREDITCARD.getCode(), isIPGEnabled);
		model.addAttribute(PaymentModeEnum.WALLET.getCode(), isWalletEnabled);
		model.addAttribute(PaymentModeEnum.LANDMARKREWARD.getCode(), isLandmarkEnabled);
	}

	/**
	 * This method is used to set mobileMode in the request parameter.
	 *
	 * @param model
	 */
	protected void setModelAttributeWithDeviceType(final Model model)
	{
		final DeviceData currentDetectedDevice = deviceDetectionFacade.getCurrentDetectedDevice();
		model.addAttribute("isMobileMode", currentDetectedDevice.getMobileBrowser());
	}

	/**
	 * This method is used to setup payment method page for Payment Gateway(ICICI) supported for credit card.
	 *
	 * @param ccPaymentDetailsForm
	 * @param model
	 */
	protected void setupCCPaymentMethodPostPage(final SparSopPaymentDetailsForm ccPaymentDetailsForm, final Model model)
	{
		Collection<PaymentModeData> paymentModeSupported = null;
		try
		{
			paymentModeSupported = getPaymentModeTypes(PaymentModeEnum.CREDITCARD.getCode());
			ccPaymentDetailsForm.setPaymentMode(paymentModeSupported.iterator().next().getName());
			final CartData cartData = getCheckoutFacade().getCheckoutCart();

			model.addAttribute("cartData", cartData);
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("ccOrderPostForm", "");
			LOG.warn("Failed to set up ICICI payment post page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		CartData cartData = getCheckoutFacade().getCheckoutCart();
		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
		model.addAttribute("ccOrderPostForm", new PaymentDetailsForm());
		model.addAttribute("cartData", cartData);
		model.addAttribute("ccPaymentDetailsForm", ccPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("paymentModeTypes", paymentModeSupported);
	}

	/**
	 * This method is used to setup payment method page for Payment Gateway(ICICI) supported for credit card.
	 *
	 * @return PaymentData
	 */
	protected PaymentData initializeCCPaymentMethodPostPage()
	{
		final String responseSuccessUrl = getSiteConfigService().getString(
				SparCoreConstants.IPGPaymentConstants.PaymentProperties.IPG_RESPONSE_SUCCESS_URL, "/checkout/multi/ipg/response");
		final String responseFailureUrl = getSiteConfigService().getString(
				SparCoreConstants.IPGPaymentConstants.PaymentProperties.IPG_RESPONSE_FAILURE_URL, "/checkout/multi/ipg/response");
		final CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService
				.getAttribute("calculatedWalletData");
		//initializing the post URL and request parameters
		final PaymentData ipgOrderPageData = getSparPaymentFacade().beginIPGCreateSubscription(responseSuccessUrl,
				responseFailureUrl, calculatedWalletData);
		return ipgOrderPageData;
	}
	
	protected Collection<CardTypeData> getSopCardTypes()
	{
		final Collection<CardTypeData> sopCardTypes = new ArrayList<CardTypeData>();

		final List<CardTypeData> supportedCardTypes = getCheckoutFacade().getSupportedCardTypes();
		for (final CardTypeData supportedCardType : supportedCardTypes)
		{
			// Add credit cards for all supported cards that have mappings for cybersource SOP
			if (CYBERSOURCE_SOP_CARD_TYPES.containsKey(supportedCardType.getCode()))
			{
				sopCardTypes.add(createCardTypeData(CYBERSOURCE_SOP_CARD_TYPES.get(supportedCardType.getCode()),
						supportedCardType.getName()));
			}
		}
		return sopCardTypes;
	}

	/**
	 * This method is used to get the supported Payment Mode
	 *
	 * @param paymentMode
	 * @return Collection<PaymentModeData>
	 */
	protected Collection<PaymentModeData> getPaymentModeTypes(final String paymentMode)
	{
		final Collection<PaymentModeData> paymentModeData = new ArrayList<PaymentModeData>();

		final List<PaymentModeData> supportedPaymentModeTypes = getSparCheckoutFacade().getSupportedPaymentModeTypes();
		for (final PaymentModeData supportedPaymentModeType : supportedPaymentModeTypes)
		{
			if (paymentMode.equals(supportedPaymentModeType.getCode()))
			{
				// Add Payment Mode Type supported by SPAR
				paymentModeData.add(createPaymentModeData(supportedPaymentModeType.getCode(), supportedPaymentModeType.getName()));
			}
		}
		return paymentModeData;
	}

	/**
	 * This method is used to create the PaymentModeData
	 *
	 * @param code
	 * @param name
	 * @return PaymentModeData
	 */
	protected PaymentModeData createPaymentModeData(final String code, final String name)
	{
		final PaymentModeData paymentModeData = new PaymentModeData();
		paymentModeData.setCode(code);
		paymentModeData.setName(name);
		return paymentModeData;
	}

	/**
	 * This method is used to set the request parameters for IPG and then redirect the URL to an intermediate page for
	 * form submit.
	 *
	 * @param request
	 * @param ccPaymentDetailsForm
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/request", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doHandleIPGRequest(final HttpServletRequest request,
			@Valid final SparSopPaymentDetailsForm ccPaymentDetailsForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap = getRequestParameterMap(request);
		SparValidateLRAmountResultData sessionLRAmountResultData = (SparValidateLRAmountResultData) sessionService.getAttribute("calculateLRAmountResultData");
		if(null != sessionLRAmountResultData)
		{
			SparValidateLRAmountResultData calculateLRAmountResultData = sparLandmarkRewardFacade.validateLandmarkRewardPoints(sessionLRAmountResultData.getEnteredLRAmount());
			if(!calculateLRAmountResultData.isIsLRAmountValid())
			{
   			resultMap.put("ccOrderPostForm", "");
   			sessionService.setAttribute("redeemAPIErrorMsg", calculateLRAmountResultData.getValidationMessage());
   			return getCheckoutStep().currentStep();
			}
		}
		try
		{
			//initializing the request parameters for IPG ICICI.
			final PaymentData ipgOrderPageData = initializeCCPaymentMethodPostPage();
			//Test if postUrl is active or not
			if (!checkPostURLActive(ipgOrderPageData.getPostUrl()))
			{
				return doHandleIPGOffline(ccPaymentDetailsForm, model, ipgOrderPageData);
			}
			//These are the request parameters that will be used in IPG request
			ccPaymentDetailsForm.setParameters(ipgOrderPageData.getParameters());
			resultMap.putAll(ccPaymentDetailsForm.getSignatureParams());
			final boolean showDebugPage = getSiteConfigService().getBoolean(
					SparCoreConstants.IPGPaymentConstants.PaymentProperties.IPG_DEBUG_MODE, false);
			//This is the IPG post URL
			model.addAttribute("showDebugPage", Boolean.valueOf(showDebugPage));
			model.addAttribute("postParams", resultMap);
			model.addAttribute("postUrl", ipgOrderPageData.getPostUrl());
			final String redirectUrl = REDIRECT_URL_ADD_PAYMENT_METHOD;
			model.addAttribute("redirectUrl", redirectUrl.replace(REDIRECT_PREFIX, ""));
		}
		catch (final IllegalArgumentException e)
		{
			resultMap.put("ccOrderPostForm", "");
			LOG.warn("Failed to set up ICICI payment post page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.ipg.globalError");
			return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
		}
		return IPG_REDIRECT_POST_PAGE;
	}

	/**
	 * This method is used to handle IPG offline scenario
	 *
	 * @param ccPaymentDetailsForm
	 * @param model
	 * @param ipgOrderPageData
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	public String doHandleIPGOffline(final SparSopPaymentDetailsForm ccPaymentDetailsForm, final Model model,
			final PaymentData ipgOrderPageData) throws CMSItemNotFoundException
	{
		setupAddPaymentPage(model);
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("cartData", cartData);
		setupCCPaymentMethodPostPage(ccPaymentDetailsForm, model);
		setupCODPaymentMethodPostPage(new SparSopPaymentDetailsForm(), model);
		GlobalMessages.addErrorMessage(model, "checkout.multi.ipg.offline.globalError");
		return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
	}

	/**
	 * This method is used to check whether Post URL for IPG is offline or not
	 *
	 * @param postURL
	 */
	protected boolean checkPostURLActive(final String postURL)
	{
		// this is used to check if the post URL is mocked.
		if (checkPostURLMock())
		{
			return true;
		}

		HttpURLConnection conn = null;
		try
		{
			final URL url = new URL(postURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
		}
		catch (final Exception e)
		{
			LOG.error("ICICI Payment Gateway is offline : " + e.getMessage());
			return false;
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}
		return true;
	}

	/**
	 * This method is to check if Post URL is mocked
	 */
	protected boolean checkPostURLMock()
	{
		final String urlStr = getSiteConfigService().getProperty(PaymentConstants.PaymentProperties.SOP_POST_URL);

		if (urlStr.charAt(0) == '/')
		{
			// its a mock URL and offline scenario can not be handled.
			return true;
		}
		return false;
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_METHOD);
	}


	/**
	 * Getter
	 *
	 * @return the sparCheckoutFacade
	 */
	public SparDefaultCheckoutFacade getSparCheckoutFacade()
	{
		return sparCheckoutFacade;
	}

	/**
	 * Setter
	 *
	 * @param sparCheckoutFacade
	 *           the sparCheckoutFacade to set
	 */
	public void setSparCheckoutFacade(final SparDefaultCheckoutFacade sparCheckoutFacade)
	{
		this.sparCheckoutFacade = sparCheckoutFacade;
	}



	/**
	 * Getter
	 *
	 * @return the sparPaymentFacade
	 */
	public SparDefaultPaymentFacade getSparPaymentFacade()
	{
		return sparPaymentFacade;
	}

	/**
	 * Setter
	 *
	 * @param sparPaymentFacade
	 *           the sparPaymentFacade to set
	 */
	public void setSparPaymentFacade(final SparDefaultPaymentFacade sparPaymentFacade)
	{
		this.sparPaymentFacade = sparPaymentFacade;
	}

	/**
	 * This method is used to validate the cart along with recalculating cart of warehouse is changed.
	 *
	 * @param redirectModel
	 * @return boolean
	 */
	@Override
	protected boolean validateCart(final RedirectAttributes redirectModel)
	{
		final CartData beforeCart = sparCartFacade.getSessionCart();
		final PriceData beforePrice = beforeCart.getTotalPrice();
		final String pointOfService = beforeCart.getOrderPointOfService() == null ? "" : beforeCart.getOrderPointOfService()
				.getDisplayName();
		final String beforeWarehouseCode = beforeCart.getOrderWarehouse() == null ? "" : beforeCart.getOrderWarehouse().getCode();
		if (StringUtils.isNotEmpty(pointOfService))
		{
			//set the warehouse so that recalculation is done by OOTB
			sparCartFacade.setOrderWarehouse(pointOfService);
		}
		//Validate the cart (OOTB)
		boolean isCartModified = super.validateCart(redirectModel);

		final CartData afterCart = sparCartFacade.getSessionCart();
		//Apart from the modification of cart due to change in inventory, change in location may result in price change.
		if (null != afterCart && null != afterCart.getOrderWarehouse())
		{
			final String afterWarehouseCode = afterCart.getOrderWarehouse().getCode();
			final PriceData afterPrice = afterCart.getTotalPrice();
			if (isCartTotalChanged(beforePrice, beforeWarehouseCode, afterWarehouseCode, afterPrice))
			{
				redirectModel.addFlashAttribute("isOrderValueChanged", Boolean.TRUE);
				isCartModified = true;
			}
		}


		return isCartModified;
	}

	/**
	 * This method is used to validate the change in cart total because of change in warehouse.
	 *
	 * @param beforePrice
	 * @param beforeWarehouseCode
	 * @param afterWarehouseCode
	 * @param afterPrice
	 * @return boolean
	 */
	protected boolean isCartTotalChanged(final PriceData beforePrice, final String beforeWarehouseCode,
			final String afterWarehouseCode, final PriceData afterPrice)
	{
		return null != beforeWarehouseCode && null != afterWarehouseCode && !beforeWarehouseCode.equals(afterWarehouseCode)
				&& null != beforePrice && null != beforePrice.getValue() && null != afterPrice && null != afterPrice.getValue()
				&& beforePrice.getValue().doubleValue() != afterPrice.getValue().doubleValue();
	}

	static
	{
		// Map hybris card type to Cybersource SOP credit card
		CYBERSOURCE_SOP_CARD_TYPES.put("visa", "001");
		CYBERSOURCE_SOP_CARD_TYPES.put("master", "002");
		CYBERSOURCE_SOP_CARD_TYPES.put("amex", "003");
		CYBERSOURCE_SOP_CARD_TYPES.put("diners", "005");
		CYBERSOURCE_SOP_CARD_TYPES.put("maestro", "024");
	}
}
