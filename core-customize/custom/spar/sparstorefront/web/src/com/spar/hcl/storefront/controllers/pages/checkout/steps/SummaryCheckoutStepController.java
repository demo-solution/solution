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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.order.data.PaymentModeData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.populators.SparCategoryData;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.OrderRestrictionModel;
import de.hybris.platform.voucher.model.RestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;


/*import com.spar.hcl.ws.plms.service.PushOrderEntryInPLMS;*/


@Controller
@RequestMapping(value = "/checkout/multi/summary")
public class SummaryCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(SummaryCheckoutStepController.class);
	
	private final static String SUMMARY = "summary";
	private final static String DISPLAY_APPLY_BTN = "displayApplyVoucherBtn";
	private final static String RELEASE_APPLY_BTN = "releaseApplyVoucherBtn";
	private final static String RELEASE_MSG = "releaseMsg";
	private final static String VOUCHER_CODE = "vouchercode";
	private final static String VOUCHER_MULTIPLE_APPLY_MSGVALUE = "spar.multiple.apply.voucher.value";

	private final static String VOUCHER_INVALID_MSG = "spar.invalid.voucher.value";
	private final static String VOUCHER_INVALID_MSGKEY = "spar.invalid.voucher.key";

	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;


	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	/* Code change start for Voucher story Sumit */
	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "commerceCartCalculationStrategy")
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "sparCartService")
	private SparCartService sparCartService;

	@Resource(name = "promotionsService")
	private PromotionsService promotionsService;

	@Resource(name = "voucherService")
	private VoucherService voucherService;

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "voucherModelService")
	private VoucherModelService voucherModelService;

	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;




	@SuppressWarnings(
	{ "deprecation", "boxing" })
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		setModelWithSessionAttributes(model);
		final Map<SparCategoryData, List<OrderEntryData>> catToOrdrEntryMap = new LinkedHashMap<SparCategoryData, List<OrderEntryData>>();
		//CartData cartData = getSparCheckoutFacade().getCheckoutCart();

		if (null != sessionService.getAttribute(RELEASE_MSG))
		{
			final String errorMsg = sessionService.getAttribute(RELEASE_MSG);
			GlobalMessages.addErrorMessage(model, errorMsg);
			sessionService.setAttribute(RELEASE_MSG, null);
		}
		CartData cartData = getSparCheckoutFacade().getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				sparCartFacade.groupCartOnCategory(entry, catToOrdrEntryMap);
				final String productCode = entry.getProduct().getCode();
				final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
				entry.setProduct(product);
			}
		}

		if (Boolean.TRUE.equals(sessionService.getAttribute("voucherFlag")))
		{
			GlobalMessages.addErrorMessage(model, VOUCHER_INVALID_MSG);
			sessionService.removeAttribute("voucherFlag");
		}

		final Collection<String> voucherCodes = sparVoucherFacade.getAppliedVoucherCodes();
		if (null != voucherCodes && CollectionUtils.isNotEmpty(voucherCodes))
		{
			final double appliedVoucherVal = sparVoucherFacade.getAppliedVoucherValue();
			if (CollectionUtils.isNotEmpty(sparVoucherFacade.getVouchersForCart()) && appliedVoucherVal != 0.0D)
			{
				//cartData = getSparCheckoutFacade().getCheckoutCart();
				sparVoucherFacade.updateVoucherValue(cartData, true);
				//if (cartData.getVoucherValue().getValue().doubleValue() > 0.0D)
				if (CollectionUtils.isNotEmpty(cartData.getAppliedVoucherTotal()))
				{
					model.addAttribute(DISPLAY_APPLY_BTN, Boolean.FALSE);
					model.addAttribute(RELEASE_APPLY_BTN, Boolean.TRUE);
				}
				else
				{
					model.addAttribute(DISPLAY_APPLY_BTN, Boolean.TRUE);
					model.addAttribute(RELEASE_APPLY_BTN, Boolean.FALSE);
				}
			}
			else
			{
				sparVoucherFacade.updateVoucherValue(cartData, false);
				model.addAttribute(DISPLAY_APPLY_BTN, Boolean.TRUE);
				model.addAttribute(RELEASE_APPLY_BTN, Boolean.FALSE);
			}
			final List<String> voucherList = sparVoucherFacade.getSparVouchersForCart();
			if (CollectionUtils.isNotEmpty(voucherList))
			{
				if (cartData.getTotalPriceWithTax().getValue().equals(BigDecimal.valueOf(0.0)))
				{
					model.addAttribute("disableMakePayment", Boolean.TRUE);
				}
				model.addAttribute(VOUCHER_CODE, voucherList);
			}
		}
		else
		{
			model.addAttribute(DISPLAY_APPLY_BTN, Boolean.TRUE);
			model.addAttribute(RELEASE_APPLY_BTN, Boolean.FALSE);
		}

		/* Code change end for Voucher Story Sumit */
		cartData = getSparCheckoutFacade().getCheckoutCart();
		model.addAttribute("catToOrdrEntryMap", catToOrdrEntryMap);
		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return ControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
	}

	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(final Model model, final HttpServletRequest request, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException, VoucherOperationException
	{

		final Collection<String> voucherCodes = sparVoucherFacade.getAppliedVoucherCodes();

		if (null != voucherCodes && CollectionUtils.isNotEmpty(voucherCodes))
		{
			final String voucherCodeForCart = voucherCodes.iterator().next();

			final String errorMsg = validateBeforePlaceOrder(voucherCodeForCart);

			if (null != errorMsg)
			{
				sessionService.setAttribute(RELEASE_MSG, errorMsg);
				setCheckoutStepLinksForModel(model, getCheckoutStep());
				return REDIRECT_URL_SUMMARY;
			}
		}

		setModelWithSessionAttributes(model);
		if (validateOrderForm(model))
		{
			return enterStep(model, redirectModel);
		}

		final CartData cartData = getSparCheckoutFacade().getCheckoutCart();

		if (CollectionUtils.isNotEmpty(sparVoucherFacade.getSparVouchersForCart())
				&& cartData.getTotalPriceWithTax().getValue().equals(BigDecimal.valueOf(0.0)))
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.VOUCHERPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(true));
		}

		//Update savings to cartmodel
		sparCartFacade.updateCartSavings(cartData);

		//if payment mode is  COD/Wallet/IPG then skip authorization check
		if (!isSparPaymentModes(cartData)
				&& !PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(cartData.getPaymentMode().getCode()))
		{
			// authorize, if failure occurs don't allow to place the order
			boolean isPaymentUthorized = false;
			try
			{
				isPaymentUthorized = getSparCheckoutFacade().authorizePayment(null);
			}
			catch (final AdapterException ae)
			{
				// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
				LOG.error(ae.getMessage(), ae);
			}
			if (!isPaymentUthorized)
			{
				GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
				return enterStep(model, redirectModel);
			}
		}

		final OrderData orderData;
		// Update Order with voucher details
		if (sparVoucherFacade.updateOrderWithVoucher(cartData))
		{
			sessionService.setAttribute("voucherFlag", Boolean.TRUE);
			return REDIRECT_URL_SUMMARY;
		}

		try
		{
			orderData = getSparCheckoutFacade().placeOrder();
			//Initiate order confirmation message to customer
			sparCheckoutFacade.triggerSMS(Config.getString("user.order.confirm", "user.order.confirm"), orderData);
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return enterStep(model, redirectModel);
		}
		//Reset cnc contact for cartmodel
		sparCartFacade.resetCncPhoneNumber();
		sessionService.removeAttribute("voucherValueExceedingOrdertotal");
		return redirectToOrderConfirmationPage(orderData);
	}

	/**
	 * @param model
	 */
	protected void setModelWithSessionAttributes(final Model model)
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
		if (null != sessionService.getAttribute("selectedSlot"))
		{
			model.addAttribute("selectedSlot", sessionService.getAttribute("selectedSlot"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedSlot"));

		}

		model.addAttribute("homePageForm", homePageForm);
	}


	/**
	 * Validates the order form before to filter out invalid order states
	 *
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	protected boolean validateOrderForm(final Model model)
	{
		boolean invalid = false;

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}

		final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
		//if payment mode is not COD then do payment check
		if (!isCODMode(cartData))
		{
			if (!isWalletMode(cartData) && !isMultiPaymentMode(cartData) && !isVoucherPaymentMode(cartData)
					&& !isLandmarkRewardMode(cartData))
			{
				if (getCheckoutFlowFacade().hasNoPaymentInfo())
				{
					GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
					invalid = true;
				}

			}
		}

		if (!getSparCheckoutFacade().containsTaxValues())
		{
			LOG.error(String.format(
					"Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue",
					cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(
					String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}


	/**
	 * This method is used to get the confirmation from user end when voucher total will exceed the order total
	 *
	 * @param voucherCode
	 * @param model
	 * @param redirectAttributes
	 * @return boolean
	 */

	@SuppressWarnings("boxing")
	@RequestMapping(value = "/voucher-method/confirmation", method = RequestMethod.GET)
	@ResponseBody
	public String getUserConfirmationForVoucherApply(@RequestParam(value = VOUCHER_CODE) final String voucherCode,
			final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, VoucherOperationException
	{
		if (CollectionUtils.isEmpty(sparVoucherFacade.getVouchersForCart()))
		{
			if (StringUtils.isNotEmpty(voucherCode))
			{
				if (sparVoucherFacade.isVoucherCodeValid(voucherCode))
				{
					final VoucherModel voucherModel = voucherService.getVoucher(voucherCode);
					sparVoucherFacade.updateTotalEquivalentPrice();
					if (!sparVoucherFacade.checkVoucherCanBeRedeemed(voucherModel, voucherCode))
					{
						String violationMessage = null;
						final Set<RestrictionModel> restrictions = voucherModel.getRestrictions();
						for (final RestrictionModel restriction : restrictions)
						{
							if (restriction.getItemtype().equalsIgnoreCase("OrderRestriction"))
							{
								final OrderRestrictionModel orderRestriction = (OrderRestrictionModel) restriction;
								final CartData cartData = sparCartFacade.getSessionCart();
								if (null != cartData.getSubTotal() && null != orderRestriction.getTotal()
										&& cartData.getSubTotal().getValue().doubleValue() < orderRestriction.getTotal().doubleValue())
								{
									violationMessage = restriction.getViolationMessage();
								}
							}

						}
						return violationMessage;
					}
					else
					{
						final boolean getConfirmationForVoucherApply = sparVoucherFacade.isVoucherValueExceedingOrdertotal(voucherCode);
						sessionService.setAttribute("voucherValueExceedingOrdertotal", getConfirmationForVoucherApply);
						return String.valueOf(getConfirmationForVoucherApply);
					}
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		return null;
	}

	/**
	 * This method is used to get common code which is getting across this class
	 *
	 * @param model
	 * @param redirectAttributes
	 * @throws CMSItemNotFoundException
	 */
	public void getUtilDataForOrderConfirmation(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		setModelWithSessionAttributes(model);
		final Map<SparCategoryData, List<OrderEntryData>> catToOrdrEntryMap = new LinkedHashMap<SparCategoryData, List<OrderEntryData>>();
		final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				sparCartFacade.groupCartOnCategory(entry, catToOrdrEntryMap);
				final String productCode = entry.getProduct().getCode();
				final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
						Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
				entry.setProduct(product);
			}
		}

		model.addAttribute("catToOrdrEntryMap", catToOrdrEntryMap);
		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}


	/**
	 * This method is used to apply voucher in the cart or order level
	 *
	 * @param voucherCode
	 * @param model
	 * @param redirectModel
	 * @return String
	 */

	@RequestMapping(value = "/voucher-method/apply", method = RequestMethod.GET)
	public String applyVoucher(@RequestParam(value = VOUCHER_CODE) final String voucherCode, final Model model,
			final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, VoucherOperationException, CommerceCartModificationException, InvalidCartException
	{
		final List<VoucherData> vouchersForCartData = sparVoucherFacade.getVouchersForCart();
		if (CollectionUtils.isEmpty(vouchersForCartData))
		{
			Map voucherCodeStatus = null;
			voucherCodeStatus = sparVoucherFacade.applyVoucherCode(voucherCode);
			if (CollectionUtils.isNotEmpty(vouchersForCartData))
			{
				model.addAttribute(DISPLAY_APPLY_BTN, Boolean.FALSE);
				model.addAttribute(RELEASE_APPLY_BTN, Boolean.TRUE);
			}
			else
			{
				model.addAttribute(DISPLAY_APPLY_BTN, Boolean.TRUE);
				model.addAttribute(RELEASE_APPLY_BTN, Boolean.FALSE);
			}
			if (CollectionUtils.isNotEmpty(vouchersForCartData))
			{
				final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
				sparVoucherFacade.doCartCalculation(cartData, voucherCode);
			}

			String errKey = null;
			if (null != voucherCodeStatus)
			{
				final Iterator itr = voucherCodeStatus.entrySet().iterator();
				while (itr.hasNext())
				{
					final Map.Entry entry = (Entry) itr.next();
					if (entry.getKey() != null)
					{
						errKey = entry.getValue().toString();
						break;
					}
				}
			}
			if (null != errKey)
			{
				if (errKey.equals(VOUCHER_MULTIPLE_APPLY_MSGVALUE))
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
							VOUCHER_MULTIPLE_APPLY_MSGVALUE);
					return REDIRECT_PREFIX + "/checkout/multi/summary/view";
				}
				else
				{
					GlobalMessages.addErrorMessage(model, errKey);
					return REDIRECT_PREFIX + "/checkout/multi/summary/view";
				}
			}

			final List<String> vouchersForCartList = sparVoucherFacade.getSparVouchersForCart();
			if (CollectionUtils.isNotEmpty(vouchersForCartList))
			{
				model.addAttribute(VOUCHER_CODE, vouchersForCartList);
				//GlobalMessages.addConfMessage(model, "spar.voucher.checkout.applied.message");
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"spar.voucher.checkout.applied.message");
			}
		}
		else
		{
			final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
			sparVoucherFacade.doCartCalculation(cartData, voucherCode);
			GlobalMessages.addErrorMessage(model, "spar.voucher.restrict.apply");
		}
		return REDIRECT_PREFIX + "/checkout/multi/summary/view";
	}


	/**
	 * This method is used to release the applied voucher from cart
	 *
	 * @param model
	 * @param redirectModel
	 * @return String
	 */

	@RequestMapping(value = "/voucher-method/release", method = RequestMethod.GET)
	public String releaseSparVoucher(final Model model, final RedirectAttributes redirectModel)
			throws VoucherOperationException, CMSItemNotFoundException, CommerceCartModificationException
	{
		final List<VoucherData> voucherDataList = sparVoucherFacade.getVouchersForCart();
		VoucherData voucherCodeForCart = null;

		if (CollectionUtils.isNotEmpty(voucherDataList))
		{
			final Iterator itr = voucherDataList.iterator();
			while (itr.hasNext())
			{
				voucherCodeForCart = (VoucherData) itr.next();
				break;
			}

			try
			{
				final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
				sparVoucherFacade.releaseSparVoucher(voucherCodeForCart.getVoucherCode());
				sparVoucherFacade.doCartCalculation(cartData, voucherCodeForCart.getVoucherCode());
				//if (null == sparVoucherFacade.getVouchersForCart() || CollectionUtils.isEmpty(sparVoucherFacade.getVouchersForCart()))
				//{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"spar.voucher.checkout.released.message");
				//return enterStep(model, redirectModel);.
				return REDIRECT_PREFIX + "/checkout/multi/summary/view";

				//}
				//else
				//{
				//	GlobalMessages.addConfMessage(model, "spar.voucher.checkout.released.message");
				//	return enterStep(model, redirectModel);
				//}
			}
			catch (final VoucherOperationException msg)
			{
				throw new VoucherOperationException("Error while removing the voucher" + msg);
			}
		}
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
				"spar.voucher.checkout.released.message");
		//return enterStep(model, redirectModel);
		return REDIRECT_PREFIX + "/checkout/multi/summary/view";

	}

	public String validateBeforePlaceOrder(final String voucherCode)
	{
		String errKey = null;
		if (CollectionUtils.isNotEmpty(sparVoucherFacade.getVouchersForCart()))
		{
			Map voucherCodeStatus = null;
			voucherCodeStatus = isVoucherApplicable(voucherCode);

			if (null != voucherCodeStatus)
			{
				final Iterator itr = voucherCodeStatus.entrySet().iterator();
				while (itr.hasNext())
				{
					final Map.Entry entry = (Entry) itr.next();
					if (entry.getKey() != null)
					{
						errKey = entry.getValue().toString();
						break;
					}
				}
			}
		}
		return errKey;
	}

	public Map isVoucherApplicable(final String voucherCode)
	{
		final Map canVoucherBeApply = new HashMap();
		if (!sparVoucherFacade.validateSparVoucherCodeParameter(voucherCode))
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		if (!sparVoucherFacade.isVoucherCodeValid(voucherCode))
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}
		final VoucherModel voucherModel = voucherService.getVoucher(voucherCode);
		final UserModel user = userService.getCurrentUser();
		if (!voucherModelService.isReservable(voucherModel, voucherCode, user))
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		final VoucherModel voucher = voucherService.getVoucher(voucherCode);
		if (voucher == null)
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		return canVoucherBeApply;
	}


	/**
	 * This method is used to check COD/CreditCard payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isSparPaymentModes(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isCashOnDelivery = (null != paymentModeData
				&& PaymentModeEnum.CASHONDELIVERY.getCode().equals(paymentModeData.getCode()));
		final boolean isIPG = (null != paymentModeData && PaymentModeEnum.CREDITCARD.getCode().equals(paymentModeData.getCode()));
		final boolean isWalletApplied = (null != paymentModeData
				&& PaymentModeEnum.WALLET.getCode().equals(paymentModeData.getCode()));
		final boolean isMultiPayment = (null != paymentModeData
				&& PaymentModeEnum.MULTIPAYMENTMODE.getCode().equals(paymentModeData.getCode()));
		final boolean isVoucherPaymentMode = (null != paymentModeData
				&& PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(paymentModeData.getCode()));
		final boolean isLandmarkRewardApplied = (null != paymentModeData
				&& PaymentModeEnum.LANDMARKREWARD.getCode().equals(paymentModeData.getCode()));
		return isCashOnDelivery || isIPG || isWalletApplied || isMultiPayment || isVoucherPaymentMode || isLandmarkRewardApplied;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isCODMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isCashOnDelivery = (null != paymentModeData
				&& PaymentModeEnum.CASHONDELIVERY.getCode().equals(paymentModeData.getCode()));
		return isCashOnDelivery;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isWalletMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isWalletApplied = (null != paymentModeData
				&& PaymentModeEnum.WALLET.getCode().equals(paymentModeData.getCode()));
		return isWalletApplied;
	}

	private boolean isLandmarkRewardMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isLandmarkRewardApplied = (null != paymentModeData
				&& PaymentModeEnum.LANDMARKREWARD.getCode().equals(paymentModeData.getCode()));
		return isLandmarkRewardApplied;
	}

	/**
	 * This method is used to check voucher payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isVoucherPaymentMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isVoucherModeApplied = (null != paymentModeData
				&& PaymentModeEnum.VOUCHERPAYMENTMODE.getCode().equals(paymentModeData.getCode()));
		return isVoucherModeApplied;
	}

	/**
	 * This method is used to check COD payment mode
	 *
	 * @param cartData
	 * @return boolean
	 */
	private boolean isMultiPaymentMode(final CartData cartData)
	{
		final PaymentModeData paymentModeData = cartData.getPaymentMode();
		final boolean isMultiPayment = (null != paymentModeData
				&& PaymentModeEnum.MULTIPAYMENTMODE.getCode().equals(paymentModeData.getCode()));
		return isMultiPayment;
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

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SUMMARY);
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
	 * @return the sparDefaultPaymentFacade
	 */
	public SparDefaultPaymentFacade getSparDefaultPaymentFacade()
	{
		return sparDefaultPaymentFacade;
	}

	/**
	 * Setter
	 *
	 * @param sparDefaultPaymentFacade
	 *           the sparDefaultPaymentFacade to set
	 */
	public void setSparDefaultPaymentFacade(final SparDefaultPaymentFacade sparDefaultPaymentFacade)
	{
		this.sparDefaultPaymentFacade = sparDefaultPaymentFacade;
	}

}
