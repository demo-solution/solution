/**
 *
 */
package com.spar.hcl.core.servicelayer.services.evaluator.impl;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.enums.SparCMSRestrictionDay;
import com.spar.hcl.model.cms.SparCMSDayRestrictionModel;


/**
 * @author manish_ku
 *
 */
public class SparCMSDayRestrictionEvaluator implements CMSRestrictionEvaluator<SparCMSDayRestrictionModel>
{
	private static final Logger LOG = Logger.getLogger(SparCMSDayRestrictionEvaluator.class);
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public boolean evaluate(final SparCMSDayRestrictionModel dayRestriction, final RestrictionData context)
	{
		final Date now = (Date) this.sessionService.getAttribute("previewTime");
		final String nowDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(now.getTime());
		boolean isActive = false;

		if (dayRestriction.getActiveDays() != null)
		{
			for (final SparCMSRestrictionDay day : dayRestriction.getActiveDays())
			{
				if (StringUtils.equalsIgnoreCase(day.getCode(), nowDay))
				{
					isActive = true;
					break;
				}
			}
		}
		return isActive;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}