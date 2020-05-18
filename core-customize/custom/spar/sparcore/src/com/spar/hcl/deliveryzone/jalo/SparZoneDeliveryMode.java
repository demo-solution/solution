package com.spar.hcl.deliveryzone.jalo;

import de.hybris.platform.deliveryzone.constants.GeneratedZoneDeliveryModeConstants;
import de.hybris.platform.deliveryzone.jalo.ZoneDeliveryModeValue;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public class SparZoneDeliveryMode extends GeneratedSparZoneDeliveryMode
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparZoneDeliveryMode.class.getName());

	@Resource
	private ModelService modelService;

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
	 * @see de.hybris.platform.deliveryzone.jalo.ZoneDeliveryMode#getCost(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.jalo.order.AbstractOrder)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public PriceValue getCost(final SessionContext ctx, final AbstractOrder order) throws JaloDeliveryModeException
	{
		final Country country = getSession().getC2LManager().getCountryByIsoCode("IN");

		if (country == null)
		{
			throw new JaloDeliveryModeException("getCost(): country is NULL", 0);
		}

		final Currency curr = order.getCurrency();
		if (curr == null)
		{
			throw new JaloDeliveryModeException("getCost(): currency was NULL in order " + order, 0);
		}

		final String propName = getPropertyName();
		if (propName == null)
		{
			throw new JaloDeliveryModeException("missing propertyname in deliverymode " + this, 0);
		}

		final double amount = getCalculationBaseValue(ctx, order, propName);

		final Map params = new HashMap();
		params.put("me", this);
		params.put("curr", curr);
		params.put("country", country);
		params.put("amount", new Double(amount));

		final String query = "SELECT {v." + PK + "} " + "FROM {" + GeneratedZoneDeliveryModeConstants.TC.ZONEDELIVERYMODEVALUE
				+ " AS v " + "JOIN " + GeneratedZoneDeliveryModeConstants.Relations.ZONECOUNTRYRELATION + " AS z2cRel " + "ON {v."
				+ "zone" + "}={z2cRel." + "source" + "} } " + "WHERE " + "{v." + "deliveryMode" + "} = ?me AND " + "{v." + "currency"
				+ "} = ?curr AND " + "{v." + "minimum" + "} <= ?amount AND " + "{z2cRel." + "target" + "} = ?country "
				+ "ORDER BY {v." + "minimum" + "} DESC ";

		List values = FlexibleSearch.getInstance().search(ctx, query, params, ZoneDeliveryModeValue.class).getResult();

		if ((values.isEmpty()) && (!(curr.isBase().booleanValue())) && (C2LManager.getInstance().getBaseCurrency() != null))
		{
			params.put("curr", C2LManager.getInstance().getBaseCurrency());
			values = FlexibleSearch.getInstance().search(ctx, query, params, ZoneDeliveryModeValue.class).getResult();
		}
		if (values.isEmpty())
		{
			throw new JaloDeliveryModeException("no delivery price defined for mode " + this + ", country " + country
					+ ", currency " + curr + " and amount " + amount, 0);
		}

		final ZoneDeliveryModeValue bestMatch = (ZoneDeliveryModeValue) values.get(0);

		final Currency myCurr = bestMatch.getCurrency();
		if ((!(curr.equals(myCurr))) && (myCurr != null))
		{
			return new PriceValue(curr.getIsoCode(), myCurr.convertAndRound(curr, bestMatch.getValueAsPrimitive()),
					isNetAsPrimitive(ctx));
		}

		return new PriceValue(curr.getIsoCode(), bestMatch.getValueAsPrimitive(), isNetAsPrimitive(ctx));

	}
}
