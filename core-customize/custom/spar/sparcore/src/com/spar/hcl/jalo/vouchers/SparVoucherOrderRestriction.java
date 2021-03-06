package com.spar.hcl.jalo.vouchers;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.model.OrderThresholdDiscountPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;


public class SparVoucherOrderRestriction extends GeneratedSparVoucherOrderRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparVoucherOrderRestriction.class.getName());

	private final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

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
	 * @see de.hybris.platform.voucher.jalo.Restriction#isFulfilledInternal(de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected boolean isFulfilledInternal(final AbstractOrder anOrder)
	{

		final Currency minimumOrderValueCurrency = getCurrency();
		final Currency currentOrderCurrency = anOrder.getCurrency();
		if ((minimumOrderValueCurrency == null) || (currentOrderCurrency == null))
		{
			return false;
		}
		final double minimumTotal = minimumOrderValueCurrency.convert(currentOrderCurrency, getTotalAsPrimitive());
		try
		{
			anOrder.calculateTotals(false);
		}
		catch (final JaloPriceFactoryException e)
		{
			LOG.warn("Order.calculateTotals(false) failed", e);
		}
		final AbstractOrderModel orderModel = (AbstractOrderModel) modelService.get(anOrder);
		double currentTotal = 0.0D;
		if (null != orderModel.getSubtotal() && orderModel.getSubtotal().doubleValue() > 0.0D)
		{
			currentTotal = orderModel.getSubtotal().doubleValue();
		}
		else
		{
			currentTotal = orderModel.getTotalEqvPrice().doubleValue();
			for (final PromotionResultModel promo : orderModel.getAllPromotionResults())
			{
				if (promo.getPromotion().getItemtype().equalsIgnoreCase("OrderThresholdDiscountPromotion")
						&& promo.getCertainty().doubleValue() == 1.0D)
				{
					final OrderThresholdDiscountPromotionModel billBusterPromo = (OrderThresholdDiscountPromotionModel) promo
							.getPromotion();
					currentTotal = currentTotal - billBusterPromo.getDiscountPrices().iterator().next().getPrice().doubleValue();
					break;
				}
			}
		}

		if (isNetAsPrimitive() != anOrder.isNet().booleanValue())
		{
			if (anOrder.isNet().booleanValue())
			{
				currentTotal += anOrder.getTotalTax().doubleValue();
			}
			else
			{
				currentTotal -= anOrder.getTotalTax().doubleValue();
			}
		}

		if (!isValueofgoodsonlyAsPrimitive())
		{
			currentTotal += anOrder.getDeliveryCosts();
			currentTotal += anOrder.getPaymentCosts();
		}

		if (isPositiveAsPrimitive())
		{
			return currentTotal >= minimumTotal;
		}
		return currentTotal <= minimumTotal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.voucher.jalo.GeneratedRestriction#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Spar order value restriction";
	}

}
