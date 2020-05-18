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


public class SparOrderThresholdFixedDiscountPromotion extends GeneratedSparOrderThresholdFixedDiscountPromotion
{
	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(SparOrderThresholdFixedDiscountPromotion.class.getName());

	private final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");

	private final CommerceCategoryService commerceCategoryService = (CommerceCategoryService) Registry.getApplicationContext()
			.getBean("commerceCategoryService");

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

	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		final List<PromotionResult> promotionResults = new ArrayList<PromotionResult>();
		if (checkRestrictions(ctx, promoContext))
		{
			final Double threshold = getPriceForOrder(ctx, getThresholdTotals(ctx), promoContext.getOrder(), "thresholdTotals");
			if (threshold != null)
			{
				final Double discountPriceValue = getPriceForOrder(ctx, getDiscountPrices(ctx), promoContext.getOrder(),
						"discountPrices");
				if (discountPriceValue != null)
				{
					final AbstractOrder order = promoContext.getOrder();
					final Double orderSubtotalAfterDiscounts = getCategoryThresholdTotals(order);
					if (orderSubtotalAfterDiscounts.doubleValue() >= threshold.doubleValue())
					{
						if (log.isDebugEnabled())
						{
							log.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + ">" + threshold
									+ ".  Creating a discount action for value:" + discountPriceValue + ".");
						}
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 1.0F);

						final double realDiscountPriceValue = discountPriceValue.doubleValue();

						result.addAction(ctx,
								PromotionsManager.getInstance().createPromotionOrderAdjustTotalAction(ctx, -realDiscountPriceValue));

						promotionResults.add(result);
					}
					else
					{
						if (log.isDebugEnabled())
						{
							log.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + "<" + threshold
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
				final Double discountPriceValue = getPriceForOrder(ctx, getDiscountPrices(ctx), order, "discountPrices");
				if (discountPriceValue != null)
				{
					if (result.getFired(ctx))
					{
						final Object[] args =
						{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
								getCategoryNames(),
								Helper.formatCurrencyAmount(ctx, locale, orderCurrency, discountPriceValue.doubleValue()) };
						return formatMessage(getMessageFired(ctx), args, locale);
					}
					if (result.getCouldFire(ctx))
					{
						final Double categoryThreshold = getCategoryThresholdTotals(order);
						//final double orderSubtotalAfterDiscounts = getOrderSubtotalAfterDiscounts(ctx, order);
						final double amountRequired = categoryThreshold.doubleValue() - threshold.doubleValue();
						if (threshold.doubleValue() > categoryThreshold.doubleValue())
						{
							final Object[] args =
							{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
									getCategoryNames(),
									Helper.formatCurrencyAmount(ctx, locale, orderCurrency, discountPriceValue.doubleValue()),
									Double.valueOf(amountRequired),
									Helper.formatCurrencyAmount(ctx, locale, orderCurrency, amountRequired) };
							return formatMessage(getMessageCouldHaveFired(ctx), args, locale);
						}
					}
				}
			}
		}
		return "";
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

}
