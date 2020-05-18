/**
 *
 */
package com.spar.hcl.core.servicelayer.services.evaluator.impl;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.model.cms.SparBannerWarehouseRestrictionModel;


/**
 * To Restrict Home page banner as per warehouse.
 *
 */
public class SparBannerWarehouseRestrictionEvaluator implements CMSRestrictionEvaluator<SparBannerWarehouseRestrictionModel>
{
	private static final Logger LOG = Logger.getLogger(SparBannerWarehouseRestrictionEvaluator.class);
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	public boolean evaluate(final SparBannerWarehouseRestrictionModel warehouseRestriction, final RestrictionData context)
	{

		final String warehouseCode = sessionService.getAttribute("selectedWarehouseCode");
		boolean isActive = false;

		if (warehouseRestriction.getWarehouses() != null)
		{

			for (final WarehouseModel warehouse : warehouseRestriction.getWarehouses())
			{

				if (StringUtils.equalsIgnoreCase(warehouse.getCode(), warehouseCode))
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