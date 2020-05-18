package com.spar.hcl.promotions.jalo;

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
import de.hybris.platform.promotions.jalo.PromotionOrderEntryAdjustAction;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderEntry;
import de.hybris.platform.promotions.result.PromotionOrderView;
import de.hybris.platform.promotions.util.Helper;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.spar.hcl.jalo.promotions.SparCategoryItemPromotionRestriction;


public class SparCategoryItemAtFixedDiscPromotion extends GeneratedSparCategoryItemAtFixedDiscPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparCategoryItemAtFixedDiscPromotion.class.getName());

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
		final List<PromotionResult> results = new ArrayList();

		final PromotionsManager.RestrictionSetResult rsr = findEligibleProductsInBasket(ctx, promoContext);
		if ((rsr.isAllowedToContinue()) && (!rsr.getAllowedProducts().isEmpty()))
		{
			final PromotionOrderView view = promoContext.createView(ctx, this, rsr.getAllowedProducts());
			final PromotionsManager promotionsManager = PromotionsManager.getInstance();
			while (view.getTotalQuantity(ctx) > 0L)
			{
				promoContext.startLoggingConsumed(this);

				final PromotionOrderEntry entry = view.peek(ctx);
				final long quantityToDiscount = entry.getQuantity(ctx);
				final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();

				final double percentageDiscount = getPercentageDiscount(ctx).doubleValue() / 100.0D;

				final double originalUnitPrice = entry.getBasePrice(ctx).doubleValue();
				final double originalEntryPrice = quantityToDiscount * originalUnitPrice;

				final Currency currency = promoContext.getOrder().getCurrency(ctx);

				final BigDecimal adjustedEntryPrice = Helper.roundCurrencyValue(ctx, currency, originalEntryPrice
						- originalEntryPrice * percentageDiscount);

				final BigDecimal adjustedUnitPrice = Helper.roundCurrencyValue(
						ctx,
						currency,
						adjustedEntryPrice.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : adjustedEntryPrice.divide(
								BigDecimal.valueOf(quantityToDiscount), RoundingMode.HALF_EVEN));

				final BigDecimal fiddleAmount = adjustedEntryPrice.subtract(adjustedUnitPrice.multiply(BigDecimal
						.valueOf(quantityToDiscount)));
				if (fiddleAmount.compareTo(BigDecimal.ZERO) == 0)
				{
					for (final PromotionOrderEntryConsumed poec : view.consume(ctx, quantityToDiscount))
					{
						poec.setAdjustedUnitPrice(ctx, adjustedUnitPrice.doubleValue());
					}
				}
				else
				{
					for (final PromotionOrderEntryConsumed poec : view.consume(ctx, quantityToDiscount - 1L))
					{
						poec.setAdjustedUnitPrice(ctx, adjustedUnitPrice.doubleValue());
					}
					for (final PromotionOrderEntryConsumed poec : view.consume(ctx, 1L))
					{
						poec.setAdjustedUnitPrice(ctx, Helper.roundCurrencyValue(ctx, currency, adjustedUnitPrice.add(fiddleAmount))
								.doubleValue());
					}
				}
				final PromotionResult result = promotionsManager.createPromotionResult(ctx, this, promoContext.getOrder(), 1.0F);
				result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));
				final BigDecimal adjustment = Helper.roundCurrencyValue(ctx, currency,
						adjustedEntryPrice.subtract(BigDecimal.valueOf(originalEntryPrice)));
				final PromotionOrderEntryAdjustAction poeac = promotionsManager.createPromotionOrderEntryAdjustAction(ctx,
						entry.getBaseOrderEntry(), quantityOfOrderEntry, adjustment.doubleValue());
				result.addAction(ctx, poeac);

				results.add(result);
			}
			return results;
		}
		else
		{
			final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this, promoContext.getOrder(),
					0.5F);
			results.add(result);
		}
		return results;
	}




	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);

		if (order != null)
		{
			if (promotionResult.getFired(ctx))
			{
				final Double percentageDiscount = (Double) getProperty(ctx, "percentageDiscount");
				final Double thresholdTotals = getPriceForOrder(ctx, getThresholdTotals(ctx), order, "thresholdTotals");
				if (percentageDiscount != null && thresholdTotals != null)
				{
					final Currency orderCurrency = order.getCurrency(ctx);
					final Object[] args =
					{ thresholdTotals, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, thresholdTotals.doubleValue()),
							percentageDiscount, getProduct(), getCategoryNames() };
					return formatMessage(getMessageFired(ctx), args, locale);
				}
			}
			if (promotionResult.getCouldFire(ctx))
			{
				final Double categoryThreshold = getCategoryThresholdTotals(order);
				Double diffValue = null;
				final Double percentageDiscount = (Double) getProperty(ctx, "percentageDiscount");
				final Double thresholdTotals = getPriceForOrder(ctx, getThresholdTotals(ctx), order, "thresholdTotals");
				if (thresholdTotals.doubleValue() > categoryThreshold.doubleValue())
				{
					diffValue = Double.valueOf(thresholdTotals.doubleValue() - categoryThreshold.doubleValue());
					if (percentageDiscount != null)
					{
						final Currency orderCurrency = order.getCurrency(ctx);
						final Object[] args =
						{ thresholdTotals, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, thresholdTotals.doubleValue()),
								percentageDiscount, getProduct(), getCategoryNames(), diffValue,
								Helper.formatCurrencyAmount(ctx, locale, orderCurrency, diffValue.doubleValue()) };
						return formatMessage(getMessageCouldHaveFired(ctx), args, locale);
					}
				}
			}
		}
		return "";
	}


	private String getProduct()
	{
		if (null != this.getProducts() && this.getProducts().size() > 0)
		{
			final Collection<Product> products = this.getProducts();
			for (final Product product : products)
			{
				return product.getName() != null ? product.getName() : product.getCode();
			}
		}
		return null;
	}

	private String getCategoryNames()
	{
		String categories = "  ";
		if (null != this.getRestrictions())
		{
			for (final AbstractPromotionRestriction promotionRestriction : this.getRestrictions())
			{
				if (promotionRestriction instanceof SparCategoryItemPromotionRestriction)
				{
					final Collection<Category> colCat = ((SparCategoryItemPromotionRestriction) promotionRestriction).getCategories();
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

	private Double getCategoryThresholdTotals(final AbstractOrder order)
	{
		if (null != this.getRestrictions())
		{
			for (final AbstractPromotionRestriction promotionRestriction : this.getRestrictions())
			{
				if (promotionRestriction instanceof SparCategoryItemPromotionRestriction)
				{
					double totalCategoryItemPrice = 0.0;
					final Collection<Category> categories = ((SparCategoryItemPromotionRestriction) promotionRestriction)
							.getCategories();
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

}
