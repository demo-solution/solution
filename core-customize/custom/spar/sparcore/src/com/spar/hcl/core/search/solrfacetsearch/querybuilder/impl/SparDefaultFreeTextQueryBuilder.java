/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.querybuilder.impl;

import de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder.impl.DefaultFreeTextQueryBuilder;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.util.Config;

import org.apache.log4j.Logger;


/**
 * This class is used for refining queries.
 *
 * @author rohan_c
 *
 */
public class SparDefaultFreeTextQueryBuilder extends DefaultFreeTextQueryBuilder
{

	private static final Logger LOG = Logger.getLogger(SparDefaultFreeTextQueryBuilder.class);

	@Override
	protected void addFreeTextQuery(final SearchQuery searchQuery, final IndexedProperty indexedProperty, final String value,
			final double boost)
	{
		final String field = indexedProperty.getName();
		if (!indexedProperty.isFacet())
		{
			if ("text".equalsIgnoreCase(indexedProperty.getType()))
			{
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "", boost);
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "*", boost / 2.0d);
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), Config.getString("sparcore.fuzzy.search", "~"),
						boost / 4.0d);
			}
			else
			{
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "", boost);
				addFreeTextQuery(searchQuery, field, value.toLowerCase(), "*", boost / 2.0d);
			}
		}
		else
		{
			LOG.warn("Not searching " + indexedProperty
					+ ". Free text search not available in facet property. Configure an additional text property for searching.");
		}
	}

}
