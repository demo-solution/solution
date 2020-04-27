/*
 *
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
 */
package com.spar.hcl.promotions.jalo;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryAdjustAction;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.jalo.PromotionsManager.RestrictionSetResult;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderEntry;
import de.hybris.platform.promotions.result.PromotionOrderView;
import de.hybris.platform.promotions.util.Helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


public class SparProductPercentDiscountPromotion extends GeneratedSparProductPercentDiscountPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparProductPercentDiscountPromotion.class.getName());

	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{

		final ArrayList results = new ArrayList();// 50
		final RestrictionSetResult rsr = this.findEligibleProductsInBasket(ctx, promoContext);// 53
		if (rsr.isAllowedToContinue() && !rsr.getAllowedProducts().isEmpty())
		{// 55
			final PromotionOrderView view = promoContext.createView(ctx, this, rsr.getAllowedProducts());// 57
			final PromotionsManager promotionsManager = PromotionsManager.getInstance();// 58

			while (view.getTotalQuantity(ctx) > 0L)
			{// 61
				promoContext.startLoggingConsumed(this);// 63
				final PromotionOrderEntry entry = view.peek(ctx);// 66
				//final long quantityToDiscount = entry.getQuantity(ctx);// 67
				final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();// 68
				final double percentageDiscount = this.getPercentageDiscount(ctx).doubleValue() / 100.0D;// 70
				final double originalUnitPrice = entry.getBasePrice(ctx).doubleValue();// 73
				final double originalEntryPrice = this.getQualifyingCountAsPrimitive(ctx) * originalUnitPrice;// 74
				final Currency currency = promoContext.getOrder().getCurrency(ctx);// 76
				final long remainingCount = view.getTotalQuantity(ctx);
				if (remainingCount >= this.getQualifyingCountAsPrimitive(ctx))
				{
					final BigDecimal adjustedEntryPrice = Helper.roundCurrencyValue(ctx, currency, originalEntryPrice
							- originalEntryPrice * percentageDiscount);// 79 80
					PromotionOrderEntryConsumed result;
					Iterator adjustment;
					adjustment = view.consume(ctx, this.getQualifyingCountAsPrimitive(ctx)).iterator();// 106

					while (adjustment.hasNext())
					{
						result = (PromotionOrderEntryConsumed) adjustment.next();
						result.setAdjustedUnitPrice(
								ctx,
								Helper.roundCurrencyValue(ctx, currency,
										adjustedEntryPrice.doubleValue() / this.getQualifyingCountAsPrimitive(ctx)).doubleValue());// 108
						final PromotionResult result1 = promotionsManager.createPromotionResult(ctx, this, promoContext.getOrder(),
								1.0F);// 119
						result1.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));// 120
						final BigDecimal adjustment1 = Helper.roundCurrencyValue(ctx, currency,
								adjustedEntryPrice.subtract(BigDecimal.valueOf(originalEntryPrice)));// 121 122
						final PromotionOrderEntryAdjustAction poeac = promotionsManager.createPromotionOrderEntryAdjustAction(ctx,
								entry.getBaseOrderEntry(), quantityOfOrderEntry, adjustment1.doubleValue());// 123 124
						result1.addAction(ctx, poeac);// 125
						results.add(result1);// 127
					}
				}
				else
				{
					//promoContext.abandonLogging(this);
					if (remainingCount > 0L)
					{
						view.consume(ctx, remainingCount);
						final PromotionResult result1 = promotionsManager.createPromotionResult(ctx, this, promoContext.getOrder(),
								0.0F);// 119
						result1.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, false));// 120
						//final BigDecimal adjustment1 = Helper.roundCurrencyValue(ctx, currency, 0);// 121 122
						final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
								.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry, 0);
						result1.addAction(ctx, poeac);// 125
						results.add(result1);// 127
						return results;
					}
				}
			}
			return results;// 131
		}
		else
		{
			return results;// 134
		}
	}

	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);// 140
		if (order != null)
		{// 141
			final Currency orderCurrency = order.getCurrency(ctx);// 143
			if (promotionResult.getFired(ctx))
			{// 145
				final Double totalDiscount = Double.valueOf(promotionResult.getTotalDiscount(ctx));// 147
				final Double percentageDiscount = this.getPercentageDiscount(ctx);// 148
				final Object[] args = new Object[]
				{ percentageDiscount, totalDiscount,
						Helper.formatCurrencyAmount(ctx, locale, orderCurrency, totalDiscount.doubleValue()) };// 151 153
				return formatMessage(this.getMessageFired(ctx), args, locale);// 154
			}
			if (promotionResult.getCouldFire(ctx))
			{
				final Double percentageDiscount = this.getPercentageDiscount(ctx);// 148
				final Object[] args = new Object[]
				{ percentageDiscount, Double.valueOf(0), Helper.formatCurrencyAmount(ctx, locale, orderCurrency, 0.0) };// 151 153
				return formatMessage(this.getMessageCouldHaveFired(ctx), args, locale);// 154
			}
		}

		return "";// 159
	}

}
