/**
 *
 */
package com.spar.hcl.core.stock.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;

import com.spar.hcl.core.stock.SparCommerceStockService;
import com.spar.hcl.core.strategies.impl.SparWarehousingAvailabilityCalculationStrategy;
import com.spar.hcl.warehousing.atp.strategy.SparWarehouseSelectionStrategy;


/**
 * @author nileshkumar.c
 *
 */
public class SparDefaultCommerceStockService extends DefaultCommerceStockService implements SparCommerceStockService
{

	private SparWarehousingAvailabilityCalculationStrategy sparWarehousingAvailabilityCalculationStrategy;

	/**
	 * This method is called from Solr value provider for warehouse specific stocks
	 *
	 * @param product
	 * @param baseStore
	 * @param warehouseCode
	 * @return Long
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore,
			final String warehouseCode)
	{
		getWarehouseSelectionStrategy().setWarehouseCode(warehouseCode);
		return getStockLevelForProductAndBaseStore(product, baseStore);
	}

	/**
	 * This method is overridden to user extended WarehouseSelectionStrategy to get the correct warehouse so that proper
	 * Calculation on stock can be done.
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		validateParameterNotNull(product, "product cannot be null");
		validateParameterNotNull(baseStore, "baseStore cannot be null");

		return getSparWarehousingAvailabilityCalculationStrategy().calculateAvailability(
				getStockService().getStockLevels(product, getWarehouseSelectionStrategy().getWarehousesForBaseStore(baseStore)));
	}

	@Override
	public Long getStockLevelForProductAndPointOfService(final ProductModel product, final PointOfServiceModel pointOfService)
	{
		validateParameterNotNull(product, "product cannot be null");
		validateParameterNotNull(pointOfService, "pointOfService cannot be null");

		List<WarehouseModel> warehouseList = null;
		warehouseList = getWarehouseSelectionStrategy().getSelectedWarehouseFromPOS(pointOfService);

		if (null == warehouseList || warehouseList.isEmpty())
		{
			return getSparWarehousingAvailabilityCalculationStrategy().calculateAvailability(
					getStockService().getStockLevels(product, pointOfService.getWarehouses()));
		}
		else
		{
			return getSparWarehousingAvailabilityCalculationStrategy().calculateAvailability(
					getStockService().getStockLevels(product, warehouseList));
		}
	}

	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore,
			final String warehouseCode)
	{
		getWarehouseSelectionStrategy().setWarehouseCode(warehouseCode);
		return super.getStockLevelStatusForProductAndBaseStore(product, baseStore);
	}

	/**
	 * @return the sparWarehousingAvailabilityCalculationStrategy
	 */
	public SparWarehousingAvailabilityCalculationStrategy getSparWarehousingAvailabilityCalculationStrategy()
	{
		return sparWarehousingAvailabilityCalculationStrategy;
	}

	/**
	 * @param sparWarehousingAvailabilityCalculationStrategy
	 *           the sparWarehousingAvailabilityCalculationStrategy to set
	 */
	public void setSparWarehousingAvailabilityCalculationStrategy(
			final SparWarehousingAvailabilityCalculationStrategy sparWarehousingAvailabilityCalculationStrategy)
	{
		this.sparWarehousingAvailabilityCalculationStrategy = sparWarehousingAvailabilityCalculationStrategy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService#getWarehouseSelectionStrategy()
	 */
	@Override
	protected SparWarehouseSelectionStrategy getWarehouseSelectionStrategy()
	{
		// YTODO Auto-generated method stub
		return (SparWarehouseSelectionStrategy) super.getWarehouseSelectionStrategy();
	}
}
