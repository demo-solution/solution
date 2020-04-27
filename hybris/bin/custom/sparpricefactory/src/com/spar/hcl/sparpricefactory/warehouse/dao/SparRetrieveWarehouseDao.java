/**
 *
 */
package com.spar.hcl.sparpricefactory.warehouse.dao;

import de.hybris.platform.ordersplitting.model.WarehouseModel;


/**
 * This interface is used to get the warehouseModel using flexible search.
 * 
 * @author rohan_c
 *
 */
public interface SparRetrieveWarehouseDao
{
	/**
	 * This method is used to get the warehouse from pointOfService
	 *
	 * @param pointOfService
	 * @return WarehouseModel
	 */
	WarehouseModel findWarehouseByPOS(final String pointOfService);

	/**
	 * This method is used to get the warehouse from code
	 *
	 * @param warehouse
	 * @return WarehouseModel
	 */
	WarehouseModel findWarehouseByCode(final String warehouse);
}
