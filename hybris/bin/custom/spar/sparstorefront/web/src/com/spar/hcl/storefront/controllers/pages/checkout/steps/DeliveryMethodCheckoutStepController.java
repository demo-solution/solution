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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.storefront.checkout.steps.validation.impl.DeliverySlotValidator;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.DeliverySlotForm;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;



@Controller
@RequestMapping(value = "/checkout/multi/delivery-method")
public class DeliveryMethodCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(DeliveryMethodCheckoutStepController.class);
	private final static String DELIVERY_METHOD = "delivery-method";
	final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;

	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "deliverySlotValidator")
	private DeliverySlotValidator deliverySlotValidator;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = DELIVERY_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		//Reset final cnc contact for cartmodel
		if (!StringUtils.equalsIgnoreCase(cartData.getDeliveryMode().getCode(), "pickup"))
		{
			sparCartFacade.resetCncPhoneNumber();
			cartData.setCncPhone(StringUtils.EMPTY);
		}

		// Try to set default delivery mode
		getCheckoutFacade().setDeliveryModeIfAvailable();
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		final DeliverySlotForm deliverySlotForm = new DeliverySlotForm();
		WarehouseModel warehouseModel = null;
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
		try
		{
			warehouseModel = storeFinderServiceInterface.getWarehouse();
		}
		catch (final Exception e1)
		{
			LOG.error("No Warehouse association found for the Store", e1);
			GlobalMessages.addErrorMessage(model, "checkout.multi.deliveryMethod.nowarehouse");
		}
		
		final Map<Date, List<DeliverySlotData>> map = sparCartFacade.getDeliverySlotsMap(cartData.getDeliveryMode().getCode());
		
		final String selectedClickNCollect = sessionService.getAttribute("selectedStore");
		final PointOfServiceData collectionCenter = storeFinderFacadeInterface.getPosStore(selectedClickNCollect);
		final boolean whetherEmployee = sparCustomerFacade.isCustomerEmployee();
		model.addAttribute("whetherEmployee", whetherEmployee);
		model.addAttribute("selectedCollectionCenter", collectionCenter);
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("map", map);
		model.addAttribute("homePageForm", homePageForm);
		model.addAttribute("deliverySlotForm", deliverySlotForm);
		model.addAttribute("deliveryMode", cartData.getDeliveryMode().getCode());

		model.addAttribute("cartData", cartData);
		model.addAttribute("deliveryMethods", getCheckoutFacade().getSupportedDeliveryModes());
		this.prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return ControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
	}

	/**
	 * This method gets called when the "Use Selected Delivery Method" button is clicked. It sets the selected delivery
	 * mode on the checkout facade and reloads the page highlighting the selected delivery Mode.
	 *
	 * @param selectedDeliveryMethod
	 *           - the id of the delivery mode.
	 * @return - a URL to the page to load.
	 * @throws CMSItemNotFoundException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doSelectDeliveryMode(final DeliverySlotForm deliverySlotForm, final Model model,
			final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CMSItemNotFoundException,
			ParseException
	{
		if (StringUtils.isEmpty(deliverySlotForm.getDeliverySlot()) || null == deliverySlotForm.getDeliverySlot())
		{
			getDeliverySlotValidator().validate(deliverySlotForm, bindingResult);
		}
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "checkout.summary.deliveryMode.deliverySlot");
			return enterStep(model, redirectModel);
		}

		// make logic for order level checks

		final List<String> statusCodes = sparCartFacade.checkCartQualifyForOrderLimits();
		final Double minOrderLimit = baseStoreService.getCurrentBaseStore().getMinOrderLimit();
		final Double minCCOrderLimit = baseStoreService.getCurrentBaseStore().getMinCCOrderLimit();
		final Double minHDOrderLimit = baseStoreService.getCurrentBaseStore().getMinHDOrderLimit();
		final Double[] limits = new Double[3];
		limits[0] = minOrderLimit;
		limits[1] = minCCOrderLimit;
		limits[2] = minHDOrderLimit;
		// validating cart on the threshold basis.
		//code change for voucher story
		if (!statusCodes.isEmpty() && CollectionUtils.isEmpty(sparVoucherFacade.getSparVouchersForCart()))
		{
			if ("Min_Ord".equals(statusCodes.get(0)))
			{
				//GlobalMessages.addErrorMessage(model, "order.min.value");
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.value", limits);

				//GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "order.min.value");
				return enterStep(model, redirectModel);
			}
			if ("Min_Ord_CNC".equals(statusCodes.get(0)))
			{
				//GlobalMessages.addErrorMessage(model, "order.min.CNC.value");
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.CNC.value", limits);
				//GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "order.min.CNC.value");
				return enterStep(model, redirectModel);
			}

			if ("Min_Ord_HD".equals(statusCodes.get(0)))
			{
				//GlobalMessages.addErrorMessage(model, "order.min.HD.value");
				GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "order.min.HD.value", limits);

				//GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "order.min.HD.value");
				return enterStep(model, redirectModel);
			}

		}
		if (StringUtils.isNotEmpty(deliverySlotForm.getDeliveryMethod()))
		{
			getCheckoutFacade().setDeliveryMode(deliverySlotForm.getDeliveryMethod());
		}
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		if (null != deliverySlotForm.getDeliverySlot())
		{
			final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			final String[] splitDateAndSlot = deliverySlotForm.getDeliverySlot().split("/");
			final Date orderDeliveryDate = dateFormatter.parse(splitDateAndSlot[0]);
			final DeliverySlotModel orderDeliverySlot = storeFinderServiceInterface.getOrderDeliverySLot(splitDateAndSlot[1]);
			getSparCheckoutFacade().saveDeliverySlotAndDate(orderDeliveryDate, orderDeliverySlot);
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

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(DELIVERY_METHOD);
	}

	protected DeliverySlotValidator getDeliverySlotValidator()
	{
		return deliverySlotValidator;
	}

	/**
	 * @return the sparCheckoutFacade
	 */
	public SparDefaultCheckoutFacade getSparCheckoutFacade()
	{
		return sparCheckoutFacade;
	}
}
