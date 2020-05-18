/**
 *
 */
package com.spar.hcl.core.mobileverification.dao.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spar.hcl.core.mobileverification.dao.SparMobileVerificationDAO;


/**
 * @author jitendriya.m
 *
 */
public class SparMobileVerificationDAOimpl extends DefaultGenericDao<CustomerModel> implements SparMobileVerificationDAO
{



	public SparMobileVerificationDAOimpl()
	{
		super(CustomerModel._TYPECODE);
		// YTODO Auto-generated constructor stub
	}


	private static final String REF_QUERY_PARAM_MOBILE_NO = "custPrimaryMobNumber";

	private static final String COUNT_MOBILE_NO = "select {c.pk} from {Customer as c} where {c.custPrimaryMobNumber}=?custPrimaryMobNumber";


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.mobileverification.dao.SparMobileVerificationDAO#countMobileNo(java.lang.String)
	 */

	@Override
	public Boolean countMobileNo(final String mobileNO)
	{
		Boolean isAvailable = Boolean.TRUE;
		try
		{
			final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(COUNT_MOBILE_NO);

			final Map<String, Object> params = new HashMap<String, Object>();
			flexibleSearchQuery.addQueryParameter(REF_QUERY_PARAM_MOBILE_NO, mobileNO);// it works

			flexibleSearchQuery.addQueryParameters(params);
			final SearchResult<CustomerModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
			final List<CustomerModel> list = searchResult.getResult();
			if (list.size() > 0)
			{
				isAvailable = Boolean.FALSE;
			}

		}
		catch (final Exception e)
		{
			//LOG.debug("No warehouse found. Error while retrieving the warehouse " + e.getMessage());
		}
		return isAvailable;


	}
}
