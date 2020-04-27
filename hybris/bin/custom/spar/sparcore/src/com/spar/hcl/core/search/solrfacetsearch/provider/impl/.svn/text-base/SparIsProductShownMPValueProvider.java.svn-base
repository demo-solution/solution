/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.CategorySource;
import de.hybris.platform.core.model.product.ProductModel;
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
import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.service.cart.SparCartService;


/**
 * @author rohan_c
 *
 */
public class SparIsProductShownMPValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
		Serializable
{
	private SparCartService sparCartService;
	private CategorySource categorySource;
	private FieldNameProvider fieldNameProvider;

	protected CategorySource getCategorySource()
	{
		return categorySource;
	}

	@Required
	public void setCategorySource(final CategorySource categorySource)
	{
		this.categorySource = categorySource;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			ProductModel product = (ProductModel) model;
			final Collection<VariantProductModel> variants = product.getVariants();
			// Since Base product do not have any stock associated to them, Its variant (1st) is used for creating inStock
			if (!CollectionUtils.isEmpty(variants))
			{
				product = variants.iterator().next();
			}
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			final Collection<CategoryModel> categories = getCategorySource().getCategoriesForConfigAndProperty(indexConfig,
					indexedProperty, model);
			final CategoryModel firstLevelCategory = getSparCartService().getFirstLevelCategory(categories);

			fieldValues.addAll(createFieldValue(firstLevelCategory.getIsProductShownMP(), indexedProperty));

			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot get isProductShownMP of non-product item");
		}
	}

	protected List<FieldValue> createFieldValue(final Boolean value, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		addFieldValues(fieldValues, indexedProperty, value);

		return fieldValues;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	/**
	 * @return the fieldNameProvider
	 */
	public FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	/**
	 * @param fieldNameProvider
	 *           the fieldNameProvider to set
	 */
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	/**
	 * @return the sparCartService
	 */
	public SparCartService getSparCartService()
	{
		return sparCartService;
	}

	/**
	 * @param sparCartService
	 *           the sparCartService to set
	 */
	public void setSparCartService(final SparCartService sparCartService)
	{
		this.sparCartService = sparCartService;
	}

}
