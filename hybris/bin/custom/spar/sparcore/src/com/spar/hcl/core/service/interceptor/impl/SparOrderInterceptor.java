/**
 *
 */
package com.spar.hcl.core.service.interceptor.impl;

import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interceptor Class for change in order entry quantity.
 *
 * @author tanveers
 */
public class SparOrderInterceptor implements PrepareInterceptor
{
	/** The Constant LOG. */
	private static final String LIMIT_CSP_CHANGE = "LIMIT_CSP_CHANGE";
	private static final Logger LOG = LoggerFactory.getLogger(SparOrderInterceptor.class);
	private ModelService modelService;

	@SuppressWarnings("boxing")
	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		LOG.debug("Entered Prepare method of order entry interceptor....");
		if (model instanceof OrderEntryModel)
		{
			final OrderEntryModel orderEntryModel = (OrderEntryModel) model;
			LOG.debug("Entered in Instance of order entry interceptor....");
			if (null == orderEntryModel.getOriginalQty() || Long.valueOf(0) == orderEntryModel.getOriginalQty())
			{
				orderEntryModel.setOriginalQty(orderEntryModel.getQuantity());
			}
			if (null == orderEntryModel.getOriginalCSP() || 0.0 == orderEntryModel.getOriginalCSP())
			{
				orderEntryModel.setOriginalCSP(orderEntryModel.getBasePrice());
			}
			if (ctx.isModified(orderEntryModel))
			{
				LOG.debug("Entered in order entry interceptor when order entry is modified....");
				if (orderEntryModel.getShortPickQty() <= orderEntryModel.getOriginalQty())
				{
					orderEntryModel.setQuantity(orderEntryModel.getOriginalQty() - orderEntryModel.getShortPickQty());
				}
				else
				{
					throw new InterceptorException("Entered Short Pick quantity is more than the Original placed Quantity");
				}
				if (orderEntryModel.getBasePrice() < ((orderEntryModel.getOriginalCSP() / 100) * (100 - Config.getDouble(
						LIMIT_CSP_CHANGE, 30)))
						|| orderEntryModel.getBasePrice() > ((orderEntryModel.getOriginalCSP() / 100) * (100 + Config.getDouble(
								LIMIT_CSP_CHANGE, 30))))
				{
					throw new InterceptorException(
							"Entered CSP is either less or more than 30% of the Original CSP, Kindly enter correct CSP value");
				}
				if (null != orderEntryModel.getConsignmentEntries())
				{
					for (final ConsignmentEntryModel consignment : orderEntryModel.getConsignmentEntries())
					{
						final ConsignmentEntryModel newConsignment = getModelService().clone(consignment, ConsignmentEntryModel.class);
						newConsignment.setQuantity(orderEntryModel.getQuantity());
						newConsignment.setShortPickQty(orderEntryModel.getShortPickQty());
						LOG.debug("setting order entry values to consigments....");
						getModelService().save(newConsignment);
						getModelService().remove(consignment);
					}
				}
			}
		}
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
