/**
 *
 */
package com.spar.hcl.warehousing.atp.strategy.impl;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingWarehouseSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import com.spar.hcl.warehousing.atp.strategy.SparWarehouseSelectionStrategy;


/**
 * This method is used to customize the warehouse strategy to retrieve correct warehouse
 *
 * @author rohan_c
 *
 */
public class SparWarehouseSelectionStrategyImpl extends WarehousingWarehouseSelectionStrategy implements
		SparWarehouseSelectionStrategy
{
	@Resource(name = "sessionService")
	SessionService sessionService;

	String warehouseCode;

	/**
	 * This method is used to retrieve correct warehouse for web/hmc flow.
	 */
	@Override
	public List<WarehouseModel> getWarehousesForBaseStore(final BaseStoreModel baseStore)
	{
		final String warehouse = sessionService.getAttribute("selectedWarehouseCode");
		if (null != warehouse)
		{
			return getSelectedWarehouse(baseStore, warehouse);
		}
		else if (null != warehouseCode)
		{
			return getSelectedWarehouse(baseStore, warehouseCode);
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<WarehouseModel> getSelectedWarehouse(final BaseStoreModel baseStore, final String warehouseCode)
	{
		final List<WarehouseModel> warehouseList = new ArrayList<WarehouseModel>();
		final Optional<WarehouseModel> model = baseStore.getWarehouses().stream()
				.filter(warehouse -> warehouseCode.equals(warehouse.getCode())).findFirst();
		if (model.isPresent())
		{
			warehouseList.add(model.get());
		}
		return warehouseList;
	}

	@Override
	public List<WarehouseModel> getSelectedWarehouseFromPOS(final PointOfServiceModel pointOfService)
	{
		final List<WarehouseModel> warehouseList = new ArrayList<WarehouseModel>();
		final String warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		if (null != warehouseCode)
		{
			final Optional<WarehouseModel> model = pointOfService.getWarehouses().stream().findFirst();
			if (model.isPresent())
			{
				warehouseList.add(model.get());
			}
		}
		return warehouseList;
	}


	/**
	 * Getter
	 *
	 * @return the warehouseCode
	 */
	@Override
	public String getWarehouseCode()
	{
		return warehouseCode;
	}

	/**
	 * Setter
	 *
	 * @param warehouseCode
	 *           the warehouseCode to set
	 */
	@Override
	public void setWarehouseCode(final String warehouseCode)
	{
		this.warehouseCode = warehouseCode;
	}
}
