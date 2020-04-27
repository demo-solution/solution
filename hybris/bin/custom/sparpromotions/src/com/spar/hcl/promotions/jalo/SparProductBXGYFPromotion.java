package com.spar.hcl.promotions.jalo;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.promotions.jalo.AbstractPromotionAction;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


public class SparProductBXGYFPromotion extends GeneratedSparProductBXGYFPromotion
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparProductBXGYFPromotion.class.getName());

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
		final List<PromotionResult> results = new ArrayList<PromotionResult>();

		final PromotionsManager.RestrictionSetResult restrictResult = findEligibleProductsInBasket(ctx, promoContext);
		if ((restrictResult.isAllowedToContinue()) && (!restrictResult.getAllowedProducts().isEmpty()))
		{

			final long qualifyingCount = getQualifyingCount(ctx).longValue();
			final long freeCount = getFreeCount(ctx).longValue();
			final PromotionsManager promotionsManager = PromotionsManager.getInstance();

			final ArrayList freeList = (ArrayList) getProperty(ctx, "freeProducts");
			Product freeProduct = null;
			if (null != freeList && freeList.size() > 0)
			{
				freeProduct = (Product) freeList.get(0);
			}

			final PromotionOrderView orderView = promoContext.createView(ctx, this, restrictResult.getAllowedProducts());
			final List freeProEntries = promoContext.getOrder().getEntriesByProduct(freeProduct);
			while (orderView.getTotalQuantity(ctx) > 0 && freeCount <= orderView.getTotalQuantity(ctx))
			{
				long freeProductsCount = 0;
				AbstractOrderEntry orderEntry = null;
				freeProductsCount = freeCount * (orderView.getTotalQuantity(ctx) / qualifyingCount);

				if (null != freeProEntries && freeProEntries.size() > 0)
				{
					final List<AbstractOrderEntry> orderEntries = promoContext.getOrder().getEntries(ctx);
					for (final AbstractOrderEntry orderEntry2 : orderEntries)
					{
						if (orderEntry2.getProduct().getCode().equals(freeProduct.getCode()))
						{
							orderEntry = orderEntry2;
							break;
						}
					}
				}
				else
				{
					orderEntry = promoContext.getOrder().addNewEntry(freeProduct, freeProductsCount, freeProduct.getUnit(), false);
				}


				if (freeProductsCount == 0 && orderEntry.getBasePrice().doubleValue() == 0)
				{
					promoContext.getOrder().removeEntry(orderEntry);
				}

				if (freeProductsCount > 0)
				{
					orderEntry.setQuantity(freeProductsCount);
					orderEntry.setGiveAway(ctx, true);
					orderEntry.setBasePrice(0.0D);
					orderEntry.setTotalPrice(ctx, 0.0D);

					promoContext.startLoggingConsumed(this);
					final List<AbstractPromotionAction> actions = new ArrayList();
					actions.add(promotionsManager.createPromotionOrderEntryAdjustAction(ctx, orderEntry, 0.0D));

					final PromotionOrderEntryConsumed consumed = PromotionsManager.getInstance().createPromotionOrderEntryConsumed(
							ctx, freeProduct.getCode(), orderEntry, freeProductsCount);

					final PromotionResult pr = promotionsManager.createPromotionResult(ctx, this, promoContext.getOrder(), 1.0F);
					//consumed.setAdjustedUnitPrice(ctx, 0.0D);
					promoContext.finishLoggingAndGetConsumed(this, true);
					pr.addConsumedEntry(ctx, consumed);
					pr.setActions(ctx, actions);
					results.add(pr);
				}
				else
				{
					final PromotionResult pr = promotionsManager.createPromotionResult(ctx, this, promoContext.getOrder(), 0.1F);
					results.add(pr);
				}

				break;
			}
		}
		return results;
	}


	@SuppressWarnings("deprecation")
	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);
		final Collection<Product> products = getProducts();
		String qualifyingProduct = null;
		if (null != products && products.size() > 0)
		{
			for (final Product product : products)
			{
				qualifyingProduct = product.getName() != null ? product.getName() : product.getCode();
				break;
			}
		}

		final ArrayList freeList = (ArrayList) getProperty(ctx, "freeProducts");
		Product freeProduct = null;
		if (null != freeList && freeList.size() > 0)
		{
			freeProduct = (Product) freeList.get(0);
		}


		if (order != null)
		{
			final Integer qualifyingCount = getQualifyingCount(ctx);
			final Integer freeCount = getFreeCount(ctx);

			if (promotionResult.getFired(ctx))
			{
				final Object[] args =
				{ qualifyingCount, qualifyingProduct, freeCount,
						freeProduct.getName() != null ? freeProduct.getName() : freeProduct.getCode() };
				return formatMessage(getMessageFired(ctx), args, locale);
			}
			if (promotionResult.getCouldFire(ctx))
			{
				final Integer neededCount = Integer.valueOf(products.size() % getQualifyingCount(ctx).intValue());
				final Object[] args =
				{ neededCount, qualifyingProduct, freeCount,
						freeProduct.getName() != null ? freeProduct.getName() : freeProduct.getCode() };
				return formatMessage(getMessageCouldHaveFired(ctx), args, locale);
			}
		}
		return "";
	}
}
