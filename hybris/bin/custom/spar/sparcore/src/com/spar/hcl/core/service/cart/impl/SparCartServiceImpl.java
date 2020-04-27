/**
 *
 */
package com.spar.hcl.core.service.cart.impl;




import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.order.CommerceDeliveryModeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.VoucherModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.order.promotion.strategy.SparCombiOfferPromotionStrategy;


/**
 * @author nikhil-ku
 * @modified by tayal.m, rohan_c
 *
 */
public class SparCartServiceImpl extends DefaultCartService implements SparCartService
{



	private ProductService productService;
	private BaseStoreService baseStoreService;
	private CommerceDeliveryModeStrategy commerceDeliveryModeStrategy;
	private PromotionsService promotionService;
	private SparCombiOfferPromotionStrategy sparCombiOfferPromotionStrategy;
	private PriceService priceService;
	@Autowired
	private ModelService modelService;
	private VoucherService voucherService;
	private VoucherModelService voucherModelService;

	/**
	 * @return the voucherModelService
	 */
	public VoucherModelService getVoucherModelService()
	{
		return voucherModelService;
	}

	/**
	 * @param voucherModelService
	 *           the voucherModelService to set
	 */
	public void setVoucherModelService(final VoucherModelService voucherModelService)
	{
		this.voucherModelService = voucherModelService;
	}

	/**
	 * @return the commerceDeliveryModeStrategy
	 */
	public CommerceDeliveryModeStrategy getCommerceDeliveryModeStrategy()
	{
		return commerceDeliveryModeStrategy;
	}

	/**
	 * @param commerceDeliveryModeStrategy
	 *           the commerceDeliveryModeStrategy to set
	 */
	public void setCommerceDeliveryModeStrategy(final CommerceDeliveryModeStrategy commerceDeliveryModeStrategy)
	{
		this.commerceDeliveryModeStrategy = commerceDeliveryModeStrategy;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Returns last level category on the basis of product code.
	 */

	@Override
	public CategoryModel getLastLevelCategoryOnProduct(final String productCode)
	{
		final Collection<CategoryModel> categoryModels = new ArrayList<>();
		try
		{
			final ProductModel product = getProductService().getProductForCode(productCode);

			final ProductModel baseProductModel = getBaseProduct(product);
			categoryModels.addAll(baseProductModel.getSupercategories());
		}
		catch (final Exception e)
		{
			// No product found
		}


		return getFirstLevelCategory(categoryModels);
	}

	/**
	 * @param categoryModels
	 * @return CategoryModel
	 */
	@Override
	public CategoryModel getFirstLevelCategory(final Collection<CategoryModel> categoryModels)
	{
		//CategoryModel lastLevelCategory = new CategoryModel();
		CategoryModel lastLevelCategory = modelService.create(CategoryModel.class);
		final List<CategoryModel> categoryModels1 = new ArrayList<>();

		while (!categoryModels.isEmpty())
		{
			CategoryModel toDisplay = null;
			for (final CategoryModel categoryModel : categoryModels)
			{
				if (!(categoryModel instanceof ClassificationClassModel))
				{
					if (toDisplay == null)
					{
						toDisplay = categoryModel;
					}
				}
			}
			categoryModels.clear();
			if (toDisplay != null)
			{
				categoryModels1.add(toDisplay);
				categoryModels.addAll(toDisplay.getSupercategories());
			}
		}
		Collections.reverse(categoryModels1);
		for (final CategoryModel c : categoryModels1)
		{
			if (null != c)
			{
				lastLevelCategory = c;
				break;
			}
		}
		return lastLevelCategory;
	}

	protected ProductModel getBaseProduct(final ProductModel product)
	{
		if (product instanceof VariantProductModel)
		{
			return getBaseProduct(((VariantProductModel) product).getBaseProduct());
		}
		return product;
	}


	protected String getProductBreadcrumb(final ProductModel product)
	{
		return product.getName();
	}

	protected String getCategoryBreadcrumb(final CategoryModel category)
	{
		return category.getName();
	}

	/**
	 * Returns total amount that can be paid by food coupon for all the OrderEntries.
	 */
	@Override
	public Double getOrderEntriesTotalPaidByFoodCoupon(final CartModel cartModel)
	{
		double fcOrderTotalWithDiscount = 0D;
		double orderTotalWithDiscount = 0D;
		double fcOrderTotalWithVoucherDiscount = 0D;
		if (cartModel.getEntries() != null && !cartModel.getEntries().isEmpty())
		{
			for (final AbstractOrderEntryModel entry : cartModel.getEntries())
			{
				if (null != entry.getProduct())
				{
					// make a method that would return list of category models and would have chain of category
					final PromotionOrderResults promotionOrderResults = getPromotionService().getPromotionResults(cartModel);
					boolean isAvailableForFC = false;
					long spareQtyOfEntry = entry.getQuantity().longValue();
					try
					{
						final List<CategoryModel> categories = getCategoryChain(entry.getProduct().getCode());
						for (final CategoryModel category : categories)
						{
							if (category.getIsAvailableForFoodCoupon().booleanValue())
							{
								isAvailableForFC = true;
								break;
							}
						}
					}
					catch (final IllegalArgumentException e)
					{
						// Product code not found
					}

					final double orderEntryDisocuntTotal = getOrderEntryDiscountTotalForCombi(entry.getEntryNumber(),
							promotionOrderResults);

					boolean combiWithOtherPromoApplied = false;
					double adjustedPriceForCombi = 0;
					final List<PromotionResult> promoProductResultList = promotionOrderResults.getAppliedProductPromotions();
					for (final PromotionResult promoResult : promoProductResultList)
					{
						if (isCombiOffer(promoResult))
						{
							spareQtyOfEntry -= 1;
						}
						else
						{
							final Iterator<PromotionOrderEntryConsumed> iterator = promoResult.getConsumedEntries().iterator();
							while (iterator.hasNext())
							{
								final PromotionOrderEntryConsumed consumedEntry = iterator.next();
								try
								{
									if (entry.getEntryNumber().equals(consumedEntry.getOrderEntry().getEntryNumber()))
									{
										adjustedPriceForCombi += consumedEntry.getAdjustedEntryPrice();
										combiWithOtherPromoApplied = true;
										spareQtyOfEntry = spareQtyOfEntry - promoResult.getConsumedCount(false);
									}
								}
								catch (final Exception e)
								{
									// Product not found
								}
							}
						}
					}

					double orderEntryTotal = orderEntryDisocuntTotal == 0 ? entry.getTotalPrice().doubleValue()
							: orderEntryDisocuntTotal;

					if (combiWithOtherPromoApplied && isAvailableForFC && orderEntryDisocuntTotal > 0 && adjustedPriceForCombi > 0)
					{
						orderEntryTotal += adjustedPriceForCombi;
					}

					if (spareQtyOfEntry > 0 && orderEntryDisocuntTotal > 0)
					{
						orderEntryTotal += (spareQtyOfEntry * entry.getBasePrice().doubleValue());
					}

					//calculate all order entry total with Discount (combi+regular)
					orderTotalWithDiscount += orderEntryTotal;
					if (isAvailableForFC)
					{
						//calculate food coupon (fc) order entry total with Discount (combi+regular)
						fcOrderTotalWithDiscount += orderEntryTotal;
					}

				}
			}
		}

		//calculate prorated food coupons in case if there is any order level promotion applicable.

		fcOrderTotalWithDiscount = getFoodCouponTotalAfterOrderLevelDiscount(fcOrderTotalWithDiscount, orderTotalWithDiscount,
				cartModel);
		final double getTotalAfterDiscount = getTotalAfterFirstLevelDiscount(orderTotalWithDiscount, cartModel);
		fcOrderTotalWithVoucherDiscount = getFoodCouponTotalAfterVoucherDiscount(fcOrderTotalWithDiscount, getTotalAfterDiscount,
				cartModel);
		fcOrderTotalWithDiscount = fcOrderTotalWithVoucherDiscount == 0 ? fcOrderTotalWithDiscount
				: fcOrderTotalWithVoucherDiscount;
		return Double.valueOf(getDecimalValue(fcOrderTotalWithDiscount));
	}

	/**
	 * This method formats the double number with 2 decimal positions.
	 *
	 * @param fcOrderTotalWithDiscount
	 * @return String
	 */
	private String getDecimalValue(final double fcOrderTotalWithDiscount)
	{
		final String value = String.valueOf(fcOrderTotalWithDiscount);
		try
		{
			final DecimalFormat df = new DecimalFormat("#.00");
			return df.format(fcOrderTotalWithDiscount);
		}
		catch (final NumberFormatException nbe)
		{
			// catch NBE and do nothing
		}
		return value;
	}

	/**
	 * This method is used to get the total amount after by first level of discount or before voucher
	 *
	 * @param foodCouponTotal
	 * @param orderEntryTotalWithDiscount
	 * @param cartModel
	 * @return double
	 */
	private double getTotalAfterFirstLevelDiscount(final double orderEntryTotalWithDiscount, final CartModel cartModel)
	{
		if (0 != orderEntryTotalWithDiscount)
		{
			final PromotionOrderResults promotionOrderResults = getPromotionService().getPromotionResults(cartModel);
			final double orderLevelDiscount = getOrderDiscountsAmount(promotionOrderResults);
			return orderEntryTotalWithDiscount - orderLevelDiscount;
		}
		return 0D;
	}

	/**
	 * This method is used to get the food coupon total amount to be paid by the customer when voucher is applied
	 *
	 * @param foodCouponTotal
	 * @param orderEntryTotalWithDiscount
	 * @param cartModel
	 * @return double
	 */
	private double getFoodCouponTotalAfterVoucherDiscount(double foodCouponTotal, final double orderEntryTotalWithDiscount,
			final CartModel cartModel)
	{
		final double voucherDiscountAmount = getVoucherTotalForFoodCoupon(cartModel);
		if (null != Double.valueOf(voucherDiscountAmount))
		{
			if (voucherDiscountAmount < foodCouponTotal)
			{
				foodCouponTotal = foodCouponTotal - ((foodCouponTotal / orderEntryTotalWithDiscount) * voucherDiscountAmount);
			}
			else if (voucherDiscountAmount > foodCouponTotal)
			{
				foodCouponTotal = 0D;
			}
		}
		return foodCouponTotal;
	}

	/**
	 * This method is used to get the food coupon total amount to be paid by the customer
	 *
	 * @param foodCouponTotal
	 * @param orderEntryTotalWithDiscount
	 * @param cartModel
	 * @return double
	 */
	private double getFoodCouponTotalAfterOrderLevelDiscount(double foodCouponTotal, final double orderEntryTotalWithDiscount,
			final CartModel cartModel)
	{
		if (0 != orderEntryTotalWithDiscount)
		{
			final PromotionOrderResults promotionOrderResults = getPromotionService().getPromotionResults(cartModel);
			final double orderLevelDiscount = getOrderDiscountsAmount(promotionOrderResults);
			foodCouponTotal = foodCouponTotal - ((foodCouponTotal / orderEntryTotalWithDiscount) * orderLevelDiscount);
		}
		return foodCouponTotal;
	}

	/**
	 * This method is used to get the order level discounts from cart.
	 *
	 * @param promotionOrderResults
	 * @return double
	 */
	private double getOrderDiscountsAmount(final PromotionOrderResults promotionOrderResults)
	{
		double orderLevelDiscount = 0D;
		final List<PromotionResult> promoResultList = promotionOrderResults.getAppliedOrderPromotions();
		for (final PromotionResult promoResult : promoResultList)
		{
			orderLevelDiscount += promoResult.getTotalDiscount();
		}

		return orderLevelDiscount;
	}

	/**
	 * This method is used to get the promotion qualified order entry level Combi-offer discount.
	 *
	 * @param entryNumber
	 * @param promotionOrderResults
	 * @return double
	 */
	@Override
	public double getOrderEntryDiscountTotalForCombi(final Integer entryNumber, final PromotionOrderResults promotionOrderResults)
	{
		final List<PromotionResult> promoProductResultList = promotionOrderResults.getAppliedProductPromotions();
		double orderEntryDiscountTotal = 0D;
		for (final PromotionResult promoResult : promoProductResultList)
		{
			if (isCombiOffer(promoResult))
			{
				final Iterator<PromotionOrderEntryConsumed> iterator = promoResult.getConsumedEntries().iterator();
				while (iterator.hasNext())
				{
					final PromotionOrderEntryConsumed consumedEntry = iterator.next();
					if (entryNumber.equals(consumedEntry.getOrderEntry().getEntryNumber()))
					{
						orderEntryDiscountTotal += consumedEntry.getAdjustedEntryPrice();
						break;
					}
				}
			}
		}
		return orderEntryDiscountTotal;
	}

	/**
	 * This method is used to check if combi-offer is applied to an order or not
	 *
	 * @param promoResult
	 */
	private boolean isCombiOffer(final PromotionResult promoResult)
	{
		for (final String promotionType : getSparCombiOfferPromotionStrategy().getCombiPromotionType())
		{
			if (null != promoResult.getPromotion().getPromotionType() && promoResult.getPromotion().getPromotionType().equals(promotionType))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to check if product in order entry is combi-offer enabled
	 *
	 * @param promoResult
	 */
	private boolean isProductCombiOffer(final PromotionResult promoResult)
	{
		if (null != promoResult.getConsumedEntries())
		{
			final Iterator<PromotionOrderEntryConsumed> iterator = promoResult.getConsumedEntries().iterator();
			while (iterator.hasNext())
			{
				final PromotionOrderEntryConsumed orderEntryConsumed = iterator.next();
				try
				{
					@SuppressWarnings("deprecation")
					final Product product = orderEntryConsumed.getProduct();
					final ProductModel productModel = getModelService().get(product);
					final List<PriceInformation> prices = this.priceService.getPriceInformationsForProduct(productModel);
					if (CollectionUtils.isNotEmpty(prices))
					{
						for (final PriceInformation price : prices)
						{
							if (null != (price).getQualifiers() && null != (price).getQualifiers().get("combiOffer")
									&& ((Boolean) (price).getQualifiers().get("combiOffer")).booleanValue())
							{
								return true;
							}
						}
					}
				}
				catch (final Exception e)
				{
					return false;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)check cart on the order level thresholds and return status code
	 * 
	 * @see com.spar.hcl.core.service.cart.SparCartService#isCartQualifyForOrderLimits(java.lang.Double,
	 * java.lang.String)
	 */
	@Override
	public List<String> isCartQualifyForOrderLimits(final Double orderValue, final DeliveryModeModel deliveryMode)
	{
		final List<String> statusCodes = new ArrayList<String>();
		// validating cart on the order threshold basis.

		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (baseStore != null)
		{
			final Double minOrderLimit = baseStore.getMinOrderLimit();
			final Double minCCOrderLimit = baseStore.getMinCCOrderLimit();
			final Double minHDOrderLimit = baseStore.getMinHDOrderLimit();
			if (deliveryMode == null)
			{
				if (orderValue < minOrderLimit)
				{
					statusCodes.add("Min_Ord");
					return statusCodes;
				}
			}
			if (deliveryMode != null)
			{
				if (orderValue < minCCOrderLimit && "pickup".equalsIgnoreCase(deliveryMode.getCode()))
				{
					statusCodes.add("Min_Ord_CNC");
				}
				else if (orderValue < minHDOrderLimit && !"pickup".endsWith(deliveryMode.getCode()))
				{
					statusCodes.add("Min_Ord_HD");
				}
			}
		}
		return statusCodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.spar.hcl.core.service.cart.SparCartService#setDeliveryMode(de.hybris.platform.commerceservices.service.data
	 * .CommerceCheckoutParameter)
	 */
	@Override
	public boolean setDeliveryMode(final CommerceCheckoutParameter parameter)
	{
		return getCommerceDeliveryModeStrategy().setDeliveryMode(parameter);
	}

	public List<CategoryModel> getCategoryChain(final String productCode) throws IllegalArgumentException
	{
		final List<CategoryModel> categoryModels1 = new ArrayList<>();
		final Collection<CategoryModel> categoryModels = new ArrayList<>();
		ProductModel baseProductModel = null;
		try
		{
			baseProductModel = getBaseProduct(getProductService().getProductForCode(productCode));
			categoryModels.addAll(baseProductModel.getSupercategories());
		}
		catch (final Exception e)
		{
			// No product found
		}

		if (null != baseProductModel)
		{
			while (!categoryModels.isEmpty())
			{
				CategoryModel toDisplay = null;
				for (final CategoryModel categoryModel : categoryModels)
				{
					if (!(categoryModel instanceof ClassificationClassModel))
					{
						if (toDisplay == null)
						{
							toDisplay = categoryModel;
						}
					}
				}
				categoryModels.clear();
				if (toDisplay != null)
				{
					categoryModels1.add(toDisplay);
					categoryModels.addAll(toDisplay.getSupercategories());
				}
			}
			Collections.reverse(categoryModels1);
		}
		return categoryModels1;
	}

	/* Code change start for voucher story Sumit */
	@SuppressWarnings("boxing")
	public double getVoucherTotalForFoodCoupon(final CartModel cartModel)
	{
		final Collection<String> voucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);
		String voucherVal = null;
		if (null != voucherCodes && CollectionUtils.isNotEmpty(voucherCodes))
		{
			for (final String voucher : voucherCodes)
			{
				voucherVal = voucher;
				break;
			}


			final VoucherModel voucherModel = getVoucherService().getVoucher(voucherVal);
			if (null != voucherModel)
			{
				//return voucherModel.getValue().doubleValue();
				final VoucherValue foodCouponVoucherDiscount = voucherModelService.getAppliedValue(voucherModel, cartModel);
				return foodCouponVoucherDiscount.getValue();
			}
		}
		return 0.0d;
	}

	/* Change end here */

	/**
	 * Getter
	 *
	 * @return the promotionService
	 */
	public PromotionsService getPromotionService()
	{
		return promotionService;
	}

	/**
	 * Setter
	 *
	 * @param promotionService
	 *           the promotionService to set
	 */
	public void setPromotionService(final PromotionsService promotionService)
	{
		this.promotionService = promotionService;
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
	 * Getter
	 *
	 * @return the priceService
	 */
	public PriceService getPriceService()
	{
		return priceService;
	}

	/**
	 * Setter
	 *
	 * @param priceService
	 *           the priceService to set
	 */
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
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

	@Override
	public CategoryModel getSecondLevelCategoryOnProduct(final CatalogVersionModel catalogVersionModel, final String productCode)
	{
		final Collection<CategoryModel> categoryModels = new ArrayList<>();

		try
		{
			final ProductModel product = getProductService().getProductForCode(catalogVersionModel, productCode);

			final ProductModel baseProductModel = getBaseProduct(product);
			categoryModels.addAll(baseProductModel.getSupercategories());
		}
		catch (final Exception e)
		{
			// no product found
		}


		return getSecondLevelCategory(categoryModels);
	}

	@Override
	public CategoryModel getSecondLevelCategory(final Collection<CategoryModel> categoryModels)
	{
		//CategoryModel secondLevelCategory = new CategoryModel();
		CategoryModel secondLevelCategory = modelService.create(CategoryModel.class);
		final List<CategoryModel> categoryModels1 = new ArrayList<>();

		while (!categoryModels.isEmpty())
		{
			CategoryModel toDisplay = null;
			for (final CategoryModel categoryModel : categoryModels)
			{
				if (!(categoryModel instanceof ClassificationClassModel))
				{
					if (toDisplay == null)
					{
						toDisplay = categoryModel;
					}
				}
			}
			categoryModels.clear();
			if (toDisplay != null)
			{
				categoryModels1.add(toDisplay);
				categoryModels.addAll(toDisplay.getSupercategories());
			}
		}
		Collections.reverse(categoryModels1);
		if (categoryModels1.size() > 2)
		{
			secondLevelCategory = categoryModels1.get(1);
		}
		return secondLevelCategory;
	}

}
