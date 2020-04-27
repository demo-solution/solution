/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.jalo.Warehouse;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.ProductPriceValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;


/**
 * This class is used to index discount range in Solr. Search result for Discount range is used for Discount Facets.
 *
 * @author rohan_c
 *
 */
public class SparProductDiscountRangeValueProvider extends ProductPriceValueProvider
{
	private static final String DISCOUNT = "discount";
	private FieldNameProvider fieldNameProvider;
	private PriceService priceService;
	private static final String WAREHOUSE = "warehouse";

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final Collection fieldValues = new ArrayList();
		try
		{
			ProductModel product = null;
			if (model instanceof ProductModel)
			{
				product = (ProductModel) model;
			}
			else
			{
				throw new FieldValueProviderException("Cannot evaluate discount of non-product item");
			}

			final Collection<VariantProductModel> variants = product.getVariants();
			// Since Base product do not have any discount associated to them, Its variant (1st) is used for creating discount range
			final VariantProductModel variant = getVariantProduct(variants);

			if (null != variant)
			{
				getFieldValuesWithConfiguredCurrency(indexConfig, indexedProperty, fieldValues, variant);
			}

		}
		catch (final Exception e)
		{
			//LOG.error(e);
			throw new FieldValueProviderException("Cannot evaluate " + indexedProperty.getName() + " using "
					+ super.getClass().getName(), e);
		}
		return fieldValues;
	}

	/**
	 * Getter for Variant (first)
	 *
	 * @param variants
	 * @return VariantProductModel
	 */
	private VariantProductModel getVariantProduct(final Collection<VariantProductModel> variants)
	{
		VariantProductModel variant = null;
		if (!(CollectionUtils.isEmpty(variants)))
		{
			variant = variants.iterator().next();
		}
		return variant;
	}

	/**
	 * This method is used to get the currency configured Discount/Discount range for Solr indexing
	 *
	 * @param indexConfig
	 * @param indexedProperty
	 * @param fieldValues
	 * @param variant
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private void getFieldValuesWithConfiguredCurrency(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Collection fieldValues, final VariantProductModel variant) throws FieldValueProviderException
	{
		for (final CurrencyModel currency : indexConfig.getCurrencies())
		{
			final CurrencyModel sessionCurrency = this.i18nService.getCurrentCurrency();
			try
			{
				this.i18nService.setCurrentCurrency(currency);
				final List prices = this.priceService.getPriceInformationsForProduct(variant);
				if (CollectionUtils.isEmpty(prices))
				{
					continue;
				}

				final Collection fieldNames = this.fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode()
						.toLowerCase());
				final boolean isRangedValue = isRanged(indexedProperty);
				final String warehouseCode = indexedProperty.getValueProviderParameter();

				if (null == warehouseCode || warehouseCode.isEmpty())
				{
					throw new FieldValueProviderException("Cannot evaluate discount without warehouse code");
				}

				for (final Object name : fieldNames)
				{
					final String fieldName = (String) name;
					Object discountValue = null;
					for (final Object obj : prices)
					{
						final PriceInformation price = (PriceInformation) obj;
						final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
						if (null != warehouseCode && isRangedValue && null != priceWarheouse
								&& warehouseCode.equals(priceWarheouse.getCode()))
						{
							discountValue = getFieldValueForDiscountIndexProperty(indexedProperty, currency, price);
							if (null == discountValue)
							{
								return;
							}
							else
							{
								break;
							}
						}
					}
					fieldValues.add(new FieldValue(fieldName, discountValue));
				}
			}
			finally
			{
				this.i18nService.setCurrentCurrency(sessionCurrency);
			}
		}
	}

	private boolean isRanged(IndexedProperty indexedProperty) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This method is used to get the field values for index property Discount for which currency is configured
	 *
	 * @param indexedProperty
	 * @param currency
	 * @param price
	 * @return String
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private Object getFieldValueForDiscountIndexProperty(final IndexedProperty indexedProperty, final CurrencyModel currency,
			final PriceInformation price) throws FieldValueProviderException
	{
		Object discountRangeValue = null;
		List rangeNameList;
		final Double value = getDiscountValue(indexedProperty, price);
		if (0 == value.doubleValue())
		{
			return null;
		}
		rangeNameList = getRangeNameList(indexedProperty, value, currency.getIsocode());
		if (!rangeNameList.isEmpty())
		{
			for (final Object range : rangeNameList)
			{
				final String rangeName = (String) range;
				discountRangeValue = (rangeName == null) ? value : rangeName;
			}
		}
		return discountRangeValue;
	}

	/**
	 * This method is used to return the Discount value depending on the type of index property.
	 *
	 * @param indexedProperty
	 * @param price
	 * @return Double
	 */
	protected Double getDiscountValue(final IndexedProperty indexedProperty, final PriceInformation price)
	{
		Object value;
		value = price.getQualifierValue(DISCOUNT);
		return value == null ? new Double(0) : Double.valueOf(value + "");
	}


	/**
	 * Getter
	 *
	 * @return the fieldNameProvider
	 */
	public FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * Setter
	 *
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	@Override
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * Getter
	 *
	 * @return the priceService
	 */
	public PriceService getPriceService()
	{
		return priceService;
	}

	/**
	 * Setter
	 *
	 * @param priceService
	 *           the priceService to set
	 */
	@Override
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

}
