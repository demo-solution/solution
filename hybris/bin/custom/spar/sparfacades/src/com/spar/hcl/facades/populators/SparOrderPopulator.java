/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.spar.hcl.core.service.voucher.SparVoucherService;
import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.order.data.PaymentModeData;
import com.spar.hcl.order.promotion.strategy.SparCombiOfferPromotionStrategy;


/**
 * This populator is used to set payment mode in OrderData
 *
 * @author rohan_c
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class SparOrderPopulator extends OrderPopulator
{
	private CommonI18NService commonI18NService;
	private PriceDataFactory priceDataFactory;
	private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;
	private Converter<DeliverySlotModel, DeliverySlotData> deliverySlotConverter;
	private ModelService modelService;
	private SparCombiOfferPromotionStrategy sparCombiOfferPromotionStrategy;
	private VoucherService voucherService;
	private Converter<VoucherModel, VoucherData> voucherConverter;
	private Populator<AbstractOrderModel, VoucherData> appliedVoucherPopulatorList;
	private SparVoucherService sparVoucherService;

	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{
		super.populate(source, target);
		populateDeliverySlot(source, target);
		populatePaymentMode(source, target);
		populateOrderSavings(source, target);
		populateAppliedOrderPromotionTotal(source, target);
		populateVoucherEntry(source, target);
		populateAppliedVoucher(source, target);
		//populateFoodCouponTotal(source, target);
		populateAlternateVoucherEntry(source, target);
		populateEmployeeDiscountTotal(source, target);
	}

	/**
	 * This method populates payment mode
	 *
	 * @param source
	 * @param target
	 */
	private void populatePaymentMode(final OrderModel source, final OrderData target)
	{
		if (source.getPaymentMode() != null)
		{
			target.setPaymentMode(paymentModeConverter.convert(source.getPaymentMode()));
		}
		else
		{
			target.setPaymentMode(null);
		}
	}

	/**
	 * This method populates delivery slot
	 *
	 * @param source
	 * @param target
	 */
	private void populateDeliverySlot(final OrderModel source, final OrderData target)
	{
		final Date deliveryDate = source.getSlotDeliveryDate();
		final DeliverySlotModel deliverySlot = source.getOrderDeliverySlot();
		if (deliverySlot != null && deliveryDate != null)
		{
			target.setSlotDeliveryDate(deliveryDate);
			target.setOrderDeliverySlot(deliverySlotConverter.convert(deliverySlot));
		}
	}

	/**
	 * This method is used to set saving in OrderData
	 *
	 * @param target
	 * @throws ConversionException
	 */
	public void populateOrderSavings(final OrderModel source, final OrderData target) throws ConversionException
	{
		BigDecimal orderSavings = BigDecimal.ZERO;
		for (final AbstractOrderEntryModel orderModelItem : source.getEntries())
		{
			for (final OrderEntryData orderEntryData : target.getEntries())
			{
				if (orderEntryData.getProduct().getCode().equals(orderModelItem.getProduct().getCode()))
				{
					if (null != orderModelItem.getSavings() 
							&& orderEntryData.getBasePrice().getValue().doubleValue() == orderModelItem.getBasePrice().doubleValue())
					{
						final BigDecimal orderEntrySavings = BigDecimal.valueOf(orderModelItem.getSavings().doubleValue());
						orderSavings = orderSavings.add(orderEntrySavings);
						final PriceData entryPriceData = getPriceDataFactory().create(PriceDataType.BUY, orderEntrySavings,
								getCommonI18NService().getCurrentCurrency());
						orderEntryData.setSavings(entryPriceData);
						orderEntryData.setCombiOfferApplied(orderModelItem.isCombiOffer());
					}
				}
			}
		}
		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderSavings,
				getCommonI18NService().getCurrentCurrency());
		target.setSavings(priceData);
	}


	/**
	 * This method is used to get the tota of Applied Order Promotion.
	 *
	 * @param source
	 * @param target
	 */
	private void populateAppliedOrderPromotionTotal(final OrderModel source, final OrderData target)
	{
		BigDecimal totalOrderDiscount = BigDecimal.ZERO;
		for (final PromotionResultData promotionresult : target.getAppliedOrderPromotions())
		{
			if (!promotionresult.getPromotionData().getPromotionType().equals("Employee Discount Promotion"))
			{
				totalOrderDiscount = totalOrderDiscount.add(promotionresult.getTotalDiscount().getValue());
			}

		}
		target.setAppliedOrderPromotionTotal(createPrice(source, new Double(totalOrderDiscount.doubleValue())));
	}

	private void populateAlternateVoucherEntry(final OrderModel source, final OrderData target)
	{
		if (source.getPaidByVoucher() != null)
		{
			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
					BigDecimal.valueOf(source.getPaidByVoucher().doubleValue()), getCommonI18NService().getCurrentCurrency());
			target.setVoucherValue(priceData);
		}
	}

	/*
	 * public void populateFoodCouponTotal(final OrderModel orderModel, final OrderData orderData) throws
	 * ConversionException { final Double totalFoodCouponAmt =
	 * getSparCartService().getOrderEntriesTotalPaidByFoodCoupon(orderModel);
	 * orderModel.setTotalFoodCouponAmount(totalFoodCouponAmt); }
	 */


	/**
	 * This method is overridden to get the Unique message per consumed entry.
	 */
	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData prototype)
	{
		super.addPromotions(source, promoOrderResults, prototype);
		// This logic is used to  get the consolidated messages for consumed entries.
		if (promoOrderResults != null)
		{
			prototype.setAppliedProductPromotions(getUniqueAppliedProductPromotions(source,
					promoOrderResults.getAppliedProductPromotions(), prototype));
		}
	}


	/**
	 * This method is used to get the consolidated messages for consumed entries having product promotion.
	 *
	 * @param source
	 * @param promotionsResults
	 * @param orderdata
	 * @return List<PromotionResultData>
	 */
	protected List<PromotionResultData> getUniqueAppliedProductPromotions(final AbstractOrderModel source,
			final List<PromotionResult> promotionsResults, final AbstractOrderData orderData)
	{
		final ArrayList<PromotionResultModel> promotionResultModels = getModelService().getAll(promotionsResults,
				new ArrayList<PromotionResultModel>());
		final List<PromotionResultData> promotionResultList = Converters.convertAll(promotionResultModels,
				getPromotionResultConverter());
		final List<PromotionResultData> appliedCombiOfferProductPromotionsList = new ArrayList<PromotionResultData>();
		final List<PromotionResultData> filteredPromotionResultList = new ArrayList<PromotionResultData>();
		filterAppliedProductPromotions(promotionResultList, filteredPromotionResultList, appliedCombiOfferProductPromotionsList);
		orderData.setCombiOfferAppliedProductPromotion(appliedCombiOfferProductPromotionsList);
		return filteredPromotionResultList;
	}


	/**
	 * This method is used to filter the promotions with duplicate message so that messages are consolidated.
	 *
	 * @param promotionResults
	 * @param filteredPromotionResultList
	 * @param appliedCombiOfferProductPromotionsList
	 */
	private void filterAppliedProductPromotions(final List<PromotionResultData> promotionResults,
			final List<PromotionResultData> filteredPromotionResultList,
			final List<PromotionResultData> appliedCombiOfferProductPromotionsList)
	{
		final boolean isUniqueFiredPromotionMessageEnabled = Config.getBoolean("UNIQUE_FIRED_PROMOTION_MESSAGE_ENABLE", true);
		final Map<String, PromotionResultData> promotionOrderEntryMessage = new HashMap<String, PromotionResultData>();

		for (final PromotionResultData promotionResult : promotionResults)
		{
			if (isUniqueFiredPromotionMessageEnabled)
			{
				final String desc = getMapKey(promotionResult).toString();
				final boolean isSamePromotionResultForOrderEntry = promotionOrderEntryMessage.containsKey(desc);
				if (!isSamePromotionResultForOrderEntry)
				{
					promotionResult.setAppliedProductPromotionCount(new Long(1L));
					promotionOrderEntryMessage.put(desc, promotionResult);
				}
				else
				{
					final PromotionResultData promotionResultData = promotionOrderEntryMessage.get(desc);
					promotionResultData.setAppliedProductPromotionCount(new Long(Long.sum(promotionResultData
							.getAppliedProductPromotionCount().longValue(), 1L)));
				}
			}
			doesPromotionResultHasCombiOffer(appliedCombiOfferProductPromotionsList, promotionResult);
		}
		if (CollectionUtils.isNotEmpty(promotionOrderEntryMessage.values()))
		{
			filteredPromotionResultList.addAll(promotionOrderEntryMessage.values());
		}
		else
		{
			filteredPromotionResultList.addAll(promotionResults);
		}
	}


	/**
	 * This method is used to validate if Combi Offer is applied to the consumed order entry.
	 *
	 * @param appliedCombiOfferProductPromotionsList
	 * @param promotionResult
	 */
	private void doesPromotionResultHasCombiOffer(final List<PromotionResultData> appliedCombiOfferProductPromotionsList,
			final PromotionResultData promotionResult)
	{
		if (CollectionUtils.isNotEmpty(promotionResult.getConsumedEntries()))
		{
			final PromotionOrderEntryConsumedData consumedEntry = promotionResult.getConsumedEntries().get(0);
			if (consumedEntry.isCombiOfferApplicable())
			{
				for (final String promotionType : getSparCombiOfferPromotionStrategy().getCombiPromotionType())
				{
					if (promotionResult.getPromotionData().getPromotionType().equals(promotionType))
					{
						appliedCombiOfferProductPromotionsList.add(promotionResult);
						break;
					}
				}
			}
		}
	}



	/**
	 * This method is used to get the Key in form of description concatinated by entry number of consumed entries.
	 *
	 * @param promotionResult
	 * @return StringBuilder
	 */
	private StringBuilder getMapKey(final PromotionResultData promotionResult)
	{
		final StringBuilder desc = new StringBuilder(promotionResult.getDescription());
		final List<PromotionOrderEntryConsumedData> sortedList = doSort(promotionResult.getConsumedEntries());
		for (final PromotionOrderEntryConsumedData consumedEntry : sortedList)
		{
			desc.append(consumedEntry.getOrderEntryNumber());
		}
		return desc;
	}


	/**
	 * This method is used to sort the list for Consumed Entries
	 *
	 * @param list
	 * @return List
	 */
	private List doSort(final List list)
	{
		if (!list.isEmpty() && list.iterator().next() instanceof PromotionOrderEntryConsumedData)
		{
			try
			{
				final List<PromotionOrderEntryConsumedData> sortedList = new ArrayList<PromotionOrderEntryConsumedData>(list);
				Collections.sort(sortedList, new SparPromotionOrderEntryComparator());
				return sortedList;
			}
			catch (final Exception e)
			{
				//in case of any failure
				//LOG.warn("Could not sort the consumedEntryList ", e);
				return list;
			}
		}
		else
		{
			return list;
		}
	}

	/* Code change start for voucher Story Sumit */
	private void populateVoucherEntry(final OrderModel source, final OrderData target)
	{
		final List<VoucherData> vouchers = new ArrayList<VoucherData>();
		final Collection<DiscountModel> discounts = voucherService.getAppliedVouchers(source);
		for (final DiscountModel discount : discounts)
		{
			if (discount instanceof VoucherModel)
			{
				final VoucherData voucher = getVoucherConverter().convert((VoucherModel) discount);
				getAppliedVoucherPopulatorList().populate(source, voucher);
				vouchers.add(voucher);
			}
		}
		target.setAppliedVoucherTotal(vouchers);

	}

	private void populateAppliedVoucher(final OrderModel source, final OrderData target)
	{
		final String voucherCode = source.getVoucherDescription();
		target.setVoucherCode(voucherCode);
	}

	/* Changes end here */
	/**
	 * Getter
	 *
	 * @return the modelService
	 */
	@Override
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Setter
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Getter
	 *
	 * @return the sparCombiOfferPromotionStrategy
	 */
	public SparCombiOfferPromotionStrategy getSparCombiOfferPromotionStrategy()
	{
		return sparCombiOfferPromotionStrategy;
	}

	/**
	 * Setter
	 *
	 * @param sparCombiOfferPromotionStrategy
	 *           the sparCombiOfferPromotionStrategy to set
	 */
	public void setSparCombiOfferPromotionStrategy(final SparCombiOfferPromotionStrategy sparCombiOfferPromotionStrategy)
	{
		this.sparCombiOfferPromotionStrategy = sparCombiOfferPromotionStrategy;
	}


	/**
	 * This class is a comparator class to sort PromotionOrderEntryConsumedData.
	 *
	 * @author rohan_c
	 *
	 */
	class SparPromotionOrderEntryComparator implements Comparator<PromotionOrderEntryConsumedData>
	{
		@Override
		public int compare(final PromotionOrderEntryConsumedData orderEntry1, final PromotionOrderEntryConsumedData orderEntry2)
		{
			try
			{
				final Integer orderEntryNumber1 = orderEntry1.getOrderEntryNumber();
				final Integer orderEntryNumber2 = orderEntry2.getOrderEntryNumber();
				return orderEntryNumber1.compareTo(orderEntryNumber2);
			}
			catch (final Exception e)
			{
				//in case of any failure
				//	LOG.warn("Could not compare Order Entry number ", e);
				return 0;
			}
		}
	}

	private void populateEmployeeDiscountTotal(final OrderModel source, final OrderData target)
	{
		BigDecimal totalEmployeeDiscount = BigDecimal.ZERO;
		for (final PromotionResultData promotionresult : target.getAppliedOrderPromotions())
		{
			if (promotionresult.getPromotionData().getPromotionType().equals("Employee Discount Promotion"))
			{
				totalEmployeeDiscount = totalEmployeeDiscount.add(promotionresult.getTotalDiscount().getValue());
			}
		}
		target.setAppliedEmployeeDiscountPromotionTotal(createPrice(source, new Double(totalEmployeeDiscount.doubleValue())));
	}



	/**
	 * Getter
	 *
	 * @return the paymentModeConverter
	 */
	public Converter<PaymentModeModel, PaymentModeData> getPaymentModeConverter()
	{
		return paymentModeConverter;
	}

	/**
	 * Setter
	 *
	 * @param paymentModeConverter
	 *           the paymentModeConverter to set
	 */
	public void setPaymentModeConverter(final Converter<PaymentModeModel, PaymentModeData> paymentModeConverter)
	{
		this.paymentModeConverter = paymentModeConverter;
	}

	/**
	 * Getter
	 *
	 * @return the commonI18NService
	 */
	@Override
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Setter
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Override
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Getter
	 *
	 * @return the priceDataFactory
	 */
	@Override
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Setter
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Override
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Getter
	 *
	 * @return the deliverySlotConverter
	 */
	public Converter<DeliverySlotModel, DeliverySlotData> getDeliverySlotConverter()
	{
		return deliverySlotConverter;
	}

	/**
	 * Setter
	 *
	 * @param deliverySlotConverter
	 *           the deliverySlotConverter to set
	 */
	public void setDeliverySlotConverter(final Converter<DeliverySlotModel, DeliverySlotData> deliverySlotConverter)
	{
		this.deliverySlotConverter = deliverySlotConverter;
	}

	/**
	 * @return the voucherService
	 */
	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	/**
	 * @param voucherService
	 *           the voucherService to set
	 */
	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

	/**
	 * @return the voucherConverter
	 */
	public Converter<VoucherModel, VoucherData> getVoucherConverter()
	{
		return voucherConverter;
	}

	/**
	 * @param voucherConverter
	 *           the voucherConverter to set
	 */
	public void setVoucherConverter(final Converter<VoucherModel, VoucherData> voucherConverter)
	{
		this.voucherConverter = voucherConverter;
	}

	/**
	 * @return the appliedVoucherPopulatorList
	 */
	public Populator<AbstractOrderModel, VoucherData> getAppliedVoucherPopulatorList()
	{
		return appliedVoucherPopulatorList;
	}

	/**
	 * @param appliedVoucherPopulatorList
	 *           the appliedVoucherPopulatorList to set
	 */
	public void setAppliedVoucherPopulatorList(final Populator<AbstractOrderModel, VoucherData> appliedVoucherPopulatorList)
	{
		this.appliedVoucherPopulatorList = appliedVoucherPopulatorList;
	}

	/**
	 * @return the sparVoucherService
	 */
	public SparVoucherService getSparVoucherService()
	{
		return sparVoucherService;
	}

	/**
	 * @param sparVoucherService
	 *           the sparVoucherService to set
	 */
	public void setSparVoucherService(final SparVoucherService sparVoucherService)
	{
		this.sparVoucherService = sparVoucherService;
	}

}
