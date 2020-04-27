/**
 *
 */
package com.spar.hcl.core.order.attibute.handler;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.apache.commons.collections.CollectionUtils;


/**
 * @author sitaram_b
 *
 */
public class TotalOrderQuantityAttributeHandler implements DynamicAttributeHandler<Integer, OrderModel>
{
	/*
	 * This functionality for calculate total entries in the order
	 * 
	 * @ parameter OrderModel
	 * 
	 * @ return totalOrderQuantity
	 */
	@Override
	public Integer get(final OrderModel orderModel)
	{
		int totalOrderQuantity = 0;
		if (orderModel != null)
		{
			if (orderModel.getEntries() != null && CollectionUtils.isNotEmpty(orderModel.getEntries()))
			{
				for (final AbstractOrderEntryModel orderEntry : orderModel.getEntries())
				{
					totalOrderQuantity = totalOrderQuantity + orderEntry.getQuantity().intValue();
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Item model is required");
		}
		return totalOrderQuantity;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final OrderModel orderModel, final Integer totalOrderQuantity)
	{
		// YTODO Auto-generated method stub

	}
}
