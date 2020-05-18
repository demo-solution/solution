/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.impl.SearchResultConverterData;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchResultFacetsPopulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;


/**
 * This class is used to filter out the facets that are warehouse specific from the Solr Response
 *
 * @author rohan_c
 *
 */
public class SparFacetSearchResultFacetsPopulator extends FacetSearchResultFacetsPopulator
{
	@Resource(name = "sessionService")
	SessionService sessionService;
	private StoreFinderServiceInterface storeFinderService;


	@Override
	public void populate(final SearchResultConverterData source, final SolrSearchResult target) throws ConversionException
	{
		super.populate(source, target);
		final QueryResponse queryResponse = source.getQueryResponse();

		List<FacetField> sourceFacets = queryResponse.getFacetFields();
		if (CollectionUtils.isEmpty(sourceFacets))
		{
			return;
		}

		final FacetSearchContext searchContext = source.getFacetSearchContext();
		final IndexedType indexedType = searchContext.getIndexedType();
		final SearchQuery searchQuery = searchContext.getSearchQuery();

		final long maxFacetValueCount = getMaxFacetValueCount(queryResponse);
		final boolean isAllFacetValuesInResponse = isAllFacetValuesInResponse(searchQuery);

		final FieldNameTranslator.FieldInfosMapping fieldInfosMapping = getFieldNameTranslator().getFieldInfos(
				source.getFacetSearchContext());
		final Map fieldInfos = fieldInfosMapping.getInvertedFieldInfos();
		sourceFacets = filterFacetFieldOnWarehouse(sourceFacets, fieldInfos);

		for (final FacetField sourceFacet : sourceFacets)
		{
			String fieldName = sourceFacet.getName();
			IndexedProperty indexedProperty = null;

			final FieldNameTranslator.FieldInfo fieldInfo = (FieldNameTranslator.FieldInfo) fieldInfos.get(sourceFacet.getName());
			if (fieldInfo != null)
			{
				fieldName = fieldInfo.getFieldName();
				indexedProperty = fieldInfo.getIndexedProperty();
			}

			final boolean isMultiSelect = (indexedProperty == null) ? false : isMultiSelect(indexedProperty);

			final List<FacetField.Count> sourceFacetValues = sourceFacet.getValues();
			if (!(CollectionUtils.isNotEmpty(sourceFacetValues)))
			{
				continue;
			}
			List facetValues = new ArrayList();

			for (final FacetField.Count count : sourceFacetValues)
			{
				if ((!(isMultiSelect)) && (!(isAllFacetValuesInResponse)) && (count.getCount() >= maxFacetValueCount))
				{
					continue;
				}
				final boolean selected = isFacetValueSelected(searchQuery, fieldName, count.getName());

				final String displayName = getFacetValueDisplayName(searchQuery, indexedProperty, count.getName());
				if (displayName == null)
				{
					facetValues.add(new FacetValue(count.getName(), count.getCount(), selected));
				}
				else
				{
					facetValues.add(new FacetValue(count.getName(), displayName, count.getCount(), selected));
				}

			}

			if (indexedProperty != null)
			{
				facetValues = sortFacetValues(searchQuery, indexedType, indexedProperty, facetValues);
			}

			target.addFacet(new Facet(fieldName, facetValues));
		}
	}

	private List sortFacetValues(SearchQuery searchQuery, IndexedType indexedType, IndexedProperty indexedProperty,
			List facetValues) {
		// TODO Auto-generated method stub
		return facetValues;
	}

	private String getFacetValueDisplayName(SearchQuery searchQuery, IndexedProperty indexedProperty, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isFacetValueSelected(SearchQuery searchQuery, String fieldName, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isMultiSelect(IndexedProperty indexedProperty) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This method is used to return the FacetField List on the basis of POS selected in Store
	 *
	 * @param sourceFacets
	 * @param fieldInfos
	 * @return List<FacetField>
	 */
	protected List<FacetField> filterFacetFieldOnWarehouse(final List<FacetField> sourceFacets, final Map fieldInfos)
	{
		final String warehouseCode = getCurrentWarehouse();

		if (null != warehouseCode)
		{
			final Iterator iterator = sourceFacets.iterator();
			while (iterator.hasNext())
			{
				IndexedProperty indexedProperty = null;
				final FacetField sourceFacet = (FacetField) iterator.next();
				final FieldNameTranslator.FieldInfo fieldInfo = (FieldNameTranslator.FieldInfo) fieldInfos.get(sourceFacet.getName());
				if (fieldInfo != null)
				{
					indexedProperty = fieldInfo.getIndexedProperty();
				}

				// if the facetable index property is ranged and warehouse specific like price_20001, then delete the irrelevant warehouse specific facets.
				if (null != indexedProperty && null != indexedProperty.getValueProviderParameter() && indexedProperty.isFacet()
						&& !warehouseCode.equals(indexedProperty.getValueProviderParameter()))
				{
					iterator.remove();
				}
			}
		}
		return sourceFacets;
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

	/**
	 * Getter
	 *
	 * @return the storeFinderService
	 */
	public StoreFinderServiceInterface getStoreFinderService()
	{
		return storeFinderService;
	}

	/**
	 * Setter
	 *
	 * @param storeFinderService
	 *           the storeFinderService to set
	 */
	public void setStoreFinderService(final StoreFinderServiceInterface storeFinderService)
	{
		this.storeFinderService = storeFinderService;
	}

}