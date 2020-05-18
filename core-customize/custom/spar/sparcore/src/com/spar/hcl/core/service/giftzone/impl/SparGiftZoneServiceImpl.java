/**
 *
 */
package com.spar.hcl.core.service.giftzone.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparGiftzoneContactUsEmailProcessModel;
import com.spar.hcl.core.model.service.giftzone.SparGiftZoneContactUsModel;
import com.spar.hcl.core.service.giftzone.SparGiftZoneService;


/**
 * @author pavan.sr
 *
 */
public class SparGiftZoneServiceImpl implements SparGiftZoneService
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private ConfigurationService configurationService;

	private static final String BASESITE = "basesite.uid";

	private static final String GIFT_ZONE_CONTACTUS_EMAIL_PROCESS = "SparGiftzoneContactUsEmailProcess";
	private static final Logger LOG = Logger.getLogger(SparGiftZoneServiceImpl.class);

	@Override
	public void saveGZContactUsDetails(final SparGiftZoneContactUsModel contactUsModel)
	{
		LOG.debug("Entering in saveGZContactUsDetails:::::::::::::::::::::::::::::::::");
		if (null != contactUsModel)
		{
			modelService.save(contactUsModel);
			sendGiftzoneContactUsEmail(contactUsModel);
		}
	}


	private void sendGiftzoneContactUsEmail(final SparGiftZoneContactUsModel contactUsModel)
	{
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration()
				.getString(BASESITE));

		final SparGiftzoneContactUsEmailProcessModel contactUsEmailProcessModel = (SparGiftzoneContactUsEmailProcessModel) businessProcessService
				.createProcess("SparGiftzoneContactUsEmailProcess-" + +System.currentTimeMillis(),
						"SparGiftzoneContactUsEmailProcess");
		contactUsEmailProcessModel.setSite(baseSite);
		contactUsEmailProcessModel.setStore(baseSite.getStores().get(0));
		contactUsEmailProcessModel.setCustomerName(contactUsModel.getCustomerName());
		contactUsEmailProcessModel.setCustomerContact(contactUsModel.getCustomerContact());
		contactUsEmailProcessModel.setCustomerEmail(contactUsModel.getCustomerEmail());
		contactUsEmailProcessModel.setCompanyName(contactUsModel.getCompanyName());
		contactUsEmailProcessModel.setCustomerCity(contactUsModel.getCustomerCity());
		contactUsEmailProcessModel.setCustomerRemarks(contactUsModel.getCustomerRemarks());

		//modelService.save(contactUsEmailProcessModel);
		businessProcessService.startProcess(contactUsEmailProcessModel);
	}


}
