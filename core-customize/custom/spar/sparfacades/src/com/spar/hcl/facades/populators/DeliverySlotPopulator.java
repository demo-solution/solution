/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.spar.hcl.deliveryslot.model.DeliverySlotModel;
import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;


/**
 * @author valechar
 *
 */
public class DeliverySlotPopulator implements Populator<DeliverySlotModel, DeliverySlotData>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final DeliverySlotModel source, final DeliverySlotData target) throws ConversionException
	{
		target.setSlotId(source.getSlotId());
		target.setOrderingDay(source.getOrderingDay());
		target.setMinimumOrderTimeSlot(source.getMinimumOrderTimeSlot());
		target.setMaximumOrderTimeSlot(source.getMaximumOrderTimeSlot());
		target.setSlotDescription(source.getSlotDescription());
		target.setDeliveryType(source.getDeliveryType().toString());
		target.setActive(source.getActive());
		target.setHomeDeliveryId(source.getHomeDeliveryId());
		target.setSlotOrdersCapacity(source.getSlotOrdersCapacity());
		target.setIsHourlyDeliverySlotType(source.getIsHourlyDeliverySlotType());
	}
}
