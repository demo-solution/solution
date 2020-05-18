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
package com.spar.hcl.storefront.controllers.misc;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartOrderForm;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.converters.populator.GroupCartModificationListPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;


/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
@Scope("tenant")
public class AddToCartController extends AbstractController
{
	private static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
	private static final String ERROR_MSG_TYPE = "errorMsg";
	private static final String MULTID_ERROR_MSGS_TYPE = "multidErrorMsgs";
	private static final String QUANTITY_INVALID_BINDING_MESSAGE_KEY = "basket.error.quantity.invalid.binding";
	private static final String SHOWN_PRODUCT_COUNT = "sparstorefront.storefront.minicart.shownProductCount";
	private static final String QUANTITY = "quantity";
	private static final String LOGIN_VIA = "loginVia";
	protected static final Logger LOG = Logger.getLogger(AddToCartController.class);

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "groupCartModificationListPopulator")
	private GroupCartModificationListPopulator groupCartModificationListPopulator;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;


	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/cart/add", method = RequestMethod.POST, produces = "application/json")
	public String addToCart(@RequestParam("productCodePost") final String code, final Model model,
			@Valid final AddToCartForm form, final BindingResult bindingErrors) throws JaloPriceFactoryException,
			VoucherOperationException
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("homePageForm", new HomePageForm());
		if (bindingErrors.hasErrors())
		{
			return getViewWithBindingErrorMessages(model, bindingErrors);
		}

		/* Code change start for voucher story */

		final List<VoucherData> voucherCode = sparVoucherFacade.getVouchersForCart();
		VoucherData voucherCodeForCart = null;
		final Iterator itr = voucherCode.iterator();
		while (itr.hasNext())
		{
			voucherCodeForCart = (VoucherData) itr.next();
			break;
		}
		if (CollectionUtils.isNotEmpty(voucherCode))
		{
			sparVoucherFacade.releaseSparVoucher(voucherCodeForCart.getVoucherCode());
			final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
			sparVoucherFacade.updateCartWithDiscountWithoutCartCalculation(cartData, voucherCodeForCart.getVoucherCode());
		}

		/* Changes end here for voucher story */
		final long qty = form.getQty();
		if (null == sessionService.getAttribute("canAddProductToCart"))
		{
			model.addAttribute(ERROR_MSG_TYPE, "select.userlocation.toaddcart");
			return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
		}

		final ProductData productData = productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC));
		model.addAttribute("product", productData);
		if (qty <= 0)
		{
			model.addAttribute(ERROR_MSG_TYPE, "basket.error.quantity.invalid");
			model.addAttribute(QUANTITY, Long.valueOf(0L));
		}
		else if (null != productData.getMaxOrderQuantity() && qty > productData.getMaxOrderQuantity().longValue())
		{
			model.addAttribute(ERROR_MSG_TYPE, "product.maxquantity.addtocart");
			return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
		}
		else
		{
			try
			{
				final CartModificationData cartModification = cartFacade.addToCart(code, qty);
				// setting the delivery mode here on the basis of session deliveryType

				model.addAttribute(QUANTITY, Long.valueOf(cartModification.getQuantityAdded()));
				model.addAttribute("entry", cartModification.getEntry());
				model.addAttribute("cartCode", cartModification.getCartCode());

				if (cartModification.getQuantityAdded() == 0L)
				{
					model.addAttribute(ERROR_MSG_TYPE, "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
					// if order threshold is less than cart quanity then display please do not add more than 25 msg

					if ((null != productData.getMaxOrderQuantity() && productData.getMaxOrderQuantity() < qty)
							&& "noStock".equals(cartModification.getStatusCode()))
					{

						model.addAttribute(ERROR_MSG_TYPE,
								"spar.basket.information1.quantity.noItemsAdded." + cartModification.getStatusCode());


					}

				}// lower quantity added cases 1 due to insuficcent stock 2 due to order thresh hold qty but have full of stock
				else if (cartModification.getQuantityAdded() < qty)
				{
					model.addAttribute(ERROR_MSG_TYPE, "spar.basket.information1.quantity.reducedNumberOfItemsAdded."
							+ cartModification.getStatusCode());



				}
				//this line is commented due to improve performance as mini cart on mouse hover is deprecated in SPAR
				//sparCartFacade.getSessionCart();
			}
			catch (final CommerceCartModificationException ex)
			{
				model.addAttribute(ERROR_MSG_TYPE, "basket.error.occurred");
				model.addAttribute(QUANTITY, Long.valueOf(0L));
			}
		}
		if (null != sessionService.getAttribute(LOGIN_VIA))
		{
			sparCartFacade.updateSocialMediaToCart(sessionService.getAttribute(LOGIN_VIA).toString());
			sessionService.setAttribute(LOGIN_VIA, null);
			sessionService.removeAttribute(LOGIN_VIA);
		}
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected String getViewWithBindingErrorMessages(final Model model, final BindingResult bindingErrors)
	{
		for (final ObjectError error : bindingErrors.getAllErrors())
		{
			if (isTypeMismatchError(error))
			{
				model.addAttribute(ERROR_MSG_TYPE, QUANTITY_INVALID_BINDING_MESSAGE_KEY);
			}
			else
			{
				model.addAttribute(ERROR_MSG_TYPE, error.getDefaultMessage());
			}
		}
		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected boolean isTypeMismatchError(final ObjectError error)
	{
		return error.getCode().equals(TYPE_MISMATCH_ERROR_CODE);
	}

	@RequestMapping(value = "/cart/addGrid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public final String addGridToCart(@RequestBody final AddToCartOrderForm form, final Model model)
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("homePageForm", new HomePageForm());
		final Set<String> multidErrorMsgs = new HashSet<String>();
		final List<CartModificationData> modificationDataList = new ArrayList<CartModificationData>();

		for (final OrderEntryData cartEntry : form.getCartEntries())
		{
			if (!isValidProductEntry(cartEntry))
			{
				LOG.error("Error processing entry");
			}
			else if (!isValidQuantity(cartEntry))
			{
				multidErrorMsgs.add("basket.error.quantity.invalid");
			}
			else
			{
				try
				{
					final long qty = cartEntry.getQuantity().longValue();
					final CartModificationData cartModificationData = cartFacade.addToCart(cartEntry.getProduct().getCode(), qty);
					if (cartModificationData.getQuantityAdded() == 0L)
					{
						multidErrorMsgs.add("basket.information.quantity.noItemsAdded." + cartModificationData.getStatusCode());
					}
					else if (cartModificationData.getQuantityAdded() < qty)
					{
						multidErrorMsgs.add("basket.information.quantity.reducedNumberOfItemsAdded."
								+ cartModificationData.getStatusCode());
					}

					modificationDataList.add(cartModificationData);

				}
				catch (final CommerceCartModificationException ex)
				{
					multidErrorMsgs.add("basket.error.occurred");
				}
			}
		}

		if (CollectionUtils.isNotEmpty(modificationDataList))
		{
			groupCartModificationListPopulator.populate(null, modificationDataList);

			model.addAttribute("modifications", modificationDataList);
		}

		if (CollectionUtils.isNotEmpty(multidErrorMsgs))
		{
			model.addAttribute("multidErrorMsgs", multidErrorMsgs);
		}

		model.addAttribute("numberShowing", Integer.valueOf(Config.getInt(SHOWN_PRODUCT_COUNT, 3)));


		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected boolean isValidProductEntry(final OrderEntryData cartEntry)
	{
		return cartEntry.getProduct() != null && StringUtils.isNotBlank(cartEntry.getProduct().getCode());
	}

	protected boolean isValidQuantity(final OrderEntryData cartEntry)
	{
		return cartEntry.getQuantity() != null && cartEntry.getQuantity().longValue() >= 1L;
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
}
