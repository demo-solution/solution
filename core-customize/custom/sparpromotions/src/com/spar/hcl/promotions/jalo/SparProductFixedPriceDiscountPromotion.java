package com.spar.hcl.promotions.jalo;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryAdjustAction;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionPriceRow;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.jalo.PromotionsManager.RestrictionSetResult;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderEntry;
import de.hybris.platform.promotions.result.PromotionOrderView;
import de.hybris.platform.promotions.util.Helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


public class SparProductFixedPriceDiscountPromotion extends GeneratedSparProductFixedPriceDiscountPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparProductFixedPriceDiscountPromotion.class.getName());

	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		final ArrayList results = new ArrayList();
		final RestrictionSetResult rsr = this.findEligibleProductsInBasket(ctx, promoContext);
		final Collection promotionDiscountPriceRows = this.getBundlePrices(ctx);
		final AbstractOrder order = promoContext.getOrder();
		final boolean hasValidPromotionPriceRow = this.hasPromotionPriceRowForCurrency(order, promotionDiscountPriceRows);
		final float certainty1 = 1.0F;
		final Currency orderCurrency = order.getCurrency(ctx);
		if (hasValidPromotionPriceRow && rsr.isAllowedToContinue() && !rsr.getAllowedProducts().isEmpty())
		{
			final PromotionOrderView view = promoContext.createView(ctx, this, rsr.getAllowedProducts());

			while (view.getTotalQuantity(ctx) > 0L)
			{
				promoContext.startLoggingConsumed(this);
				final PromotionOrderEntry entry = view.peek(ctx);
				final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();
				final Double fixedUnitDiscountPrice = this.getPriceForOrder(ctx, promotionDiscountPriceRows, order, "bundlePrices");
				final long remainingCount = view.getTotalQuantity(ctx);

				if (fixedUnitDiscountPrice != null && remainingCount >= this.getQualifyingCountAsPrimitive(ctx))
				{
					final Iterator arg15 = view.consume(ctx, this.getQualifyingCountAsPrimitive()).iterator();
					final double effectiveUnitPrice = entry.getBasePrice(ctx).doubleValue()
							- (fixedUnitDiscountPrice.doubleValue() / this.getQualifyingCountAsPrimitive());
					while (arg15.hasNext())
					{
						final PromotionOrderEntryConsumed adjustment = (PromotionOrderEntryConsumed) arg15.next();
						adjustment.setAdjustedUnitPrice(ctx, Helper.roundCurrencyValue(ctx, orderCurrency, effectiveUnitPrice)
								.doubleValue());
					}

					final PromotionPriceRow priceRow = this.getBundlePrices(ctx).iterator().next();
					final double adjustment1 = -priceRow.getPrice().doubleValue();
					final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
							promoContext.getOrder(), certainty1);
					result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));
					final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
							.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry, adjustment1);
					result.addAction(ctx, poeac);
					results.add(result);
				}
				else
				{
					//promoContext.abandonLogging(this);
					if (remainingCount > 0L)
					{
						view.consume(ctx, remainingCount);
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 0.0F);
						result.setConsumedEntries(promoContext.finishLoggingAndGetConsumed(this, false));
						final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
								.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry, 0);
						result.addAction(ctx, poeac);
						results.add(result);
						return results;
					}
				}
			}

			return results;
		}
		else
		{
			return results;
		}
	}

	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);// 144
		if (order != null && promotionResult.getFired(ctx))
		{// 145 147
			final Double fixedUnitPrice = this.getPriceForOrder(ctx, this.getBundlePrices(ctx), order, "bundlePrices");// 149 150
			if (fixedUnitPrice != null)
			{// 151
				final double totalDiscount = promotionResult.getTotalDiscount(ctx);// 153
				final Currency orderCurrency = order.getCurrency(ctx);// 154
				final Object[] args = new Object[]
				{ fixedUnitPrice, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, fixedUnitPrice.doubleValue()),
						Double.valueOf(totalDiscount), Helper.formatCurrencyAmount(ctx, locale, orderCurrency, totalDiscount) };// 157 158 159
				String message = "";
				message = formatMessage(this.getMessageFired(ctx), args, locale);
				return message;// 160
			}
		}
		if (promotionResult.getCouldFire(ctx))
		{
			final Double fixedUnitPrice = this.getPriceForOrder(ctx, this.getBundlePrices(ctx), order, "bundlePrices");// 149 150
			if (fixedUnitPrice != null)
			{// 151
				final double totalDiscount = promotionResult.getTotalDiscount(ctx);// 153

				final Currency orderCurrency = order.getCurrency(ctx);// 154
				final Object[] args = new Object[]
				{ fixedUnitPrice, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, fixedUnitPrice.doubleValue()),
						Double.valueOf(totalDiscount), Helper.formatCurrencyAmount(ctx, locale, orderCurrency, totalDiscount) };// 157 158 159
				return formatMessage(this.getMessageCouldHaveFired(ctx), args, locale);// 160
			}
		}

		return "";// 166
	}

	private boolean hasPromotionPriceRowForCurrency(final AbstractOrder order,
			final Collection<PromotionPriceRow> promotionPriceRows)
	{
		final String name = this.getComposedType().getName() + " (" + this.getCode() + ": " + this.getTitle() + ")";
		if (promotionPriceRows.isEmpty())
		{
			LOG.warn(name + " has no PromotionPriceRow. Skipping evaluation");
			return false;
		}
		else
		{
			final Currency currency = order.getCurrency();
			final Iterator arg5 = promotionPriceRows.iterator();

			while (arg5.hasNext())
			{
				final PromotionPriceRow ppr = (PromotionPriceRow) arg5.next();
				if (currency.equals(ppr.getCurrency()))
				{
					return true;
				}
			}

			LOG.warn(name + " has no PromotionPriceRow for currency " + currency.getName() + ". Skipping evaluation");
			return false;
		}
	}
}
