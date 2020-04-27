/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.search.impl;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;


/**
 * This class is used for filtering the warehouse specific facets for solr request.
 * 
 * @author rohan_c
 *
 */
public class SparDefaultSolrQueryConverter extends DefaultSolrQueryConverter
{
	@Resource(name = "sessionService")
	SessionService sessionService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrQueryConverter#getFacetInfo(de.hybris.platform.
	 * solrfacetsearch.search.SearchQuery)
	 */
	@Override
	protected Map<String, IndexedFacetInfo> getFacetInfo(final SearchQuery searchQuery)
	{

		final Map results = new HashMap();

		int index = 0;

		final IndexedType indexedType = searchQuery.getIndexedType();
		final Set<String> facets = filterFacetFieldOnWarehouse(indexedType.getTypeFacets(), indexedType);
		for (final String facetName : facets)
		{
			final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facetName);
			if (indexedProperty != null)
			{
				final IndexedFacetInfo facetInfo = new IndexedFacetInfo();
				final FacetType facetType = indexedProperty.getFacetType();
				if (FacetType.MULTISELECTAND.equals(facetType))
				{
					facetInfo.setMultiSelect(true);
					facetInfo.setMultiSelectAnd(true);
				}
				else if (FacetType.MULTISELECTOR.equals(facetType))
				{
					facetInfo.setMultiSelect(true);
					facetInfo.setMultiSelectOr(true);
				}

				facetInfo.setTranslatedFieldName(getFieldNameTranslator().translate(searchQuery, facetName,
						FieldNameProvider.FieldType.INDEX));
				facetInfo.setKey("fk" + index);
				results.put(facetInfo.getTranslatedFieldName(), facetInfo);
			}

			++index;
		}

		return results;

	}

	/**
	 * This method is used to return the Facet List on the basis of POS selected in Store
	 *
	 * @param facets
	 * @param indexedType
	 * @return Set<String>
	 */
	protected Set<String> filterFacetFieldOnWarehouse(final Set<String> facets, final IndexedType indexedType)
	{
		final String warehouseCode = getCurrentWarehouse();

		if (null != warehouseCode)
		{
			final Iterator iterator = facets.iterator();
			while (iterator.hasNext())
			{
				final String facetName = (String) iterator.next();
				final IndexedProperty indexedProperty = indexedType.getIndexedProperties().get(facetName);

				// if the facetable index property is ranged and warehouse specific like price_20001, then delete the irrelevant warehouse specific facets.
				if (null != indexedProperty && null != indexedProperty.getValueProviderParameter() && indexedProperty.isFacet()
						&& !warehouseCode.equals(indexedProperty.getValueProviderParameter()))
				{
					iterator.remove();
				}
			}
		}
		return facets;
	}

	/**
	 * This method is used to get the warehouse from the current POS selected.
	 *
	 * @return String
	 */
	protected String getCurrentWarehouse()
	{
		String warehouseCode = null;
		if (null != sessionService)
		{
			warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		}
		return warehouseCode;

	}

}
