/**
 *
 */
package com.spar.hcl.core.search.solr.postprocessor;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SolrQueryPostProcessor;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author manish_ku
 *
 */
@SuppressWarnings("deprecation")
public class SparSolrQueryPostProcessor implements SolrQueryPostProcessor
{

	@Autowired
	private SessionService sessionService;

	@Override
	public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery)
	{
		final String warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		final String[] filterQueries = query.getFilterQueries();
		final ArrayList<String> aListFilterQueries;
		if (filterQueries.length > 0)
		{
			aListFilterQueries = new ArrayList<String>(Arrays.asList(filterQueries));
		}
		else
		{
			aListFilterQueries = new ArrayList<String>();
		}
		if (!aListFilterQueries.contains("displayFlag_" + warehouseCode + "_inr_boolean:true"))
		{
			aListFilterQueries.add("displayFlag_" + warehouseCode + "_inr_boolean:true");
			final String[] updatedFilterQueries = Arrays.copyOf(aListFilterQueries.toArray(), aListFilterQueries.toArray().length,
					String[].class);
			query.setFilterQueries(updatedFilterQueries);
		}
		return query;
	}

}
