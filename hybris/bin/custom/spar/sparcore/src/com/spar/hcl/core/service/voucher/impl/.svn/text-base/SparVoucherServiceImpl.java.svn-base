/**
 *
 */
package com.spar.hcl.core.service.voucher.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.jalo.Restriction;
import de.hybris.platform.voucher.jalo.Voucher;
import de.hybris.platform.voucher.jalo.util.VoucherEntry;
import de.hybris.platform.voucher.jalo.util.VoucherEntrySet;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.VoucherModel;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import com.spar.hcl.core.service.voucher.SparVoucherService;


/**
 * @author kumarchoubeys
 *
 */
public class SparVoucherServiceImpl implements SparVoucherService
{
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "modelService")
	private ModelService modelService;
	
	@Resource(name = "sessionService")
	SessionService sessionService;
	
	@Resource(name = "cartService")
	private CartService cartService;
	
	@Resource(name = "voucherService")
	private VoucherService voucherService;

	private static final String GET_INVALID_VOUCHER_DETAIL = "select * from {voucherinvalidation as vi} "
			+ "where {vi.code}=?voucherCode";

	/**
	 * This method is used to check the fraud apply of voucher
	 *
	 * @param voucherCode
	 * @return List
	 */
	@Override
	public List currentVoucherInvalidationCheck(final String voucherCode)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(GET_INVALID_VOUCHER_DETAIL);
		flexibleSearchQuery.addQueryParameter("voucherCode", voucherCode);
		final SearchResult searchResult = flexibleSearchService.search(flexibleSearchQuery);
		return searchResult.getResult();
	}

	/**
	 * This method is used to get the applied voucher value in populator
	 *
	 * @param voucherVal
	 * @param order
	 * @param cartData
	 * @return VoucherValue
	 */
	@SuppressWarnings(
	{ "deprecation", "boxing" })
	@Override
	public VoucherValue getAppliedValue(final VoucherModel voucherVal, final AbstractOrderModel order, final CartData cartData)
	{
		final AbstractOrder anOrder = modelService.getSource(order);
		final Iterator<Discount> discounts = anOrder.getDiscounts().iterator();
		boolean found = false;
		boolean applyFreeShipping = false;
		Voucher voucher = null;
		while ((discounts.hasNext()) && (!found))
		{
			final Discount discount = discounts.next();
			if ((discount instanceof Voucher))
			{
				voucher = (Voucher) discount;
				if ((isApplicable(anOrder, voucher)) && (voucher.isFreeShippingAsPrimitive()))
				{
					if (voucher.equals(this))
					{
						applyFreeShipping = true;
					}
					found = true;
				}
			}
		}
		VoucherValue applicableValue = getApplicableValue(anOrder, voucher, cartData);
		Currency resultCurrency = applicableValue.getCurrency();
		double resultValue = 0.00D;
		double discountAmount = 0.00D;
		if (null != voucher)
		{
			if (voucher.isAbsolute().booleanValue())
			{
				resultValue = Math.min(
						applicableValue.getCurrency().convertAndRound(applicableValue.getCurrency(), applicableValue.getValue()),
						voucher.getValue().doubleValue());
				resultCurrency = applicableValue.getCurrency();
			}
			else
			{
				applicableValue = getApplicableValueForRelative(order, anOrder, voucher);
				resultValue = applicableValue.getValue() * voucher.getValue().doubleValue() / 100.0D;
				resultCurrency = applicableValue.getCurrency();
			}
			if ((voucher.isFreeShippingAsPrimitive()) && (applyFreeShipping))
			{
				resultValue += anOrder.getCurrency().convertAndRound(resultCurrency, anOrder.getDeliveryCosts());
			}
		}

		if (null != cartData.getAppliedOrderPromotionTotal()
				&& cartData.getSubTotalWithDiscounts().getValue().doubleValue() == 0.0D)
		{
			discountAmount += cartData.getAppliedOrderPromotionTotal().getValue().doubleValue();
		}
		if (null != cartData.getAppliedEmployeeDiscountPromotionTotal()
				&& cartData.getSubTotalWithDiscounts().getValue().doubleValue() == 0.0D)
		{
			discountAmount += cartData.getAppliedEmployeeDiscountPromotionTotal().getValue().doubleValue();
		}

		boolean voucherValueExceedingOrdertotal = false;
		if(null != sessionService.getAttribute("voucherValueExceedingOrdertotal"))
		{
			voucherValueExceedingOrdertotal =(Boolean) sessionService.getAttribute("voucherValueExceedingOrdertotal");
		}
			if(voucherValueExceedingOrdertotal)
			{
				if (null != cartData.getCombiOfferAppliedProductPromotion() && CollectionUtils.isNotEmpty(cartData.getCombiOfferAppliedProductPromotion()))
				{
					for(PromotionResultData promotionResultData : cartData.getCombiOfferAppliedProductPromotion())
					{
						discountAmount += promotionResultData.getTotalDiscount().getValue().doubleValue();
					}
				}
			}
		
		resultValue -= discountAmount;
		resultValue = Double.valueOf(getDecimalValue(resultValue));
		return new VoucherValue(resultValue, resultCurrency);

	}

	/**
	 * This method is used to get the applied voucher value in populator
	 *
	 * @param voucherVal
	 * @param order
	 * @param orderData
	 * @return VoucherValue
	 */
	@SuppressWarnings(
	{ "deprecation", "boxing" })
	@Override
	public VoucherValue getAppliedValueForOrder(final VoucherModel voucherVal, final AbstractOrderModel order,
			final OrderData orderData)
	{
		final AbstractOrder anOrder = modelService.getSource(order);
		final Iterator<Discount> discounts = anOrder.getDiscounts().iterator();
		boolean found = false;
		boolean applyFreeShipping = false;
		Voucher voucher = null;
		while ((discounts.hasNext()) && (!found))
		{
			final Discount discount = discounts.next();
			if ((discount instanceof Voucher))
			{
				voucher = (Voucher) discount;
				if ((isApplicable(anOrder, voucher)) && (voucher.isFreeShippingAsPrimitive()))
				{
					if (voucher.equals(this))
					{
						applyFreeShipping = true;
					}
					found = true;
				}
			}
		}
		VoucherValue applicableValue = getApplicableValueForOrder(anOrder, voucher, orderData);
		Currency resultCurrency = applicableValue.getCurrency();
		double resultValue = 0.00D;
		double discountAmount = 0.00D;
		if (null != voucher)
		{
			if (voucher.isAbsolute().booleanValue())
			{
				resultValue = Math.min(
						applicableValue.getCurrency().convertAndRound(applicableValue.getCurrency(), applicableValue.getValue()),
						voucher.getValue().doubleValue());
				resultCurrency = applicableValue.getCurrency();
			}
			else
			{
				applicableValue = getApplicableValueForRelative(order, anOrder, voucher);
				resultValue = applicableValue.getValue() * voucher.getValue().doubleValue() / 100.0D;
				resultCurrency = applicableValue.getCurrency();
			}
			if ((voucher.isFreeShippingAsPrimitive()) && (applyFreeShipping))
			{
				resultValue += anOrder.getCurrency().convertAndRound(resultCurrency, anOrder.getDeliveryCosts());
			}
		}

		if (null != orderData.getAppliedOrderPromotionTotal()
				&& orderData.getSubTotalWithDiscounts().getValue().doubleValue() == 0.0D)
		{
			discountAmount += orderData.getAppliedOrderPromotionTotal().getValue().doubleValue();
			boolean voucherValueExceedingOrdertotal =(Boolean) sessionService.getAttribute("voucherValueExceedingOrdertotal");
				if(voucherValueExceedingOrdertotal)
				{
					if (null != orderData.getCombiOfferAppliedProductPromotion() && CollectionUtils.isNotEmpty(orderData.getCombiOfferAppliedProductPromotion()))
					{
						for(PromotionResultData promotionResultData : orderData.getCombiOfferAppliedProductPromotion())
						{
							discountAmount += promotionResultData.getTotalDiscount().getValue().doubleValue();
						}
					}
				}

			resultValue -= discountAmount;
		}
		resultValue = Double.valueOf(getDecimalValue(resultValue));
		return new VoucherValue(resultValue, resultCurrency);

	}

	/**
	 * This method formats the double number with 2 decimal positions.
	 *
	 * @param resultValue
	 * @return String
	 */
	private String getDecimalValue(final double resultValue)
	{
		final String value = String.valueOf(resultValue);
		try
		{
			final DecimalFormat df = new DecimalFormat("#.00");
			return df.format(resultValue);
		}
		catch (final NumberFormatException nbe)
		{
			// catch NBE and do nothing
		}
		return value;
	}

	/**
	 * This method is used to check the restrictions for voucher.
	 *
	 * @param anOrder
	 * @param voucher
	 * @return boolean
	 */
	public boolean isApplicable(final AbstractOrder anOrder, final Voucher voucher)
	{
		for (final Iterator iterator = voucher.getRestrictions().iterator(); iterator.hasNext();)
		{
			final Restriction nextRestriction = (Restriction) iterator.next();
			if (!nextRestriction.isFulfilled(anOrder))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * This method is used to check the applicable restrictions entry for voucher
	 *
	 * @param anOrder
	 * @param voucher
	 * @return VoucherEntrySet
	 */
	@SuppressWarnings("deprecation")
	public VoucherEntrySet getApplicableEntries(final AbstractOrder anOrder, final Voucher voucher)
	{
		final VoucherEntrySet entries = new VoucherEntrySet(anOrder.getAllEntries());
		if (null != voucher)
		{
			for (final Iterator iterator = voucher.getRestrictions().iterator(); (!entries.isEmpty()) && (iterator.hasNext());)
			{
				final Restriction nextRestriction = (Restriction) iterator.next();
				entries.retainAll(nextRestriction.getApplicableEntries(anOrder));
			}
		}
		return entries;
	}

	/**
	 * This method is used to get the applicable voucher value for the cart
	 *
	 * @param anOrder
	 * @param voucher
	 * @param cartData
	 * @return VoucherValue
	 */

	@Override
	public VoucherValue getApplicableValue(final AbstractOrder anOrder, final Voucher voucher, final CartData cartData)
	{
		double applicableValue = 0.0D;
		if (null != voucher)
		{
			for (final Iterator iterator = getApplicableEntries(anOrder, voucher).iterator(); iterator.hasNext();)
			{
				final VoucherEntry voucherEntry = (VoucherEntry) iterator.next();
				final AbstractOrderEntry orderEntry = voucherEntry.getOrderEntry();
				final long voucherEntryQuantity = voucherEntry.getQuantity();
				final long orderEntryQuantity = orderEntry.getQuantity().longValue();
				if (voucherEntryQuantity == orderEntryQuantity)
				{
					applicableValue += orderEntry.getTotalPrice().doubleValue();
				}
				else
				{
					final double calculatedBasePrice = orderEntry.getTotalPrice().doubleValue() / orderEntryQuantity;
					applicableValue += Math.min(voucherEntryQuantity, orderEntryQuantity) * calculatedBasePrice;
				}
			}
		}
		return new VoucherValue(applicableValue, anOrder.getCurrency());
	}

	@Override
	public VoucherValue getApplicableValueForOrder(final AbstractOrder anOrder, final Voucher voucher, final OrderData orderData)
	{
		double applicableValue = 0.0D;
		if (null != voucher)
		{
			for (final Iterator iterator = getApplicableEntries(anOrder, voucher).iterator(); iterator.hasNext();)
			{
				final VoucherEntry voucherEntry = (VoucherEntry) iterator.next();
				final AbstractOrderEntry orderEntry = voucherEntry.getOrderEntry();
				final long voucherEntryQuantity = voucherEntry.getQuantity();
				final long orderEntryQuantity = orderEntry.getQuantity().longValue();
				if (voucherEntryQuantity == orderEntryQuantity)
				{
					applicableValue += orderEntry.getTotalPrice().doubleValue();
				}
				else
				{
					final double calculatedBasePrice = orderEntry.getTotalPrice().doubleValue() / orderEntryQuantity;
					applicableValue += Math.min(voucherEntryQuantity, orderEntryQuantity) * calculatedBasePrice;
				}
			}
		}
		return new VoucherValue(applicableValue, anOrder.getCurrency());
	}

	/**
	 * This method is used to get the applicable voucher value for relative vouchers for the cart
	 *
	 * @param order
	 * @param anOrder
	 * @param voucher
	 * @return VoucherValue
	 */
	public VoucherValue getApplicableValueForRelative(final AbstractOrderModel order, final AbstractOrder anOrder,
			final Voucher voucher)
	{
		//final double calculatedBasePrice = order.getTotalPrice().doubleValue();
		final double calculatedBasePrice = order.getSubtotal().doubleValue();
		return new VoucherValue(calculatedBasePrice, anOrder.getCurrency());
	}

	/**
	 * This method is used to get the discount value for the applied voucher
	 *
	 * @param order
	 * @return AbstractOrder
	 */
	@Override
	public AbstractOrder getDiscountList(final AbstractOrderModel order)
	{
		return modelService.getSource(order);
	}
}
