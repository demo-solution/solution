/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchFiltersPopulator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.search.FacetValueField;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.HashSet;
import java.util.Set;


/**
 * @author valechar
 *
 */
public class SearchFiltersPopulatorImpl extends SearchFiltersPopulator
{
	SessionService sessionService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SearchQueryPageableData source, final SolrSearchRequest target)
	{
		super.populate(source, target);
		//final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<IndexedPropertyValueData<IndexedProperty>>();
		//final List<SolrSearchQueryTermData> terms = target.getSearchQueryData().getFilterTerms();
		//final List<SolrSearchQueryTermData> filterTerms = new ArrayList<SolrSearchQueryTermData>();
		//final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		//searchQueryTermData.setKey("availableInStores");
		//searchQueryTermData.setValue("Spar_koramangala");
		//filterTerms.add(searchQueryTermData);
		//target.getSearchQueryData().setFilterTerms(filterTerms);
		final SearchQuery query = (SearchQuery) target.getSearchQuery();
		final Set set = new HashSet();
		String selectedStore = "";
		if (null != sessionService.getAttribute("selectedStore"))
		{
			selectedStore = sessionService.getAttribute("selectedStore");
		}
		set.add(selectedStore);
		final FacetValueField facetValue = new FacetValueField("availableInStores", set);
		query.addFacetValue(facetValue);
		//query.addFilterQuery("availableInStores", "Spar_koramangala");
		target.setSearchQuery(query);
		//System.out.println("Filter Terms : " + target.getSearchQueryData().getFilterTerms().get(0).getValue());
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}