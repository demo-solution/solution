/**
 *
 */
package com.spar.hcl.facades.provider.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.DefaultTopValuesProvider;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author valechar
 *
 */
public class SparDefaultTopValuesProvider extends DefaultTopValuesProvider
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.DefaultTopValuesProvider#getTopValues(de.
	 * hybris.platform.solrfacetsearch.config.IndexedProperty, java.util.List)
	 */
	@Override
	public List<FacetValue> getTopValues(final IndexedProperty indexedProperty, final List<FacetValue> facets)
	{
		// // Sort the facet values by Count in descending order
		Collections.sort(facets, FacetValueCountComparator.INSTANCE);

		final List<FacetValue> topFacetValues = new ArrayList<FacetValue>();

		if (facets != null)
		{
			for (final FacetValue facetValue : facets)
			{
				if (facetValue != null && (topFacetValues.size() < getTopFacetCount() || facetValue.isSelected()))
				{
					topFacetValues.add(facetValue);
				}
			}

			/*
			 * if (topFacetValues.size() >= facets.size()) { return Collections.emptyList(); }
			 */
		}

		return topFacetValues;
	}

}
