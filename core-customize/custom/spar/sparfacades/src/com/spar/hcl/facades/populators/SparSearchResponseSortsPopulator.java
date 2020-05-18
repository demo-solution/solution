/**
 *
 */
package com.spar.hcl.facades.populators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.spar.hcl.core.model.solrsearch.config.SparSolrSortModel;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;

import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;


/**
 * This class is used to filter the warehouse specific sort fields
 *
 * @author rohan_c
 *
 */
public class SparSearchResponseSortsPopulator extends SearchResponseSortsPopulator
{
	@Resource(name = "sessionService")
	SessionService sessionService;
	private StoreFinderServiceInterface storeFinderService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseSortsPopulator#buildSorts(
	 * de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse)
	 */
	@Override
	protected List<SortData> buildSorts(final SolrSearchResponse source)
	{
		final List<SortData> result = new ArrayList<SortData>();
		final IndexedType indexedType = ((SearchQuery) source.getRequest().getSearchQuery()).getIndexedType();
		final IndexedTypeSort currentSort = (IndexedTypeSort) source.getRequest().getCurrentSort();
		final String currentSortCode = currentSort != null ? currentSort.getCode() : null;
		final List<IndexedTypeSort> sorts = indexedType.getSorts();
		filterSortFieldOnWarehouse(sorts);
		if (sorts != null && !sorts.isEmpty())
		{
			for (final IndexedTypeSort sort : sorts)
			{
				final SortData sortData = createSortData();
				sortData.setCode(sort.getCode());
				sortData.setName(sort.getName());

				if (currentSortCode != null && currentSortCode.equals(sort.getCode()))
				{
					sortData.setSelected(true);
				}
				result.add(sortData);
			}
		}
		return result;
	}

	/**
	 * This method is used to filter the sort field on the basis of warehouse.
	 *
	 * @param sorts
	 */
	protected void filterSortFieldOnWarehouse(final List<IndexedTypeSort> sorts)
	{
		final String warehouseCode = getCurrentWarehouse();
		if (null != warehouseCode)
		{
			if (!CollectionUtils.isEmpty(sorts))
			{
				final Iterator iterator = sorts.iterator();
				while (iterator.hasNext())
				{
					final IndexedTypeSort sort = (IndexedTypeSort) iterator.next();
					if (null != sort.getSort() && sort.getSort() instanceof SparSolrSortModel)
					{
						final String warhouseCodeSolrSort = ((SparSolrSortModel) sort.getSort()).getWarehouse();
						if (null != warhouseCodeSolrSort && !warhouseCodeSolrSort.equals(warehouseCode))
						{
							iterator.remove();
						}
					}
				}
			}
		}

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
