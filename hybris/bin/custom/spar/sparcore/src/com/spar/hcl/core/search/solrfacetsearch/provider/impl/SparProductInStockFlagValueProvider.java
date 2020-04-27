/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductInStockFlagValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.stock.SparCommerceStockService;


/**
 * This value provider is an extension of ProductInStockFlagValueProvider to get the variant data indexed in Solr. The
 * index property is warehouse specific.
 *
 * @author rohan_c
 *
 */
public class SparProductInStockFlagValueProvider extends ProductInStockFlagValueProvider
{
	/**
	 * This method is used to get the field values for indexing stock
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty, Object model)
			throws FieldValueProviderException
	{

		if (model instanceof ProductModel)
		{
			final ProductModel product = (ProductModel) model;
			final Collection<VariantProductModel> variants = product.getVariants();
			// Since Base product do not have any stock associated to them, Its variant (1st) is used for creating inStock
			if (!CollectionUtils.isEmpty(variants))
			{
				model = variants.iterator().next();
			}
		}
		return super.getFieldValues(indexConfig, indexedProperty, model);
	}

	/**
	 * This method is used to create field values for stock
	 */
	@Override
	protected List<FieldValue> createFieldValue(final ProductModel product, final BaseStoreModel baseStore,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (baseStore != null)
		{
			addFieldValues(fieldValues, indexedProperty, Boolean.valueOf(isInStock(product, baseStore, indexedProperty)));
		}
		else
		{
			addFieldValues(fieldValues, indexedProperty, Boolean.TRUE);
		}

		return fieldValues;
	}

	/**
	 * This method is used to get the StockLevelStatus using stock service.
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
	 * This method is used check whether a product is in-stock or out of stock.
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
	 * This method is overridden to get custom CommerceStockService
	 */
	@Override
	protected SparCommerceStockService getCommerceStockService()
	{
		return (SparCommerceStockService) super.getCommerceStockService();
	}
}
