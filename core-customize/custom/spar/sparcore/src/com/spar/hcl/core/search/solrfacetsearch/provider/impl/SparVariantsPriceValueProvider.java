package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.jalo.Warehouse;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.PriceService;
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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.constants.SparCoreConstants;
import com.spar.hcl.core.model.SparVariantProductModel;


/**
 * This class is used for indexing CSP of variants for the corresponding products.
 *
 * @author rohan_c
 *
 */
public class SparVariantsPriceValueProvider extends AbstractPropertyFieldValueProvider implements Serializable,
		FieldValueProvider
{
	private static final String ERROR_MESSAGE = "Cannot evaluate variant's price of non-product item";
	private FieldNameProvider fieldNameProvider;
	private PriceService priceService;
	private static final String WAREHOUSE = "warehouse";

	/**
	 * This method is used to get the FieldValue for price (CSP/MRP) of a variants and push it into Solr
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		ProductModel productModel;
		if (model instanceof ProductModel)
		{
			productModel = (ProductModel) model;
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			fieldValues.addAll(createFieldValue(productModel.getVariants(), indexedProperty, indexConfig));
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
	 * @param indexConfig
	 * @return List<FieldValue>
	 */
	@SuppressWarnings("deprecation")
	protected List<FieldValue> createFieldValue(final Collection<VariantProductModel> variants,
			final IndexedProperty indexedProperty, final IndexConfig indexConfig)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		for (final VariantProductModel variantProductModel : variants)
		{
			if (variantProductModel instanceof SparVariantProductModel)
			{

				PriceInformation price = null;
				Double value = null;
				String priceValue = null;
				for (final CurrencyModel currency : indexConfig.getCurrencies())
				{
					final CurrencyModel sessionCurrency = this.i18nService.getCurrentCurrency();
					try
					{
						this.i18nService.setCurrentCurrency(currency);
						final List prices = this.priceService.getPriceInformationsForProduct(variantProductModel);
						if (!CollectionUtils.isEmpty(prices))
						{
							priceValue = "";
							for (final Object obj : prices)
							{
								price = (PriceInformation) obj;
								value = Double.valueOf(price.getPriceValue().getValue());
								final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
								final String warehouse = priceWarheouse == null ? null : priceWarheouse.getCode();
								priceValue = priceValue + warehouse + SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER
										+ value + SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER;
							}
							addFieldValues(indexedProperty, fieldValues, priceValue);
						}
						else
						{
							final BaseSiteModel baseSiteModel = indexConfig.getBaseSite();
							if (baseSiteModel != null && baseSiteModel.getStores() != null && !baseSiteModel.getStores().isEmpty())
							{
								priceValue = "";
								final BaseStoreModel baseStore = baseSiteModel.getStores().get(0);
								for (final WarehouseModel warehouse : baseStore.getWarehouses())
								{
									priceValue = priceValue + warehouse.getCode()
											+ SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER + "0"
											+ SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER;
								}
								addFieldValues(indexedProperty, fieldValues, priceValue);
							}
						}
					}
					finally
					{
						this.i18nService.setCurrentCurrency(sessionCurrency);
					}
				}

			}
		}
		return fieldValues;
	}

	/**
	 * This method is used to add the variants price in solr
	 *
	 * @param indexedProperty
	 * @param fieldValues
	 * @param priceValue
	 */
	protected void addFieldValues(final IndexedProperty indexedProperty, final List<FieldValue> fieldValues, String priceValue)
	{
		if (priceValue.length() > 0)
		{
			priceValue = priceValue.substring(0, priceValue.length() - 1);
		}
		addFieldValues(fieldValues, indexedProperty, priceValue);
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
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

}
