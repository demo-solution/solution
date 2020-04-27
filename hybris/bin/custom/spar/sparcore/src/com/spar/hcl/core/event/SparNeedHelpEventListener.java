/**
 *
 */
package com.spar.hcl.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

import org.apache.log4j.Logger;

import com.spar.hcl.core.model.process.SparNeedHelpEmailProcessModel;


/**
 * @author tanveers
 *
 */
public class SparNeedHelpEventListener extends AbstractSiteEventListener<SparNeedHelpEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private BaseSiteService baseSiteService;
	private ConfigurationService configurationService;
	/** The BASESITE_UID */
	private static String BASESITE_UID = "basesite.uid";

	@Override
	protected void onSiteEvent(final SparNeedHelpEvent event)
	{
		final Logger LOG = Logger.getLogger(SparNeedHelpEventListener.class);
		LOG.debug("Inside SparNeedHelpEventListener:initiateSparNeedHelpEmailProcess");

		final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID(
				getConfigurationService().getConfiguration().getString(BASESITE_UID));

		final SparNeedHelpEmailProcessModel sparNeedHelpEmailProcessModel = (SparNeedHelpEmailProcessModel) getBusinessProcessService()
				.createProcess("SparNeedHelpEmailProcess-" + System.currentTimeMillis(), "SparNeedHelpEmailProcess");
		sparNeedHelpEmailProcessModel.setSite(baseSite);
		sparNeedHelpEmailProcessModel.setStore(baseSite.getStores().get(0));
		sparNeedHelpEmailProcessModel.setHelpType(event.getSelectNeedHelpOption());
		sparNeedHelpEmailProcessModel.setCustomerName(event.getCustomerName());
		sparNeedHelpEmailProcessModel.setCustomerNumber(event.getCustomerContact());
		sparNeedHelpEmailProcessModel.setDesiredTime(event.getCustomerDesiredTime());
		sparNeedHelpEmailProcessModel.setCustomerRemark(event.getCustomerRemarks());
		getModelService().save(sparNeedHelpEmailProcessModel);
		getBusinessProcessService().startProcess(sparNeedHelpEmailProcessModel);

		LOG.debug("SparNeedHelpEmailProcess started");
	}

	@Override
	protected boolean shouldHandleEvent(final SparNeedHelpEvent event)
	{
		final BaseSiteModel site = getBaseSiteService().getBaseSiteForUID(
				getConfigurationService().getConfiguration().getString(BASESITE_UID));
		ServicesUtil.validateParameterNotNullStandardMessage("event.site", site);
		return SiteChannel.B2C.equals(site.getChannel());
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
