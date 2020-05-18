package com.spar.hcl.jalo.vouchers;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.ordersplitting.jalo.Warehouse;
import de.hybris.platform.voucher.jalo.Restriction;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
/**
 * @author ravindra.kr
 *
 */
public class SparWarehouseVoucherRestriction extends GeneratedSparWarehouseVoucherRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger( SparWarehouseVoucherRestriction.class.getName() );
	
	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException
	{
		// business code placed here will be executed before the item is created
		// then create the item
		final Item item = super.createItem( ctx, type, allAttributes );
		// business code placed here will be executed after the item was created
		// and return the item
		return item;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.voucher.jalo.Restriction#isFulfilledInternal(de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected boolean isFulfilledInternal(final AbstractOrder anOrder)
	{
   	if (anOrder != null)
   	{
   		final String currentWarehouseCode = (String) getSession().getAttribute("selectedWarehouseCode");
   		final Collection<Restriction> collection = this.getVoucher().getRestrictions();
   		for (final Restriction restriction : collection)
   		{
   			if (restriction instanceof SparWarehouseVoucherRestriction)
   			{
   				final Collection<Warehouse> warehouses = ((SparWarehouseVoucherRestriction) restriction).getWarehouses();
   				if (null != warehouses && !warehouses.isEmpty())
   				{
   					for (final Warehouse warehouse : warehouses)
   					{
   						if (StringUtils.isNotEmpty(currentWarehouseCode)
   								&& StringUtils.equals(currentWarehouseCode, warehouse.getCode()))
   						{
   							return true;
   						}
   					}
   				}
   			}
   		}
   	}
   	return false;
	}
}
