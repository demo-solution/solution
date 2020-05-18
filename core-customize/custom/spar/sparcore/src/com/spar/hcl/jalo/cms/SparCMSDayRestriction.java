package com.spar.hcl.jalo.cms;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.util.localization.Localization;

import java.util.List;

import org.apache.log4j.Logger;


public class SparCMSDayRestriction extends GeneratedSparCMSDayRestriction
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(SparCMSDayRestriction.class.getName());

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
	 * @see
	 * de.hybris.platform.cms2.jalo.restrictions.AbstractRestriction#getDescription(de.hybris.platform.jalo.SessionContext
	 * )
	 */
	@SuppressWarnings("deprecation")
	@Override
	public String getDescription(final SessionContext arg0)
	{
		final StringBuilder result = new StringBuilder();
		final List<EnumerationValue> activeDaysList = this.getActiveDays();

		final Object[] args = new Object[]
		{ activeDaysList.toArray() };
		final String localizedString = Localization.getLocalizedString("type.CMSDayRestriction.description.text", args);
		if (localizedString == null)
		{
			result.append("Page only applies on ");
			for (final EnumerationValue day : activeDaysList)
			{
				result.append(day.getCode());
			}
			return result.toString();
		}
		else
		{
			return localizedString;
		}
	}
}
