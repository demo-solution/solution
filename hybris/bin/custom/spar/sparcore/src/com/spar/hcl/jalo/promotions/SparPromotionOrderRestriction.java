package com.spar.hcl.jalo.promotions;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * This class is used to put restriction on OrderThresholdChangeDeliveryModePromotion so that promotions are not applied
 * on CnC/pickup orders
 * 
 * @author rohan_c
 *
 */
public class SparPromotionOrderRestriction extends GeneratedSparPromotionOrderRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparPromotionOrderRestriction.class.getName());

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

	/**
	 * This method is used to put restriction on OrderThresholdChangeDeliveryModePromotion so that promotions are not
	 * applied on CnC/pickup orders
	 */
	@Override
	public RestrictionResult evaluate(final SessionContext ctx, final Collection<Product> products, final Date date,
			final AbstractOrder order)
	{
		if (order != null)
		{
			final DeliveryMode deliveryMode = order.getDeliveryMode();
			if (null != deliveryMode)
			{
				if ("pickup".equals(deliveryMode.getCode()))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("PromoFreeHomeDelivery is denied for deliverymode pickup");
					}
					return RestrictionResult.DENY;
				}
				else
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("PromoFreeHomeDelivery is Allowed for deliverymode other than pickup");
					}
					return RestrictionResult.ALLOW;
				}
			}
		}
		return RestrictionResult.DENY;
	}
}
