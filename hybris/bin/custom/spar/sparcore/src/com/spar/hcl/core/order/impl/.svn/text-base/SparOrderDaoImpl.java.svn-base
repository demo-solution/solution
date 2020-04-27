/**
 *
 */
package com.spar.hcl.core.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.HashMap;
import java.util.Map;

import com.spar.hcl.core.order.SparOrderDao;


/**
 * @author jitendriya.m
 *
 */
public class SparOrderDaoImpl extends DefaultGenericDao<OrderModel> implements SparOrderDao
{

	private static final String FIND_ORDERS_BY_CODE_QUERY = "select {o.pk} from {Order as o join creditcardpaymentinfo as p on {o:paymentinfo}={p:pk}} where {p.subscriptionid}=?oid";


	public SparOrderDaoImpl()
	{
		super(OrderModel._TYPECODE);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.order.SparOrderDao#findOrderByCode(java.lang.String)
	 */
	@Override
	public OrderModel findOrderByOid(final String oid)
	{

		validateParameterNotNull(oid, "Oid must not be null");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("oid", oid);
		final OrderModel result = getFlexibleSearchService().searchUnique(
				new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_QUERY, queryParams));
		return result;
	}

}
