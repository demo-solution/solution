/**
 *
 */
package com.spar.hcl.facades.giftzone.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

import com.spar.hcl.core.model.service.giftzone.SparGiftZoneContactUsModel;
import com.spar.hcl.core.service.giftzone.SparGiftZoneService;
import com.spar.hcl.facades.giftzone.SparGiftZoneFacade;
import com.spar.hcl.facades.service.delivery.data.SparGiftZoneContactUsData;


public class SparDefaultGiftZoneFacade implements SparGiftZoneFacade
{

	private ModelService modelService;
	private SparGiftZoneService sparGiftZoneService;

	private Populator<SparGiftZoneContactUsData, SparGiftZoneContactUsModel> sparGiftZoneContactUsPopulator;

	private static final Logger LOG = Logger.getLogger(SparDefaultGiftZoneFacade.class);

	@Override
	public void saveGZContactUsFormValue(final SparGiftZoneContactUsData contactUsData)
	{
		LOG.debug("Entering in saveGZContactUsFormValue method ::::::::::::::::::::::::::");
		if (null != contactUsData)
		{
			final SparGiftZoneContactUsModel contactUsModel = new SparGiftZoneContactUsModel();
			getSparGiftZoneContactUsPopulator().populate(contactUsData, contactUsModel);
			getSparGiftZoneService().saveGZContactUsDetails(contactUsModel);
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
	 * @return the sparGiftZoneContactUsPopulator
	 */
	public Populator<SparGiftZoneContactUsData, SparGiftZoneContactUsModel> getSparGiftZoneContactUsPopulator()
	{
		return sparGiftZoneContactUsPopulator;
	}

	/**
	 * @param sparGiftZoneContactUsPopulator
	 *           the sparGiftZoneContactUsPopulator to set
	 */
	public void setSparGiftZoneContactUsPopulator(
			final Populator<SparGiftZoneContactUsData, SparGiftZoneContactUsModel> sparGiftZoneContactUsPopulator)
	{
		this.sparGiftZoneContactUsPopulator = sparGiftZoneContactUsPopulator;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the sparGiftZoneService
	 */
	public SparGiftZoneService getSparGiftZoneService()
	{
		return sparGiftZoneService;
	}

	/**
	 * @param sparGiftZoneService
	 *           the sparGiftZoneService to set
	 */
	public void setSparGiftZoneService(final SparGiftZoneService sparGiftZoneService)
	{
		this.sparGiftZoneService = sparGiftZoneService;
	}


}