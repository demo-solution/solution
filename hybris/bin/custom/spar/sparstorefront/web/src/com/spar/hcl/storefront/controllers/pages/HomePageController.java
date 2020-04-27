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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.core.mobileverification.service.SparMobileVerificationService;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.storelocator.data.WarehouseData;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.storefront.controllers.ControllerConstants;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ContentPageBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/")
public class HomePageController extends AbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(HomePageController.class);
	
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

	@Resource(name = "contentPageBreadcrumbBuilder")
	private ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder;
	
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	
	@Resource(name = "sparMobileVerificationService")
	private SparMobileVerificationService sparMobileVerificationService;
	
	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestParam(value = "logout", defaultValue = "false") final boolean logout, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (logout)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, "account.confirmation.signout.title");
			return REDIRECT_PREFIX + ROOT;
		}
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();

		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		else
		{
			final String selectedStore = storeFinderFacadeInterface.getDefaultStore().getName();
			model.addAttribute("selectedStore", selectedStore);
			sessionService.setAttribute("selectedStore", selectedStore);
			homePageForm.setStore(selectedStore);
		}

		if (null != (String) sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String) sessionService.getAttribute("cityName"));
		}
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		model.addAttribute("customerData", customerData);

		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("homePageForm", homePageForm);
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_FOLLOW);
		updatePageTitle(model, getContentPageForLabelOrId(null));

		return getViewForPage(model);
	}

	@RequestMapping(value = "/setStore", method = RequestMethod.POST)
	public String updateStoreValue(final Model model, final HomePageForm form, final BindingResult bindingResult,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (null != form.getStore())
		{
			sessionService.setAttribute("selectedStore", form.getStore());
			setWarehouseForPOS();
		}
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("homePageForm", form);
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, getContentPageForLabelOrId(null));
		return getViewForPage(model);
	}

	/* Code Change start by sumit for store locator */

	/**
	 * This method is used to set warehouse against the sectedStore
	 */
	public void setWarehouseForPOS()
	{
		final SessionService sessionService = getSessionService();
		final String selectedstore = sessionService.getAttribute("selectedStore");
		final WarehouseData warehouseData = storeFinderFacadeInterface.getWarehouse(selectedstore);
		sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
	}

	@RequestMapping(value = "/setConfirmedStore", method = RequestMethod.POST)
	public String updateStoreValues(@RequestParam(value = "deliveryCityName") final String deliveryCityName, final Model model,
			final HomePageForm form, final BindingResult bindingResult, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (null != form.getStore())
		{
			sessionService.setAttribute("selectedStore", form.getStore());
			sessionService.setAttribute("selectedType", form.getDeliveryType());
			sessionService.setAttribute("canAddProductToCart", "true");
			setWarehouseForPOS();
			setDeliveryMode();
			setOrderPointOfServiceAndWarehouse();
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
			try
			{
				sparVoucherFacade.releaseSparVoucher(voucherCodeForCart.getVoucherCode());
				final CartData cartData = getSparCheckoutFacade().getCheckoutCart();
				sparVoucherFacade.doCartCalculation(cartData, voucherCodeForCart.getVoucherCode());
			}
			catch (final VoucherOperationException e)
			{
				LOG.info("VoucherOperationException on location set");
			}
		}

		setDeliveryCityName(deliveryCityName, model, form);
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("homePageForm", form);
		storeCmsPageInModel(model, getContentPageForLabelOrId(null));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
		updatePageTitle(model, getContentPageForLabelOrId(null));
		return getViewForPage(model);
	}

	/**
	 * This method is used to set the delivery city name in model and form
	 *
	 * @param deliveryCityName
	 * @param model
	 * @param form
	 */
	protected void setDeliveryCityName(final String deliveryCityName, final Model model, final HomePageForm form)
	{
		if (null != deliveryCityName && StringUtils.isNotEmpty(deliveryCityName))
		{
			sessionService.removeAttribute("cityName");
			sessionService.setAttribute("cityName", deliveryCityName);
			model.addAttribute("cityName", sessionService.getAttribute("cityName"));
			form.setDeliveryCityName(deliveryCityName);
		}
	}

	/**
	 * This method is is used to save POS & warehouse in order and invokes recalculation of Cart
	 */
	protected void setOrderPointOfServiceAndWarehouse()
	{
		final String pointOfService = (String) sessionService.getAttribute("selectedStore");
		sparCartFacade.setOrderPointOfService(pointOfService);
		sparCartFacade.setOrderWarehouse(pointOfService, true);
	}

	/**
	 * This method is used to set DeliveryMode in the cart
	 */
	protected void setDeliveryMode()
	{
		// setting the delivery mode
		final String selectedType = sessionService.getAttribute("selectedType");

		if (null != selectedType)
		{
			if (DeliveryTypeEnum.HD.getCode().equalsIgnoreCase(selectedType))
			{
				sparCartFacade.setDeliveryMode("standard-gross");
			}

			else if (DeliveryTypeEnum.CNC.getCode().equalsIgnoreCase(selectedType))
			{
				sparCartFacade.setDeliveryMode("pickup");
			}
		}
	}

	// Pavan


	@RequestMapping(value = "/giftingzone")
	public String getGiftingZonePage(final Model model) throws CMSItemNotFoundException
	{
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
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		model.addAttribute("homePageForm", homePageForm);
		final ContentPageModel pageForRequest = getCmsPageService().getPageForLabel("/giftingzone");
		storeCmsPageInModel(model, pageForRequest);
		setUpMetaDataForContentPage(model, pageForRequest);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getContentPageBreadcrumbBuilder().getBreadcrumbs(pageForRequest));
		return ControllerConstants.Views.Pages.GiftSolution.GiftingZoneTagPage;
	}


	/* Change end here */

	/**
	 * @return the contentPageBreadcrumbBuilder
	 */
	public ContentPageBreadcrumbBuilder getContentPageBreadcrumbBuilder()
	{
		return contentPageBreadcrumbBuilder;
	}

	/**
	 * @param contentPageBreadcrumbBuilder
	 *           the contentPageBreadcrumbBuilder to set
	 */
	public void setContentPageBreadcrumbBuilder(final ContentPageBreadcrumbBuilder contentPageBreadcrumbBuilder)
	{
		this.contentPageBreadcrumbBuilder = contentPageBreadcrumbBuilder;
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
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
