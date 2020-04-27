/**
 *
 */
package com.spar.hcl.core.storefinder;


import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.inclusionexclusion.data.SparInExResponse;


/**
 * @author kumarchoubeys
 *
 */
public interface StoreFinderServiceHelperInterface<ITEM extends PointOfServiceDistanceData, RESULT extends StoreFinderSearchPageData<ITEM>>
{

	/* Change start by sumit for store locator */

	RESULT positionSearchStore(BaseStoreModel baseStore, GeoPoint geoPoint, PageableData pageableData,
			SparInExResponse inExResponse);

	RESULT positionSearchPOS(BaseStoreModel baseStore, GeoPoint geoPoint, PageableData pageableData, String sanitizedSearchQuery);

	RESULT doSearchResultStore(final BaseStoreModel baseStore, final String locationText, final GeoPoint centerPoint,
			final PageableData pageableData, SparInExResponse inExResponse);

	RESULT doSearchResultPOS(final BaseStoreModel baseStore, final String locationText, final GeoPoint centerPoint,
			final PageableData pageableData, final String sanitizedSearchQuery);

	List<DeliverySlotModel> getDeliverySlotsForUserLocation(final String deliveryType, final String posName);

	/* Change end here */
}
