package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.constants.SparCoreConstants;
import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.stock.SparCommerceStockService;


/**
 * This class is used for indexing Stock Level of variants for the corresponding products.
 *
 * @author rohan_c
 *
 */
public class SparVariantsStockValueProvider extends AbstractPropertyFieldValueProvider implements Serializable,
		FieldValueProvider
{
	private static final String ERROR_MESSAGE = "Cannot evaluate variant's stock of non-product item";
	private FieldNameProvider fieldNameProvider;
	private SparCommerceStockService commerceStockService;
	private IndexConfig indexConfig;

	/**
	 * This method is used to get the FieldValue for StockLevel of Variants and push it into Solr
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		ProductModel productModel;
		this.indexConfig = indexConfig;

		if (model instanceof ProductModel)
		{
			productModel = (ProductModel) model;
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			fieldValues.addAll(createFieldValue(productModel.getVariants(), indexedProperty));
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException(ERROR_MESSAGE);
		}
	}

	/**
	 *
	 * This method creates index value for Variants
	 *
	 * @param variants
	 * @param indexedProperty
	 * @return List<FieldValue>
	 */
	protected List<FieldValue> createFieldValue(final Collection<VariantProductModel> variants,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		for (final VariantProductModel variantProductModel : variants)
		{
			if (variantProductModel instanceof SparVariantProductModel)
			{
				addFieldValues(fieldValues, indexedProperty, getStockLevel(variantProductModel));
			}
		}
		return fieldValues;
	}

	/**
	 * This method is used to get the StockLevel in a particular base store
	 *
	 * @param product
	 * @param baseStore
	 * @return String
	 */
	protected String getProductStockLevel(final ProductModel product, final BaseStoreModel baseStore)
	{
		final List<WarehouseModel> warehouseList = baseStore.getWarehouses();
		final StringBuilder stockValue = new StringBuilder();
		for (final WarehouseModel warehouse : warehouseList)
		{
			final String warehouseCode = warehouse.getCode();
			Long stockLevel = getCommerceStockService().getStockLevelForProductAndBaseStore(product, baseStore, warehouseCode);
			stockLevel = null == stockLevel ? Long.valueOf(0L) : stockLevel;
			stockValue.append(warehouseCode + SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER + stockLevel
					+ SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER);
		}

		if (stockValue.length() > 0)
		{
			return stockValue.substring(0, stockValue.length() - 1);
		}
		return stockValue.toString();
	}

	/**
	 * This method is used to get the StockLevel
	 *
	 * @param product
	 * @return String
	 */
	protected String getStockLevel(final ProductModel product)
	{
		final BaseSiteModel baseSiteModel = indexConfig.getBaseSite();
		if (baseSiteModel != null && baseSiteModel.getStores() != null && !baseSiteModel.getStores().isEmpty()
				&& getCommerceStockService().isStockSystemEnabled(baseSiteModel.getStores().get(0)))
		{
			return getProductStockLevel(product, baseSiteModel.getStores().get(0));
		}
		else
		{
			return "0";
		}
	}



	/**
	 * This method adds the fieldValue for the respective indexProperty
	 *
	 * @param fieldValues
	 * @param indexedProperty
	 * @param value
	 */
	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	/**
	 * Getter for FieldNameProvider
	 *
	 * @return FieldNameProvider
	 */
	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * Setter for FieldNameProvider
	 *
	 * @param fieldNameProvider
	 */
	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * This method is used to get custom CommerceStockService
	 *
	 * @return CommerceStockService
	 */
	protected SparCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final SparCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}


}
