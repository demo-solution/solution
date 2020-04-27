/**
 *
 */
package com.spar.hcl.core.stock;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;



/**
 * Service that collects functionality for stock levels related with point of service (warehouse)
 */
public interface SparCommerceStockService extends CommerceStockService
{

	/**
	 * This method is called from Solr value provider for warehouse specific stocks
	 *
	 * @param product
	 * @param baseStore
	 * @param warehouseCode
	 * @return Long
	 */
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore,
			final String warehouseCode);

	/**
	 * This method is called from Solr value provider for warehouse specific stock status
	 *
	 * @param product
	 * @param baseStore
	 * @param warehouseCode
	 * @return StockLevelStatus
	 */
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(ProductModel product, BaseStoreModel baseStore,
			final String warehouseCode);

}
