package com.spar.hcl.jalo.promotions;

import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.jalo.AbstractPromotionRestriction;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.util.Helper;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


public class SparOrderThresholdPercentDiscountPromotion extends GeneratedSparOrderThresholdPercentDiscountPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparOrderThresholdPercentDiscountPromotion.class.getName());

	private final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

	private final CommerceCategoryService commerceCategoryService = (CommerceCategoryService) Registry.getApplicationContext()
			.getBean("commerceCategoryService");

	private double orderSubtotalAfterDiscounts;

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
	 * @see de.hybris.platform.promotions.jalo.AbstractPromotion#evaluate(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.promotions.result.PromotionEvaluationContext)
	 */
	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		final List<PromotionResult> promotionResults = new ArrayList();
		if (checkRestrictions(ctx, promoContext))
		{
			final Double threshold = getPriceForOrder(ctx, getThresholdTotals(ctx), promoContext.getOrder(), "thresholdTotals");
			if (threshold != null)
			{
				final Double percentageDiscountValue = getDiscountPercentage(ctx);

				if (percentageDiscountValue != null)
				{
					final AbstractOrder order = promoContext.getOrder();
					final Double categoryThresholdTotals = getCategoryThresholdTotals(order);
					if (categoryThresholdTotals.doubleValue() >= threshold.doubleValue())
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + ">" + threshold
									+ ".  Creating a discount action for value:" + percentageDiscountValue + ".");
						}
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 1.0F);
						final double realDiscountPriceValue = categoryThresholdTotals.doubleValue()
								* (percentageDiscountValue.doubleValue() / 100);

						result.addAction(ctx,
								PromotionsManager.getInstance().createPromotionOrderAdjustTotalAction(ctx, -realDiscountPriceValue));

						promotionResults.add(result);
					}
					else
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + "<" + threshold
									+ ".  Skipping discount action.");
						}
						//final float certainty = (float) (orderSubtotalAfterDiscounts / threshold.doubleValue());
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 0.1f);
						promotionResults.add(result);
					}
				}
			}
		}
		return promotionResults;
	}

	private Double getDiscountPercentage(final SessionContext ctx)
	{
		final Double percentageDiscount = (Double) getProperty(ctx, "percentageDiscount");
		return percentageDiscount;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.promotions.jalo.AbstractPromotion#getResultDescription(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.promotions.jalo.PromotionResult, java.util.Locale)
	 */
	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult result, final Locale locale)
	{
		final AbstractOrder order = result.getOrder(ctx);
		if (order != null)
		{
			final Currency orderCurrency = order.getCurrency(ctx);
			final Double threshold = getPriceForOrder(ctx, getThresholdTotals(ctx), order, "thresholdTotals");
			if (threshold != null)
			{
				final Double discountPricePercentageValue = getDiscountPercentage(ctx);

				final Double discountPriceValue = Double.valueOf(orderSubtotalAfterDiscounts
						* (discountPricePercentageValue.doubleValue() / 100));

				if (discountPriceValue != null)
				{
					if (result.getFired(ctx))
					{
						final Object[] args =
						{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
								getCategoryNames(), discountPricePercentageValue };
						return formatMessage(getMessageFired(ctx), args, locale);
					}
					if (result.getCouldFire(ctx))
					{
						final Double categoryThreshold = getCategoryThresholdTotals(order);
						final double amountRequired = categoryThreshold.doubleValue() - threshold.doubleValue();
						if (threshold.doubleValue() > categoryThreshold.doubleValue())
						{
							final Object[] args =
							{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
									getCategoryNames(), discountPricePercentageValue, Double.valueOf(amountRequired),
									Helper.formatCurrencyAmount(ctx, locale, orderCurrency, amountRequired) };
							return formatMessage(getMessageCouldHaveFired(ctx), args, locale);
						}
					}
				}
			}
		}
		return "";
	}

	private Double getCategoryThresholdTotals(final AbstractOrder order)
	{
		if (null != this.getRestrictions())
		{
			for (final AbstractPromotionRestriction promotionRestriction : this.getRestrictions())
			{
				if (promotionRestriction instanceof SparPromotionCategoryRestriction)
				{
					double totalCategoryItemPrice = 0.0;
					final Collection<Category> categories = ((SparPromotionCategoryRestriction) promotionRestriction).getCategories();
					for (final AbstractOrderEntry orderEntry : order.getEntries())
					{
						final List<String> parentCategoryIdsHierarchy = getParentCategoryIdsHierarchy(orderEntry);
						for (final Category category : categories)
						{
							final CategoryModel restrictedCategoryModel = modelService.get(category);
							if (parentCategoryIdsHierarchy.contains(restrictedCategoryModel.getCode()))
							{
								final AbstractOrderEntryModel entryModel = modelService.get(orderEntry);
								totalCategoryItemPrice = totalCategoryItemPrice + entryModel.getTotalPrice().doubleValue();
							}
						}
					}
					return Double.valueOf(totalCategoryItemPrice);
				}
			}
		}
		return null;
	}

	private List<String> getParentCategoryIdsHierarchy(final AbstractOrderEntry orderEntry)
	{
		final List<String> parentCategoryIdsHierarchy = new ArrayList<String>();
		final AbstractOrderEntryModel entryModel = modelService.get(orderEntry);
		final CommerceCategoryService commerceCategoryService = (CommerceCategoryService) Registry.getApplicationContext().getBean(
				"commerceCategoryService");

		for (final CategoryModel categoryModel : entryModel.getProduct().getSupercategories())
		{
			final Collection<List<CategoryModel>> lists = commerceCategoryService.getPathsForCategory(categoryModel);
			for (final List<CategoryModel> lstCategoryModels : lists)
			{
				for (final CategoryModel model : lstCategoryModels)
				{
					parentCategoryIdsHierarchy.add(model.getCode());
				}
			}
		}
		return parentCategoryIdsHierarchy;
	}

	private String getCategoryNames()
	{
		String categories = "  ";
		if (null != this.getRestrictions())
		{
			for (final AbstractPromotionRestriction promotionRestriction : this.getRestrictions())
			{
				if (promotionRestriction instanceof SparPromotionCategoryRestriction)
				{
					final Collection<Category> colCat = ((SparPromotionCategoryRestriction) promotionRestriction).getCategories();
					if (null != colCat && colCat.size() > 0)
					{
						for (final Category category : colCat)
						{
							categories = categories + category.getName();
						}
					}
				}
			}
		}
		return categories;
	}

	@Override
	protected boolean checkRestrictions(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		if (!promoContext.getObserveRestrictions())
		{
			return true;
		}
		final AbstractOrder order = promoContext.getOrder();
		final List<Product> products = new ArrayList();
		for (final Object entry : order.getAllEntries())
		{
			final AbstractOrderEntry _entry = (AbstractOrderEntry) entry;
			products.add(_entry.getProduct());
		}
		final PromotionsManager.RestrictionSetResult evaluateRestrictions = PromotionsManager.getInstance().evaluateRestrictions(
				ctx, products, order, this, promoContext.getDate());
		return evaluateRestrictions.isAllowedToContinue();
	}

}
