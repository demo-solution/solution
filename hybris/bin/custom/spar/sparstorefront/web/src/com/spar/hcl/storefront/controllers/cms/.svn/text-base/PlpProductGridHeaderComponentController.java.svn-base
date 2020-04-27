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
package com.spar.hcl.storefront.controllers.cms;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.platform.cms2lib.model.PlpProductGridHeaderComponentModel;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.controllers.pages.HomePageForm;


/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("PlpProductGridHeaderComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.PlpProductGridHeaderComponent)
public class PlpProductGridHeaderComponentController extends AbstractCMSComponentController<PlpProductGridHeaderComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;
	@Resource(name = "sessionService")
	SessionService sessionService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final PlpProductGridHeaderComponentModel component)
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

		final List<ProductData> products = new ArrayList<>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));

		model.addAttribute("title", component.getTitle());
		model.addAttribute("productData", products);
	}

	protected List<ProductData> collectLinkedProducts(final PlpProductGridHeaderComponentModel component)
	{
		final List<ProductData> products = new ArrayList<>();

		for (final ProductModel productModel : component.getProducts())
		{
			if (storeFinderFacadeInterface.isProductValidForPOS(productModel))
			{
				products.add(productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS));
			}
		}

		for (final CategoryModel categoryModel : component.getCategories())
		{
			for (final ProductModel productModel : categoryModel.getProducts())
			{
				products.add(productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS));
			}
		}

		return products;
	}

	protected List<ProductData> collectSearchProducts(final PlpProductGridHeaderComponentModel component)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(component.getSearchQuery());
		final String categoryCode = component.getCategoryCode();

		if (searchQueryData.getValue() != null && categoryCode != null)
		{
			final SearchStateData searchState = new SearchStateData();
			searchState.setQuery(searchQueryData);

			final PageableData pageableData = new PageableData();
			pageableData.setPageSize(100); // Limit to 100 matching results

			return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
		}

		return Collections.emptyList();
	}
}
