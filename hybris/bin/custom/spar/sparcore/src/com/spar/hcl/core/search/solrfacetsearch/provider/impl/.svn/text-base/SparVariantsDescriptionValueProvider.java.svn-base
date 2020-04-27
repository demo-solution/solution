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

import com.spar.hcl.core.model.SparVariantProductModel;


/**
 * This class is used for indexing Description of variants for the corresponding products.
 *
 * @author rohan_c
 *
 */
public class SparVariantsDescriptionValueProvider extends AbstractPropertyFieldValueProvider implements Serializable,
		FieldValueProvider
{
	private static final String ERROR_MESSAGE = "Cannot evaluate variant's description of non-product item";
	private FieldNameProvider fieldNameProvider;

	/**
	 * This method is used to get FieldValue for description of Variants and push it into Solr
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
				//This has been commented out after the decision that Variant description should come using FunctionalName+variant coming from BrandBank in S1 - rohan_c
				//addFieldValues(fieldValues, indexedProperty, variantProductModel.getERPDescription(Locale.ENGLISH));

				final String functionalname = variantProductModel.getFunctionalName(Locale.ENGLISH) == null ? ""
						: variantProductModel.getFunctionalName(Locale.ENGLISH);
				final String variant = variantProductModel.getVariant(Locale.ENGLISH) == null ? "" : variantProductModel
						.getVariant(Locale.ENGLISH);
				final String description = functionalname + " " + variant;

				addFieldValues(fieldValues, indexedProperty, description);
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

}
