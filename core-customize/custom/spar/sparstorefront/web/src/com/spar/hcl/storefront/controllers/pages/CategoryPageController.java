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


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCategoryPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Controller for a category page
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/**/c")
public class CategoryPageController extends AbstractCategoryPageController
{
	protected static final Logger LOG = Logger.getLogger(CategoryPageController.class);

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String category(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws UnsupportedEncodingException
	{
		final String warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
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
		if (StringUtils.isNotEmpty(searchQuery))
		{
			if (searchQuery.contains("relevance") && !searchQuery.contains("relevance_"))
			{
				searchQuery = searchQuery.replace("relevance", "relevance_" + warehouseCode);
			}
		}
		else
		{
			searchQuery = ":relevance_" + warehouseCode;
		}


		return performSearchAndGetResultsPage(categoryCode, searchQuery, page, showMode, sortCode, model, request, response);
	}

	@ResponseBody
	@RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/facets", method = RequestMethod.GET)
	public FacetRefinement<SearchStateData> getFacets(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException
	{
		return performSearchAndGetFacets(categoryCode, searchQuery, page, showMode, sortCode);
	}

	/**
	 * This method is overriden to remove cross site scripting filters for apostrophe. Facets vlues with apostrophe was
	 * not working.
	 */
	@Override
	protected String performSearchAndGetResultsPage(final String categoryCode, final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final Model model, final HttpServletRequest request,
			final HttpServletResponse response) throws UnsupportedEncodingException
	{
		final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);

		final String redirection = checkRequestUrl(request, response, getCategoryModelUrlResolver().resolve(category));
		if (StringUtils.isNotEmpty(redirection))
		{
			return redirection;
		}

		final CategoryPageModel categoryPage = getCategoryPage(category);

		final CategorySearchEvaluator categorySearch = new CategorySearchEvaluator(categoryCode, XSSFilterUtil.filter(searchQuery),
				page, showMode, sortCode, categoryPage);
		categorySearch.doSearch();

		final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchPageData = categorySearch
				.getSearchPageData();
		final boolean showCategoriesOnly = categorySearch.isShowCategoriesOnly();

		storeCmsPageInModel(model, categorySearch.getCategoryPage());
		storeContinueUrl(request);

		populateModel(model, searchPageData, showMode);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getSearchBreadcrumbBuilder().getBreadcrumbs(categoryCode, searchPageData));
		model.addAttribute("showCategoriesOnly", Boolean.valueOf(showCategoriesOnly));
		model.addAttribute("categoryName", category.getName());
		//model.addAttribute("pageType", PageType.Category);
		model.addAttribute("pageType", PageType.CATEGORY.name());
		model.addAttribute("userLocation", getCustomerLocationService().getUserLocation());

		updatePageTitle(category, model);

		final RequestContextData requestContextData = getRequestContextData(request);
		requestContextData.setCategory(category);
		requestContextData.setSearch(searchPageData);

		if (searchQuery != null)
		{
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_FOLLOW);
		}

		List<String> keywords = new ArrayList<>();
		category.getKeywords().stream().forEach(keywordModel -> keywords.add(keywordModel.getKeyword()));
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(keywords);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(category.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewPage(categorySearch.getCategoryPage());

	}



	@ResponseBody
	@RequestMapping(value = CATEGORY_CODE_PATH_VARIABLE_PATTERN + "/results", method = RequestMethod.GET)
	public SearchResultsData<ProductData> getResults(@PathVariable("categoryCode") final String categoryCode,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws UnsupportedEncodingException
	{
		return performSearchAndGetResultsData(categoryCode, searchQuery, page, showMode, sortCode);
	}
}

/**
 * Class file for cross site scripting filter
 *
 * @author rohan_c
 *
 */
class XSSFilterUtil
{
	/**
	 * This method sanitizes the value and remove characters to avoid cross site scripting forgery. Apostrophe handling
	 * has been removed from sanitizing the string.
	 *
	 * @param value
	 *           to be sanitized
	 * @return sanitized content
	 */
	public static String filter(final String value)
	{
		if (value == null)
		{
			return null;
		}
		String sanitized = value;
		sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
		sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		return sanitized;
	}
}
