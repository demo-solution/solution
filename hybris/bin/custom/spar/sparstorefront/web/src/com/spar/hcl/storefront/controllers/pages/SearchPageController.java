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

import de.hybris.platform.acceleratorcms.model.components.SearchBoxComponentModel;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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

import com.sap.security.core.server.csi.XSSEncoder;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;



@Controller
@Scope("tenant")
@RequestMapping("/search")
public class SearchPageController extends AbstractSearchPageController
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SearchPageController.class);

	private static final String COMPONENT_UID_PATH_VARIABLE_PATTERN = "{componentUid:.*}";
	private static final String FACET_SEPARATOR = ":";
	private static final String DELIMETER = "_";
	private static final String PRICE_UNAVAILABLE = "sparstorefront.product.price.unavailable";

	private static final String SEARCH_CMS_PAGE_ID = "search";
	private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "searchBreadcrumbBuilder")
	private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;


	@RequestMapping(method = RequestMethod.GET, params = "!q")
	public String textSearch(@RequestParam(value = "text", defaultValue = "") final String searchText,
			final HttpServletRequest request, final Model model) throws CMSItemNotFoundException
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
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		if (StringUtils.isNotBlank(searchText))
		{
			final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);
			final String encodedSearchText = XSSFilterUtil.filter(searchText);

			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();
			searchQueryData.setValue(encodedSearchText);
			searchState.setQuery(searchQueryData);

			ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
					pageableData);
			searchPageData = encodeSearchPageData(searchPageData);
			if (searchPageData == null)
			{
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
			}
			else if (searchPageData.getKeywordRedirectUrl() != null)
			{
				// if the search engine returns a redirect, just
				return "redirect:" + searchPageData.getKeywordRedirectUrl();
			}
			else if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
			{
				model.addAttribute("searchPageData", searchPageData);
				storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
				updatePageTitle(encodedSearchText, model);
			}
			else
			{
				storeContinueUrl(request);
				populateModel(model, searchPageData, ShowMode.Page);
				storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
				updatePageTitle(encodedSearchText, model);
			}
			model.addAttribute("userLocation", customerLocationService.getUserLocation());
			getRequestContextData(request).setSearch(searchPageData);
			if (searchPageData != null)
			{
				model.addAttribute(
						WebConstants.BREADCRUMBS_KEY,
						searchBreadcrumbBuilder.getBreadcrumbs(null, encodedSearchText,
								CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
			}
		}
		else
		{
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
		}
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);

		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(
				"search.meta.description.results", null, "search.meta.description.results", getI18nService().getCurrentLocale())
				+ " "
				+ searchText
				+ " "
				+ getMessageSource().getMessage("search.meta.description.on", null, "search.meta.description.on",
						getI18nService().getCurrentLocale()) + " " + getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewForPage(model);
	}

	@RequestMapping(method = RequestMethod.GET, params = "q")
	public String refineSearch(@RequestParam("q") String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "text", required = false) final String searchText, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		final String warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		if (StringUtils.isNotEmpty(searchQuery))
		{
			if (searchQuery.contains("benefitType") && !searchQuery.contains("benefitType_"))
			{
				searchQuery = searchQuery.replace("benefitType", "benefitType_" + warehouseCode);
			}
			if (searchQuery.contains("combiOffer") && !searchQuery.contains("combiOffer_"))
			{
				searchQuery = searchQuery.replace("combiOffer", "combiOffer_" + warehouseCode);
			}
			if (searchQuery.contains("relevance") && !searchQuery.contains("relevance_"))
			{
				searchQuery = searchQuery.replace("relevance", "relevance_" + warehouseCode);
			}
		}
		else
		{
			searchQuery = "relevance_" + warehouseCode;
		}

		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());

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
		model.addAttribute("sparCities", storeFinderFacadeInterface.getSparCities());
		populateModel(model, searchPageData, showMode);
		model.addAttribute("userLocation", customerLocationService.getUserLocation());

		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
		}
		else
		{
			storeContinueUrl(request);
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
		}
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchPageData));
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());

		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(
				"search.meta.description.results", null, "search.meta.description.results", getI18nService().getCurrentLocale())
				+ " "
				+ searchText
				+ " "
				+ getMessageSource().getMessage("search.meta.description.on", null, "search.meta.description.on",
						getI18nService().getCurrentLocale()) + " " + getSiteName());

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewForPage(model);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final int pageSize)
	{
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		return encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
	}

	@ResponseBody
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public SearchResultsData<ProductData> jsonSearchResults(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());
		final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
		searchResultsData.setResults(searchPageData.getResults());
		searchResultsData.setPagination(searchPageData.getPagination());
		return searchResultsData;
	}

	@ResponseBody
	@RequestMapping(value = "/facets", method = RequestMethod.GET)
	public FacetRefinement<SearchStateData> getFacets(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
				createPageableData(page, getSearchPageSize(), sortCode, showMode));
		final List<FacetData<SearchStateData>> facets = refineFacets(searchPageData.getFacets(),
				convertBreadcrumbsToFacets(searchPageData.getBreadcrumbs()));
		final FacetRefinement<SearchStateData> refinement = new FacetRefinement<>();
		refinement.setFacets(facets);
		refinement.setCount(searchPageData.getPagination().getTotalNumberOfResults());
		refinement.setBreadcrumbs(searchPageData.getBreadcrumbs());
		return refinement;
	}

	@ResponseBody
	@RequestMapping(value = "/autocomplete/" + COMPONENT_UID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public AutocompleteResultData getAutocompleteSuggestions(@PathVariable final String componentUid,
			@RequestParam("term") final String term) throws CMSItemNotFoundException
	{
		final AutocompleteResultData resultData = new AutocompleteResultData();

		final SearchBoxComponentModel component = (SearchBoxComponentModel) cmsComponentService.getSimpleCMSComponent(componentUid);

		if (component.isDisplaySuggestions())
		{
			resultData.setSuggestions(subList(productSearchFacade.getAutocompleteSuggestions(term), component.getMaxSuggestions()));
		}

		if (component.isDisplayProducts())
		{
			resultData.setProducts(subListProduct(productSearchFacade.textSearch(term).getResults(), component.getMaxProducts(),
					term));
		}

		return resultData;
	}

	protected <E> List<E> subList(final List<E> list, final int maxElements)
	{
		if (CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}

		if (list.size() > maxElements)
		{
			return list.subList(0, maxElements);
		}

		return list;
	}

	private <E> List<ProductData> subListProduct(final List<ProductData> list, final int maxElements, final String term)
	{
		if (CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}
		final Iterator<ProductData> itr = list.iterator();
		final List<ProductData> productList = new ArrayList<ProductData>();
		while (itr.hasNext())
		{
			final ProductData product = itr.next();
			boolean variantAdded = false;
			for (final VariantOptionData variant : product.getVariantOptions())
			{
				if (null != variant.getName()
						&& null != variant.getCode()
						&& (variant.getName().toUpperCase().trim().contains(term.toUpperCase().trim()) || variant.getCode()
								.toUpperCase().trim().contains(term.toUpperCase().trim())))
				{
					variantAdded = true;
					final ProductData productVariant = new ProductData();
					productVariant.setName(variant.getName());
					productVariant.setPrice(variant.getPriceData());
					productVariant.setUrl(variant.getUrl());
					productVariant.setImages(variant.getImages());
					productVariant.setCode(variant.getCode());
					productList.add(productVariant);
				}
			}
			if (!variantAdded)
			{
				productList.add(product);
			}
		}

		if (productList.size() > maxElements)
		{

			return productList.subList(0, maxElements);
		}

		return productList;
	}

	protected void updatePageTitle(final String searchText, final Model model)
	{
		storeContentPageTitleInModel(
				model,
				getPageTitleResolver().resolveContentPageTitle(
						getMessageSource().getMessage("search.meta.title", null, "search.meta.title",
								getI18nService().getCurrentLocale())
								+ " " + searchText));
	}

	@Override
	protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode)
	{
		super.populateModel(model, searchPageData, showMode);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> encodeSearchPageData(
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final SearchStateData currentQuery = searchPageData.getCurrentQuery();

		if (currentQuery != null)
		{
			try
			{
				final SearchQueryData query = currentQuery.getQuery();
				final String encodedQueryValue = XSSEncoder.encodeHTML(query.getValue());
				query.setValue(encodedQueryValue);
				currentQuery.setQuery(query);
				searchPageData.setCurrentQuery(currentQuery);
				searchPageData.setFreeTextSearch(XSSEncoder.encodeHTML(searchPageData.getFreeTextSearch()));

				final List<FacetData<SearchStateData>> facets = searchPageData.getFacets();
				if (CollectionUtils.isNotEmpty(facets))
				{
					for (final FacetData<SearchStateData> facetData : facets)
					{
						final List<FacetValueData<SearchStateData>> facetValueDatas = facetData.getValues();
						if (CollectionUtils.isNotEmpty(facetValueDatas))
						{
							for (final FacetValueData<SearchStateData> facetValueData : facetValueDatas)
							{
								final SearchStateData facetQuery = facetValueData.getQuery();
								final SearchQueryData queryData = facetQuery.getQuery();
								final String queryValue = queryData.getValue();
								if (StringUtils.isNotBlank(queryValue))
								{
									final String[] queryValues = queryValue.split(FACET_SEPARATOR);
									final StringBuilder queryValueBuilder = new StringBuilder();
									queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
									for (int i = 1; i < queryValues.length; i++)
									{
										queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
									}
									queryData.setValue(queryValueBuilder.toString());
								}
							}
						}

						final List<FacetValueData<SearchStateData>> topFacetValueDatas = facetData.getTopValues();
						if (CollectionUtils.isNotEmpty(topFacetValueDatas))
						{
							for (final FacetValueData<SearchStateData> topFacetValueData : topFacetValueDatas)
							{
								final SearchStateData facetQuery = topFacetValueData.getQuery();
								final SearchQueryData queryData = facetQuery.getQuery();
								final String queryValue = queryData.getValue();
								if (StringUtils.isNotBlank(queryValue))
								{
									final String[] queryValues = queryValue.split(FACET_SEPARATOR);
									final StringBuilder queryValueBuilder = new StringBuilder();
									queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
									for (int i = 1; i < queryValues.length; i++)
									{
										queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
									}
									queryData.setValue(queryValueBuilder.toString());
								}
							}
						}
					}
				}

			}
			catch (final UnsupportedEncodingException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error occured during Encoding the Search Page data values", e);
				}
			}
		}
		return searchPageData;
	}

	/**
	 * This method is used to get the Variant details.
	 *
	 * @param code
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value = "/plp/display/variant/details", method = RequestMethod.POST)
	public String getProductPrice(@RequestParam(value = "productCode") final String code)
	{
		final List<ProductOption> option = Arrays.asList(ProductOption.PRICE, ProductOption.STOCK, ProductOption.DESCRIPTION);
		final ProductData productData = productFacade.getProductForCodeAndOptions(code, option);
		final String unavailablePrice = Config.getString(PRICE_UNAVAILABLE, "Unavailable");
		final String unitMRP = null == productData.getPrice() ? unavailablePrice : productData.getUnitMRP().getFormattedValue();
		final String saving = null == productData.getOfferType() ? (null == productData.getPrice() ? unavailablePrice
				: getProductSavings(productData).getFormattedValue()) : productData.getPromoMessage();
		return productData.getPrice().getFormattedValue() + DELIMETER + productData.getStock().getStockLevelStatus().getCode()
				+ DELIMETER + productData.getStock().getStockLevel() + DELIMETER + productData.getERPVariantDescription() + DELIMETER
				+ unitMRP + DELIMETER + saving + DELIMETER + productData.getName() + DELIMETER + getPrimaryImageUrl(productData)
				+ DELIMETER + productData.getUrl() + DELIMETER + productData.getOfferType()+ DELIMETER + productData.getProductOnBogo()
				+DELIMETER + productData.getPromotionDiscount();
	}

	/**
	 * This method is used to get Product Savings in Product Data in PriceData format
	 *
	 * @param productData
	 */
	protected PriceData getProductSavings(final ProductData productData)
	{
		if (null != productData.getUnitMRP() && null != productData.getPrice())
		{
			productData.setSavings(getPriceDataFactory().create(PriceDataType.BUY,
					productData.getUnitMRP().getValue().subtract(productData.getPrice().getValue()),
					getCommonI18NService().getCurrentCurrency()));
		}
		return productData.getSavings();
	}

	/**
	 * Get primary image url for a product
	 *
	 * @param product
	 * @return String
	 */
	protected String getPrimaryImageUrl(final ProductData product)
	{
		final String IMAGE_FORMAT = "sparstorefront.storefront.plp.imageformat";
		final String DEFAULT_IMAGE_FORMAT = "plpProduct";

		if (product != null)
		{
			final Collection<ImageData> images = product.getImages();
			if (images != null && !images.isEmpty())
			{
				for (final ImageData image : images)
				{
					if (ImageDataType.PRIMARY.equals(image.getImageType())
							&& Config.getString(IMAGE_FORMAT, DEFAULT_IMAGE_FORMAT).equals(image.getFormat()))
					{
						return image.getUrl();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getter for commonI18NService
	 *
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Setter for commonI18NService
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Getter for PriceDataFactory
	 *
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Setter for PriceDataFactory
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}


}
