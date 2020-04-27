/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

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
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is used to create facet for Brand
 *
 * @author rohan_c
 *
 */
public class SparBrandFacetValuesProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{

	private FieldNameProvider fieldNameProvider;

	/**
	 * This method is used to push Brand data in Solr for 1st Variant product
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexconfig, final IndexedProperty indexedproperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			ProductModel product = (ProductModel) model;
			final Collection<VariantProductModel> variants = product.getVariants();
			// Since Base product do not have any Brand associated to them, Its variant (1st) is used for creating price range
			if (null != variants && !variants.isEmpty())
			{
				product = variants.iterator().next();
			}

			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			final String brand = product.getBrand(Locale.ENGLISH);

			if (brand != null && !brand.isEmpty())
			{
				fieldValues.addAll(createFieldValue(brand, indexedproperty));
			}

			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot get brand of non-product item");
		}
	}

	/**
	 * This method is used to create field values for Solr
	 *
	 * @param brand
	 * @param indexedProperty
	 * @return List<FieldValue>
	 */
	protected List<FieldValue> createFieldValue(final String brand, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, brand));
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


}
