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
package com.spar.hcl.sparpricefactory.jalo;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.europe1.channel.strategies.RetrieveChannelStrategy;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.europe1.jalo.PDTRowsQueryBuilder;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.util.DateRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.sparpricefactory.constants.SparEurope1Tools;
import com.spar.hcl.sparpricefactory.constants.SparpricefactoryConstants;
import com.spar.hcl.sparpricefactory.strategy.SparRetrieveWarehouseStrategy;



/**
 * This is the extension manager of the Sparpricefactory extension. This is used to get the store specific behaviour for
 * Price rows as per the requirement of SPAR
 *
 * @author rohan_c
 */
@SuppressWarnings("deprecation")
public class SparpricefactoryManager extends GeneratedSparpricefactoryManager
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(SparpricefactoryManager.class.getName());

	private SparRetrieveWarehouseStrategy retrieveWarehouseStrategy;
	private RetrieveChannelStrategy retrieveChannelStrategy;


	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SparpricefactoryManager getInstance()
	{
		return (SparpricefactoryManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SparpricefactoryConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public SparpricefactoryManager() // NOPMD
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of SparpricefactoryManager called.");
		}
	}

	/**
	 * Use this method to do some basic work only ONCE in the lifetime of a tenant resp. "deployment". This method is
	 * called after manager creation (for example within startup of a tenant). Note that if you have more than one tenant
	 * you have a manager instance for each tenant.
	 */
	@Override
	public void init()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("init() of SparpricefactoryManager called. " + getTenant().getTenantID());
		}
	}

	/**
	 * Use this method as a callback when the manager instance is being destroyed (this happens before system
	 * initialization, at redeployment or if you shutdown your VM). Note that if you have more than one tenant you have a
	 * manager instance for each tenant.
	 */
	@Override
	public void destroy()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("destroy() of SparpricefactoryManager called, current tenant: " + getTenant().getTenantID());
		}
	}

	/**
	 * This method is overriden from Europe1PriceFactory to query warehouse specific price rows for a product.
	 *
	 * @see de.hybris.platform.europe1.jalo.Europe1PriceFactory#getPriceInformations(de.hybris.platform.jalo.SessionContext,
	 *      de.hybris.platform.jalo.product.Product, de.hybris.platform.jalo.enumeration.EnumerationValue,
	 *      de.hybris.platform.jalo.user.User, de.hybris.platform.jalo.enumeration.EnumerationValue,
	 *      de.hybris.platform.jalo.c2l.Currency, boolean, java.util.Date, java.util.Collection)
	 */
	@Override
	protected List getPriceInformations(final SessionContext ctx, final Product product, final EnumerationValue productGroup,
			final User user, final EnumerationValue userGroup, final Currency curr, final boolean net, final Date date,
			final Collection taxValues) throws JaloPriceFactoryException
	{

		final Collection priceRows = matchPriceRowsForInfo(ctx, product, productGroup, user, userGroup, curr, date, net);
		final List priceInfos = new ArrayList(priceRows.size());
		Collection theTaxValues = taxValues;
		final List defaultPriceInfos = new ArrayList(priceRows.size());
		final PriceRowChannel channel = retrieveChannelStrategy.getChannel(ctx);
		for (final Iterator iterator = priceRows.iterator(); iterator.hasNext();)
		{
			final PriceRow row = (PriceRow) iterator.next();
			PriceInformation pInfo = SparEurope1Tools.createPriceInformation(row, curr);
			if (pInfo.getPriceValue().isNet() != net)
			{
				if (theTaxValues == null)
				{
					theTaxValues = Europe1Tools.getTaxValues(getTaxInformations(product, getPTG(ctx, product), user,
							getUTG(ctx, user), date));
				}
				pInfo = new PriceInformation(pInfo.getQualifiers(), pInfo.getPriceValue().getOtherPrice(theTaxValues));
			}
			if (row.getChannel() == null)
			{
				defaultPriceInfos.add(pInfo);
			}
			if (channel == null && row.getChannel() == null)
			{
				priceInfos.add(pInfo);
			}
			else if (channel != null && row.getChannel() != null && row.getChannel().getCode().equalsIgnoreCase(channel.getCode()))
			{
				priceInfos.add(pInfo);
			}
		}

		if (priceInfos.size() == 0)
		{
			return defaultPriceInfos;
		}
		else
		{
			return priceInfos;
		}

	}

	/**
	 * This method is overridden to add warehouse param to the query builder.
	 */
	@Override
	protected Collection<PriceRow> queryPriceRows4Price(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup, final User user, final EnumerationValue userGroup)
	{

		if (null == retrieveWarehouseStrategy)
		{
			return super.queryPriceRows4Price(ctx, product, productGroup, user, userGroup);
		}

		WarehouseModel warehouse = null;
		warehouse = retrieveWarehouseStrategy.getWarehouse(ctx);
		if (null == warehouse)
		{
			LOG.debug("Warehouse is null while determining price for the product : " + product.getCode());
			return super.queryPriceRows4Price(ctx, product, productGroup, user, userGroup);
		}
		else
		{
			LOG.debug("Warehouse PK is : " + warehouse.getPk() + "  used while determining price for the product : "
					+ product.getCode());
		}

		final PK warehousePk = warehouse.getPk();
		final PK productPk = product.getPK();
		final PK productGroupPk = (productGroup == null) ? null : productGroup.getPK();
		final PK userPk = (user == null) ? null : user.getPK();
		final PK userGroupPk = (userGroup == null) ? null : userGroup.getPK();
		final String productId = extractProductId(ctx, product);

		final SparPDTRowsQueryBuilder builder = getPDTRowsQueryBuilderFor(SparpricefactoryConstants.TC.SPARPRICEROW);
		PDTRowsQueryBuilder.QueryWithParams queryAndParams = null;
		queryAndParams = ((SparPDTRowsQueryBuilder) builder.withAnyProduct().withAnyUser().withProduct(productPk)
				.withProductId(productId).withProductGroup(productGroupPk).withUser(userPk).withUserGroup(userGroupPk))
				.withWarehouse(warehousePk).build();

		LOG.debug("Flexiblesearch query was : " + queryAndParams.getQuery());
		LOG.debug("Flexiblesearch query parameters was : " + queryAndParams.getParams());

		final Collection<PriceRow> searchResultList = FlexibleSearch.getInstance()
				.search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), PriceRow.class).getResult();

		if (LOG.isDebugEnabled() && CollectionUtils.isNotEmpty(searchResultList))
		{
			LOG.debug(" Flexi query search result : " + searchResultList.iterator().next());
		}

		return searchResultList;
	}

	/**
	 * This method is used to get the warehouse specific price row for a product
	 *
	 * @see de.hybris.platform.europe1.jalo.Europe1PriceFactory#getProductPriceRowsFast(de.hybris.platform.jalo.SessionContext
	 *      , de.hybris.platform.jalo.product.Product, de.hybris.platform.jalo.enumeration.EnumerationValue)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection getProductPriceRowsFast(final SessionContext ctx, final Product product, final EnumerationValue productGroup)
	{
		if (null == product)
		{
			throw new JaloInvalidParameterException("cannot find price rows without product ", 0);
		}

		if (null == retrieveWarehouseStrategy)
		{
			return super.getProductPriceRowsFast(ctx, product, productGroup);
		}

		WarehouseModel warehouse = null;
		warehouse = retrieveWarehouseStrategy.getWarehouse(ctx);
		if (null == warehouse)
		{
			LOG.debug("Warehouse is null while determining price for the product : " + product.getCode());
			return super.getProductPriceRowsFast(ctx, product, productGroup);
		}
		else
		{
			LOG.debug("Warehouse PK is : " + warehouse.getPk() + " used while determining price for the product : "
					+ product.getCode());
		}

		final PK warehousePk = warehouse.getPk();
		final PK productGroupPk = (productGroup == null) ? null : productGroup.getPK();
		final String productId = extractProductId(ctx, product);
		final PDTRowsQueryBuilder builder = getPDTRowsQueryBuilderFor(SparpricefactoryConstants.TC.SPARPRICEROW);
		PDTRowsQueryBuilder.QueryWithParams queryAndParams = null;
		queryAndParams = ((SparPDTRowsQueryBuilder) builder.withAnyProduct().withProduct(product.getPK()).withProductId(productId)
				.withProductGroup(productGroupPk)).withWarehouse(warehousePk).build();

		return getSession()
				.getFlexibleSearch()
				.search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), Collections.singletonList(PriceRow.class), true,
						true, 0, -1).getResult();
	}


	/**
	 * This method is used to get the warehouse specific price row for a product
	 *
	 * @see de.hybris.platform.europe1.jalo.Europe1PriceFactory#getRealPartOfPriceRows(de.hybris.platform.jalo.SessionContext,
	 *      de.hybris.platform.jalo.product.Product, de.hybris.platform.jalo.enumeration.EnumerationValue)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Collection<PriceRow> getRealPartOfPriceRows(final SessionContext ctx, final Product product,
			final EnumerationValue productGroup)
	{

		if (product == null)
		{
			throw new JaloInvalidParameterException("cannot find price rows without product ", 0);
		}

		if (null == retrieveWarehouseStrategy)
		{
			return super.getRealPartOfPriceRows(ctx, product, productGroup);
		}

		WarehouseModel warehouse = null;
		warehouse = retrieveWarehouseStrategy.getWarehouse(ctx);
		if (null == warehouse)
		{
			LOG.debug("Warehouse is null while determining price for the product : " + product.getCode());
			return super.getProductPriceRowsFast(ctx, product, productGroup);
		}
		else
		{
			LOG.debug("Warehouse PK is : " + warehouse.getPk() + " used while determining price for the product : "
					+ product.getCode());
		}

		final PK warehousePk = warehouse.getPk();
		final PDTRowsQueryBuilder builder = getPDTRowsQueryBuilderFor(SparpricefactoryConstants.TC.SPARPRICEROW);
		PDTRowsQueryBuilder.QueryWithParams queryAndParams = null;
		queryAndParams = ((SparPDTRowsQueryBuilder) builder.withProduct(product.getPK())).withWarehouse(warehousePk).build();

		Collection ret = getSession()
				.getFlexibleSearch()
				.search(ctx, queryAndParams.getQuery(), queryAndParams.getParams(), Collections.singletonList(PriceRow.class), true,
						true, 0, -1).getResult();
		if ((!(useFastAlg(ctx))) && (ret.size() > 1))
		{
			final List list = new ArrayList(ret);
			Collections.sort(list, PR_COMP);
			ret = list;
		}
		return ret;
	}

	/**
	 * This method is used during price comparison
	 *
	 * @param ctx
	 * @return boolean
	 */
	private boolean useFastAlg(final SessionContext ctx)
	{
		return ((ctx == null) || (!(Boolean.FALSE.equals(ctx.getAttribute("use.fast.algorithms")))));
	}

	/**
	 * This method is used to get the custom query builder This method is overridden to add warehouse param in the
	 * querybuilder. Extended version of interface is retrieved using the below getter.
	 *
	 * @see de.hybris.platform.europe1.jalo.Europe1PriceFactory#getPDTRowsQueryBuilderFor(java.lang.String)
	 */
	@Override
	protected SparPDTRowsQueryBuilder getPDTRowsQueryBuilderFor(final String type)
	{
		return new SparDefaultPDTRowsQueryBuilder(type);
	}

	/**
	 * Getter retrieveWarehouseStrategy
	 *
	 * @return the retrieveWarehouseStrategy
	 */
	public SparRetrieveWarehouseStrategy getRetrieveWarehouseStrategy()
	{
		return retrieveWarehouseStrategy;
	}


	/**
	 * Setter retrieveWarehouseStrategy
	 *
	 * @param retrieveWarehouseStrategy
	 *           the retrieveWarehouseStrategy to set
	 */
	public void setRetrieveWarehouseStrategy(final SparRetrieveWarehouseStrategy retrieveWarehouseStrategy)
	{
		this.retrieveWarehouseStrategy = retrieveWarehouseStrategy;
	}

	/**
	 * Comparator for sorting Price row. The code has been taken up from decompiled code of Europe1PriceFactory
	 */
	private static final Comparator<PriceRow> PR_COMP = new Comparator()
	{
		@Override
		public int compare(final Object obj1, final Object obj2)
		{
			final PriceRow object1 = (PriceRow) obj1;
			final PriceRow object2 = (PriceRow) obj2;
			final User user1 = object1.getUser();
			final User user2 = object2.getUser();
			String uid1 = (user1 != null) ? user1.getUID() : "ZZZZZZZZZZZZZZZZZZ";
			String uid2 = (user2 != null) ? user2.getUID() : "ZZZZZZZZZZZZZZZZZZ";
			int ret = uid1.compareToIgnoreCase(uid2);
			if (ret != 0)
			{
				return ret;
			}

			final EnumerationValue ug1 = object1.getUserGroup();
			final EnumerationValue ug2 = object2.getUserGroup();
			uid1 = (ug1 != null) ? ug1.getCode() : "ZZZZZZZZZZZZZZZZZZ";
			uid2 = (ug2 != null) ? ug2.getCode() : "ZZZZZZZZZZZZZZZZZZ";
			ret = uid1.compareToIgnoreCase(uid2);
			if (ret != 0)
			{
				return ret;
			}

			int product1 = (object1.getProduct() != null) ? 1 : 0;
			int product2 = (object2.getProduct() != null) ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}

			final int productId1 = (object1.getProductId() != null) ? 1 : 0;
			final int productId2 = (object2.getProductId() != null) ? 1 : 0;
			ret = productId1 - productId2;
			if (ret != 0)
			{
				return ret;
			}

			product1 = (object1.getProductGroup() != null) ? 1 : 0;
			product2 = (object2.getProductGroup() != null) ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}

			final String currency1 = object1.getCurrency().getIsoCode();
			final String currency2 = object2.getCurrency().getIsoCode();
			ret = currency1.compareToIgnoreCase(currency2);
			if (ret != 0)
			{
				return ret;
			}

			product1 = (object1.isNetAsPrimitive()) ? 1 : 0;
			product2 = (object2.isNetAsPrimitive()) ? 1 : 0;
			ret = product1 - product2;
			if (ret != 0)
			{
				return ret;
			}

			final String un1 = object1.getUnit().getCode();
			final String un2 = object2.getUnit().getCode();
			ret = un1.compareToIgnoreCase(un2);
			if (ret != 0)
			{
				return ret;
			}

			final long min1 = object1.getMinqtdAsPrimitive();
			final long min2 = object2.getMinqtdAsPrimitive();
			ret = (int) (min1 - min2);
			if (ret != 0)
			{
				return ret;
			}

			return object1.getPK().compareTo(object2.getPK());
		}

	};

	/**
	 * This method is overridden to skip the date range check for order.
	 */
	@Override
	protected List<PriceRow> filterPriceRows4Price(final Collection<PriceRow> rows, final long _quantity, final Unit unit,
			final Currency curr, final Date date, final boolean giveAwayMode, final PriceRowChannel channel)
	{
		if (rows.isEmpty())
		{
			return Collections.EMPTY_LIST;
		}

		final Currency base = (curr.isBase().booleanValue()) ? null : C2LManager.getInstance().getBaseCurrency();
		final Set convertible = unit.getConvertibleUnits();

		final List ret = new ArrayList(rows);

		final long quantity = (_quantity == 0L) ? 1L : _quantity;
		boolean hasChannelRowMatching = false;
		for (final ListIterator it = ret.listIterator(); it.hasNext();)
		{
			final PriceRow priceRow = (PriceRow) it.next();
			if (quantity < priceRow.getMinqtdAsPrimitive())
			{
				it.remove();
			}
			else
			{
				final Currency currency = priceRow.getCurrency();
				if ((!(curr.equals(currency))) && (((base == null) || (!(base.equals(currency))))))
				{
					it.remove();
				}
				else
				{
					final Unit user = priceRow.getUnit();
					if ((!(unit.equals(user))) && (!(convertible.contains(user))))
					{
						it.remove();
					}
					else
					{
						final DateRange dateRange = priceRow.getDateRange();
						if ((dateRange != null) && (!(dateRange.encloses(date))))
						{
							//This code has been commented as order creationDate does not lie between price row range.
							//it.remove();
						}
						else if (giveAwayMode != priceRow.isGiveAwayPriceAsPrimitive())
						{
							it.remove();
						}
						else if ((channel != null) && (priceRow.getChannel() != null)
								&& (!(priceRow.getChannel().getCode().equalsIgnoreCase(channel.getCode()))))
						{
							it.remove();
						}
						else
						{
							if ((channel == null) || (priceRow.getChannel() == null)
									|| (!(priceRow.getChannel().getCode().equalsIgnoreCase(channel.getCode()))))
							{
								continue;
							}
							hasChannelRowMatching = true;
						}
					}
				}
			}
		}
		if ((hasChannelRowMatching) && (ret.size() > 1))
		{
			for (final ListIterator it = ret.listIterator(); it.hasNext();)
			{
				final PriceRow priceRow = (PriceRow) it.next();
				if (priceRow.getChannel() != null)
				{
					continue;
				}
				it.remove();
			}

		}

		return ret;
	}


	/**
	 * Getter for RetrieveChannelStrategy
	 *
	 * @return the retrieveChannelStrategy
	 */
	public RetrieveChannelStrategy getRetrieveChannelStrategy()
	{
		return retrieveChannelStrategy;
	}


	/**
	 * Setter RetrieveChannelStrategy
	 *
	 * @param retrieveChannelStrategy
	 *           the retrieveChannelStrategy to set
	 */
	@Override
	public void setRetrieveChannelStrategy(final RetrieveChannelStrategy retrieveChannelStrategy)
	{
		this.retrieveChannelStrategy = retrieveChannelStrategy;
		super.setRetrieveChannelStrategy(retrieveChannelStrategy);
	}


}
