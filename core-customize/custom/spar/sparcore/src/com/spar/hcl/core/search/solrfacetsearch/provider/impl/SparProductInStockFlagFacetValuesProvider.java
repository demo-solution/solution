/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductInStockFlagValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.spar.hcl.core.stock.SparCommerceStockService;


/**
 * This class is used to have in stock facets for SPAR and extends ProductInStockFlagValueProvider. This property is
 * warehouse specific.
 *
 * @author rohan_c
 *
 */
public class SparProductInStockFlagFacetValuesProvider extends ProductInStockFlagValueProvider
{
	// mapping in project.properties
	private static final String FACET_KEY = "sparcore.facet.instock";
	private static final String FACET_VALUE = "Show In-Stock items Only";


	/**
	 * This method is used to push variant stock in solr for in stock facets
	 */
	@Override
	protected List<FieldValue> createFieldValue(ProductModel product, final BaseStoreModel baseStore,
			final IndexedProperty indexedProperty)
	{
		final Collection<VariantProductModel> variants = product.getVariants();
		// Since Base product do not have any stock associated to them, Its variant (1st) is used for creating inStock
		if (null != variants && !variants.isEmpty())
		{
			product = variants.iterator().next();
		}

		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();

		if (null != baseStore && isInStock(product, baseStore, indexedProperty))
		{
			addFieldValues(fieldValues, indexedProperty, Config.getString(FACET_KEY, FACET_VALUE));
		}
		return fieldValues;
	}

	/**
	 * This method is used to invoke stock service to index stockstatus w.r.t. warehouse
	 *
	 * @param product
	 * @param baseStore
	 * @param warehouseCode
	 * @return StockLevelStatus
	 */
	protected StockLevelStatus getProductStockLevelStatus(final ProductModel product, final BaseStoreModel baseStore,
			final String warehouseCode)
	{
		return getCommerceStockService().getStockLevelStatusForProductAndBaseStore(product, baseStore, warehouseCode);
	}

	/**
	 * This method is used to check in stock and OOS for stock.
	 *
	 * @param product
	 * @param baseStore
	 * @param indexedProperty
	 * @return boolean
	 */
	protected boolean isInStock(final ProductModel product, final BaseStoreModel baseStore, final IndexedProperty indexedProperty)
	{
		final String warehouseCode = indexedProperty.getValueProviderParameter();
		return isInStock(getProductStockLevelStatus(product, baseStore, warehouseCode));
	}

	/**
	 * This method is overridden to get custom CommerceStockService (SparCommerceStockService)
	 */
	@Override
	protected SparCommerceStockService getCommerceStockService()
	{
		return (SparCommerceStockService) super.getCommerceStockService();
	}
}
