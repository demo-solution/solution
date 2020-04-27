/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.TopValuesProvider;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;


/**
 * @author valechar
 *
 */
public class SparSearchResponseFacetsPopulator extends SearchResponseFacetsPopulator
{

	@Resource(name = "categoryService")
	private CategoryService categoryService;

	private static final String ALL_CATEGORIES = "allCategories";
	private static final String CATEGORY = "category";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator#
	 * buildFacetValues(de.hybris.platform.commerceservices.search.facetdata.FacetData,
	 * de.hybris.platform.solrfacetsearch.search.Facet, de.hybris.platform.solrfacetsearch.config.IndexedProperty,
	 * de.hybris.platform.solrfacetsearch.search.SearchResult,
	 * de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData)
	 */
	/*
	 * This method is overridden in lieu of showing only 2nd level categories as facets, in case the user clicks on first
	 * level categories.
	 */
	@Override
	protected void buildFacetValues(final FacetData facetData, final Facet facet, final IndexedProperty indexedProperty,
			final SearchResult solrSearchResult, final SolrSearchQueryData searchQueryData)
	{
		final List<FacetValue> originalFacetValues = facet.getFacetValues();
		List<FacetValue> facetValues = new ArrayList<FacetValue>();
		final List<String> subCategoryCodesList = new ArrayList<String>();

		if ((null != searchQueryData.getCategoryCode() && StringUtils.isNotEmpty(searchQueryData.getCategoryCode()))
				&& ((ALL_CATEGORIES.equals(facet.getName()) || CATEGORY.equals(facet.getName()))))
		{
			final CategoryModel selectedCategoryModel = categoryService.getCategoryForCode(searchQueryData.getCategoryCode());
			// Fetching the list of sub categories for the selected category
			final List<CategoryModel> subCategoryModelsList = selectedCategoryModel.getCategories();

			for (final CategoryModel categoryModel : subCategoryModelsList)
			{
				subCategoryCodesList.add(categoryModel.getCode());
			}

			// Removing the list of extra categories so that the displayed facets can be refined
			for (final FacetValue facetValue : originalFacetValues)
			{
				for (final String catCode : subCategoryCodesList)
				{
					if (catCode.equals(facetValue.getName()))
					{
						facetValues.add(facetValue);
						break;
					}
				}
			}
		}
		if (CollectionUtils.isEmpty(facetValues))
		{
			facetValues = originalFacetValues;
		}

		if (!facetValues.isEmpty())
		{
			final List<FacetValueData<SolrSearchQueryData>> allFacetValues = new ArrayList<FacetValueData<SolrSearchQueryData>>(
					facetValues.size());

			for (final FacetValue facetValue : facetValues)
			{
				final FacetValueData<SolrSearchQueryData> facetValueData = buildFacetValue(facetData, facet, facetValue,
						solrSearchResult, searchQueryData);
				if (facetValueData != null)
				{
					allFacetValues.add(facetValueData);
				}
			}

			facetData.setValues(allFacetValues);

			final TopValuesProvider topValuesProvider = getTopValuesProvider(indexedProperty);
			if (!isRanged(indexedProperty) && topValuesProvider != null)
			{
				final List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetValues);
				if (topFacetValues != null && !CollectionUtils.isEmpty(topFacetValues))
				{
					final List<FacetValueData<SolrSearchQueryData>> topFacetValuesData = new ArrayList<FacetValueData<SolrSearchQueryData>>();
					for (final FacetValue facetValue : topFacetValues)
					{
						final FacetValueData<SolrSearchQueryData> topFacetValueData = buildFacetValue(facetData, facet, facetValue,
								solrSearchResult, searchQueryData);
						if (topFacetValueData != null)
						{
							topFacetValuesData.add(topFacetValueData);
						}
					}
					facetData.setTopValues(topFacetValuesData);
				}
			}
		}
	}

	private TopValuesProvider getTopValuesProvider(IndexedProperty indexedProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	/**
	 * @param categoryService
	 *           the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
