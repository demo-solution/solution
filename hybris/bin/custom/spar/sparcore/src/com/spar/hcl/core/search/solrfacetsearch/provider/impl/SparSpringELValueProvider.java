/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.SpELValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;


/**
 * This class is used to index the name of 1st variant into Solr for base products. This helps in sorting of products on
 * PLP.
 *
 * @author rohan_c
 *
 */
public class SparSpringELValueProvider extends SpELValueProvider
{

	/**
	 * This method pushes name of first variant in base product index to resolve sorting issues.
	 */
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{

		ProductModel product = (ProductModel) model;
		final Collection<VariantProductModel> variants = product.getVariants();
		// Since Base product do not have any Name associated to them, Its variant (1st) is used for creating Name
		if (null != variants && !variants.isEmpty())
		{
			product = variants.iterator().next();
		}
		return super.getFieldValues(indexConfig, indexedProperty, product);
	}
}
