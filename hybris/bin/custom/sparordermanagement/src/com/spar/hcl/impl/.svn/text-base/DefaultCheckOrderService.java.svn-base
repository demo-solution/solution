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
package com.spar.hcl.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;

import com.spar.hcl.CheckOrderService;
import com.spar.hcl.actions.order.CheckOrderAction;


public class DefaultCheckOrderService implements CheckOrderService
{
	private static final Logger LOG = Logger.getLogger(DefaultCheckOrderService.class);
	private static final String PICKUP_CODE = "pickup";
	@Resource
	private CalculationService calculationService;

	@Override
	public boolean check(final OrderModel order)
	{
		if (!order.getCalculated().booleanValue())
		{
			// Order must be calculated
			try {
				calculationService.calculateTotals(order,false);
			} catch (CalculationException e) {
				LOG.error("Order Calculation Exception:::::::::::::::::::::"+e.getMessage());
			}
			//LOG.info("!order.getCalculated().booleanValue()::::::::::::::::::"+!order.getCalculated().booleanValue());
			return true;
		}
		if (order.getEntries().isEmpty())
		{
			// Order must have some lines
			//LOG.info("order.getEntries().isEmpty():::::::::::::::::::::::::::"+order.getEntries().isEmpty());
			return false;
		}
		else if (order.getPaymentInfo() == null)
		{
			// Order must have some payment info to use in the process
			//LOG.info("order.getPaymentInfo():::::::::::::::::::::::::::::::::"+order.getPaymentInfo());
			return false;
		}
		else
		{
			// Order delivery options must be valid
			return checkDeliveryOptions(order);
		}
	}

	protected boolean checkDeliveryOptions(final OrderModel order)
	{
		if (order.getDeliveryMode() == null)
		{
			// Order must have an overall delivery mode 
			//LOG.info("order.getDeliveryMode():::::::::::::::::::::::::::::::::"+order.getDeliveryMode());
			return false;
		}

		if (order.getDeliveryAddress() == null)
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				if (entry.getDeliveryPointOfService() == null && entry.getDeliveryAddress() == null)
				{
					// Order and Entry have no delivery address and some entries are not for pickup
					return false;
				}
			}
			//it is a Pickup Order
			order.getDeliveryMode().setCode(PICKUP_CODE);
			//LOG.info("Inside order.getDeliveryAddress() block:::::::::::::::::::::::::::::::::");
		}

		return true;
	}
}
