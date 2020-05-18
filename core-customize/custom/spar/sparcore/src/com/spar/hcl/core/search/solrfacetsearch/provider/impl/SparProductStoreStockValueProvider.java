/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductStoreStockValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class is used to index stock on the basis of POS for availableInStores property.
 *
 * @author rohan_c
 *
 */
public class SparProductStoreStockValueProvider extends ProductStoreStockValueProvider
{

	/**
	 * This method is used to push 1st Variant stock in Solr for property availableInStores
	 */
	@Override
	protected List<FieldValue> createFieldValue(ProductModel product, final BaseStoreModel baseStore,
			final IndexedProperty indexedProperty)
	{
		final Collection<VariantProductModel> variants = product.getVariants();
		// Since Base product do not have any stock associated to them, Its variant (1st) is used for creating price range
		if (null != variants && !variants.isEmpty())
		{
			product = variants.iterator().next();
		}


		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		if (baseStore != null && baseStore.getPointsOfService() != null && !baseStore.getPointsOfService().isEmpty())
		{
			// The OOTB has been overriden so that all the products is assumed to be available in the store. This is done with the intent that OOS products are also shown in search results.
			for (final PointOfServiceModel pos : baseStore.getPointsOfService())
			{
				addFieldValues(fieldValues, indexedProperty, pos.getName());
			}
		}
		return fieldValues;

	}
}
