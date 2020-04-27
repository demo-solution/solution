/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.core.plms.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.core.plms.dao.SparPlmsDao;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 * This class is used for implementation for Plms orders
 * 
 * @author ravindra
 *
 */

public class DefaultSparPlmsDao extends AbstractItemDao implements SparPlmsDao
{
	private static final String ORDER_CODE = "orderCode";
	private static final String MARK_DELETED = "markDeleted";
	private static final String QUERY = "SELECT {p.PK}  FROM {PlmsRetryOrders as p} where {p.markDeleted} != ?markDeleted";
	private static final String GET_ORDER_MODEL="select {o.pk} from {order as o} where {o.code} = ?orderCode";
	private static final String GET_SPAR_PLMS_ORDER_MODEL="select {s.pk} from {PlmsRetryOrders as s} where {s.ordercode}= ?orderCode";
	public static final String WAREHOUSE = "warehouse";
	public static final String PRODUCTID = "productId";
	private static final Logger LOGGER = Logger.getLogger(DefaultSparPlmsDao.class);
	
	/**
	 * This method is used to read plms orders for cron job activity
	 *
	 */
	@Override
	public List<PlmsRetryOrdersModel> readOrders()
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY);
		params.put(MARK_DELETED, Boolean.TRUE);
		query.addQueryParameters(params);
		LOGGER.info("query :" + query);
		final SearchResult<PlmsRetryOrdersModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	
	/**
	 * This method is used to read a particular order from order model
	 *
	 * @param orderCode
	 */
	@Override
	public OrderModel findOrderData(final String orderCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final FlexibleSearchQuery orderQuery = new FlexibleSearchQuery(GET_ORDER_MODEL);
		params.put(ORDER_CODE, orderCode);
		orderQuery.addQueryParameters(params);
		LOGGER.info("orderQuery :" + orderQuery);
		final SearchResult<OrderModel> result = getFlexibleSearchService().search(orderQuery);
		if(result.getResult().isEmpty()){
			return null;
		}
		return result.getResult().get(0);
	}
	

	/**
	 * This method is used to find the PlmsRetryOrdersModel for a particular order number
	 *
	 * @param orderCode
	 */	
	@Override
	public PlmsRetryOrdersModel findPlmsOrders(final String orderCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final FlexibleSearchQuery orderQuery = new FlexibleSearchQuery(GET_SPAR_PLMS_ORDER_MODEL);
		params.put(ORDER_CODE, orderCode);
		orderQuery.addQueryParameters(params);
		LOGGER.info("orderQuery :" + orderQuery);
		final SearchResult<PlmsRetryOrdersModel> result = getFlexibleSearchService().search(orderQuery);
		if(CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}	
		return null;
	}

}
