/**
 *
 */
package com.spar.hcl.core.sitemap.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spar.hcl.core.enums.SparSitemapPageType;
import com.spar.hcl.core.sitemap.dao.SparSiteMapListDAO;
import com.spar.hcl.deliveryslot.model.SparSiteMapModel;


/**
 * @author jitendriya.m
 *
 */
public class SparSiteMapListDAOImpl extends DefaultGenericDao<SparSiteMapModel> implements SparSiteMapListDAO
{

	private static final String REF_QUERY_PARAM_PAGETYPE = "pageType";

	private static final String ALL_SITE_MAP_LIST = "select {sm.pk} from {SparSiteMap as sm join SparSitemapPageType as smpe on {sm.pageType}={smpe.pk} } where {smpe.code}=?pageType and {sm.visible}=TRUE";



	public SparSiteMapListDAOImpl()
	{
		super(SparSitemapPageType._TYPECODE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.spar.hcl.core.sitemap.dao.SparSiteMapListDAO#getAllListOnAreaType(com.spar.hcl.core.enums.SparSitemapPageType)
	 */
	@Override
	public List<SparSiteMapModel> getAllSiteMapByPageType(final String pageType)
	{
		List<SparSiteMapModel> list = null;
		try
		{
			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(ALL_SITE_MAP_LIST);

			final Map<String, Object> params = new HashMap<String, Object>();
			flexibleSearchQuery.addQueryParameter(REF_QUERY_PARAM_PAGETYPE, pageType);// it works

			flexibleSearchQuery.addQueryParameters(params);
			final SearchResult<SparSiteMapModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
			list = searchResult.getResult();

		}
		catch (final Exception e)
		{
			//LOG.debug("No warehouse found. Error while retrieving the warehouse " + e.getMessage());
		}
		return list;
	}

}
