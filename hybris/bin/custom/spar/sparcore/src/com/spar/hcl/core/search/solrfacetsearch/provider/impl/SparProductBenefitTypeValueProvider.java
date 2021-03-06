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
 * This class is used to index benefit type (indexproperty) for base products that are warehouse specific in Solr.
 *
 */
public class SparProductBenefitTypeValueProvider extends ProductPriceValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private PriceService priceService;

	private static final String BENEFIT_TYPE = "benefitType";
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
				throw new FieldValueProviderException("Cannot evaluate non-product item");
			}

			final Collection<VariantProductModel> variants = product.getVariants();
			// Check values of all variants if any variant have benefit type
			for (final VariantProductModel variant : variants)
			{
				if (null != variant && indexConfig.getCurrencies().isEmpty())
				{
					final List prices = this.priceService.getPriceInformationsForProduct(variant);
					if (!CollectionUtils.isEmpty(prices))
					{
						getFieldValuesWithoutConfiguredCurrency(indexedProperty, fieldValues, prices);
						return fieldValues;
					}
				}
				if (null != variant)
				{
					getFieldValuesWithConfiguredCurrency(indexConfig, indexedProperty, fieldValues, variant);
				}
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
	 * This method is used to get the field values for prices for which currency is not configured
	 *
	 * @param indexedProperty
	 * @param fieldValues
	 * @param prices
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private void getFieldValuesWithoutConfiguredCurrency(final IndexedProperty indexedProperty, final Collection fieldValues,
			final List prices) throws FieldValueProviderException
	{
		final Collection fieldNames = this.fieldNameProvider.getFieldNames(indexedProperty, ((PriceInformation) prices.get(0))
				.getPriceValue().getCurrencyIso());
		final String warehouseCode = indexedProperty.getValueProviderParameter();

		if (null == warehouseCode || warehouseCode.isEmpty())
		{
			throw new FieldValueProviderException("Cannot evaluate benefit Type without warehouse code");
		}
		for (final Object field : fieldNames)
		{
			final String fieldName = (String) field;
			String benefitType = null;
			for (final Object obj : prices)
			{
				final PriceInformation price = (PriceInformation) obj;
				//To make entry warehouse specific
				final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
				if (null != warehouseCode && null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
				{
					if (null != (price).getQualifiers() && null != (price).getQualifiers().get(BENEFIT_TYPE))
					{
						benefitType = (price).getQualifiers().get(BENEFIT_TYPE).toString();
						break;
					}
				}
			}
			fieldValues.add(new FieldValue(fieldName, benefitType));
		}
	}

	/**
	 * This method is used to get the currency configured Price/Price range for Solr indexing
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
				final String warehouseCode = indexedProperty.getValueProviderParameter();

				if (null == warehouseCode || warehouseCode.isEmpty())
				{
					throw new FieldValueProviderException("Cannot evaluate price without warehouse code");
				}

				for (final Object name : fieldNames)
				{
					final String fieldName = (String) name;
					String benefitType = null;
					for (final Object obj : prices)
					{
						final PriceInformation price = (PriceInformation) obj;
						final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
						if (null != warehouseCode && null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
						{
							if (null != (price).getQualifiers().get(BENEFIT_TYPE))
							{
								benefitType = (price).getQualifiers().get(BENEFIT_TYPE).toString();
								break;
							}
						}
					}
					fieldValues.add(new FieldValue(fieldName, benefitType));
				}
			}
			finally
			{
				this.i18nService.setCurrentCurrency(sessionCurrency);
			}
		}
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
