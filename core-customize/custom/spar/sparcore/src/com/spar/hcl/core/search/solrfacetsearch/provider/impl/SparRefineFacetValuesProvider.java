/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.jalo.Warehouse;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.util.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Iterables;


/**
 * This class is used to create facet values for New Item Arrival & Product on offer
 *
 * @author rohan_c
 *
 */
public class SparRefineFacetValuesProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	/**
	 *
	 */
	private static final String CHECK_FOR_PROMOTION = "checkForPromotion";
	// mapping in project.properties
	private static final String FACET_KEY_NEW_ARRIVAL = "sparcore.facet.newArrival";
	private static final String FACET_VALUE_NEW_ARRIVAL = "New Arrival";
	private static final String FACET_KEY_PROMO_CHECK = "sparcore.facet.promocheck";
	private static final String FACET_VALUE_PROMO_CHECK = "Product on Offer";
	private static final String WAREHOUSE = "warehouse";
	private FieldNameProvider fieldNameProvider;
	private PriceService priceService;

	/**
	 * This method is overriden to pass variant products for indexing.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexconfig, final IndexedProperty indexedproperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			final ProductModel product = (ProductModel) model;
			// Since Base product is not enriched, its variant (1st) is used for creating New Arrival/ProductOnOffer
			final ProductModel variant = Iterables.get(product.getVariants(), 0, product);

			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			addNewArrival(indexedproperty, variant, fieldValues);
			addProductOnOffer(indexconfig, indexedproperty, variant, fieldValues);
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot get New Arrival of non-product item");
		}
	}

	/**
	 * This method is used to add new arrival facet values
	 *
	 * @param indexedproperty
	 * @param product
	 * @param fieldValues
	 */
	private void addNewArrival(final IndexedProperty indexedproperty, final ProductModel product,
			final Collection<FieldValue> fieldValues)
	{
		final Boolean isNewArrival = product.getNewItem();

		if (null != isNewArrival)
		{
			fieldValues.addAll(createFieldValue(isNewArrival, indexedproperty, FACET_KEY_NEW_ARRIVAL, FACET_VALUE_NEW_ARRIVAL));
		}
	}

	/**
	 * This method is used to add product in Offer facet values
	 *
	 * @param indexConfig
	 * @param indexedproperty
	 * @param product
	 * @param fieldValues
	 * @throws FieldValueProviderException
	 */
	private void addProductOnOffer(final IndexConfig indexConfig, final IndexedProperty indexedproperty,
			final ProductModel product, final Collection<FieldValue> fieldValues) throws FieldValueProviderException
	{
		final Collection<PriceRowModel> listPriceRowModel = product.getEurope1Prices();
		if (null != listPriceRowModel)
		{
			Boolean isProductOnOffer = Boolean.FALSE;

			// Since Base product do not have any price associated to them, Its variant (1st) is used for creating price range

			if (indexConfig.getCurrencies().isEmpty())
			{
				final List prices = this.priceService.getPriceInformationsForProduct(product);
				if (!CollectionUtils.isEmpty(prices))
				{
					isProductOnOffer = getFieldValuesWithoutConfiguredCurrency(indexedproperty, prices);
				}
			}
			else
			{
				isProductOnOffer = getFieldValuesWithConfiguredCurrency(indexConfig, indexedproperty, product);
			}

			if (null != isProductOnOffer && isProductOnOffer.booleanValue())
			{
				fieldValues
						.addAll(createFieldValue(isProductOnOffer, indexedproperty, FACET_KEY_PROMO_CHECK, FACET_VALUE_PROMO_CHECK));
			}
		}
	}


	/**
	 * This method is used to get the field values for prices for which currency is not configured
	 *
	 * @param indexedProperty
	 * @param prices
	 * @return Boolean
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private Boolean getFieldValuesWithoutConfiguredCurrency(final IndexedProperty indexedProperty, final List prices)
			throws FieldValueProviderException
	{
		final String warehouseCode = indexedProperty.getValueProviderParameter();

		if (null == warehouseCode || warehouseCode.isEmpty())
		{
			throw new FieldValueProviderException("Cannot evaluate promoCheck without warehouse code");
		}
		Boolean checkForPromo = Boolean.FALSE;
		for (final Object obj : prices)
		{
			final PriceInformation price = (PriceInformation) obj;
			final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
			if (null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
			{
				checkForPromo = (Boolean) price.getQualifierValue(CHECK_FOR_PROMOTION);
				checkForPromo = checkForPromo == null ? Boolean.FALSE : checkForPromo;
				return checkForPromo;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * This method is used to get the currency configured Price/Price range for Solr indexing
	 *
	 * @param indexConfig
	 * @param indexedProperty
	 * @param variant
	 * @return Boolean
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private Boolean getFieldValuesWithConfiguredCurrency(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final ProductModel variant) throws FieldValueProviderException
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

				final String warehouseCode = indexedProperty.getValueProviderParameter();

				if (null == warehouseCode || warehouseCode.isEmpty())
				{
					throw new FieldValueProviderException("Cannot evaluate price without warehouse code");
				}

				Boolean checkForPromo = null;
				for (final Object obj : prices)
				{
					final PriceInformation price = (PriceInformation) obj;
					final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
					if (null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
					{
						checkForPromo = (Boolean) price.getQualifierValue(CHECK_FOR_PROMOTION);
						checkForPromo = checkForPromo == null ? Boolean.FALSE : checkForPromo;
						return checkForPromo;
					}
				}
			}
			finally
			{
				this.i18nService.setCurrentCurrency(sessionCurrency);
			}
		}
		return Boolean.FALSE;
	}


	/**
	 * This method is used to create field values for Solr
	 *
	 * @param isfacetValue
	 * @param indexedProperty
	 * @param facetKey
	 * @param defaultFacetValue
	 * @return List<FieldValue>
	 */
	protected List<FieldValue> createFieldValue(final Boolean isfacetValue, final IndexedProperty indexedProperty,
			final String facetKey, final String defaultFacetValue)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (isfacetValue.booleanValue())
		{
			final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
			for (final String fieldName : fieldNames)
			{
				fieldValues.add(new FieldValue(fieldName, Config.getString(facetKey, defaultFacetValue)));
			}
		}
		return fieldValues;
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
