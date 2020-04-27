/**
 *
 */
package com.spar.hcl.facades.voucher.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commercefacades.voucher.impl.DefaultVoucherFacade;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.promotions.model.OrderThresholdDiscountPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.jalo.Voucher;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.DateRestrictionModel;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.RestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.core.service.voucher.SparVoucherService;
import com.spar.hcl.facades.voucher.SparVoucherFacade;


/**
 * @author kumarchoubeys
 *
 */
/**
 * @author ravindra.kr
 *
 */
public class SparDefaultVoucherFacade extends DefaultVoucherFacade implements SparVoucherFacade
{

	/*
	 * @Resource(name = "sparCartFacade") private SparCartFacade sparCartFacade;
	 */
	private final static String VOUCHER_INVALID_MSG = "spar.invalid.voucher.value";
	private final static String VOUCHER_INVALID_MSGKEY = "spar.invalid.voucher.key";

	private final static String VOUCHER_MULTIPLE_APPLY_MSGKEY = "spar.multiple.apply.voucher.key";
	private final static String VOUCHER_MULTIPLE_APPLY_MSGVALUE = "spar.multiple.apply.voucher.value";
	private final String DATE_FORMAT = "dd-MM-yyyy";
	@Autowired
	private ModelService modelService;

	@Resource(name = "commerceCartCalculationStrategy")
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "sparVoucherService")
	private SparVoucherService sparVoucherService;

	@Resource(name = "voucherService")
	private VoucherService voucherService;

	@Resource(name = "sparCartService")
	private SparCartService sparCartService;

	@Resource(name = "orderService")
	private OrderService orderService;

	@Resource(name = "voucherModelService")
	private VoucherModelService voucherModelService;

	@Resource(name = "userService")
	private UserService userService;


	protected static final Logger LOG = Logger.getLogger(SparDefaultVoucherFacade.class);

	/**
	 * This method is used to apply the voucher in order review page
	 *
	 * @param voucherCode
	 * @return map
	 * @throws VoucherOperationException
	 */

	@Override
	public Map applyVoucherCode(final String voucherCode) throws VoucherOperationException
	{
		final Map canVoucherBeApply = new HashMap();
		updateTotalEquivalentPrice();
		if (!validateSparVoucherCodeParameter(voucherCode))
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		if (!isVoucherCodeValid(voucherCode))
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		final CartModel cartModel = getCartService().getSessionCart();
		final VoucherModel voucher = getVoucherModel(voucherCode);
		if (voucher == null)
		{
			canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
			return canVoucherBeApply;
		}

		final String voucherKey = Config.getString("spar.voucher." + voucherCode, "spar.voucher." + voucherCode);
		if (voucherKey.equalsIgnoreCase("SPAR_MONTHLY_VOCHER"))
		{
			final int count = getMonthlyVouchers();
			final String maxVoucherCount = Config.getParameter("spar.voucher.count");
			if (count >= Integer.parseInt(maxVoucherCount))
			{
				canVoucherBeApply.put(VOUCHER_MULTIPLE_APPLY_MSGKEY, VOUCHER_MULTIPLE_APPLY_MSGVALUE);
				return canVoucherBeApply;
			}
		}

		synchronized (cartModel)
		{
			if (!checkVoucherCanBeRedeemed(voucher, voucherCode))
			{
				canVoucherBeApply.put(VOUCHER_INVALID_MSGKEY, VOUCHER_INVALID_MSG);
				return canVoucherBeApply;
			}

			else
			{
				try
				{
					if (!getVoucherService().redeemVoucher(voucherCode, cartModel))
					{
						canVoucherBeApply.put("ErrorWhileApplyingVoucher", "Error While Applying Voucher");
					}
					//Important! Checking cart, if total amount <0, release this voucher
					commerceCartCalculationStrategy.recalculateCart(cartModel);
					checkCartAfterApplyVoucher(voucherCode, voucher);
					return canVoucherBeApply;
				}
				catch (final JaloPriceFactoryException e)
				{
					throw new VoucherOperationException("Error while applying voucher: " + voucherCode);
				}
			}
		}
	}

	/**
	 * This method is used to verify the cart after voucher apply
	 *
	 * @param lastVoucherCode
	 * @param lastVoucher
	 * @return boolean
	 * @throws VoucherOperationException
	 */
	@Override
	public boolean checkCartAfterApplyVoucher(final String lastVoucherCode, final VoucherModel lastVoucher)
			throws VoucherOperationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		modelService.refresh(cartModel);
		final double cartTotal = cartModel.getTotalPrice().doubleValue();
		if (cartTotal <= 0.0d)
		{
			cartModel.setTotalPrice(Double.valueOf(0));
			modelService.save(cartModel);
			return false;
		}
		return true;
	}


	/**
	 * This method is used to check whether the voucherValue is Exceeding the orderTotal
	 *
	 * @param voucherCode
	 * @return boolean
	 * @throws VoucherOperationException
	 */
	@Override
	public boolean isVoucherValueExceedingOrdertotal(final String voucherCode) throws VoucherOperationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final double cartTotal = cartModel.getTotalPrice().doubleValue();
		VoucherModel voucher = null;
		try
		{
			voucher = getVoucherModel(voucherCode);
			if (null != voucher)
			{
				final double voucherVal = voucher.getValue().doubleValue();
				if (voucherVal < cartTotal)
				{
					return false;
				}
			}
		}
		catch (final VoucherOperationException voucherOpr)
		{
			throw new VoucherOperationException("Error while applying voucher:::" + voucherOpr.getMessage());
		}

		return true;
	}

	/**
	 * This method is used to verify whether the voucher code is blank or not
	 *
	 * @param voucherCode
	 * @return boolean
	 */
	@Override
	public boolean validateSparVoucherCodeParameter(final String voucherCode)
	{
		if (StringUtils.isBlank(voucherCode))
		{
			return false;
		}
		return true;
	}

	/**
	 * This method is used to get the voucher model
	 *
	 * @param voucherCode
	 * @return VoucherModel
	 */
	@Override
	protected VoucherModel getVoucherModel(final String voucherCode) throws VoucherOperationException
	{
		final VoucherModel voucher = getVoucherService().getVoucher(voucherCode);
		if (voucher == null)
		{
			return null;
		}
		return voucher;
	}

	/**
	 * This method is used to get the applied voucher code in the cart
	 *
	 * @return List
	 */
	@Override
	public List<String> getSparVouchersForCart()
	{
		final List<String> vouchersData = new ArrayList<String>();
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel != null)
		{
			final Collection<String> voucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);
			for (final String code : voucherCodes)
			{
				try
				{
					if (null != getSingleVouchersByCode(code))
					{
						vouchersData.add(getSingleVouchersByCode(code).toString());
					}
				}
				catch (final VoucherOperationException e)
				{
					// nothing
				}
			}
			return vouchersData;
		}
		return Collections.EMPTY_LIST;
	}


	/**
	 * This method is used to check whether the applied voucher code is valid or not
	 *
	 * @param voucherCode
	 * @return boolean
	 */
	@Override
	public boolean isVoucherCodeValid(final String voucherCode)
	{
		final VoucherModel voucher = getVoucherService().getVoucher(voucherCode);
		if (voucher == null)
		{
			return false;
		}
		return true;
	}


	/**
	 * This method is used to do the cart calculation on apply voucher and on release voucher
	 *
	 * @param cartData
	 * @param voucherCode
	 */
	@SuppressWarnings("deprecation")
	public void doCartCalculation(final CartData cartData, final String voucherCode)
	{

		double discountAmount = 0.0;
		if (null != cartData.getOrderDiscounts())
		{
			discountAmount += cartData.getOrderDiscounts().getValue().doubleValue();
		}

		final VoucherModel voucherModel = getVoucherService().getVoucher(voucherCode);
		if (null != voucherModel)
		{
			discountAmount += voucherModel.getValue().doubleValue();
		}
		final PriceData discountData = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(discountAmount),
				commonI18NService.getCurrentCurrency());
		cartData.setOrderDiscounts(discountData);
		final CartModel cartModel = cartService.getSessionCart();
		commerceCartCalculationStrategy.recalculateCart(cartModel);
	}

	/**
	 * @param cartData
	 * @param voucherCode
	 */
	public void updateCartWithDiscountWithoutCartCalculation(final CartData cartData, final String voucherCode)
	{

		double discountAmount = 0.0;
		if (null != cartData.getOrderDiscounts())
		{
			discountAmount += cartData.getOrderDiscounts().getValue().doubleValue();
		}

		final VoucherModel voucherModel = getVoucherService().getVoucher(voucherCode);
		if (null != voucherModel)
		{
			discountAmount += voucherModel.getValue().doubleValue();
		}
		final PriceData discountData = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(discountAmount),
				commonI18NService.getCurrentCurrency());
		cartData.setOrderDiscounts(discountData);
	}

	/**
	 * This method is used to check whether the voucher is having any restriction on apply
	 *
	 * @param voucher
	 * @param voucherCode
	 * @return boolean
	 */
	@Override
	public boolean checkVoucherCanBeRedeemed(final VoucherModel voucher, final String voucherCode)
	{
		return getVoucherModelService().isApplicable(voucher, getCartService().getSessionCart())
				&& getVoucherModelService().isReservable(voucher, voucherCode, getCartService().getSessionCart());
	}

	public List currentVoucherInvalidationCheck(final String voucherCode)
	{
		return sparVoucherService.currentVoucherInvalidationCheck(voucherCode);
	}


	/**
	 * This method is used to release the applied voucher forcefully in case of any fraud voucher apply
	 *
	 * @param voucherCode
	 * @param cartModel
	 * @throws VoucherOperationException
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void releaseSparVoucher(final String voucherCode) throws VoucherOperationException
	{
		final CartModel cartModel = cartService.getSessionCart();
		final AbstractOrder anOrder = modelService.getSource(getCartService().getSessionCart());
		final Iterator<Discount> discounts = anOrder.getDiscounts().iterator();
		final boolean found = false;
		Voucher voucher = null;
		final VoucherModel voucherModel = voucherService.getVoucher(voucherCode);
		if (discounts.hasNext())
		{
			while ((discounts.hasNext()) && (!found))
			{
				final Discount discount = discounts.next();
				if ((discount instanceof Voucher))
				{
					voucher = (Voucher) discount;
					try
					{
						voucher.release(voucherCode, (Cart) modelService.getSource(cartModel));
						voucher.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
						discount.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
					}
					catch (final Exception e)
					{
						LOG.error("Error while releasing the voucher:::" + e.getMessage());
						voucher.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
						discount.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
					}
				}
			}
		}
		else if (null != voucherModel)
		{
			final Voucher voucherJalo = modelService.getSource(voucherModel);
			try
			{
				voucherJalo.release(voucherCode, (Cart) modelService.getSource(cartModel));
				voucherJalo.removeFromOrders(anOrder);
				//discount.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
			}
			catch (final Exception e)
			{
				LOG.error("Error while releasing the voucher:::" + e.getMessage());
				voucherJalo.removeFromOrders(anOrder);
				//discount.removeFromOrders(JaloSession.getCurrentSession().getSessionContext(), anOrder);
			}
		}
		cartModel.setPaidByVoucher(Double.valueOf(0));
		modelService.saveAll();
		modelService.refresh(voucherModel);
		modelService.refresh(cartModel);
	}

	@Override
	public boolean updateOrderWithVoucher(final CartData cartData)
	{
		boolean flag = false;
		final List<VoucherData> voucherCode = getVouchersForCart();
		VoucherData voucherCodeForCart = null;
		if (CollectionUtils.isNotEmpty(voucherCode))
		{
			final Iterator itr = voucherCode.iterator();
			while (itr.hasNext())
			{
				voucherCodeForCart = (VoucherData) itr.next();
				break;
			}
		}
		if (null != voucherCodeForCart)
		{
			final String voucherCodeCheckInvalid = voucherCodeForCart.getVoucherCode();
			final VoucherModel voucherModel = voucherService.getVoucher(voucherCodeCheckInvalid);
			if (CollectionUtils.isNotEmpty(currentVoucherInvalidationCheck(voucherCodeCheckInvalid))
					&& voucherModel.getItemtype().equals("SerialVoucher"))
			{
				try
				{
					releaseSparVoucher(voucherCodeForCart.getVoucherCode());
					sparCartService.getOrderEntriesTotalPaidByFoodCoupon(cartService.getSessionCart());
					doCartCalculation(cartData, voucherCodeCheckInvalid);
					flag = true;
				}
				catch (final VoucherOperationException e)
				{
					LOG.error("Execption occured for spar voucher release");
				}
			}
			else
			{
				if (null != voucherCodeForCart.getVoucherCode())
				{
					final CartModel cartModel = cartService.getSessionCart();
					OrderModel order;
					try
					{
						order = orderService.createOrderFromCart(cartModel);
						cartModel.setVoucherDescription(voucherCodeForCart.getVoucherCode());
						modelService.save(order);
						voucherService.reserveVoucher(voucherCodeForCart.getVoucherCode(), order);
						modelService.remove(order);
					}
					catch (final InvalidCartException e)
					{
						LOG.error("Execption occured for creating order for cart");
					}
				}
			}
		}

		return flag;
	}

	@Override
	public void updateVoucherValue(final CartData cartData, final boolean isVoucherApplied)
	{
		String voucherCode = null;
		VoucherValue voucherValue = null;
		final CartModel cartModel = getCartService().getSessionCart();
		Double totalVoucherValue = Double.valueOf(0.0);
		if (isVoucherApplied)
		{
			final Collection<String> voucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);
			for (final String voucher : voucherCodes)
			{
				if (null != voucher)
				{
					voucherCode = voucher;
					break;
				}
			}
			if (null != voucherCode)
			{
				final VoucherModel voucherModel = getVoucherService().getVoucher(voucherCode);
				voucherValue = sparVoucherService.getAppliedValue(voucherModel, cartModel, cartData);
				if (voucherValue.getValue() == 0.0D)
				{
					voucherValue = getVoucherModelService().getAppliedValue(voucherModel, cartModel);
				}
			}
			if (null != voucherValue)
			{
				totalVoucherValue = Double.valueOf(voucherValue.getValue());

			}
		}
		cartModel.setPaidByVoucher(totalVoucherValue);
		modelService.save(cartModel);
		modelService.refresh(cartModel);
	}

	@Override
	public double getAppliedVoucherValue()
	{
		double appliedVoucherVal = 0.0D;
		String voucherVal = null;
		VoucherModel voucherModel = null;
		final CartModel cartModel = cartService.getSessionCart();
		final Collection<String> voucherCodes = voucherService.getAppliedVoucherCodes(cartModel);

		if (null != voucherCodes && CollectionUtils.isNotEmpty(voucherCodes))
		{
			for (final String voucher : voucherCodes)
			{
				voucherVal = voucher;
				break;
			}
			if (null != voucherVal)
			{
				voucherModel = voucherService.getVoucher(voucherVal);
			}
			if (null != voucherModel)
			{
				final VoucherValue appliedValue = voucherModelService.getAppliedValue(voucherModel, cartModel);
				appliedVoucherVal = appliedValue.getValue();
			}
		}
		return appliedVoucherVal;
	}

	public Collection<String> getAppliedVoucherCodes()
	{
		final CartModel cartModel = cartService.getSessionCart();
		final Collection<String> voucherCodes = voucherService.getAppliedVoucherCodes(cartModel);
		return voucherCodes;
	}

	@Override
	public void updateTotalEquivalentPrice()
	{
		final CartModel cartModel = cartService.getSessionCart();

		double currentTotal = cartModel.getSubtotal().doubleValue();
		for (final PromotionResultModel promo : cartModel.getAllPromotionResults())
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
		cartModel.setTotalEqvPrice(Double.valueOf(currentTotal));
		modelService.save(cartModel);
		modelService.refresh(cartModel);
	}

	protected int getMonthlyVouchers()
	{
		int totalVoucherCount = 0;
		final UserModel userModel = userService.getCurrentUser();
		if (userModel != null)
		{
			if (CollectionUtils.isNotEmpty(userModel.getOrders()))
			{
				for (final OrderModel orderModel : userModel.getOrders())
				{
					if (orderModel != null && CollectionUtils.isNotEmpty(orderModel.getDiscounts()))
					{
						final int count = calculateDiscountForMonthlyVoucher(orderModel);
						totalVoucherCount = totalVoucherCount + count;
					}
				}
			}
		}
		return totalVoucherCount;
	}

	private int calculateDiscountForMonthlyVoucher(final OrderModel orderModel)
	{
		int count = 0;
		final Date orderCreationDate = checkVoucherDate(orderModel.getCreationtime(), DATE_FORMAT);
		final Calendar calender = Calendar.getInstance();
		final Date date = calender.getTime();
		final Date currentDate = checkVoucherDate(date, DATE_FORMAT);
		final int currentMonth = getMonth(currentDate);
		final int orderMonth = getMonth(orderCreationDate);
		final int currentYear = getYear(currentDate);
		final int orderYear = getYear(orderCreationDate);
		if (orderMonth == currentMonth && orderYear == currentYear)
		{
			for (final DiscountModel discountModel : orderModel.getDiscounts())
			{
				Assert.notNull(discountModel, "DiscountModel should not be null");
				if (discountModel instanceof PromotionVoucherModel)
				{
					final PromotionVoucherModel voucherModel = (PromotionVoucherModel) discountModel;
					Assert.notNull(voucherModel, "VoucherModel should not be null");
					final String voucherKey = Config.getString("spar.voucher." + voucherModel.getVoucherCode(),
							"spar.voucher." + voucherModel.getVoucherCode());
					final DateRestrictionModel dateRestrictionModel = getVoucherRestrictions(voucherModel);
					Assert.notNull(dateRestrictionModel, "DateRestrictionModel should not be null");
					final Date startDate = checkVoucherDate(dateRestrictionModel.getStartDate(), DATE_FORMAT);
					final Date voucherEndDate = checkVoucherDate(dateRestrictionModel.getEndDate(), DATE_FORMAT);

					if (startDate != null && orderCreationDate != null && voucherEndDate != null)
					{
						if (orderCreationDate.getTime() >= startDate.getTime() && currentDate.getTime() >= startDate.getTime()
								&& voucherEndDate.getTime() >= currentDate.getTime())
						{
							if (voucherKey.equalsIgnoreCase("SPAR_MONTHLY_VOCHER"))
							{
								count = count + 1;
							}
						}
					}

				}
			}
		}

		return count;
	}

	private DateRestrictionModel getVoucherRestrictions(final VoucherModel vocherModel)
	{
		DateRestrictionModel dateRestriction = null;
		if (vocherModel.getRestrictions() != null && CollectionUtils.isNotEmpty(vocherModel.getRestrictions()))
		{
			for (final RestrictionModel restriction : vocherModel.getRestrictions())
			{
				if (restriction instanceof DateRestrictionModel)
				{
					dateRestriction = ((DateRestrictionModel) restriction);
				}
			}
		}
		return dateRestriction;
	}

	private Date checkVoucherDate(final Date date, final String formater)
	{
		Date startDate = null;
		if (date != null)
		{
			final SimpleDateFormat dateFormat = new SimpleDateFormat(formater);
			try
			{
				startDate = dateFormat.parse(dateFormat.format(date));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
		}

		return startDate;
	}

	private int getMonth(final Date date)
	{
		final Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		final int orderMonth = calender.get(Calendar.MONTH);
		return orderMonth + 1;
	}

	private int getYear(final Date date)
	{
		final Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		final int orderYear = calender.get(Calendar.YEAR);
		return orderYear;
	}

}
