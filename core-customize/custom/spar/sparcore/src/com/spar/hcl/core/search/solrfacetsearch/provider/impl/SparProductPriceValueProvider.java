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
import de.hybris.platform.solrfacetsearch.config.ValueRangeType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.ProductPriceValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.model.SparVariantProductModel;


/**
 * This class is used to index pricevalue (indexproperty) for base products that are warehouse specific in Solr.
 *
 * @author rohan_c
 *
 */
public class SparProductPriceValueProvider extends ProductPriceValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private PriceService priceService;

	private static final String WAREHOUSE = "warehouse";
	private static final String CHECK_FOR_PROMOTION = "checkForPromotion";
	private static final String COMBI_OFFER = "combiOffer";
	private static final String BEST_DEAL = "bestDeal";
	private static final String REGULAR_OFFER = "regularOffer";

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
				throw new FieldValueProviderException("Cannot evaluate price of non-product item");
			}

			final Collection<VariantProductModel> variants = product.getVariants();
			// Since Base product do not have any price associated to them, Its variant (1st) is used for creating price range
			final VariantProductModel variant = getVariantProduct(variants, indexConfig, indexedProperty);

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
	//private VariantProductModel getVariantProduct(final Collection<VariantProductModel> variants)
	//	{
	//	VariantProductModel variant = null;
	//	if (!(CollectionUtils.isEmpty(variants)))
	//	{
	//		variant = variants.iterator().next();
	//	}
	//	return variant;
	//	}

	private VariantProductModel getVariantProduct(final Collection<VariantProductModel> variants, final IndexConfig indexConfig,
			final IndexedProperty indexedProperty)
	{
		VariantProductModel variant = null;
		if (!(CollectionUtils.isEmpty(variants)))
		{
			variant = variants.iterator().next();
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
									final Boolean promoCheck = (Boolean) (price).getQualifiers().get(CHECK_FOR_PROMOTION);
									final Boolean bestDeal = (Boolean) (price).getQualifiers().get(BEST_DEAL);
									final Boolean combiOffer = (Boolean) (price).getQualifiers().get(COMBI_OFFER);
									final Boolean regularOffer = (Boolean) (price).getQualifiers().get(REGULAR_OFFER);
									if (indexedProperty.getValueProviderParameter().equals(warehouse))
									{
										if (null != promoCheck && promoCheck.booleanValue())
										{
											if (null != bestDeal && bestDeal.booleanValue())
											{
												variant = variantProductModel;
												break;
											}
											if (null != combiOffer && combiOffer.booleanValue())
											{
												variant = variantProductModel;

											}
											if (null != regularOffer && regularOffer.booleanValue())
											{
												variant = variantProductModel;

											}
										}
									}
									/*
									 * priceValue = priceValue + warehouse +
									 * SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER + value +
									 * SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER;
									 */
								}

							}
							/*
							 * else { final BaseSiteModel baseSiteModel = indexConfig.getBaseSite(); if (baseSiteModel != null
							 * && baseSiteModel.getStores() != null && !baseSiteModel.getStores().isEmpty()) { priceValue = "";
							 * final BaseStoreModel baseStore = baseSiteModel.getStores().get(0); for (final WarehouseModel
							 * warehouse : baseStore.getWarehouses()) { priceValue = priceValue + warehouse.getCode() +
							 * SparCoreConstants.SparValueProviderConstants.FIELD_VALUE_DELIMETER + "0" +
							 * SparCoreConstants.SparValueProviderConstants.POS_LEVEL_DELIMETER; }
							 * 
							 * } }
							 */
						}
						finally
						{
							this.i18nService.setCurrentCurrency(sessionCurrency);
						}
					}

				}
			}
		}
		return variant;
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
			throw new FieldValueProviderException("Cannot evaluate price without warehouse code");
		}
		for (final Object field : fieldNames)
		{
			final String fieldName = (String) field;
			Object priceValue = null;
			for (final Object obj : prices)
			{
				final PriceInformation price = (PriceInformation) obj;
				final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
				if (null != warehouseCode && null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
				{
					priceValue = getFieldValueForPriceValueIndexPropertyWithoutCurrency(indexedProperty, price);
					break;
				}
			}
			fieldValues.add(new FieldValue(fieldName, priceValue));
		}
	}


	/**
	 * This method is used to get the field values for index property Price for which currency is not configured
	 *
	 * @param indexedProperty
	 * @param price
	 * @return String
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private Object getFieldValueForPriceValueIndexPropertyWithoutCurrency(final IndexedProperty indexedProperty,
			final PriceInformation price) throws FieldValueProviderException
	{
		Object priceValue = null;
		List rangeNameList;
		final Object value = getPriveValue(indexedProperty, price);
		rangeNameList = getRangeNameList(indexedProperty, value);
		if (rangeNameList.isEmpty())
		{
			priceValue = value;
		}
		return priceValue;
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
					Object priceValue = null;
					for (final Object obj : prices)
					{
						final PriceInformation price = (PriceInformation) obj;
						final Warehouse priceWarheouse = (Warehouse) (price).getQualifiers().get(WAREHOUSE);
						if (null != warehouseCode && null != priceWarheouse && warehouseCode.equals(priceWarheouse.getCode()))
						{
							priceValue = getFieldValueForPriceValueIndexProperty(indexedProperty, currency, price);
							break;
						}
					}
					fieldValues.add(new FieldValue(fieldName, priceValue));
				}
			}
			finally
			{
				this.i18nService.setCurrentCurrency(sessionCurrency);
			}
		}
	}


	/**
	 * This method is used to return the price value depending on the type of index property.
	 *
	 * @param indexedProperty
	 * @param price
	 * @return Object (String/Double)
	 */
	protected Object getPriveValue(final IndexedProperty indexedProperty, final PriceInformation price)
	{
		Object value;
		if (ValueRangeType.STRING.toString().equalsIgnoreCase(indexedProperty.getType()))
		{
			value = price.getPriceValue().getValue() + "";
		}
		else
		{
			value = Double.valueOf(price.getPriceValue().getValue());
		}
		return value;
	}

	/**
	 * This method is used to get the field values for index property Price Value for which currency is configured
	 *
	 * @param indexedProperty
	 * @param currency
	 * @param price
	 * @return String
	 * @throws FieldValueProviderException
	 */
	@SuppressWarnings("deprecation")
	private Object getFieldValueForPriceValueIndexProperty(final IndexedProperty indexedProperty, final CurrencyModel currency,
			final PriceInformation price) throws FieldValueProviderException
	{
		Object val = null;
		List rangeNameList;
		final Object value = getPriveValue(indexedProperty, price);
		rangeNameList = getRangeNameList(indexedProperty, value, currency.getIsocode());
		if (rangeNameList.isEmpty())
		{
			val = value;
		}
		return val;
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
