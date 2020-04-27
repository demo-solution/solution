/**
 *
 */
package com.spar.hcl.core.strategies.impl;

import de.hybris.platform.warehousing.atp.dao.impl.DefaultAvailableToPromiseDao;

import java.util.Map;


/**
 * This class is used to extend the DefaultAvailableToPromiseDao to override behaviour of allocation event and
 * cancellation event
 *
 * @author nileshkumar.c/Rohan_c
 *
 */

public class SparDefaultAvailableToPromiseDao extends DefaultAvailableToPromiseDao
{
	/**
	 * This method is used to retrieve the sum of quantity of the allocation events that are after the rmsModifiedTime
	 */
	@Override
	public Long getAllocationQuantityForStockLevels(final Map<String, Object> params)
	{
		final StringBuilder inventoryEvtQryString = new StringBuilder();
		inventoryEvtQryString
				.append("SELECT SUM({quantity}) FROM {AllocationEvent as ae join StockLevel as sl  on {sl:pk}={ae:stockLevel}} WHERE {ae:eventdate} > {sl:rmsmodifiedtime} and {ae.stockLevel} IN (?stockLevels)");
		return this.returnAggregateQuantity(inventoryEvtQryString.toString(), params, "stockLevels");
	}

	/**
	 * This method is used to retrieve the sum of quantity of the cancellation events that are after the rmsModifiedTime
	 */
	@Override
	public Long getCancellationQuantityForStockLevels(final Map<String, Object> params)
	{
		final StringBuilder inventoryEvtQryString = new StringBuilder();
		inventoryEvtQryString
				.append("SELECT SUM({quantity}) FROM {CancellationEvent as ce join StockLevel as sl on {sl:pk}={ce:stockLevel}} WHERE  {ce:eventdate} > {sl:rmsmodifiedtime} and  {ce.stockLevel} IN (?stockLevels)");
		return this.returnAggregateQuantity(inventoryEvtQryString.toString(), params, "stockLevels");
	}

}
