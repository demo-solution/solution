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

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.StorefinderBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.StoreFinderForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.StorePositionForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.core.inclusionexclusion.service.InclusionExclusionAreaListService;
import com.spar.hcl.facades.cart.SparCartFacade;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExRequest;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;


/**
 * Controller for store locator search and detail pages.
 */

@Controller
@Scope("tenant")
@RequestMapping(value = "/store-finder")
public class StoreLocatorPageController extends AbstractSearchPageController
{
	protected static final Logger LOG = Logger.getLogger(StoreLocatorPageController.class);

	private static final String STORE_FINDER_CMS_PAGE_LABEL = "storefinder";
	private static final String GOOGLE_API_KEY_ID = "googleApiKey";
	private static final String GOOGLE_API_VERSION = "googleApiVersion";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "storefinderBreadcrumbBuilder")
	private StorefinderBreadcrumbBuilder storefinderBreadcrumbBuilder;

	@Resource(name = "storeFinderFacade")
	private StoreFinderFacade storeFinderFacade;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparInclusionExclusionService")
	private InclusionExclusionAreaListService sparInclusionExclusionService;

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource(name = "sparCartFacade")
	private SparCartFacade sparCartFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "sparCheckoutFacade")
	private SparDefaultCheckoutFacade sparCheckoutFacade;
	
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@ModelAttribute("googleApiVersion")
	public String getGoogleApiVersion()
	{
		return configurationService.getConfiguration().getString(GOOGLE_API_VERSION);
	}

	@ModelAttribute("googleApiKey")
	public String getGoogleApiKey(final HttpServletRequest request)
	{
		final String googleApiKey = getHostConfigService().getProperty(GOOGLE_API_KEY_ID, request.getServerName());
		if (StringUtils.isEmpty(googleApiKey))
		{
			LOG.warn("No Google API key found for server: " + request.getServerName());
		}
		return googleApiKey;
	}

	// Method to get the empty search form
	@RequestMapping(method = RequestMethod.GET)
	public String getStoreFinderPage(final Model model) throws CMSItemNotFoundException
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
		setUpPageForms(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, storefinderBreadcrumbBuilder.getBreadcrumbs());
		storeCmsPageInModel(model, getStoreFinderPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getStoreFinderPage());
		return getViewForPage(model);
	}

	@RequestMapping(method = RequestMethod.GET, params = "q")
	public String findStores(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "q") final String locationQuery,
			@RequestParam(value = "latitude", required = false) final Double latitude,
			@RequestParam(value = "longitude", required = false) final Double longitude,
			@RequestParam(value = "postalCode") final String postalCode,
			@RequestParam(value = "longFormatted_address") final String longFormatted_address,
			@RequestParam(value = "addressId") final String addressId,
			@RequestParam(value = "isCheckoutCall") final Boolean isCheckoutCall, final StoreFinderForm storeFinderForm,
			final Model model, final BindingResult bindingResult) throws GeoLocatorException, MapServiceException,
			CMSItemNotFoundException
	{
		final String sanitizedSearchQuery = XSSFilterUtil.filter(locationQuery);

		/*
		 * if (latitude != null && longitude != null) { final GeoPoint geoPointSelected = new GeoPoint();
		 * geoPointSelected.setLatitude(latitude.doubleValue()); geoPointSelected.setLongitude(longitude.doubleValue());
		 * final PageableData pageableData = createPageableData(page, getStoreLocatorPageSize(), sortCode, showMode);
		 * final StoreFinderSearchPageData<PointOfServiceData> storeSearchResult;
		 * 
		 * final SparInExResponse sparResponseForIn = sparInclusionExclusionService.isLocationInAreaListOnAreaType(
		 * createAndpopulateSparInExRequest(longFormatted_address, postalCode), InclusionExclusionTypeEnum.IN);
		 * LOG.info("response code for inclusion:" + sparResponseForIn.getIsInArealist()); // run the default 5k.m.
		 * code.... // getting data for HDs if (isCheckoutCall != null && !isCheckoutCall.booleanValue()) {
		 * storeSearchResult = storeFinderFacadeInterface.positionSearchStore(geoPointSelected, pageableData,
		 * sparResponseForIn); if (storeSearchResult.getResults().size() > 0) { final String selectedStoreName =
		 * storeSearchResult.getResults().get(0).getName(); sessionService.setAttribute("selectedStore",
		 * selectedStoreName); final WarehouseData warehouseData =
		 * storeFinderFacadeInterface.getWarehouse(selectedStoreName);
		 * sessionService.setAttribute("selectedWarehouseCode", warehouseData.getCode());
		 * sessionService.setAttribute("cityName", sanitizedSearchQuery); } } }
		 */

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

		if (latitude != null && longitude != null)
		{
			final GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(latitude.doubleValue());
			geoPoint.setLongitude(longitude.doubleValue());
			/* Code Change by sumit for Store Locator */
			setUpSearchResultsForStorePOS(addressId, geoPoint, sanitizedSearchQuery,
					createPageableData(page, getStoreLocatorPageSize(), sortCode, showMode), model,
					createAndpopulateSparInExRequest(longFormatted_address, postalCode));
			/* Change end here */
		}
		else if (StringUtils.isNotBlank(sanitizedSearchQuery))
		{

			setUpSearchResultsForLocationQuery(sanitizedSearchQuery,
					createPageableData(page, getStoreLocatorPageSize(), sortCode, showMode), model);
			setUpMetaData(sanitizedSearchQuery, model);
			setUpPageForms(model);
			setUpPageTitle(sanitizedSearchQuery, model);

		}
		else
		{

			GlobalMessages.addErrorMessage(model, "storelocator.error.no.results.subtitle");
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					storefinderBreadcrumbBuilder.getBreadcrumbsForLocationSearch(sanitizedSearchQuery));

		}

		storeCmsPageInModel(model, getStoreFinderPage());

		return ControllerConstants.Views.Pages.StoreFinder.StoreFinderSearchPage;
	}


	@RequestMapping(value = "/mappedStore", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String mappedStore(@RequestParam(value = "addressId") final String addressId)
	{
		AddressData addressData = null;
		String mappedStore = null;
		Map dSlotsStores = null;
		String dslots = null;
		LOG.info("Entered in mappedStore method with address code : " + addressId);
		//getting session cart for refresh data
		sparCartFacade.getSessionCart();
		
		if (StringUtils.isNotEmpty(addressId))
		{
			addressData = checkoutFacade.getDeliveryAddressForCode(addressId);
			if (null != addressData)
			{
				final List<PointOfServiceData> posData = storeFinderFacadeInterface.getMappedStore(addressData.getArea(),
						addressData.getPostalCode());

				if (null != posData && posData.size() > 0)
				{
					LOG.info("Found mapped store in inclusion list. warehouse : " + posData.get(0).getName());
					mappedStore = posData.get(0).getName();
				}
				else
				{
					LOG.info("Store already mapped with address, Mapped warehouse : " + addressData.getMappedStore());
					LOG.info("Start :: Check for mapped store catchment area ::::::: ");
					if (StringUtils.isNotEmpty(addressData.getMappedStore()))
					{
						final PointOfServiceModel pointOfServiceModel = new PointOfServiceModel();
						pointOfServiceModel.setName(addressData.getMappedStore());

						final PointOfServiceModel pos = flexibleSearchService.getModelByExample(pointOfServiceModel);
						if (null != pos && null != pos.getStoreCatchmentArea() && pos.getStoreCatchmentArea().doubleValue() > 0)
						{
							mappedStore = addressData.getMappedStore();
							LOG.info("Found valid Store ::::::: Mapped store = " + mappedStore);
						}
						LOG.info("End :: Check for mapped store catchment area ::::::: ");
					}

				}

				if (null != mappedStore)
				{
					dSlotsStores = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("HD", mappedStore);
					sessionService.setAttribute("selectedStore", mappedStore);
					if (null != dSlotsStores)
					{
						final Set setDSlot = dSlotsStores.entrySet();
						final Iterator itr = setDSlot.iterator();
						while (itr.hasNext())
						{
							final Map.Entry me = (Map.Entry) itr.next();
							final Date date = (Date) me.getKey();
							dslots = dateFormatter(date) + " " + ((DeliverySlotData) me.getValue()).getSlotDescription();
							break;
						}
					}
				}
			}
		}
		final JSONObject obj = new JSONObject();
		try
		{
			obj.put("dslots", dslots);
			obj.put("name", mappedStore);
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
		return obj.toString();
	}

	private String dateFormatter(final Date date)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("E - dd MMM");
		final Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		System.out.println(formatter.format(now.getTime()));
		now.set(Calendar.HOUR_OF_DAY, 0);
		System.out.println(formatter.format(now.getTime()));
		return formatter.format(now.getTime());
	}



	@RequestMapping(value = "/position", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String searchByCurrentPosition(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final AbstractSearchPageController.ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final StorePositionForm storePositionForm,
			final Model model) throws GeoLocatorException, MapServiceException, CMSItemNotFoundException
	{
		final GeoPoint geoPoint = new GeoPoint();
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

		geoPoint.setLatitude(storePositionForm.getLatitude());
		geoPoint.setLongitude(storePositionForm.getLongitude());

		setUpSearchResultsForPosition(geoPoint, createPageableData(page, getStoreLocatorPageSize(), sortCode, showMode), model);
		setUpPageForms(model);
		storeCmsPageInModel(model, getStoreFinderPage());

		return ControllerConstants.Views.Pages.StoreFinder.StoreFinderSearchPage;
	}

	// setup methods to populate the model
	protected void setUpMetaData(final String locationQuery, final Model model)
	{
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(locationQuery);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getSiteName()
				+ " "
				+ getMessageSource().getMessage("storeFinder.meta.description.results", null, "storeFinder.meta.description.results",
						getI18nService().getCurrentLocale()) + " " + locationQuery);
		super.setUpMetaData(model, metaKeywords, metaDescription);
	}

	protected void setUpNoResultsErrorMessage(final Model model, final StoreFinderSearchPageData<PointOfServiceData> searchResult)
	{
		if (searchResult.getResults().isEmpty())
		{
			GlobalMessages.addErrorMessage(model, "storelocator.error.no.results.subtitle");
		}
	}

	protected void setUpPageData(final Model model, final StoreFinderSearchPageData<PointOfServiceData> searchResult,
			final List<Breadcrumb> breadCrumbsList)
	{
		populateModel(model, searchResult, ShowMode.Page);
		model.addAttribute("locationQuery", StringEscapeUtils.escapeHtml(searchResult.getLocationText()));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadCrumbsList);
	}

	/* Code Change start by sumit for Store Locator */

	protected void setUpSearchResultsForStorePOS(final String addressId, final GeoPoint geoPoint,
			final String sanitizedSearchQuery, final PageableData pageableData, final Model model,
			final SparInExRequest requestForInEx)
	{

		LOG.info("postal code:" + requestForInEx.getPostalCode());
		LOG.info("long addresss:" + requestForInEx.getLongfullAddress());

		StoreFinderSearchPageData<PointOfServiceData> storeSearchResult = new StoreFinderSearchPageData<PointOfServiceData>();
		StoreFinderSearchPageData<PointOfServiceData> posSearchResult = new StoreFinderSearchPageData<PointOfServiceData>();

		final SparInExResponse sparResponseForEx = sparInclusionExclusionService.isLocationInAreaListOnAreaType(requestForInEx,
				InclusionExclusionTypeEnum.EXL);
		LOG.info("response code for exclusion:" + sparResponseForEx.getIsInArealist());


		if (null != sparResponseForEx.getIsInArealist() && sparResponseForEx.getIsInArealist())

		{
			posSearchResult = storeFinderFacadeInterface.positionSearchPOS(geoPoint, pageableData, sanitizedSearchQuery);
		}


		else
		{
			// now get the data for inclusion and check for incluslion.
			final SparInExResponse sparResponseForIn = sparInclusionExclusionService.isLocationInAreaListOnAreaType(requestForInEx,
					InclusionExclusionTypeEnum.IN);
			LOG.info("response code for inclusion:" + sparResponseForIn.getIsInArealist());
			if (null != sparResponseForEx.getIsInArealist() && sparResponseForIn.getIsInArealist())
			{
				LOG.info("Running Inclusion code:");
				// you need to fetech out the respective store of that entry
				// getting data for HDs
				// but i need specific maaped store not by this method.. and also delivery slots for that.
				// get pos by name and then its slots....
				storeSearchResult = storeFinderFacadeInterface.positionSearchStore(geoPoint, pageableData, sparResponseForIn);

				// getting data for CNCs there is no 5k.m. checks
				posSearchResult = storeFinderFacadeInterface.positionSearchPOS(geoPoint, pageableData, sanitizedSearchQuery);
			}

			else
			{
				LOG.info("user location is not in both list........");
				// run the default 5k.m. code....

				// Fetching the nearest Store within its specified catchment area
				storeSearchResult = storeFinderFacadeInterface.positionSearchStore(geoPoint, pageableData, sparResponseForIn);
				// getting data for CNCs
				posSearchResult = storeFinderFacadeInterface.positionSearchPOS(geoPoint, pageableData, sanitizedSearchQuery);


			}

		}
		Map dSlotsStores = null;
		Map dSlotsPOS = null;
		final String userCurrentEmailID = sparCustomerFacade.findUserEmailId();


		if (CollectionUtils.isNotEmpty(storeSearchResult.getResults()) && null != storeSearchResult.getResults().get(0).getName())
		{
			dSlotsStores = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("HD", storeSearchResult.getResults().get(0)
					.getName());
		}

		if (CollectionUtils.isNotEmpty(posSearchResult.getResults()) && null != posSearchResult.getResults().get(0).getName())
		{
			dSlotsPOS = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("CNC", posSearchResult.getResults().get(0).getName());
		}
		setUpPageData(model, storeSearchResult, posSearchResult, userCurrentEmailID, dSlotsStores, dSlotsPOS,
				storefinderBreadcrumbBuilder.getBreadcrumbsForCurrentPositionSearch());

		if (StringUtils.isNotEmpty(addressId) && CollectionUtils.isNotEmpty(storeSearchResult.getResults())
				&& null != storeSearchResult.getResults().get(0).getName())
		{
			final AddressData selectedAddressData = checkoutFacade.getDeliveryAddressForCode(addressId);
			selectedAddressData.setMappedStore(storeSearchResult.getResults().get(0).getName());
			userFacade.editAddress(selectedAddressData);
		}
	}

	protected void setUpPageData(final Model model, final StoreFinderSearchPageData<PointOfServiceData> storeSearchResult,
			final StoreFinderSearchPageData<PointOfServiceData> posSearchResult, final String userCurrentEmailID,
			final Map dSlotsStores, final Map dSlotsPOS, final List<Breadcrumb> breadCrumbsList)
	{
		populateModel(model, storeSearchResult, posSearchResult, userCurrentEmailID, dSlotsStores, dSlotsPOS, ShowMode.Page);
		/* model.addAttribute("locationQuery", StringEscapeUtils.escapeHtml(searchResult.getLocationText())); */
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadCrumbsList);
	}

	protected void populateModel(final Model model, final StoreFinderSearchPageData<PointOfServiceData> storeSearchResult,
			final StoreFinderSearchPageData<PointOfServiceData> posSearchResult, final String userCurrentEmailID,
			final Map dSlotsStores, final Map dSlotsPOS, final ShowMode showMode)
	{
		final int numberPagesShown = 5;
		model.addAttribute("numberPagesShown", Integer.valueOf(numberPagesShown));
		model.addAttribute("STORE", storeSearchResult);
		model.addAttribute("POS", posSearchResult);
		model.addAttribute("DSSLOTSSTORES", dSlotsStores);
		model.addAttribute("DSSLOTSPOS", dSlotsPOS);
		model.addAttribute("USEREMAILID", userCurrentEmailID);
	}

	/* Change end here */

	protected void setUpSearchResultsForPosition(final GeoPoint geoPoint, final PageableData pageableData, final Model model)
	{
		// Run the location search & populate the model
		final StoreFinderSearchPageData<PointOfServiceData> searchResult = storeFinderFacade.positionSearch(geoPoint, pageableData);

		final GeoPoint newGeoPoint = new GeoPoint();
		newGeoPoint.setLatitude(searchResult.getSourceLatitude());
		newGeoPoint.setLongitude(searchResult.getSourceLongitude());

		updateLocalUserPreferences(newGeoPoint, searchResult.getLocationText());
		setUpPageData(model, searchResult, storefinderBreadcrumbBuilder.getBreadcrumbsForCurrentPositionSearch());
		setUpPosition(model, newGeoPoint);
		setUpNoResultsErrorMessage(model, searchResult);
	}

	protected void setUpPosition(final Model model, final GeoPoint geoPoint)
	{
		model.addAttribute("geoPoint", geoPoint);
	}

	protected void setUpSearchResultsForLocationQuery(final String locationQuery, final PageableData pageableData,
			final Model model)
	{
		// Run the location search & populate the model
		final StoreFinderSearchPageData<PointOfServiceData> searchResult = storeFinderFacade.locationSearch(locationQuery,
				pageableData);
		final GeoPoint geoPoint = new GeoPoint();
		geoPoint.setLatitude(searchResult.getSourceLatitude());
		geoPoint.setLongitude(searchResult.getSourceLongitude());

		updateLocalUserPreferences(geoPoint, searchResult.getLocationText());
		setUpPageData(model, searchResult, storefinderBreadcrumbBuilder.getBreadcrumbsForLocationSearch(locationQuery));
		setUpNoResultsErrorMessage(model, searchResult);
	}


	protected void updateLocalUserPreferences(final GeoPoint geoPoint, final String location)
	{
		final UserLocationData userLocationData = new UserLocationData();
		userLocationData.setSearchTerm(location);
		userLocationData.setPoint(geoPoint);
		customerLocationService.setUserLocation(userLocationData);
	}


	protected void setUpPageForms(final Model model)
	{
		final StoreFinderForm storeFinderForm = new StoreFinderForm();
		final StorePositionForm storePositionForm = new StorePositionForm();
		model.addAttribute("storeFinderForm", storeFinderForm);
		model.addAttribute("storePositionForm", storePositionForm);
	}

	protected void setUpPageTitle(final String searchText, final Model model)
	{
		storeContentPageTitleInModel(
				model,
				getPageTitleResolver().resolveContentPageTitle(
						getMessageSource().getMessage("storeFinder.meta.title", null, "storeFinder.meta.title",
								getI18nService().getCurrentLocale())
								+ " " + searchText));
	}

	protected AbstractPageModel getStoreFinderPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId(STORE_FINDER_CMS_PAGE_LABEL);
	}

	/**
	 * Get the default search page size.
	 *
	 * @return the number of results per page, <tt>0</tt> (zero) indicated 'default' size should be used
	 */
	protected int getStoreLocatorPageSize()
	{
		return getSiteConfigService().getInt("storefront.storelocator.pageSize", 0);
	}

	/**
	 * @param longFormatted_address
	 * @param postalCode
	 */
	private SparInExRequest createAndpopulateSparInExRequest(final String longFormatted_address, final String postalCode)
	{
		final SparInExRequest request = new SparInExRequest();
		request.setPostalCode(postalCode);
		request.setLongfullAddress(longFormatted_address);
		return request;


	}

}
