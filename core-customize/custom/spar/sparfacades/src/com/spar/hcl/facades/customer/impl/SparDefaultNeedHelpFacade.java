/**
 *
 */
package com.spar.hcl.facades.customer.impl;

import de.hybris.platform.servicelayer.event.EventService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.event.SparNeedHelpEvent;
import com.spar.hcl.facades.customer.SparNeedHelpFacade;


/**
 * @author tanveers
 *
 */
public class SparDefaultNeedHelpFacade implements SparNeedHelpFacade
{
	private static final Logger LOG = Logger.getLogger(SparDefaultNeedHelpFacade.class);
	private EventService eventService;

	@Override
	public void sendNeedHelpFormValue(final String selectedNeedHelpOption, final String customerName,
			final String customerContact, final String customerDesiredTime, final String customerRemarks)
	{
		final SparNeedHelpEvent sparNeedHelpEvent = new SparNeedHelpEvent();
		sparNeedHelpEvent.setSelectNeedHelpOption(selectedNeedHelpOption);
		sparNeedHelpEvent.setCustomerName(customerName);
		sparNeedHelpEvent.setCustomerContact(customerContact);
		sparNeedHelpEvent.setCustomerDesiredTime(customerDesiredTime);
		sparNeedHelpEvent.setCustomerRemarks(customerRemarks);
		getEventService().publishEvent(sparNeedHelpEvent);
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

}
