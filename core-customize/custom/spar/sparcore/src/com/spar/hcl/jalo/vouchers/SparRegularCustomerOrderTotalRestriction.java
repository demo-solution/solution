package com.spar.hcl.jalo.vouchers;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.Order;
import de.hybris.platform.jalo.type.ComposedType;

import java.util.Iterator;

import org.apache.log4j.Logger;


public class SparRegularCustomerOrderTotalRestriction extends GeneratedSparRegularCustomerOrderTotalRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparRegularCustomerOrderTotalRestriction.class.getName());

	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes)
			throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem(ctx, type, allAttributes);
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean isFulfilledInternal(final AbstractOrder anOrder)
	{
		double currentTotal = 0.0D;
		final Currency currency = getCurrency();
		for (final Iterator iterator = anOrder.getUser().getOrders().iterator(); iterator.hasNext();)
		{
			final Order order = (Order) iterator.next();
			if (!order.equals(anOrder))
			{
				currentTotal += order.getCurrency().convert(currency,
						(order.getSubtotal().doubleValue() - order.getTotalDiscounts().doubleValue()));
				if (isNetAsPrimitive() != order.isNet().booleanValue())
				{
					if (order.isNet().booleanValue())
					{
						currentTotal += order.getTotalTax().doubleValue();
					}
					else
					{
						currentTotal -= order.getTotalTax().doubleValue();
					}
				}
				if (!isValueofgoodsonlyAsPrimitive())
				{
					currentTotal += order.getDeliveryCosts();
					currentTotal += order.getPaymentCosts();
				}
			}
		}
		return currentTotal >= getAllOrdersTotalAsPrimitive();
	}

}
