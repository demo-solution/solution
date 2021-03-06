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
package com.spar.hcl.v2.controller;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.core.inclusionexclusion.service.InclusionExclusionAreaListService;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExRequest;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.v2.helper.StoresHelper;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;


/**
 * @pathparam storeId Store identifier (currently store name)
 */
@Controller
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
public class StoresController extends BaseController
{
	protected static final Logger LOG = Logger.getLogger(StoresController.class);
	private static final String DEFAULT_SEARCH_RADIUS_METRES = "100000.0";
	private static final String DEFAULT_ACCURACY = "0.0";
	@Resource(name = "storesHelper")
	private StoresHelper storesHelper;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;
	public static final int MAX_PAGE_LIMIT = 100;

	@Resource(name = "sparInclusionExclusionService")
	private InclusionExclusionAreaListService sparInclusionExclusionService;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	/**
	 * Lists all store locations that are near the location specified in a query or based on latitude and longitude.
	 *
	 * @queryparam query Location in natural language i.e. city or country.
	 * @queryparam latitude Coordinate that specifies the north-south position of a point on the Earth's surface.
	 * @queryparam longitude Coordinate that specifies the east-west position of a point on the Earth's surface.
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam sort Sorting method applied to the return results.
	 * @queryparam radius Radius in meters. Max value: 40075000.0 (Earth's perimeter).
	 * @queryparam accuracy Accuracy in meters.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Lists of store near given location
	 * @throws RequestParameterException
	 */
	@RequestMapping(value = "/{baseSiteId}/stores", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderSearchPageWsDTO locationSearch(@RequestParam(required = false) final String query,
			@RequestParam(required = false) final Double latitude, @RequestParam(required = false) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false, defaultValue = "asc") final String sort,
			@RequestParam(required = false, defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@RequestParam(required = false, defaultValue = DEFAULT_ACCURACY) final double accuracy,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response)
			throws RequestParameterException
	{
		final StoreFinderSearchPageWsDTO result = storesHelper.locationSearch(query, latitude, longitude, currentPage, pageSize,
				sort, radius, accuracy, addPaginationField(fields));

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());

		return result;
	}

	/**
	 * Returns {@value com.spar.hcl.v2.controller.BaseController#HEADER_TOTAL_COUNT} header with the number of all store
	 * locations that are near the location specified in a query, or based on latitude and longitude.
	 *
	 * @queryparam query Location in natural language i.e. city or country.
	 * @queryparam latitude Coordinate that specifies the north-south position of a point on the Earth's surface.
	 * @queryparam longitude Coordinate that specifies the east-west position of a point on the Earth's surface.
	 * @queryparam radius Radius in meters. Max value: 40075000.0 (Earth's perimeter).
	 * @queryparam accuracy Accuracy in meters.
	 * @throws RequestParameterException
	 */
	@RequestMapping(value = "/{baseSiteId}/stores", method = RequestMethod.HEAD)
	public void countLocationSearch(@RequestParam(required = false) final String query,
			@RequestParam(required = false) final Double latitude, @RequestParam(required = false) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@RequestParam(required = false, defaultValue = DEFAULT_ACCURACY) final double accuracy,
			final HttpServletResponse response) throws RequestParameterException
	{
		final StoreFinderSearchPageData<PointOfServiceData> result = storesHelper.locationSearch(query, latitude, longitude, 0, 1,
				"asc", radius, accuracy);

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());
	}

	/**
	 * Returns store location based on its unique name.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Store details
	 */
	@RequestMapping(value = "/{baseSiteId}/stores/{storeId}", method = RequestMethod.GET)
	@ResponseBody
	public PointOfServiceWsDTO locationDetails(@PathVariable final String storeId,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return storesHelper.locationDetails(storeId, fields);
	}

	@RequestMapping(value = "/{baseSiteId}/stores/{City}/[location]", method = RequestMethod.GET)
	@ResponseBody
	public void setlocationDetails(@PathVariable final String storeId,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		storesHelper.locationDetails(storeId, fields);
	}

	@RequestMapping(value = "/{baseSiteId}/findStores", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderSearchPageWsDTO getStores(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			/* @RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode, */
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "latitude", required = false) final Double latitude,
			@RequestParam(value = "longitude", required = false) final Double longitude,
			@RequestParam(value = "postalCode") final String postalCode,
			@RequestParam(value = "longFormatted_address") final String longFormatted_address) throws WebserviceValidationException
	{

		//final String sanitizedSearchQuery = XSSFilterUtil.filter(locationQuery);
		StoreFinderSearchPageData<PointOfServiceData> storeSearchResult = new StoreFinderSearchPageData<PointOfServiceData>();
		final StoreFinderSearchPageWsDTO storeSearchResultWsDTO = new StoreFinderSearchPageWsDTO();
		if (latitude != null && longitude != null)
		{
			final GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(latitude.doubleValue());
			geoPoint.setLongitude(longitude.doubleValue());

			final SparInExResponse sparResponseForEx = sparInclusionExclusionService.isLocationInAreaListOnAreaType(
					createAndpopulateSparInExRequest(longFormatted_address, postalCode), InclusionExclusionTypeEnum.EXL);
			LOG.info("In getStores Method, response code for exclusion:" + sparResponseForEx.getIsInArealist());
			if (null != sparResponseForEx.getIsInArealist() && sparResponseForEx.getIsInArealist().booleanValue())
			{
				return null;

			}
			else
			{
				final SparInExResponse sparResponseForIn = sparInclusionExclusionService.isLocationInAreaListOnAreaType(
						createAndpopulateSparInExRequest(longFormatted_address, postalCode), InclusionExclusionTypeEnum.IN);
				LOG.info("In getStores Method, response code for inclusion:" + sparResponseForIn.getIsInArealist());
				if (null != sparResponseForEx.getIsInArealist() && sparResponseForIn.getIsInArealist().booleanValue())
				{
					storeSearchResult = storeFinderFacadeInterface.positionSearchStore(geoPoint,
							createPageableData(page, getStoreLocatorPageSize(), sortCode), sparResponseForIn);

				}
				else
				{
					storeSearchResult = storeFinderFacadeInterface.positionSearchStore(geoPoint,
							createPageableData(page, getStoreLocatorPageSize(), sortCode), sparResponseForIn);
				}
			}
			if (!storeSearchResult.getResults().isEmpty())
			{
				final List<PointOfServiceData> pos = storeSearchResult.getResults();
				final List<PointOfServiceData> nearPOS = new ArrayList<PointOfServiceData>();
				if (pos.size() > 0)
				{
					nearPOS.add(pos.get(0));
					final float nearPOSdistance = Float.parseFloat(pos.get(0).getFormattedDistance().replace(" Km", ""));
					for (final PointOfServiceData result : pos)
					{
						if (nearPOSdistance > Float.parseFloat(result.getFormattedDistance().replace(" Km", "")))
						{
							nearPOS.clear();
							nearPOS.add(result);
						}
					}
					storeSearchResult.setResults(nearPOS);
					dataMapper.map(storeSearchResult, storeSearchResultWsDTO, fields);
				}
				return storeSearchResultWsDTO;
			}
			else
			{
				final Errors errors = new BeanPropertyBindingResult(storeSearchResult, "storeSearchResult");
				errors.reject("storeSearchResult.empty");
				throw new WebserviceValidationException(errors);
			}

		}
		return null;

	}

	private SparInExRequest createAndpopulateSparInExRequest(final String longFormatted_address, final String postalCode)
	{
		final SparInExRequest request = new SparInExRequest();
		request.setPostalCode(postalCode);
		request.setLongfullAddress(longFormatted_address);
		return request;


	}

	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);
		pageableData.setPageSize(MAX_PAGE_LIMIT);
		return pageableData;
	}

	protected int getStoreLocatorPageSize()
	{
		return siteConfigService.getInt("storefront.storelocator.pageSize", 0);
	}




}
