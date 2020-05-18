/**
 *
 */
package com.spar.hcl.core.order;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * @author jitendriya.m
 *
 */
public interface SparOrderDao
{
	public OrderModel findOrderByOid(final String code);
}
