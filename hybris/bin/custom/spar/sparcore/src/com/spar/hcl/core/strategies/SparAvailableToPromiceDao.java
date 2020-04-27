/**
 *
 */
package com.spar.hcl.core.strategies;

import java.util.Map;


/**
 * @author nileshkumar.c
 *
 */
public interface SparAvailableToPromiceDao
{
	Long getAvailabilityForStockLevels(final Map<String, Object> params);

	Long getAllocationQuantityForStockLevels(final Map<String, Object> params);

	Long getCancellationQuantityForStockLevels(final Map<String, Object> params);

	Long getReservedQuantityForStockLevels(final Map<String, Object> params);

	Long returnAggregateQuantity(final String queryString, final Map<String, Object> params);


}
