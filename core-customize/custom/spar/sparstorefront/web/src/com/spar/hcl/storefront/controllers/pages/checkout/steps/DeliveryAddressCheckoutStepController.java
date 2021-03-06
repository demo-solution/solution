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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
//import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import java.util.Set;
import java.util.regex.Pattern;

//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.mobileverification.service.SparMobileVerificationService;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;
import com.spar.hcl.storefront.controllers.pages.SparAddressForm;
import com.spar.hcl.storefront.validation.forms.SparAddressValidator;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PickupInStoreForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;



@Controller
@RequestMapping(value = "/checkout/multi/delivery-address")
public class DeliveryAddressCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(DeliveryAddressCheckoutStepController.class);
	
	private final static String DELIVERY_ADDRESS = "delivery-address";
	private static Pattern numberPattern = Pattern.compile("-?\\d*(\\.\\d+)?");

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sparAddressValidator")
	private SparAddressValidator sparAddressValidator;

	/* Code change start by sumit */


	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	private String sysGenOTPKey;
	private String mobilenum;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;
	/* Code change end here */

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Autowired
	private SparCartFacade sparCartFacade;

	@Autowired
	private UserService userService;

	@Resource(name = "sparMobileVerificationService")
	private SparMobileVerificationService sparMobileVerificationService;

	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	@Resource(name = "sparAddressPopulator")
	private Populator<AddressModel, AddressData> sparAddressPopulator;

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_ADDRESS)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		model.addAttribute("sysGenOTPKey", sysGenOTPKey);
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		String primaryCNCPhone = StringUtils.EMPTY;
		final PickupInStoreForm pickupInStoreForm = new PickupInStoreForm();

		if (sparCustomerFacade.findRegistrationOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else if (sparCustomerFacade.findCheckoutOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else
		{
			model.addAttribute("showOTPLink", Boolean.TRUE);
		}
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("selectedType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedType"));

		}

		if (null != (String) sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));

			final String cityName = (String) sessionService.getAttribute("cityName");
			final CustomerModel customerModel = (CustomerModel) getCartService().getSessionCart().getUser();
			if (null != customerModel && null != customerModel.getWhetherEmployee()
					&& customerModel.getWhetherEmployee().booleanValue() && cityName.equalsIgnoreCase("Bengaluru"))
			{
				model.addAttribute("employeeDeliveryAddressMsg",
						Config.getString("spar.delivery.address.checkout.message", "spar.delivery.address.checkout.message"));
			}
		}

		if (StringUtils.isNotEmpty(cartData.getCncPhone()))
		{
			primaryCNCPhone = cartData.getCncPhone();
		}
		else if (StringUtils.isNotEmpty(sparCustomerFacade.findPrimaryMobileNumber()))
		{
			primaryCNCPhone = sparCustomerFacade.findPrimaryMobileNumber();
		}

		final String loggedInUser = getCartService().getSessionCart().getUser().getUid();
		model.addAttribute("loggedInUser", loggedInUser);
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity());
		/*
		 * model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity((String)
		 * sessionService.getAttribute("cityName"), "POS"));
		 */
		model.addAttribute("primaryCNCPhone", primaryCNCPhone);
		model.addAttribute("pickupInStoreForm", pickupInStoreForm);
		getCheckoutFacade().setDeliveryAddressIfAvailable();

		//added for populating address form with values START

		final AddressData addressDta = getUserFacade().getDefaultAddress();
		if (null != addressDta)
		{
			if (!addressDta.isShippingAddress())
			{

				model.addAttribute("custAdd", addressDta);

			}
		}

		getLastUsedAddress(model);

		//added for populating address form with values END
		cartData = getCheckoutFacade().getCheckoutCart();
		populateCommonModelAttributes(model, cartData, new SparAddressForm());
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	@RequestMapping(value = "/getdeladdresses", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getDeleveryAddresses(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		model.addAttribute("sysGenOTPKey", sysGenOTPKey);
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		String primaryCNCPhone = StringUtils.EMPTY;
		final PickupInStoreForm pickupInStoreForm = new PickupInStoreForm();

		if (sparCustomerFacade.findRegistrationOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else if (sparCustomerFacade.findCheckoutOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else
		{
			model.addAttribute("showOTPLink", Boolean.TRUE);
		}
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}

		if (null != sessionService.getAttribute("selectedType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedType"));

		}

		if (null != (String) sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));

			final String cityName = (String) sessionService.getAttribute("cityName");
			final CustomerModel customerModel = (CustomerModel) getCartService().getSessionCart().getUser();
			if (null != customerModel && null != customerModel.getWhetherEmployee()
					&& customerModel.getWhetherEmployee().booleanValue() && cityName.equalsIgnoreCase("Bengaluru"))
			{
				model.addAttribute("employeeDeliveryAddressMsg",
						Config.getString("spar.delivery.address.checkout.message", "spar.delivery.address.checkout.message"));
			}
		}

		if (StringUtils.isNotEmpty(cartData.getCncPhone()))
		{
			primaryCNCPhone = cartData.getCncPhone();
		}
		else if (StringUtils.isNotEmpty(sparCustomerFacade.findPrimaryMobileNumber()))
		{
			primaryCNCPhone = sparCustomerFacade.findPrimaryMobileNumber();
		}

		final String loggedInUser = getCartService().getSessionCart().getUser().getUid();
		model.addAttribute("loggedInUser", loggedInUser);
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity());
		/*
		 * model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity((String)
		 * sessionService.getAttribute("cityName"), "POS"));
		 */
		model.addAttribute("primaryCNCPhone", primaryCNCPhone);
		model.addAttribute("pickupInStoreForm", pickupInStoreForm);
		getCheckoutFacade().setDeliveryAddressIfAvailable();

		//added for populating address form with values START

		final AddressData addressDta = getUserFacade().getDefaultAddress();
		if (null != addressDta)
		{
			if (!addressDta.isShippingAddress())
			{

				model.addAttribute("custAdd", addressDta);

			}
		}

		getLastUsedAddress(model);

		//added for populating address form with values END
		cartData = getCheckoutFacade().getCheckoutCart();
		populateCommonModelAttributes(model, cartData, new SparAddressForm());
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	private void getLastUsedAddress(final Model model)
	{
		final Collection<OrderModel> orders = getCartService().getSessionCart().getUser().getOrders();
		final AddressData lastUsedDelAddress = new AddressData();
		if (null != orders && orders.size() > 0)
		{
			final List<OrderModel> orderList = new ArrayList<OrderModel>();
			for (final OrderModel orderModel : orders)
			{
				orderList.add(orderModel);
			}
			Collections.sort(orderList, (obj1, obj2) -> Integer.parseInt(obj2.getCode()) - Integer.parseInt(obj1.getCode()));
			LOG.debug("Last order :::::::::::::::::::" + orderList.get(0).getDeliveryAddress());
			if(null != orderList.get(0).getDeliveryAddress()){
				sparAddressPopulator.populate(orderList.get(0).getDeliveryAddress(), lastUsedDelAddress);
				model.addAttribute("lastUsedDelAddress", lastUsedDelAddress);
			}
			model.addAttribute("lastUsedDelAddress", lastUsedDelAddress);
		}
	}

	/*
	 * public String add(@RequestParam("sparServiceAreaId") final Integer sparServiceAreaId,
	 * 
	 * @RequestParam("defaultStore") final String defaultStore,
	 * 
	 * @RequestParam("defaultCncCenter") final String defaultCncCenter, final SparAddressForm sparAddressForm, final
	 * BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel) throws
	 * CMSItemNotFoundException, CommerceCartModificationException, DuplicateUidException
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(final SparAddressForm sparAddressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, CommerceCartModificationException,
			DuplicateUidException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		setModelAttributeFromSession(model);
		model.addAttribute("sysGenOTPKey", sysGenOTPKey);

		if (sparCustomerFacade.findRegistrationOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else if (sparCustomerFacade.findCheckoutOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else
		{
			model.addAttribute("showOTPLink", Boolean.TRUE);
		}

		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		if (sparCustomerFacade.isOTPValidate())
		{
			if (sparCustomerFacade.findRegistrationOTPStatus())
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
			}
			else
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
			}
		}
		/*
		 * else { sparCustomerFacade.commitOTPData(mobilenum, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE); }
		 */

		//sparAddressForm.setSparserviceareaid(sparServiceAreaId);
		getSparAddressValidator().validate(sparAddressForm, bindingResult);
		populateCommonModelAttributes(model, cartData, sparAddressForm);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			//model.addAttribute("sparserviceareaid", sparAddressForm.getSparserviceareaid());
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = new AddressData();
		if (sparAddressForm.getDateOfBirth() != null)
		{
			final CustomerModel customer = getCurrentCustomer();
			customer.setDateOfBirth(sparAddressForm.getDateOfBirth());
		}
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
		newAddress.setEmail(sparAddressForm.getEmail());
		newAddress.setDateOfBirth(sparAddressForm.getDateOfBirth());
		newAddress.setArea(sparAddressForm.getArea());
		newAddress.setBuildingName(sparAddressForm.getBuildingName());
		newAddress.setLandmark(sparAddressForm.getLandmark());
		newAddress.setLongAddress(sparAddressForm.getLongAddress());

		/*
		 * if (StringUtils.isNotEmpty(defaultStore)) { newAddress.setDefaultStore(defaultStore); } if
		 * (StringUtils.isNotEmpty(defaultCncCenter)) { newAddress.setDefaultCncCenter(defaultCncCenter); } if (null !=
		 * sparServiceAreaId) { final SparServiceAreaData sparServiceArea =
		 * storeFinderFacadeInterface.getAddressServiceArea(sparServiceAreaId);
		 * newAddress.setSparServiceArea(sparServiceArea); }
		 */
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

		if (sparAddressForm.getSaveInAddressBook() != null)
		{
			newAddress.setVisibleInAddressBook(sparAddressForm.getSaveInAddressBook().booleanValue());
			if (sparAddressForm.getSaveInAddressBook().booleanValue() && getUserFacade().isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
			}
		}
		else if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		// Verify the address data.
		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		if (addressRequiresReview)
		{
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}
		getUserFacade().addAddress(newAddress);

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{ // temporary address should be removed
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		// Set the new address as the selected checkout delivery address
		getCheckoutFacade().setDeliveryAddress(newAddress);
		setHdDeliveryMode();
		final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
		if (null != customerModel && null != customerModel.getLrOptStatus() && customerModel.getLrOptStatus())
		{
			if (null != customerModel.getIsEnrolledToLR() && !customerModel.getIsEnrolledToLR())
			{
				sparLandmarkRewardFacade.enrollMember();
			}
		}
		return getCheckoutStep().currentStep();
	}

	/**
	 * Sets the "standard-gross" delivery mode and POS at order level
	 *
	 * @throws CommerceCartModificationException
	 */

	protected void setHdDeliveryMode() throws CommerceCartModificationException
	{
		final CartData cartData = cartFacade.getSessionCartWithEntryOrdering(true);
		if (null != cartData.getEntries() && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				final ProductData product = entry.getProduct();
				if (null != entry.getDeliveryPointOfService())
				{
					if (null != entry.getBasePrice() && 0 != entry.getBasePrice().getValue().compareTo(BigDecimal.ZERO))
					{
						final long entryNumber = entry.getEntryNumber().longValue();
						getSparCheckoutFacade().modifyCart(product.getCode(), entryNumber, null);

					}
				}
			}
			getCheckoutFacade().setDeliveryMode("standard-gross");
		}
		sessionService.setAttribute("selectedType", "HD");
	}

	@SuppressWarnings("boxing")
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	@RequireHardLogIn
	public String editAddressForm(@RequestParam("editAddressCode") final String editAddressCode, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		setModelAttributeFromSession(model);
		model.addAttribute("sysGenOTPKey", sysGenOTPKey);

		if (sparCustomerFacade.findRegistrationOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else if (sparCustomerFacade.findCheckoutOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else
		{
			model.addAttribute("showOTPLink", Boolean.TRUE);
		}
		final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
		if (getCheckoutStep().checkIfValidationErrors(validationResults))
		{
			return getCheckoutStep().onValidation(validationResults);
		}

		AddressData addressData = null;
		if (StringUtils.isNotEmpty(editAddressCode))
		{
			addressData = getCheckoutFacade().getDeliveryAddressForCode(editAddressCode);
		}

		final SparAddressForm sparAddressForm = new SparAddressForm();
		final boolean hasAddressData = addressData != null;
		if (hasAddressData)
		{
			sparAddressForm.setAddressId(addressData.getId());
			sparAddressForm.setTitleCode(addressData.getTitleCode());
			sparAddressForm.setFirstName(addressData.getFirstName());
			sparAddressForm.setLastName(addressData.getLastName());
			sparAddressForm.setLine1(addressData.getLine1());
			sparAddressForm.setLine2(addressData.getLine2());
			sparAddressForm.setTownCity(addressData.getTown());
			sparAddressForm.setPostcode(addressData.getPostalCode());
			sparAddressForm.setCountryIso(addressData.getCountry().getIsocode());
			sparAddressForm.setSaveInAddressBook(Boolean.valueOf(addressData.isVisibleInAddressBook()));
			sparAddressForm.setShippingAddress(Boolean.valueOf(addressData.isShippingAddress()));
			sparAddressForm.setBillingAddress(Boolean.valueOf(addressData.isBillingAddress()));
			sparAddressForm.setPhone(addressData.getPhone());
			sparAddressForm.setEmail(addressData.getEmail());
			sparAddressForm.setArea(addressData.getArea());
			sparAddressForm.setBuildingName(addressData.getBuildingName());
			sparAddressForm.setLandmark(addressData.getLandmark());
			sparAddressForm.setDateOfBirth(addressData.getDateOfBirth());
			sparAddressForm.setLongAddress(addressData.getLongAddress());
			if (addressData.getRegion() != null && !StringUtils.isEmpty(addressData.getRegion().getIsocode()))
			{
				sparAddressForm.setRegionIso(addressData.getRegion().getIsocode());
			}
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		populateCommonModelAttributes(model, cartData, sparAddressForm);
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		if (addressData != null)
		{
			model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
		}

		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}

	/*
	 * public String edit(@RequestParam("sparServiceAreaId") final Integer sparServiceAreaId,
	 * 
	 * @RequestParam("defaultStore") final String defaultStore,
	 * 
	 * @RequestParam("defaultCncCenter") final String defaultCncCenter, final SparAddressForm sparAddressForm, final
	 * BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel) throws
	 * CMSItemNotFoundException, CommerceCartModificationException, DuplicateUidException
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@RequireHardLogIn
	public String edit(final SparAddressForm sparAddressForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException, CommerceCartModificationException,
			DuplicateUidException
	{
		//getting session cart for refresh data

		sparCartFacade.getSessionCart();
		setModelAttributeFromSession(model);

		model.addAttribute("sysGenOTPKey", sysGenOTPKey);

		if (sparCustomerFacade.findRegistrationOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else if (sparCustomerFacade.findCheckoutOTPStatus())
		{
			model.addAttribute("showOTPLink", Boolean.FALSE);
		}
		else
		{
			model.addAttribute("showOTPLink", Boolean.TRUE);
		}


		if (sparCustomerFacade.isOTPValidate())
		{
			if (sparCustomerFacade.findRegistrationOTPStatus())
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
			}
			else
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
			}
		}
		else
		{
			sparCustomerFacade.commitOTPData(mobilenum, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		}
		//sparAddressForm.setSparserviceareaid(sparServiceAreaId);
		model.addAttribute("isdobSet", sparCustomerFacade.isdateofBirthset());
		getSparAddressValidator().validate(sparAddressForm, bindingResult);

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		populateCommonModelAttributes(model, cartData, sparAddressForm);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
			model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
			//model.addAttribute("sparserviceareaid", sparAddressForm.getSparserviceareaid());
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final AddressData newAddress = new AddressData();
		if (sparAddressForm.getDateOfBirth() != null)
		{
			final CustomerModel customer = getCurrentCustomer();
			customer.setDateOfBirth(sparAddressForm.getDateOfBirth());
		}
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
		newAddress.setEmail(sparAddressForm.getEmail());
		newAddress.setDateOfBirth(sparAddressForm.getDateOfBirth());
		newAddress.setArea(sparAddressForm.getArea());
		newAddress.setBuildingName(sparAddressForm.getBuildingName());
		newAddress.setLandmark(sparAddressForm.getLandmark());
		newAddress.setLongAddress(sparAddressForm.getLongAddress());
		/*
		 * if (StringUtils.isNotEmpty(defaultStore)) { newAddress.setDefaultStore(defaultStore); } if
		 * (StringUtils.isNotEmpty(defaultCncCenter)) { newAddress.setDefaultCncCenter(defaultCncCenter); } if (null !=
		 * sparServiceAreaId) { final SparServiceAreaData sparServiceArea =
		 * storeFinderFacadeInterface.getAddressServiceArea(sparServiceAreaId);
		 * newAddress.setSparServiceArea(sparServiceArea); }
		 */
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

		if (sparAddressForm.getSaveInAddressBook() == null)
		{
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setVisibleInAddressBook(Boolean.TRUE.equals(sparAddressForm.getSaveInAddressBook()));
		}

		newAddress.setDefaultAddress(getUserFacade().isAddressBookEmpty() || getUserFacade().getAddressBook().size() == 1
				|| Boolean.TRUE.equals(sparAddressForm.getDefaultAddress()));

		// Verify the address data.
		final AddressVerificationResult<AddressVerificationDecision> verificationResult = getAddressVerificationFacade()
				.verifyAddressData(newAddress);
		final boolean addressRequiresReview = getAddressVerificationResultHandler().handleResult(verificationResult, newAddress,
				model, redirectModel, bindingResult, getAddressVerificationFacade().isCustomerAllowedToIgnoreAddressSuggestions(),
				"checkout.multi.address.updated");

		if (addressRequiresReview)
		{
			if (StringUtils.isNotEmpty(sparAddressForm.getAddressId()))
			{
				final AddressData addressData = getCheckoutFacade().getDeliveryAddressForCode(sparAddressForm.getAddressId());
				if (addressData != null)
				{
					model.addAttribute("showSaveToAddressBook", Boolean.valueOf(!addressData.isVisibleInAddressBook()));
					model.addAttribute("edit", Boolean.TRUE);
				}
			}

			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		getUserFacade().editAddress(newAddress);
		getCheckoutFacade().setDeliveryAddress(newAddress);
		setHdDeliveryMode();
		return getCheckoutStep().currentStep();
	}

	/**
	 * This method is used to set the Model attributes using session objects
	 *
	 * @param model
	 */
	protected void setModelAttributeFromSession(final Model model)
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if (null != (String) sessionService.getAttribute("cityName"))
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
		model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity());
		/*
		 * model.addAttribute("clickNCollect", storeFinderFacadeInterface.getPosForCity((String)
		 * sessionService.getAttribute("cityName"), "POS"));
		 */
	}

	@RequestMapping(value = "/remove", method =
	{ RequestMethod.GET, RequestMethod.POST })
	@RequireHardLogIn
	public String removeAddress(@RequestParam("addressCode") final String addressCode, final RedirectAttributes redirectModel,
			final Model model) throws CMSItemNotFoundException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		setModelAttributeFromSession(model);
		if (getCheckoutFacade().isRemoveAddressEnabledForCart())
		{
			final AddressData addressData = new AddressData();
			addressData.setId(addressCode);
			getUserFacade().removeAddress(addressData);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"account.confirmation.address.removed");
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute("addressForm", new AddressForm());

		return getCheckoutStep().currentStep();
	}

	@RequestMapping(value = "/select", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectSuggestedAddress(final SparAddressForm sparAddressForm, final RedirectAttributes redirectModel)
	{
		final Set<String> resolveCountryRegions = org.springframework.util.StringUtils.commaDelimitedListToSet(Config
				.getParameter("resolve.country.regions"));

		final AddressData selectedAddress = new AddressData();
		selectedAddress.setId(sparAddressForm.getAddressId());
		selectedAddress.setTitleCode(sparAddressForm.getTitleCode());
		selectedAddress.setFirstName(sparAddressForm.getFirstName());
		selectedAddress.setLastName(sparAddressForm.getLastName());
		selectedAddress.setLine1(sparAddressForm.getLine1());
		selectedAddress.setLine2(sparAddressForm.getLine2());
		selectedAddress.setTown(sparAddressForm.getTownCity());
		selectedAddress.setPostalCode(sparAddressForm.getPostcode());
		selectedAddress.setBillingAddress(false);
		selectedAddress.setShippingAddress(true);
		final CountryData countryData = getI18NFacade().getCountryForIsocode(sparAddressForm.getCountryIso());
		selectedAddress.setCountry(countryData);
		selectedAddress.setPhone(sparAddressForm.getPhone());
		selectedAddress.setLongAddress(sparAddressForm.getLongAddress());

		if (resolveCountryRegions.contains(countryData.getIsocode()) && sparAddressForm.getRegionIso() != null
				&& !StringUtils.isEmpty(sparAddressForm.getRegionIso()))
		{
			final RegionData regionData = getI18NFacade().getRegion(sparAddressForm.getCountryIso(), sparAddressForm.getRegionIso());
			selectedAddress.setRegion(regionData);
		}

		if (sparAddressForm.getSaveInAddressBook() != null)
		{
			selectedAddress.setVisibleInAddressBook(sparAddressForm.getSaveInAddressBook().booleanValue());
		}

		if (Boolean.TRUE.equals(sparAddressForm.getEditAddress()))
		{
			getUserFacade().editAddress(selectedAddress);
		}
		else
		{
			getUserFacade().addAddress(selectedAddress);
		}

		final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();

		getCheckoutFacade().setDeliveryAddress(selectedAddress);
		if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
		{
			getUserFacade().removeAddress(previousSelectedAddress);
		}

		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "checkout.multi.address.added");
		return getCheckoutStep().nextStep();
	}


	/**
	 * This method gets called when the "Use this Address" button is clicked. It sets the selected delivery address on
	 * the checkout facade - if it has changed, and reloads the page highlighting the selected delivery address.
	 *
	 * @param selectedAddressCode
	 *           - the id of the delivery address.
	 *
	 * @return - a URL to the page to load.
	 * @throws CommerceCartModificationException
	 */

	/*
	 * public String doSelectDeliveryAddress(@RequestParam("selectedAddressCode") final String selectedAddressCode, final
	 * RedirectAttributes redirectAttributes, final Model model) throws CMSItemNotFoundException,
	 * CommerceCartModificationException
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectDeliveryAddress(@RequestParam("selectedAddressCode") final String selectedAddressCode,
			final RedirectAttributes redirectAttributes, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
		if (getCheckoutStep().checkIfValidationErrors(validationResults))
		{
			return getCheckoutStep().onValidation(validationResults);
		}
		sessionService.setAttribute("selectedType", "HD");
		if (StringUtils.isNotBlank(selectedAddressCode))
		{
			final AddressData selectedAddressData = getCheckoutFacade().getDeliveryAddressForCode(selectedAddressCode);
			final boolean hasSelectedAddressData = selectedAddressData != null;



			if (hasSelectedAddressData)
			{
				final AddressData cartCheckoutDeliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
				if (isAddressIdChanged(cartCheckoutDeliveryAddress, selectedAddressData))
				{
					getCheckoutFacade().setDeliveryAddress(selectedAddressData);
					if (cartCheckoutDeliveryAddress != null && !cartCheckoutDeliveryAddress.isVisibleInAddressBook())
					{ // temporary address should be removed
						getUserFacade().removeAddress(cartCheckoutDeliveryAddress);
					}
				}
			}
			if (StringUtils.isNotEmpty(request.getParameter("addressFindStore")))
			{
				final String pos = request.getParameter("addressFindStore");
				setWarehouseAndPOS(pos);
				LOG.info("Mapped warehouse :::::::::::::::: " + pos);
				selectedAddressData.setMappedStore(pos);
				getUserFacade().editAddress(selectedAddressData);
			}
			/*
			 * if (null == selectedAddressData.getSparServiceArea()) { GlobalMessages.addErrorMessage(model,
			 * "address.selectedAddress.invalid"); return enterStep(model, redirectAttributes); } else { if
			 * (!selectedAddressData.getSparServiceArea().getActive()) { GlobalMessages.addErrorMessage(model,
			 * "address.homedelivery.notavailable"); return enterStep(model, redirectAttributes); } } if
			 * (StringUtils.isNotEmpty(selectedAddressData.getSparServiceArea().getDefaultStore())) {
			 * setWarehouseAndPOS(selectedAddressData.getSparServiceArea().getDefaultStore()); }
			 */
			else
			{
				GlobalMessages.addErrorMessage(model, "address.selectedAddress.invalid");
				return enterStep(model, redirectAttributes);
			}
			setHdDeliveryMode();
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

	protected String getBreadcrumbKey()
	{
		return "checkout.multi." + getCheckoutStep().getProgressBarId() + ".breadcrumb";
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(DELIVERY_ADDRESS);
	}

	protected void populateCommonModelAttributes(final Model model, final CartData cartData, final SparAddressForm sparAddressForm)
			throws CMSItemNotFoundException
	{
		setModelAttributeFromSession(model);
		model.addAttribute("cartData", cartData);
		model.addAttribute("sparAddressForm", sparAddressForm);
		model.addAttribute("deliveryAddresses", getDeliveryAddresses(cartData.getDeliveryAddress()));
		model.addAttribute("noAddress", Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
		model.addAttribute("addressFormEnabled", Boolean.valueOf(getCheckoutFacade().isNewAddressEnabledForCart()));
		model.addAttribute("removeAddressEnabled", Boolean.valueOf(getCheckoutFacade().isRemoveAddressEnabledForCart()));
		model.addAttribute("showSaveToAddressBook", Boolean.TRUE);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(getBreadcrumbKey()));
		model.addAttribute("metaRobots", "noindex,nofollow");
		if (StringUtils.isNotBlank(sparAddressForm.getCountryIso()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sparAddressForm.getCountryIso()));
			model.addAttribute("country", sparAddressForm.getCountryIso());
		}
		prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	/**
	 * This method is called when the user clicks on "Click & Collect" radio button in the address checkout step and
	 * click on Continue button.
	 *
	 * @throws CMSItemNotFoundException
	 * @throws CommerceCartModificationException
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/clickncollect", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectClickNCollect(@RequestParam("selectedClickNCollect") final String selectedClickNCollect,
			@RequestParam("clickCollectPhone") final String clickCollectPhone, final SparAddressForm sparAddressForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		String cncPhone = StringUtils.EMPTY;

		try
		{
			if (sparCustomerFacade.isOTPValidate())
			{
				if (sparCustomerFacade.findRegistrationOTPStatus())
				{
					sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
				}
				else
				{
					sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
				}
			}
			else
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
			}
		}
		catch (final DuplicateUidException e)
		{
			LOG.error("Click and collect OTP error");
		}

		sparAddressForm.setPhone(clickCollectPhone);
		sparAddressForm.setFirstName("cncPhoneValidation");

		getSparAddressValidator().validate(sparAddressForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			setModelAttributeFromSession(model);
			model.addAttribute("sysGenOTPKey", sysGenOTPKey);

			if (sparCustomerFacade.findRegistrationOTPStatus())
			{
				model.addAttribute("showOTPLink", Boolean.FALSE);
			}
			else if (sparCustomerFacade.findCheckoutOTPStatus())
			{
				model.addAttribute("showOTPLink", Boolean.FALSE);
			}
			else
			{
				model.addAttribute("showOTPLink", Boolean.TRUE);
			}

			final CartData cartData = getCheckoutFacade().getCheckoutCart();
			sparAddressForm.setPhone(cncPhone);
			populateCommonModelAttributes(model, cartData, sparAddressForm);

			GlobalMessages.addErrorMessage(model, "address.error.formentry.invalid");
			return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
		}

		final ValidationResults validationResults = getCheckoutStep().validate(redirectAttributes);
		if (getCheckoutStep().checkIfValidationErrors(validationResults))
		{
			return getCheckoutStep().onValidation(validationResults);
		}

		if (StringUtils.isNotEmpty(selectedClickNCollect))
		{
			setWarehouseAndPOS(selectedClickNCollect);
		}

		if (StringUtils.isNotEmpty(clickCollectPhone) && numberPattern.matcher(clickCollectPhone).matches()
				&& clickCollectPhone.length() == 10)
		{
			cncPhone = clickCollectPhone;
		}
		else
		{
			cncPhone = sparCustomerFacade.findPrimaryMobileNumber();
		}

		sparCartFacade.setCncPhone(cncPhone);
		final PointOfServiceData pointOfService = storeFinderFacadeInterface.getPosStore(selectedClickNCollect);
		setPickUpDeliveryMode(pointOfService);
		return getCheckoutStep().nextStep();
	}

	/**
	 * Sets the "pickup" delivery mode and POS at order level
	 *
	 * @param pointOfService
	 * @throws CommerceCartModificationException
	 */
	protected void setPickUpDeliveryMode(final PointOfServiceData pointOfService) throws CommerceCartModificationException
	{
		final CartData cartData = cartFacade.getSessionCartWithEntryOrdering(true);
		if (null != cartData.getEntries() && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				if (null != entry.getBasePrice() && 0 != entry.getBasePrice().getValue().compareTo(BigDecimal.ZERO))
				{
					final ProductData product = entry.getProduct();
					final long entryNumber = entry.getEntryNumber().longValue();
					getSparCheckoutFacade().modifyCart(product.getCode(), entryNumber, pointOfService);
				}
			}
			getCheckoutFacade().setDeliveryMode("pickup");
		}
		sessionService.setAttribute("selectedType", "CNC");

	}

	/**
	 * This method is used to set selectedStorev(POS) and warehouse against the sectedStore. This method also sets the
	 * POS in an Order.
	 *
	 * @param selectedStore
	 */
	public void setWarehouseAndPOS(final String selectedStore)
	{
		sessionService.setAttribute("selectedStore", selectedStore);
		final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(selectedStore);
		sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		getCartFacade().setOrderPointOfService(selectedStore);
		getCartFacade().setOrderWarehouse(selectedStore);
	}

	/**
	 * @return the sparAddressValidator
	 */
	public SparAddressValidator getSparAddressValidator()
	{
		return sparAddressValidator;
	}

	/**
	 * @return the sparCheckoutFacade
	 */
	public SparDefaultCheckoutFacade getSparCheckoutFacade()
	{
		return sparCheckoutFacade;
	}





	/**
	 * This method is used to generate the OTP key on Checkout flow
	 *
	 * @param mobilenum
	 * @throws IOException
	 * @throws DuplicateUidException
	 */

	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "mobilenum")
	@ResponseBody
	public Boolean generateOTP(@RequestParam(value = "mobilenum") final String mobilenum) throws IOException,
			DuplicateUidException
	{
		final Boolean isAvailable = sparMobileVerificationService.countMobileNo(mobilenum);
		if (isAvailable.booleanValue())
		{
			this.mobilenum = mobilenum;
			sysGenOTPKey = RandomStringUtils.randomNumeric(6);
			LOG.info("sysGenOTPKey:::" + sysGenOTPKey);
			//getmGageOTPResponse(sysGenOTPKey, mobilenum);
		}
		return isAvailable;
	}


	/**
	 * This method is getting called from generateOTP(method chaining),
	 *
	 * @param mobilenum
	 * @throws IOException
	 * @throws DuplicateUidException
	 */
	@SuppressWarnings("boxing")
	public void getmGageOTPResponse(final String sysGenOTPKey, final String mobilenum) throws IOException, DuplicateUidException
	{
		this.sysGenOTPKey = sysGenOTPKey;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		final String msg = Config.getString("mGage.user.message", "mGage.user.message");
		final String destmobnum = mobilenum;
		final String USER_AGENT = "Mozilla/5.0";
		final String OTP = sysGenOTPKey;
		final String OTPmsg = msg.replaceAll("XXXXXX", OTP);
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");
		/*
		 * final String mGageURL = mgageURL + uname + "&pass=" + pass + "&send=345555&dest=" + destmobnum + "&msg=" +
		 * URLEncoder.encode(OTPmsg, "UTF-8");
		 */
		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(OTPmsg, "UTF-8");
		LOG.info("mgage URL:::" + mGageURL);

		sessionService.setAttribute("OTPKey", sysGenOTPKey);
		sparCustomerFacade.commitOTPData(mobilenum, Boolean.FALSE, sparCustomerFacade.findRegistrationOTPStatus(), Boolean.FALSE);
		final URL url = new URL(mGageURL);
		HttpURLConnection urlConnection = null;
		try
		{
			urlConnection = (HttpURLConnection) url.openConnection();
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
		}
		catch (final Exception urlFormatException)
		{
			urlFormatException.printStackTrace();
			LOG.error("Connection Failed", urlFormatException);
		}
		finally
		{
			urlConnection.disconnect();
		}
	}


	/**
	 * This method is used to match the system generated OTP key with User entered OTP key,in case of match we will save
	 * the status in DB
	 *
	 * @param userenteredotp
	 * @throws DuplicateUidException
	 */
	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "userenteredotp")
	@ResponseBody
	public String validateUserEnteredOTP(@RequestParam(value = "userenteredotp") final String userenteredotp,
			@RequestParam(value = "lrOptStatus") final boolean lrOptStatus) throws DuplicateUidException
	{
		sessionService.setAttribute("userenteredotp", userenteredotp);
		if (sessionService.getAttribute("OTPKey").equals(userenteredotp))
		{
			if (!sparCustomerFacade.isOTPValidate())
			{
				sparCustomerFacade.commitOTPData(mobilenum, Boolean.TRUE, sparCustomerFacade.findRegistrationOTPStatus(),
						Boolean.FALSE);
				if (lrOptStatus)
				{
					sparLandmarkRewardFacade.changeLROptStatus();
				}
			}
		}
		return ControllerConstants.Views.Pages.MultiStepCheckout.AddEditDeliveryAddressPage;
	}


	/**
	 * @return the otpKey
	 */
	public String getSysGenOTPKey()
	{
		return sysGenOTPKey;
	}

	/**
	 * @param otpKey
	 *           the otpKey to set
	 */
	public void setSysGenOTPKey(final String sysGenOTPKey)
	{
		this.sysGenOTPKey = sysGenOTPKey;
	}


	/* Code change end here */

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController
	 * #getCartFacade()
	 */
	@Override
	protected SparCartFacade getCartFacade()
	{
		return (SparCartFacade) super.getCartFacade();
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
