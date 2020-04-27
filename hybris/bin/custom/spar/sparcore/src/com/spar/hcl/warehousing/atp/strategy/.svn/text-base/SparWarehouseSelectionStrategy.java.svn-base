/**
 *
 */
package com.spar.hcl.warehousing.atp.strategy;

import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;



/**
 * This interface is used to get the correct warehouse
 *
 * @author rohan_c
 *
 */
public interface SparWarehouseSelectionStrategy extends WarehouseSelectionStrategy
{
	/**
	 * This method is used to get the warehousemodel corresponding to POS.
	 *
	 * @param pointOfService
	 * @return List<WarehouseModel>
	 */
	public List<WarehouseModel> getSelectedWarehouseFromPOS(final PointOfServiceModel pointOfService);

	/**
	 * This method is used to get the selected warehousemodel using the warehousecode.
	 *
	 * @param baseStore
	 * @param warehouseCode
	 * @return List<WarehouseModel>
	 */
	public List<WarehouseModel> getSelectedWarehouse(final BaseStoreModel baseStore, final String warehouseCode);

	/**
	 * Getter
	 *
	 * @return the warehouseCode
	 */
	public String getWarehouseCode();

	/**
	 * Setter
	 *
	 * @param warehouseCode
	 *           the warehouseCode to set
	 */
	public void setWarehouseCode(final String warehouseCode);
}
