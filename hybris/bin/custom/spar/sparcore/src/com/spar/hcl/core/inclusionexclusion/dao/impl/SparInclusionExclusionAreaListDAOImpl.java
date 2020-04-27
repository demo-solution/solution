/**
 *
 */
package com.spar.hcl.core.inclusionexclusion.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spar.hcl.core.enums.InclusionExclusionTypeEnum;
import com.spar.hcl.core.inclusionexclusion.dao.InclusionExclusionAreaListDAO;
import com.spar.hcl.deliveryslot.model.InclusionExclusionEntryModel;


/**
 * @author nikhil-ku
 *
 */
public class SparInclusionExclusionAreaListDAOImpl extends DefaultGenericDao<InclusionExclusionEntryModel> implements
		InclusionExclusionAreaListDAO
{
	/**
	 * @param typecode
	 */
	public SparInclusionExclusionAreaListDAOImpl()
	{
		super(InclusionExclusionEntryModel._TYPECODE);
	}


	private static final String REF_QUERY_PARAM_AREATYPE = "areaType";

	//private static final String ALL_EXCLUSION_AREA_LIST = "select {iee.pk} from {InclusionExclusionEntry as iee join InclusionExclusionTypeEnum as ieenum on {iee.areaType}={ieenum.pk} } where {ieenum.code}=?areaType";
	private static final String ALL_EXCLUSION_AREA_LIST = "select {iee.pk} from {InclusionExclusionEntry as iee join InclusionExclusionTypeEnum as ieenum on {iee.areaType}={ieenum.pk} join InclusionExclusion2POSRel as inrel on {iee.pk}={inrel.source} join pointofservice as pos on {pos.pk}={inrel.target} } where {pos.storeCatchmentArea} > 0 and {ieenum.code}=?areaType";


	/*
	 * (non-Javadoc)
	 *
	 * @see com.spar.hcl.core.inclusionexclusion.dao.InclusionExclusionAreaListDAO#getAllExclusionListArea()
	 */


	@Override
	public List<InclusionExclusionEntryModel> getAllListOnAreaType(final InclusionExclusionTypeEnum areaType)
	{
		List<InclusionExclusionEntryModel> list = null;
		try
		{
			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(ALL_EXCLUSION_AREA_LIST);
			//flexibleSearchQuery.addQueryParameter("pos", pointOfService);


			final Map<String, Object> params = new HashMap<String, Object>();

			if (InclusionExclusionTypeEnum.EXL.equals(areaType))
			{
				//params.put(REF_QUERY_PARAM_AREATYPE, "'EXL'");

				flexibleSearchQuery.addQueryParameter("areaType", "EXL");// it works

				//flexibleSearchQuery.addQueryParameter(REF_QUERY_PARAM_AREATYPE, InclusionExclusionTypeEnum.EXL);
			}
			else
			{
				flexibleSearchQuery.addQueryParameter("areaType", "IN");// it works
				//flexibleSearchQuery.addQueryParameter(REF_QUERY_PARAM_AREATYPE, InclusionExclusionTypeEnum.IN);
				//params.put(REF_QUERY_PARAM_AREATYPE, InclusionExclusionTypeEnum.IN);
			}

			flexibleSearchQuery.addQueryParameters(params);
			final SearchResult<InclusionExclusionEntryModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
			list = searchResult.getResult();




		}
		catch (final Exception e)
		{
			//LOG.debug("No warehouse found. Error while retrieving the warehouse " + e.getMessage());
		}
		return list;
	}
}
