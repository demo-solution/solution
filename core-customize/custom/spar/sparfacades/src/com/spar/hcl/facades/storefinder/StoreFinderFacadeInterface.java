/**
 *
 */
package com.spar.hcl.facades.storefinder;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;
import com.spar.hcl.facades.service.delivery.data.SparCitiesData;
import com.spar.hcl.facades.storelocator.data.WarehouseData;


/**
 * @author valechar
 *
 */
public interface StoreFinderFacadeInterface
{
	List<PointOfServiceData> getStoresList();

	PointOfServiceData getDefaultStore();

	List<DeliveryTypeEnum> getDeliveryTypes();

	List getDeliverySlots(final String deliveryType);

	PointOfServiceData getPointOfService();

	List<PointOfServiceData> getPosForCity();

	//List<PointOfServiceData> getPosForCity(final String cityName,final String posType);

	PointOfServiceData getPosStore(final String pos);

	/* Code added by sumit for Store Locator */
	StoreFinderSearchPageData<PointOfServiceData> positionSearchStore(GeoPoint geoPoint, PageableData pageableData,
			SparInExResponse inExResponse);

	StoreFinderSearchPageData<PointOfServiceData> positionSearchPOS(GeoPoint geoPoint, PageableData pageableData,
			String sanitizedSearchQuery);

	/* Change end here */

	/*
	 * List<SparServiceAreaData> getSparServiceAreaForCity(final String city, final String area);
	 *
	 * List<SparServiceAreaData> getSparServiceArea();
	 *
	 * SparServiceAreaData getAddressServiceArea(final Integer areaId);
	 */

	List<SparCitiesData> getSparCities();

	/**
	 * This method is used to get warehouse w.r.t Point of Service
	 *
	 * @param pos
	 * @return WarehouseModel
	 */
	public WarehouseData getWarehouse(final String pos);

	Map<Date, DeliverySlotData> getDeliverySlotsForStorePOS(String deliveryType, String warehouseCode);

	public List<DeliverySlotData> getDeliverySlotsForUserLocation(String deliveryType, String posName);

	public boolean isProductValidForPOS(final ProductModel product);

	public List<PointOfServiceData> getMappedStore(final String entryCode, final String postalCode);
}
