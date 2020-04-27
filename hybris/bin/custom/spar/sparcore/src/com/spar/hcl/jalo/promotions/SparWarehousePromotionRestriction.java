package com.spar.hcl.jalo.promotions;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.ordersplitting.jalo.Warehouse;
import de.hybris.platform.promotions.jalo.AbstractPromotionRestriction;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class SparWarehousePromotionRestriction extends GeneratedSparWarehousePromotionRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparWarehousePromotionRestriction.class.getName());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.promotions.jalo.AbstractPromotionRestriction#evaluate(de.hybris.platform.jalo.SessionContext,
	 * java.util.Collection, java.util.Date, de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@Override
	public RestrictionResult evaluate(final SessionContext ctx, final Collection<Product> arg1, final Date arg2,
			final AbstractOrder order)
	{
		if (order != null)
		{
			LOG.info("Cart ID ::::::: "+order.getCode());
			final String currentWarehouseCode = (String) getSession().getAttribute("selectedWarehouseCode");
			LOG.info("currentWarehouseCode fetched from session ::::::: "+currentWarehouseCode);
			//SparWarehousePromotionRestriction
			final Collection<AbstractPromotionRestriction> collection = this.getPromotion().getRestrictions(ctx);

			for (final AbstractPromotionRestriction promotionRestriction : collection)
			{
				if (promotionRestriction instanceof SparWarehousePromotionRestriction)
				{
					final Collection<Warehouse> warehouses = ((SparWarehousePromotionRestriction) promotionRestriction)
							.getWarehouses(ctx);
					if (null != warehouses && !warehouses.isEmpty())
					{
						for (final Warehouse warehouse : warehouses)
						{
							if (StringUtils.isNotEmpty(currentWarehouseCode)
									&& StringUtils.equals(currentWarehouseCode, warehouse.getCode(ctx)))
							{
								LOG.info("Promotion Allowed For ::::::: "+order.getCode());
								return RestrictionResult.ALLOW;
							}
						}
					}
				}
			}
		}
		return RestrictionResult.DENY;
	}

}
