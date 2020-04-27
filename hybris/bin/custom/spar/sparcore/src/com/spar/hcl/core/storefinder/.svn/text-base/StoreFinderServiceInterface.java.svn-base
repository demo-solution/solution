/**
 *
 */
package com.spar.hcl.core.storefinder;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.plano.model.PlanoGramModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.core.model.service.delivery.SparCitiesModel;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;


/**
 * @author valechar/tanveers
 *
 */
public interface StoreFinderServiceInterface
{
	List<PointOfServiceModel> getStores();

	List<DeliveryTypeEnum> getDeliveryTypes();

	List<DeliverySlotModel> getDeliverySlots(final String deliveryType);

	PointOfServiceModel getDefaultStore();

	WarehouseModel getWarehouse();

	PointOfServiceModel getPointOfService();

	DeliverySlotModel getOrderDeliverySLot(final String slotId);

	List<ArrayList> getTotalOrderPerSlot(final List<Date> date, final List<Integer> slotId);

	List<PointOfServiceModel> getPosForCity();

	/* List<PointOfServiceModel> getPosForCity(final String city, final String posType); */

	PointOfServiceModel getPosStore(final String pos);

	/*
	 * List<SparServiceAreaModel> getSparServiceAreaForCity(final String city, final String area);
	 * 
	 * List<SparServiceAreaModel> getSparServiceArea();
	 * 
	 * SparServiceAreaModel getAddressServiceArea(final Integer areaId);
	 */
	List<SparCitiesModel> getSparCities();

	/**
	 * This method is used to get warehouse w.r.t Point of Service
	 *
	 * @param pos
	 * @return WarehouseModel
	 */
	public WarehouseModel getWarehouse(final String pos);

	List<PlanoGramModel> getPlanogramDetail(final String productId, final String warehouseId);

	public Double getMaxCatchmentAerialDistance();

	List<PointOfServiceModel> getMappedStore(final String entryCode, final String postalCode);
}
