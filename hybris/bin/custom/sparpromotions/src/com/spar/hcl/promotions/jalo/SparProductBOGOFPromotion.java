package com.spar.hcl.promotions.jalo;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.util.Helper;

import java.util.Locale;

import org.apache.log4j.Logger;


/**
 * This class extends OOTB ProductBOGOFPromotion to handle bugs seen in OOTB BOGO promotion.
 *
 * @author rohan_c
 *
 */
public class SparProductBOGOFPromotion extends GeneratedSparProductBOGOFPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparProductBOGOFPromotion.class.getName());

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
	 * This method is overridden to handle bugs in BOGO.
	 *
	 * @see de.hybris.platform.promotions.jalo.ProductBOGOFPromotion#getResultDescription(de.hybris.platform.jalo.SessionContext
	 *      , de.hybris.platform.promotions.jalo.PromotionResult, java.util.Locale)
	 */
	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{

		final AbstractOrder order = promotionResult.getOrder(ctx);
		if (order != null)
		{
			final Currency orderCurrency = order.getCurrency(ctx);

			Integer qualifyingCount = getQualifyingCount(ctx);
			qualifyingCount = new Integer(qualifyingCount.intValue() - 1);
			final Integer freeCount = getFreeCount(ctx);

			if (promotionResult.getFired(ctx))
			{
				final double totalDiscount = promotionResult.getTotalDiscount(ctx);

				final Object[] args =
				{ qualifyingCount, freeCount, Double.valueOf(totalDiscount),
						Helper.formatCurrencyAmount(ctx, locale, orderCurrency, totalDiscount) };
				return formatMessage(getMessageFired(ctx), args, locale);
			}
			if (promotionResult.getCouldFire(ctx))
			{
				final Object[] args =
				{ Long.valueOf((getQualifyingCount(ctx).longValue() - 1) - promotionResult.getConsumedCount(ctx, true)),
						qualifyingCount, freeCount };
				return formatMessage(getMessageCouldHaveFired(ctx), args, locale);
			}
		}
		return "";

	}

}
