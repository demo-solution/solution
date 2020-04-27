/**
 *
 */
package com.spar.hcl.core.service.order.impl;

import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.apache.commons.collections.CollectionUtils;


/**
 * @author tanveers
 *
 */
public class SparDefaultDeliveryModeLookupStrategy extends DefaultDeliveryModeLookupStrategy
{
	@Override
	protected boolean isPickUpOnlyOrder(final AbstractOrderModel abstractOrderModel)
	{
		if (CollectionUtils.isNotEmpty(abstractOrderModel.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : abstractOrderModel.getEntries())
			{
				if (null != entry.getBasePrice() && 0 != entry.getBasePrice().compareTo(Double.valueOf(0.0d)))
				{
					if (entry.getDeliveryPointOfService() == null)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
