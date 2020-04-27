/**
 *
 */
package com.spar.hcl.core.model;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

import java.util.List;

import com.spar.hcl.core.enums.SparCMSRestrictionDay;
import com.spar.hcl.model.cms.SparCMSDayRestrictionModel;


/**
 * @author manish_ku
 *
 */
public class DayRestrictionDescription implements DynamicAttributeHandler<String, SparCMSDayRestrictionModel>
{
	@Override
	public String get(final SparCMSDayRestrictionModel model)
	{
		final StringBuilder result = new StringBuilder();
		final List<SparCMSRestrictionDay> activeDaysList = model.getActiveDays();

		final Object[] args = new Object[]
		{ activeDaysList.toArray() };
		final String localizedString = Localization.getLocalizedString("type.SparCMSDayRestriction.description.text", args);
		if (localizedString == null)
		{
			result.append("Page only applies on ");
			for (final SparCMSRestrictionDay day : activeDaysList)
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

	public void set(final SparCMSDayRestrictionModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}
}