/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.jalo;

import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.promotions.jalo.AbstractPromotion;
import de.hybris.platform.promotions.jalo.OrderPromotion;
import de.hybris.platform.promotions.jalo.ProductPromotion;
import de.hybris.platform.promotions.jalo.PromotionGroup;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.util.Helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparpromotionsConstants;



/**
 * This is the extension manager of the Sparpromotions extension.
 */
@SuppressWarnings("deprecation")
public class SparpromotionsManager extends GeneratedSparpromotionsManager
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(SparpromotionsManager.class.getName());

	/*
	 * Some important tips for development:
	 * 
	 * Do NEVER use the default constructor of manager's or items. => If you want to do something whenever the manger is
	 * created use the init() or destroy() methods described below
	 * 
	 * Do NEVER use STATIC fields in your manager or items! => If you want to cache anything in a "static" way, use an
	 * instance variable in your manager, the manager is created only once in the lifetime of a "deployment" or tenant.
	 */


	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SparpromotionsManager getInstance()
	{
		return (SparpromotionsManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SparpromotionsConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public SparpromotionsManager() // NOPMD
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of SparpromotionsManager called.");
		}
	}

	/**
	 * Use this method to do some basic work only ONCE in the lifetime of a tenant resp. "deployment". This method is
	 * called after manager creation (for example within startup of a tenant). Note that if you have more than one tenant
	 * you have a manager instance for each tenant.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void init()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("init() of SparpromotionsManager called. " + getTenant().getTenantID());
		}
	}

	/**
	 * Use this method as a callback when the manager instance is being destroyed (this happens before system
	 * initialization, at redeployment or if you shutdown your VM). Note that if you have more than one tenant you have a
	 * manager instance for each tenant.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void destroy()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("destroy() of SparpromotionsManager called, current tenant: " + getTenant().getTenantID());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List getProductPromotions(final SessionContext ctx, final Collection promotionGroups, final Product product,
			final boolean evaluateRestrictions, Date date)
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("getProductPromotions for [" + product + "] promotionGroups=[" + Helper.join(promotionGroups)
						+ "] evaluateRestrictions=[" + evaluateRestrictions + "] date=[" + date + "]");
			}
			if (promotionGroups == null || product == null || promotionGroups.isEmpty())
			{
				return Collections.EMPTY_LIST;
			}
			if (date == null)
			{
				date = Helper.getDateNowRoundedToMinute();
			}
			final Map args = new HashMap();
			args.put("promotionGroups", promotionGroups);
			args.put("product", product);
			args.put("now", date);
			args.put("true", Boolean.TRUE);
			final String query = buildQueryForDistinctProductPromotionQuery(product, ctx, args);
			@SuppressWarnings("deprecation")
			final List allPromotions = getSession().getFlexibleSearch()
					.search(ctx, query, args, Collections.singletonList(ProductPromotion.class), true, false, 0, -1).getResult();
			List availablePromotions = null;
			if (evaluateRestrictions)
			{
				availablePromotions = filterPromotionsByRestrictions(ctx, allPromotions, product, date);
			}
			else
			{
				availablePromotions = new ArrayList(allPromotions);
			}
			if (LOG.isDebugEnabled())
			{
				ProductPromotion promotion;
				for (final Iterator iterator = availablePromotions.iterator(); iterator.hasNext(); LOG.debug((new StringBuilder(
						"getProductPromotions for [")).append(product).append("] available promotion [").append(promotion).append("]")
						.toString()))
				{
					promotion = (ProductPromotion) iterator.next();
				}

			}
			return availablePromotions;
		}
		catch (final Exception ex)
		{
			LOG.error("Failed to getProductPromotions", ex);
		}
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("deprecation")
	public String buildQueryForDistinctProductPromotionQuery(final Product product, final SessionContext ctx, final Map args)
	{
		final StringBuilder promQuery = new StringBuilder("SELECT DISTINCT pprom.pk, pprom.prio FROM (");
		promQuery.append((new StringBuilder(" {{ SELECT {p.")).append(ProductPromotion.PK).append("} as pk, ").toString());
		promQuery.append(" {p.priority} as prio FROM");
		promQuery.append((new StringBuilder(" {"))
				.append(TypeManager.getInstance().getComposedType(ProductPromotion.class).getCode()).append(" AS p").toString());
		promQuery.append((new StringBuilder(" JOIN "))
				.append(de.hybris.platform.promotions.constants.GeneratedPromotionsConstants.Relations.PRODUCTPROMOTIONRELATION)
				.append(" AS p2p ").toString());
		promQuery.append((new StringBuilder(" ON {p.")).append(ProductPromotion.PK).append("} = {p2p.").append("target")
				.append("} ").toString());
		promQuery.append(" AND {p2p.source} = ?product } ");
		promQuery.append(" WHERE {p.PromotionGroup} IN (?promotionGroups) AND");
		promQuery.append(" {p.enabled} =?true AND ");
		promQuery.append(" {p.startDate} <= ?now AND ");
		promQuery.append(" ?now <= {p.endDate} }}");
		final Collection productCategories = CategoryManager.getInstance().getCategoriesByProduct(product, ctx);
		if (!productCategories.isEmpty())
		{
			promQuery.append(" UNION ");
			promQuery.append((new StringBuilder(" {{ SELECT {p.")).append(ProductPromotion.PK).append("} as pk, ").toString());
			promQuery.append(" {p.priority} as prio FROM");
			promQuery.append((new StringBuilder(" {"))
					.append(TypeManager.getInstance().getComposedType(ProductPromotion.class).getCode()).append(" AS p").toString());
			promQuery.append((new StringBuilder(" JOIN "))
					.append(de.hybris.platform.promotions.constants.GeneratedPromotionsConstants.Relations.CATEGORYPROMOTIONRELATION)
					.append(" AS c2p ").toString());
			promQuery.append((new StringBuilder(" ON {p.")).append(ProductPromotion.PK).append("} = {c2p.").append("target")
					.append("} ").toString());
			promQuery.append(" AND {c2p.source} IN (?productCategories) } ");
			promQuery.append(" WHERE {p.PromotionGroup} IN (?promotionGroups) AND");
			promQuery.append(" {p.enabled} =?true AND ");
			promQuery.append(" {p.startDate} <= ?now AND ");
			promQuery.append(" ?now <= {p.endDate} }}");
			final Set productSuperCategories = new HashSet();
			Category cat;
			for (final Iterator iterator = productCategories.iterator(); iterator.hasNext(); productSuperCategories.addAll(cat
					.getAllSupercategories(ctx)))
			{
				cat = (Category) iterator.next();
				productSuperCategories.add(cat);
			}

			args.put("productCategories", productSuperCategories);
		}
		promQuery.append(" )pprom ORDER BY pprom.prio DESC");
		return promQuery.toString();
	}

	@Override
	public List<OrderPromotion> getOrderPromotions(final SessionContext ctx, final Collection<PromotionGroup> promotionGroups,
			final boolean evaluateRestrictions, final Product product, Date date)
	{
		List<OrderPromotion> availablePromotions;
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("getOrderPromotions promotionGroups=[" + Helper.join(promotionGroups) + "] evaluateRestrictions=["
						+ evaluateRestrictions + "] product=[" + product + "] date=[" + date + "]");
			}

			if ((promotionGroups == null) || (promotionGroups.isEmpty()))
			{
				return new ArrayList(0);
			}

			if (date == null)
			{
				date = Helper.getDateNowRoundedToMinute();
			}

			final String query = "SELECT DISTINCT {promo:" + Item.PK + "}, {promo:" + "priority" + "} " + "FROM {"
					+ TypeManager.getInstance().getComposedType(OrderPromotion.class).getCode() + " as promo } " + "WHERE " + "( "
					+ "  {promo:" + "PromotionGroup" + "} IN (?promotionGroups) " + ") " + "AND " + "( " + "  {promo:" + "enabled"
					+ "}=1 AND {promo:" + "startDate" + "} <= ?now AND ?now <= {promo:" + "endDate" + "} " + ") " + "ORDER BY {promo:"
					+ "priority" + "} DESC";

			final HashMap args = new HashMap();
			args.put("promotionGroups", promotionGroups);
			args.put("now", date);

			final List allPromotions = getSession().getFlexibleSearch()
					.search(ctx, query, args, Collections.singletonList(OrderPromotion.class), true, false, 0, -1).getResult();

			availablePromotions = null;
			if (evaluateRestrictions)
			{
				availablePromotions = filterPromotionsByRestrictions(ctx, allPromotions, product, date);
			}
			else
			{
				availablePromotions = new ArrayList(allPromotions);
			}

			if (LOG.isDebugEnabled())
			{
				for (final OrderPromotion promotion : availablePromotions)
				{
					LOG.debug("getOrderPromotions available promotion [" + promotion + "]");
				}
			}

			return availablePromotions;
		}
		catch (final Exception ex)
		{
			LOG.error("Failed to getOrderPromotions", ex);
		}
		return new ArrayList(0);
	}


	public static List findOrderAndProductPromotionsSortByPriority(final SessionContext ctx, final JaloSession jaloSession,
			final Collection promotionGroups, final Collection products, final Date date)
	{
		if (promotionGroups == null || promotionGroups.isEmpty())
		{
			return Collections.EMPTY_LIST;
		}
		final StringBuilder promQuery = new StringBuilder("SELECT DISTINCT pprom.pk, pprom.prio FROM (");
		final HashMap args = new HashMap();
		if (products != null && !products.isEmpty())
		{
			promQuery.append(" {{ SELECT {p.").append(ProductPromotion.PK).append("} as pk, ");
			promQuery.append(" {p.").append("priority").append("} as prio FROM");
			promQuery.append(" {").append(TypeManager.getInstance().getComposedType(ProductPromotion.class).getCode())
					.append(" AS p");
			promQuery.append(" JOIN ")
					.append(de.hybris.platform.promotions.constants.GeneratedPromotionsConstants.Relations.PRODUCTPROMOTIONRELATION)
					.append(" AS p2p ");
			promQuery.append(" ON {p.").append(ProductPromotion.PK).append("} = {p2p.").append("target").append("} ");
			promQuery.append(" AND {p2p.").append("source").append("} in (?products) } ");
			promQuery.append(" WHERE {p.").append("PromotionGroup").append("} IN (?promotionGroups) AND");
			promQuery.append(" {p.").append("enabled").append("} =?true AND ");
			promQuery.append(" {p.").append("startDate").append("} <= ?now AND ");
			promQuery.append(" ?now <= {p.").append("endDate").append("} }}");
			args.put("products", products);
			final Set productCategories = new HashSet();
			for (final Iterator iterator = products.iterator(); iterator.hasNext();)
			{
				final Product product = (Product) iterator.next();
				Category cat;
				for (final Iterator iterator1 = CategoryManager.getInstance().getCategoriesByProduct(product, ctx).iterator(); iterator1
						.hasNext(); productCategories.addAll(cat.getAllSupercategories(ctx)))
				{
					cat = (Category) iterator1.next();
					productCategories.add(cat);
				}

			}

			if (!productCategories.isEmpty())
			{
				promQuery.append(" UNION ");
				promQuery.append(" {{ SELECT {p.").append(ProductPromotion.PK).append("} as pk, ");
				promQuery.append(" {p.").append("priority").append("} as prio FROM");
				promQuery.append(" {").append(TypeManager.getInstance().getComposedType(ProductPromotion.class).getCode())
						.append(" AS p");
				promQuery
						.append(" JOIN ")
						.append(
								de.hybris.platform.promotions.constants.GeneratedPromotionsConstants.Relations.CATEGORYPROMOTIONRELATION)
						.append(" AS c2p ");
				promQuery.append(" ON {p.").append(ProductPromotion.PK).append("} = {c2p.").append("target").append("} ");
				promQuery.append(" AND {c2p.").append("source").append("} IN (?productCategories) } ");
				promQuery.append(" WHERE {p.").append("PromotionGroup").append("} IN (?promotionGroups) AND");
				promQuery.append(" {p.").append("enabled").append("} =?true AND ");
				promQuery.append(" {p.").append("startDate").append("} <= ?now AND ");
				promQuery.append(" ?now <= {p.").append("endDate").append("} }}");
				args.put("productCategories", productCategories);
			}
			promQuery.append(" UNION ALL ");
		}
		promQuery.append("{{ SELECT {p3:").append(OrderPromotion.PK).append("}, {p3.").append("priority").append("} as prio ");
		promQuery.append(" FROM {").append(TypeManager.getInstance().getComposedType(OrderPromotion.class).getCode())
				.append(" as p3} ");
		promQuery.append(" WHERE {p3.").append("PromotionGroup").append("} IN (?promotionGroups) AND");
		promQuery.append(" {p3.").append("enabled").append("} =?true AND ");
		promQuery.append(" {p3.").append("startDate").append("} <= ?now AND ");
		promQuery.append(" ?now <= {p3.").append("endDate").append("}").append("        }} ");
		promQuery.append(" )pprom ORDER BY pprom.prio DESC");
		args.put("now", date);
		args.put("true", Boolean.TRUE);
		args.put("promotionGroups", promotionGroups);
		return jaloSession.getFlexibleSearch().search(ctx, promQuery.toString(), args, AbstractPromotion.class).getResult();
	}

	@Override
	public void cleanupOrphanedResults(final SessionContext ctx)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("cleanupOrphanedResults");
		}
		try
		{
			final String query = (new StringBuilder("SELECT {pr:")).append(Item.PK).append("} ").append("FROM   {")
					.append(TypeManager.getInstance().getComposedType(PromotionResult.class).getCode()).append(" as pr LEFT JOIN ")
					.append(TypeManager.getInstance().getComposedType(AbstractOrder.class).getCode()).append(" AS order ON {pr:")
					.append("order").append("}={order:").append(Item.PK).append("} } ").append("WHERE  {order:").append(Item.PK)
					.append("} IS NULL").toString();
			final List promotionResults = getSession().getFlexibleSearch()
					.search(ctx, query, Collections.EMPTY_MAP, PromotionResult.class).getResult();
			if (promotionResults != null)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug((new StringBuilder("cleanupOrphanedResults found [")).append(promotionResults.size())
							.append("] results to remove").toString());
				}
				for (final Iterator iterator = promotionResults.iterator(); iterator.hasNext();)
				{
					final PromotionResult result = (PromotionResult) iterator.next();
					try
					{
						result.remove(ctx);
					}
					catch (final ConsistencyCheckException ccEx)
					{
						LOG.error((new StringBuilder("In cleanupOrphanedResults failed to remove promotion result [")).append(result)
								.append("]").toString(), ccEx);
					}
				}

			}
		}
		catch (final Exception ex)
		{
			LOG.error("Failed to cleanupOrphanedResults", ex);
		}
	}

}
