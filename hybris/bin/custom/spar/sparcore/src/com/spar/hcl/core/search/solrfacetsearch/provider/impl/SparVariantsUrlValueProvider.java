package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.model.SparVariantProductModel;


/**
 * This class is used for Url of variants for the corresponding products.
 *
 * @author rohan_c
 *
 */
public class SparVariantsUrlValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private static final String ERROR_MESSAGE = "Cannot evaluate variant's URL of non-product item";
	private UrlResolver<ProductModel> urlResolver;
	private FieldNameProvider fieldNameProvider;
	private CommonI18NService commonI18NService;

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	protected UrlResolver<ProductModel> getUrlResolver()
	{
		return urlResolver;
	}

	@Required
	public void setUrlResolver(final UrlResolver<ProductModel> urlResolver)
	{
		this.urlResolver = urlResolver;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * This method is used to get the FieldValues for URL.
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			final ProductModel product = (ProductModel) model;

			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();

			if (indexedProperty.isLocalized())
			{
				final Collection<LanguageModel> languages = indexConfig.getLanguages();
				for (final LanguageModel language : languages)
				{
					fieldValues.addAll(createFieldValue(product.getVariants(), language, indexedProperty));
				}
			}
			else
			{
				fieldValues.addAll(createFieldValue(product.getVariants(), null, indexedProperty));
			}
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
	protected List<FieldValue> createFieldValue(final Collection<VariantProductModel> variants, final LanguageModel language,
			final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		for (final VariantProductModel variantProductModel : variants)
		{
			if (variantProductModel instanceof SparVariantProductModel)
			{
				final String variantProductUrl = getVariantProductUrl(variantProductModel, language);
				if (variantProductUrl != null)
				{
					addFieldValues(fieldValues, indexedProperty, language, variantProductUrl);
				}
			}
		}
		return fieldValues;
	}

	/**
	 * This method adds the fieldValue for the respective indexProperty
	 *
	 * @param fieldValues
	 * @param indexedProperty
	 * @param value
	 */
	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final LanguageModel language, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				language == null ? null : language.getIsocode());
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	/**
	 * This method is used to get the URL for a product
	 *
	 * @param product
	 * @param language
	 * @return
	 */
	protected String getVariantProductUrl(final ProductModel product, final LanguageModel language)
	{
		i18nService.setCurrentLocale(commonI18NService.getLocaleForLanguage(language));
		return getUrlResolver().resolve(product);
	}
}
