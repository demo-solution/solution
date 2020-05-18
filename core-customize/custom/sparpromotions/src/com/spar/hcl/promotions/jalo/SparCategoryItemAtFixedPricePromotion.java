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
import de.hybris.platform.promotions.jalo.PromotionPriceRow;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderEntry;
import de.hybris.platform.promotions.result.PromotionOrderView;
import de.hybris.platform.promotions.util.Helper;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.spar.hcl.jalo.promotions.SparCategoryItemPromotionRestriction;


public class SparCategoryItemAtFixedPricePromotion extends GeneratedSparCategoryItemAtFixedPricePromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparCategoryItemAtFixedPricePromotion.class.getName());

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
		final Collection<PromotionPriceRow> promotionPriceRows = getProductFixedUnitPrice(ctx);
		final AbstractOrder order = promoContext.getOrder();
		final boolean hasValidPromotionPriceRow = hasPromotionPriceRowForCurrency(order, promotionPriceRows);
		if ((hasValidPromotionPriceRow) && (rsr.isAllowedToContinue()) && (!rsr.getAllowedProducts().isEmpty()))
		{
			final PromotionOrderView view = promoContext.createView(ctx, this, rsr.getAllowedProducts());
			while (view.getTotalQuantity(ctx) > 0L)
			{
				promoContext.startLoggingConsumed(this);

				final PromotionOrderEntry entry = view.peek(ctx);
				final long quantityToDiscount = entry.getQuantity(ctx);
				final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();

				final Double fixedUnitPrice = getPriceForOrder(ctx, promotionPriceRows, order, "productFixedUnitPrice");
				if (fixedUnitPrice != null)
				{
					for (final PromotionOrderEntryConsumed poec : view.consume(ctx, quantityToDiscount))
					{
						poec.setAdjustedUnitPrice(ctx, fixedUnitPrice);
					}
					final double adjustment = quantityToDiscount
							* (fixedUnitPrice.doubleValue() - entry.getBasePrice(ctx).doubleValue());

					final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
							promoContext.getOrder(), 1.0F);

					result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));
					final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
							.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry, adjustment);
					result.addAction(ctx, poeac);

					results.add(result);
				}
				else
				{
					promoContext.abandonLogging(this);
				}
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


	private boolean hasPromotionPriceRowForCurrency(final AbstractOrder order,
			final Collection<PromotionPriceRow> promotionPriceRows)
	{
		final String name = getComposedType().getName() + " (" + getCode() + ": " + getTitle() + ")";
		if (promotionPriceRows.isEmpty())
		{
			LOG.warn(name + " has no PromotionPriceRow. Skipping evaluation");
			return false;
		}
		final Currency currency = order.getCurrency();
		for (final PromotionPriceRow ppr : promotionPriceRows)
		{
			if (currency.equals(ppr.getCurrency()))
			{
				return true;
			}
		}
		LOG.warn(name + " has no PromotionPriceRow for currency " + currency.getName() + ". Skipping evaluation");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.promotions.jalo.ProductFixedPricePromotion#evaluate(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.promotions.result.PromotionEvaluationContext)
	 */

	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);

		if (order != null)
		{
			if (promotionResult.getFired(ctx))
			{
				final Double fixedUnitPrice = getPriceForOrder(ctx, getProductFixedUnitPrice(ctx), order, "productFixedUnitPrice");
				final Double thresholdTotals = getPriceForOrder(ctx, getThresholdTotals(ctx), order, "thresholdTotals");
				if (fixedUnitPrice != null && thresholdTotals != null)
				{
					final Currency orderCurrency = order.getCurrency(ctx);
					final Object[] args =
					{ thresholdTotals, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, thresholdTotals.doubleValue()),
							fixedUnitPrice, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, fixedUnitPrice.doubleValue()),
							getProduct(), getCategoryNames() };
					return formatMessage(getMessageFired(ctx), args, locale);
				}
			}
			if (promotionResult.getCouldFire(ctx))
			{
				final Double categoryThreshold = getCategoryThresholdTotals(order);
				Double diffValue = null;
				final Double fixedUnitPrice = getPriceForOrder(ctx, getProductFixedUnitPrice(ctx), order, "productFixedUnitPrice");
				final Double thresholdTotals = getPriceForOrder(ctx, getThresholdTotals(ctx), order, "thresholdTotals");
				if (thresholdTotals.doubleValue() > categoryThreshold.doubleValue())
				{
					diffValue = Double.valueOf(thresholdTotals.doubleValue() - categoryThreshold.doubleValue());
					if (fixedUnitPrice != null)
					{
						final Currency orderCurrency = order.getCurrency(ctx);
						final Object[] args =
						{ thresholdTotals, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, thresholdTotals.doubleValue()),
								fixedUnitPrice, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, fixedUnitPrice.doubleValue()),
								getProduct(), getCategoryNames(), diffValue,
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
