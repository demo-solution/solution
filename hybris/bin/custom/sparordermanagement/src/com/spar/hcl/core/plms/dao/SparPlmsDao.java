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
package com.spar.hcl.core.plms.dao;

import java.util.List;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * This interface is used to read the plms orders
 * 
 * @author kumarchoubeys
 *
 */

public interface SparPlmsDao
{
	List<PlmsRetryOrdersModel> readOrders();
	OrderModel findOrderData(final String orderCode);
	PlmsRetryOrdersModel findPlmsOrders(final String orderCode);
}
