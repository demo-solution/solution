/**
 *
 */
package com.spar.hcl.core.storefinder.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commerceservices.storefinder.impl.DefaultStoreFinderService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.plano.model.PlanoGramModel;
import de.hybris.platform.servicelayer.internal.converter.util.ModelUtils;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.core.model.service.delivery.SparCitiesModel;
import com.spar.hcl.core.storefinder.StoreFinderServiceHelperInterface;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;


/**
 * @author valechar/tanveers
 *
 */
public class DefaultStoreFinderServiceImpl<ITEM extends PointOfServiceDistanceData> extends DefaultStoreFinderService implements
		StoreFinderServiceInterface, StoreFinderServiceHelperInterface
{
	private static final Logger LOG = Logger.getLogger(DefaultStoreFinderServiceImpl.class);
	@Resource(name = "sessionService")
	SessionService sessionService;
	FlexibleSearchService flexibleSearchService;

	private static final String GET_STORES = "select {pos.PK} from {PointOfService as pos} where {pos.basestore}=?baseStore";
	private static final String GET_DELIVERY_TYPE = "select {PK} from {DeliveryTypeEnum}";
	private static final String GET_DEFAULT_STORE = "select {pos.PK} from {PointOfService as pos} where {pos.basestore}=?baseStore and {pos.name}=?pos";
	private static final String WAREHOUSE_CODE = "select {wh.PK} from {PointOfService as pos JOIN PoS2WarehouseRel as pwrel on {pos.PK}={pwrel.source}"
			+ " JOIN Warehouse as wh on {wh.PK}={pwrel.target}} where {pos.name}=?posName or {pos.displayname}=?posDisplayName";
	private static final String GET_DELIVERY_SLOTS = "select {ds.pk}  from {DeliverySlot as ds JOIN DeliveryTypeEnum as dte on {dte.PK}={ds.deliveryType}"
			+ " JOIN DeliverySlot2WarehouseRel as dsw on {ds.pk}={dsw.target} JOIN Warehouse as w on {w.pk}={dsw.source}} where {ds.isHourlyDeliverySlotType}=false and {dte.code}=?deliveryType"
			+ " and {ds.minimumOrderTimeSlot}<=?timeOfOrderMax	and {ds.maximumOrderTimeSlot}>=?timeOfOrderMax and {ds.active}=true and {w.PK}=?warehouseCode";
	private static final String GET_POINTOFSERVICE = "select {pos.PK} from {PointOfService as pos} where {pos.name}=?pointOfService";
	private static final String GET_ORDER_DELIVERY_SLOT = "select {ds.PK} from {DeliverySlot as ds} where {ds.slotId}=?slotId";
	private static final String GET_TOTAL_ORDER_PER_SLOT = "select count({ds.slotId}),{ds.slotId}, {ord.slotDeliveryDate} from {Order as ord JOIN DeliverySlot as ds on {ds.pk}={ord.orderDeliverySlot}}"
			+ " where {ord.orderWarehouse} = ?orderWarehouse and {ord.slotDeliveryDate} in (?date) and {ds.slotId} in (?slotId) group by {ds.slotId}, {ord.slotDeliveryDate}";
	BaseStoreService baseStoreService;
	private static final String GET_AREA_POS = "select {pos.pk} from {PointofService as pos join address as add on {pos.address}={add.pk} "
			+ " join PointOfServiceTypeEnum as pte on {pos.type}={pte.pk} join BaseStore as bs on {pos.basestore}={bs.pk}} where {add.town}=?city "
			+ " and {pte.code}=?type and {bs.uid}=?bsid";
	private static final String GET_POS_FOR_WAREHOUSE = "select {pos.PK} from {PointOfService as pos JOIN PoS2WarehouseRel as pwrel on {pos.PK}={pwrel.source}"
			+ " join PointOfServiceTypeEnum as pte on {pos.type}={pte.pk}} where {pwrel.target}=?warehouse and {pte.code}=?posType";
	private static final String GET_DEL_SLOTS_ORDBY_HDID = "select {ds.pk}  from {DeliverySlot as ds JOIN DeliveryTypeEnum as dte on {dte.PK}={ds.deliveryType}"
			+ " JOIN DeliverySlot2WarehouseRel as dsw on {ds.pk}={dsw.target} JOIN Warehouse as w on {w.pk}={dsw.source}} where {dte.code}=?deliveryType"
			+ " and {ds.minimumOrderTimeSlot}<=?timeOfOrderMax	and {ds.maximumOrderTimeSlot}>=?timeOfOrderMax and {ds.active}=true "
			+ " and {w.PK}=?warehouseCode order by {ds.homeDeliveryId}";
	private static final String GET_CITY_NAME = "select {sc.pk} from {SparCities as sc} where {sc.status}=true";
	private static final String GET_PLANOGRAM_DETAIL = "select {p:pk} from {planogram as p} where {p:productid}=?productId and {p:warehouse}=?warehouseId";
	private static final String QUERY_STORES_MAX_AERIAL_DISTANCE = "SELECT MAX({storeCatchmentArea}) FROM {PointOfService as pos JOIN PointOfServiceTypeEnum as posEnum on {pos.type}={posEnum.pk} AND {posEnum.code}=?posType}";
	//private static final String GET_MAPPED_STORE = "select {inl.entryCode}, {pos.name} from {InclusionExclusionEntry as inl join InclusionExclusion2POSRel as inrel on {inl.pk}={inrel.source} join pointofservice as pos on {pos.pk}={inrel.target}} where {inl.entryCode} = ?entryCode and {inl.postalCode} = ?postalCode";
	private static final String GET_MAPPED_STORE = "select {pos.pk} from {InclusionExclusionEntry as inl join InclusionExclusion2POSRel as inrel on {inl.pk}={inrel.source} join pointofservice as pos on {pos.pk}={inrel.target}} where {pos.storeCatchmentArea} > 0 and {inl.areaName} = ?entryCode and {inl.postalCode} = ?postalCode";
	private static final String POS_TYPE_STORE = "STORE";
	private static final String POS_TYPE = "posType";
	private static final String GET_HOURLY_DELIVERY_SLOTS = "select {ds.pk}  from {DeliverySlot as ds JOIN DeliveryTypeEnum as dte on {dte.PK}={ds.deliveryType}"
			+ " JOIN DeliverySlot2WarehouseRel as dsw on {ds.pk}={dsw.target} JOIN Warehouse as w on {w.pk}={dsw.source}} where {ds.isHourlyDeliverySlotType}=true and {dte.code}=?deliveryType"
			+ " and {ds.minimumOrderTimeSlot}=?timeOfOrderMin	and {ds.maximumOrderTimeSlot}=?timeOfOrderMax and {ds.active}=true and {w.PK}=?warehouseCode";

	//private static final String GET_SERVICE_AREA_FOR_CITY = "select {sa.pk} from {SparServiceArea as sa} where {sa.city} = ?city";

	/*
	 * private static final String GET_SERVICE_AREA_FOR_CITY =
	 * "SELECT INNERTABLE.PK FROM ( {{ SELECT {sa.pk} AS PK, {sa.displayName} AS DISPLAYNAME, {sa.pincode} AS PINCODE FROM {SparServiceArea AS sa} WHERE {sa.city} = ?city }} ) INNERTABLE WHERE lower(INNERTABLE.DISPLAYNAME) LIKE lower(?area) OR INNERTABLE.PINCODE LIKE ?area"
	 * ;
	 *
	 * private static final String GET_SERVICE_AREA = "select {sa.pk} from {SparServiceArea as sa}"; private static final
	 * String GET_ADDRESS_SERVICE_AREA = "select {sa.pk} from {SparServiceArea as sa} where {sa.areaId}=?areaId";
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see hcl.spar.core.storefinder.StoreFinderServiceInterface#getStores()
	 */
	@Override
	public List<PointOfServiceModel> getStores()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_STORES);
		final BaseStoreModel storeModel = getBaseStoreService().getCurrentBaseStore();
		flexibleSearchQuery.addQueryParameter("baseStore", storeModel);
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	@Override
	public List<PointOfServiceModel> getMappedStore(final String entryCode, final String postalCode)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_MAPPED_STORE);
		flexibleSearchQuery.addQueryParameter("entryCode", entryCode);
		flexibleSearchQuery.addQueryParameter("postalCode", postalCode);
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}
	
	/**
	 * This method is used to fetch the planogram detail for each product in order level data
	 *
	 * @param productId
	 * @param warehouseId
	 * @return List<PlanoGramModel>
	 */

	@Override
	public List<PlanoGramModel> getPlanogramDetail(final String productId, final String warehouseId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_PLANOGRAM_DETAIL);
		flexibleSearchQuery.addQueryParameter("productId", productId);
		flexibleSearchQuery.addQueryParameter("warehouseId", warehouseId);
		final SearchResult<PlanoGramModel> result = getFlexibleSearchService().search(flexibleSearchQuery);
		return result.getResult();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see hcl.spar.core.storefinder.StoreFinderServiceInterface#getDeliveryTypes()
	 */
	@Override
	public List<DeliveryTypeEnum> getDeliveryTypes()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_DELIVERY_TYPE);
		final SearchResult<DeliveryTypeEnum> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();

	}

	@Override
	public WarehouseModel getWarehouse()
	{
		String pos = sessionService.getAttribute("selectedStore");
		if (null == pos || pos.isEmpty())
		{
			pos = sessionService.getAttribute("backofficeSelectedStore");
		}
		return getWarehouse(pos);
	}

	@Override
	public WarehouseModel getWarehouse(final String pos)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(WAREHOUSE_CODE);
		flexibleSearchQuery.addQueryParameter("posName", pos);
		flexibleSearchQuery.addQueryParameter("posDisplayName", pos);
		final SearchResult<WarehouseModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		if (null != searchResult && !CollectionUtils.isEmpty(searchResult.getResult()))
		{
			return searchResult.getResult().get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method retrieve all the service area from the offline service area Database for Selected City
	 *
	 */
	/*
	 * @Override public List<SparServiceAreaModel> getSparServiceAreaForCity(final String city, String area) { area =
	 * area + "%"; final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_SERVICE_AREA_FOR_CITY);
	 * flexibleSearchQuery.addQueryParameter("city", city); flexibleSearchQuery.addQueryParameter("area", area);
	 * flexibleSearchQuery.addQueryParameter("pincode", area); final SearchResult<SparServiceAreaModel>
	 * serviceAreaSearchResult = flexibleSearchService.search(flexibleSearchQuery); return
	 * serviceAreaSearchResult.getResult(); }
	 * 
	 * @Override public SparServiceAreaModel getAddressServiceArea(final Integer areaId) { final FlexibleSearchQuery
	 * flexibleSearchQuery = new FlexibleSearchQuery(GET_ADDRESS_SERVICE_AREA);
	 * flexibleSearchQuery.addQueryParameter("areaId", areaId); final SearchResult<SparServiceAreaModel> searchResult =
	 * flexibleSearchService.search(flexibleSearchQuery); return searchResult.getResult().get(0); }
	 */

	@Override
	public List<SparCitiesModel> getSparCities()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_CITY_NAME);
		final SearchResult<SparCitiesModel> sparCitiesSearchResult = flexibleSearchService.search(flexibleSearchQuery);
		return sparCitiesSearchResult.getResult();
	}

	/**
	 * This method retrieve all the service area from the offline service area Database
	 *
	 */
	/*
	 * @Override public List<SparServiceAreaModel> getSparServiceArea() { final FlexibleSearchQuery flexibleSearchQuery =
	 * new FlexibleSearchQuery(GET_SERVICE_AREA); final SearchResult<SparServiceAreaModel> serviceAreaSearchResult =
	 * flexibleSearchService.search(flexibleSearchQuery); return serviceAreaSearchResult.getResult(); }
	 */

	@SuppressWarnings("boxing")
	/*
	 * (non-Javadoc)
	 * 
	 * @see hcl.spar.core.storefinder.StoreFinderServiceInterface#getDeliverySlots(java.lang.String)
	 */
	@Override
	public List<DeliverySlotModel> getDeliverySlots(final String deliveryType)
	{
		FlexibleSearchQuery flexibleSearchQuery = null;
		final WarehouseModel WarehouseModel = getWarehouse();
		if(null != WarehouseModel && null != WarehouseModel.getEnabledHourlyDeliverySlots()
				&& WarehouseModel.getEnabledHourlyDeliverySlots().booleanValue())
		{
			flexibleSearchQuery = new FlexibleSearchQuery(GET_HOURLY_DELIVERY_SLOTS);
			final Calendar cal = Calendar.getInstance();
			Integer orderingTime = Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			cal.add(Calendar.HOUR_OF_DAY, 1);
			Integer timeOfOrderMax = Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			if(orderingTime >= 0 && orderingTime < 7)
			{
				orderingTime = 0 ;
				timeOfOrderMax = 7;
			}
			else if(orderingTime >= 18)
			{
				orderingTime = 18;
				timeOfOrderMax = 0;
			}
			flexibleSearchQuery.addQueryParameter("deliveryType",
					DeliveryTypeEnum.HD.getCode().equals(deliveryType) ? DeliveryTypeEnum.HD.getCode() : DeliveryTypeEnum.CNC.getCode());
			flexibleSearchQuery.addQueryParameter("timeOfOrderMin", orderingTime);
			flexibleSearchQuery.addQueryParameter("timeOfOrderMax", timeOfOrderMax);
			flexibleSearchQuery.addQueryParameter("warehouseCode", WarehouseModel);
		}
		else
		{
			flexibleSearchQuery = new FlexibleSearchQuery(GET_DELIVERY_SLOTS);
			final Calendar cal = Calendar.getInstance();
			final Integer orderingTime = Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			flexibleSearchQuery.addQueryParameter("deliveryType",
					DeliveryTypeEnum.HD.getCode().equals(deliveryType) ? DeliveryTypeEnum.HD.getCode() : DeliveryTypeEnum.CNC.getCode());
			flexibleSearchQuery.addQueryParameter("timeOfOrderMin", orderingTime);
			flexibleSearchQuery.addQueryParameter("timeOfOrderMax", orderingTime);
			flexibleSearchQuery.addQueryParameter("warehouseCode", WarehouseModel);
		}
		final SearchResult<DeliverySlotModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}


	/*
	 * (non-Javadoc)
	 *
	 *
	 * @see
	 * hcl.spar.core.storefinder.StoreFinderServiceInterface#getDeliverySlotsForUserLocation(java.lang.String,java.lang
	 * .String)
	 */
	@Override
	public List<DeliverySlotModel> getDeliverySlotsForUserLocation(final String deliveryType, final String posName)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_DEL_SLOTS_ORDBY_HDID);
		final WarehouseModel WarehouseModel = getWarehouse(posName);
		final Calendar cal = Calendar.getInstance();
		final Integer orderingTime = Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		flexibleSearchQuery.addQueryParameter("deliveryType",
				DeliveryTypeEnum.HD.getCode().equals(deliveryType) ? DeliveryTypeEnum.HD.getCode() : DeliveryTypeEnum.CNC.getCode());
		flexibleSearchQuery.addQueryParameter("timeOfOrderMin", orderingTime);
		flexibleSearchQuery.addQueryParameter("timeOfOrderMax", orderingTime);
		flexibleSearchQuery.addQueryParameter("warehouseCode", WarehouseModel);
		final SearchResult<DeliverySlotModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.core.storefinder.StoreFinderServiceInterface#getPointOfService()
	 */
	@Override
	public PointOfServiceModel getPointOfService()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_POINTOFSERVICE);
		final String pos = sessionService.getAttribute("selectedStore");
		flexibleSearchQuery.addQueryParameter("pointOfService", pos);
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.storefinder.StoreFinderServiceInterface#getOrderDeliverySLot()
	 */
	@Override
	public DeliverySlotModel getOrderDeliverySLot(final String slotId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_ORDER_DELIVERY_SLOT);
		flexibleSearchQuery.addQueryParameter("slotId", slotId);
		final SearchResult<DeliverySlotModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hcl.spar.core.storefinder.StoreFinderServiceInterface#getDefaultStore()
	 */
	@Override
	public PointOfServiceModel getDefaultStore()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_DEFAULT_STORE);
		final BaseStoreModel storeModel = getBaseStoreService().getCurrentBaseStore();
		flexibleSearchQuery.addQueryParameter("baseStore", storeModel);
		flexibleSearchQuery.addQueryParameter("pos", "SPAR Bannerghatta");
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult().get(0);
	}


	@Override
	public PointOfServiceModel getPosStore(final String pos)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_DEFAULT_STORE);
		final BaseStoreModel storeModel = getBaseStoreService().getCurrentBaseStore();
		flexibleSearchQuery.addQueryParameter("baseStore", storeModel);
		flexibleSearchQuery.addQueryParameter("pos", pos);
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult().get(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.core.storefinder.StoreFinderServiceInterface#getTotalOrderPerSlot(java.util.Date)
	 */
	@Override
	public List getTotalOrderPerSlot(final List<Date> date, final List<Integer> slotId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_TOTAL_ORDER_PER_SLOT);
		flexibleSearchQuery.addQueryParameter("orderWarehouse", getWarehouse());
		flexibleSearchQuery.addQueryParameter("date", date);
		flexibleSearchQuery.addQueryParameter("slotId", slotId);
		flexibleSearchQuery.setResultClassList(Arrays.asList(Integer.class, Integer.class, Date.class));
		final SearchResult<List<?>> resultForOrders = flexibleSearchService.search(flexibleSearchQuery);
		final List orderList = (List) ModelUtils.getFieldValue(resultForOrders, "resultList");
		return orderList;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/* Code change start by sumit for store locator */

	public List<PointOfServiceModel> getAreaPos(final String sanitizedSearchQuery)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_AREA_POS);
		flexibleSearchQuery.addQueryParameter("city", sanitizedSearchQuery);
		flexibleSearchQuery.addQueryParameter("type", "POS");
		flexibleSearchQuery.addQueryParameter("bsid", "spar");
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	/*
	 * @Override public List<PointOfServiceModel> getPosForCity(final String cityName, final String posType) { final
	 * FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_AREA_POS); if (null != cityName) {
	 * flexibleSearchQuery.addQueryParameter("city", cityName); } else { flexibleSearchQuery.addQueryParameter("city",
	 * sessionService.getAttribute("cityName")); } flexibleSearchQuery.addQueryParameter("city", cityName);
	 * flexibleSearchQuery.addQueryParameter("type", posType); flexibleSearchQuery.addQueryParameter("bsid", "spar");
	 * final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery); return
	 * searchResult.getResult(); }
	 */

	@Override
	public List<PointOfServiceModel> getPosForCity()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_AREA_POS);
		final String cityName = sessionService.getAttribute("cityName");
		flexibleSearchQuery.addQueryParameter("city", cityName);
		flexibleSearchQuery.addQueryParameter("type", "POS");
		flexibleSearchQuery.addQueryParameter("bsid", "spar");
		final SearchResult<PointOfServiceModel> searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	@Override
	public StoreFinderSearchPageData<ITEM> doSearchResultStore(final BaseStoreModel baseStore, final String locationText,
			final GeoPoint centerPoint, final PageableData pageableData, final SparInExResponse inExResponse)
	{
		Collection<PointOfServiceModel> posResults = new ArrayList<PointOfServiceModel>();

		final int resultRangeStart = pageableData.getCurrentPage() * pageableData.getPageSize();
		final int resultRangeEnd = (pageableData.getCurrentPage() + 1) * pageableData.getPageSize();
		final double radiusVal = Config.getDouble("storelocator.default.radius", 5.00d);
		// if area is in list I or E, then get the POS from response and pass it to posResults.
		if (null != inExResponse.getIsInArealist() && inExResponse.getIsInArealist())
		{
			posResults.addAll(inExResponse.getAreaPOSlist());
			// Sort all the POS
			final List<ITEM> orderedResults = calculateDistances(centerPoint, posResults);
			final PaginationData paginationData = createPagination(pageableData, posResults.size());
			// Slice the required range window out of the results
			final List<ITEM> orderedResultsWindow = orderedResults.subList(Math.min(orderedResults.size(), resultRangeStart),
					Math.min(orderedResults.size(), resultRangeEnd));
			return createSearchResult(locationText, centerPoint, orderedResultsWindow, paginationData);
		}
		else
		{
			// Arial Distance store wise code start here
			final Double maxAerialDistance = getMaxCatchmentAerialDistance();
			LOG.info("maxAerialDistance : " + maxAerialDistance);
			if (null == maxAerialDistance)
			{
				posResults = getPointsOfServiceNear(centerPoint, radiusVal, baseStore);
			}
			else
			{
				posResults = getPointsOfServiceNear(centerPoint, maxAerialDistance.doubleValue(), baseStore);
			}

			if (posResults != null)
			{
				// Sort all the POS
				final List<ITEM> orderedResults = calculateDistances(centerPoint, posResults);
				final PaginationData paginationData = createPagination(pageableData, posResults.size());

				for (final ITEM orderedResult : orderedResults)
				{
					LOG.info("PointOfService : " + orderedResult.getPointOfService().getName() + " = DistanceKM : "
							+ orderedResult.getDistanceKm());
				}
				for (final ITEM orderedResult : orderedResults)
				{
					if (null != orderedResult.getPointOfService() && null != orderedResult.getPointOfService().getStoreCatchmentArea()
							&& orderedResult.getPointOfService().getStoreCatchmentArea().doubleValue() > 0)
					{
						final double storeCatchmentArea = orderedResult.getPointOfService().getStoreCatchmentArea().doubleValue();
						if (orderedResult.getDistanceKm() <= storeCatchmentArea)
						{
							final List<ITEM> finalResultsWindow = new ArrayList<ITEM>();
							finalResultsWindow.add(orderedResult);
							return createSearchResult(locationText, centerPoint, finalResultsWindow, paginationData);
						}
					}
				}
				// Arial Distance store wise code end here
			}
		}
		// Return no results
		return createSearchResult(locationText, centerPoint, Collections.<ITEM> emptyList(), createPagination(pageableData, 0));
	}

	@Override
	public StoreFinderSearchPageData<ITEM> doSearchResultPOS(final BaseStoreModel baseStore, final String locationText,
			final GeoPoint centerPoint, final PageableData pageableData, final String sanitizedSearchQuery)
	{
		final Collection<PointOfServiceModel> posResults;

		final int resultRangeStart = pageableData.getCurrentPage() * pageableData.getPageSize();
		final int resultRangeEnd = (pageableData.getCurrentPage() + 1) * pageableData.getPageSize();

		posResults = getAreaPos(sanitizedSearchQuery);

		if (posResults != null)
		{
			// Sort all the POS
			final List<ITEM> orderedResults = calculateDistances(centerPoint, posResults);
			final PaginationData paginationData = createPagination(pageableData, posResults.size());
			// Slice the required range window out of the results
			final List<ITEM> orderedResultsWindow = orderedResults.subList(Math.min(orderedResults.size(), resultRangeStart),
					Math.min(orderedResults.size(), resultRangeEnd));

			return createSearchResult(locationText, centerPoint, orderedResultsWindow, paginationData);
		}

		// Return no results
		return createSearchResult(locationText, centerPoint, Collections.<ITEM> emptyList(), createPagination(pageableData, 0));
	}


	@Override
	public StoreFinderSearchPageData<ITEM> positionSearchStore(final BaseStoreModel baseStore, final GeoPoint geoPoint,
			final PageableData pageableData, final SparInExResponse inExResponse)
	{
		return doSearchResultStore(baseStore, null, geoPoint, pageableData, inExResponse);
	}

	@Override
	public StoreFinderSearchPageData<ITEM> positionSearchPOS(final BaseStoreModel baseStore, final GeoPoint geoPoint,
			final PageableData pageableData, final String sanitizedSearchQuery)
	{
		return doSearchResultPOS(baseStore, null, geoPoint, pageableData, sanitizedSearchQuery);
	}

	/* Change end here */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.storefinder.StoreFinderServiceInterface#getMaxCatchmentAerialDistance()
	 */
	@Override
	public Double getMaxCatchmentAerialDistance()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(QUERY_STORES_MAX_AERIAL_DISTANCE);
		flexibleSearchQuery.addQueryParameter(POS_TYPE, POS_TYPE_STORE);
		flexibleSearchQuery.setResultClassList(Collections.singletonList(Double.class));
		LOG.info("In getMaxCatchmentAerialDistance Method, Query : " + flexibleSearchQuery);
		final SearchResult<Double> result = flexibleSearchService.search(flexibleSearchQuery);
		final Iterator<Double> elems = result.getResult().iterator();
		return elems.next();
	}
}
