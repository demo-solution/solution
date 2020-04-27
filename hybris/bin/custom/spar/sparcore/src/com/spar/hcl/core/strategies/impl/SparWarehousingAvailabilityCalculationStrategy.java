/**
 *
 */
package com.spar.hcl.core.strategies.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.atp.dao.AvailableToPromiseDao;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingAvailabilityCalculationStrategy;

/**
 * This class is used to customize the caluclation strategy for stock by
 * removing reserved quantity check.
 *
 * @author nileshkumar.c/Rohan_c
 *
 */
public class SparWarehousingAvailabilityCalculationStrategy extends WarehousingAvailabilityCalculationStrategy {

	/**
	 * This method is used to customize the caluclation strategy for stock by
	 * removing reserved quantity check.
	 */
	private AvailableToPromiseDao availableToPromiseDao;

	/**
	 * This method is used to customize the calculation strategy for stock by
	 * removing reserved quantity check.
	 */
	@Override
	public Long calculateAvailability(final Collection<StockLevelModel> stockLevels) {
		if (stockLevels.isEmpty()) {
			return Long.valueOf(0L);
		}

		final Map params = setupParameters(stockLevels);

		Long availquantity = null;
		Long alocquantity = null;
		Long cancelquantity = null;
		availquantity = alocquantity = cancelquantity = Long.valueOf(0);

		availquantity = getAvailableToPromiseDao().getAvailabilityForStockLevels(params);

		if (null != getAvailableToPromiseDao().getAllocationQuantityForStockLevels(params)) {
			alocquantity = getAvailableToPromiseDao().getAllocationQuantityForStockLevels(params);
		}
		if (null != getAvailableToPromiseDao().getCancellationQuantityForStockLevels(params)) {
			cancelquantity = getAvailableToPromiseDao().getCancellationQuantityForStockLevels(params);
		}
		return Long.valueOf(availquantity.longValue() - alocquantity.longValue() + cancelquantity.longValue());
	}

	protected Map<String, Object> setupParameters(Collection<StockLevelModel> stockLevels) {
		Map<String, Object> params = new HashMap();
		params.put("stockLevels", stockLevels);
		return params;
	}

	public AvailableToPromiseDao getAvailableToPromiseDao() {
		return availableToPromiseDao;
	}

	public void setAvailableToPromiseDao(AvailableToPromiseDao availableToPromiseDao) {
		this.availableToPromiseDao = availableToPromiseDao;
	}

}
