/**
 *
 */
package com.spar.hcl.sparpricefactory.strategy;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.ordersplitting.model.WarehouseModel;


/**
 * This interface is used to get the warehouse details from the POS selected by the customer.
 *
 * @author rohan_c
 *
 */
public interface SparRetrieveWarehouseStrategy
{
	/**
	 * This method is used to retrieve warehouseModel from the POS information received from SessionContext
	 *
	 * @param ctx
	 * @return WarehouseModel
	 */
	WarehouseModel getWarehouse(SessionContext ctx);

}
