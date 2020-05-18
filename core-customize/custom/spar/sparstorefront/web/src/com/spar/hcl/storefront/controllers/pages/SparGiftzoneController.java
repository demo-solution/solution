/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.core.GenericSearchConstants.LOG;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.facades.giftzone.SparGiftZoneFacade;
import com.spar.hcl.facades.service.delivery.data.SparGiftZoneContactUsData;


/**
 * @author pavan.sr
 *
 */

@Controller
@RequestMapping(value = "/giftzone")
public class SparGiftzoneController extends AbstractPageController
{

	protected static final Logger LOG = Logger.getLogger(SparGiftzoneController.class);

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparGiftZoneFacade")
	private SparGiftZoneFacade sparGiftZoneFacade;

	@Resource(name = "modelService")
	ModelService modelService;


	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	public String getGiftzoneContactUsDetail(@RequestParam("gsCustomerName") final String customerName,
			@RequestParam("gsCustomerEmail") final String customerEmail,
			@RequestParam("gsCustomerContact") final String customerContact,
			@RequestParam("gsCompanyName") final String companyName, @RequestParam("gsCustomerCity") final String customerCity,
			@RequestParam("customerRemarksh") final String customerRemarks, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		LOG.debug("Entering in getGiftzoneContactUsDetail :::::::::::::::::::::::::::");
		String response = null;
		try
		{
			final SparGiftZoneContactUsData giftZoneContactUsData = new SparGiftZoneContactUsData();

			giftZoneContactUsData.setCompanyName(companyName);
			giftZoneContactUsData.setCustomerCity(customerCity);
			giftZoneContactUsData.setCustomerContact(customerContact);
			giftZoneContactUsData.setCustomerEmail(customerEmail);
			giftZoneContactUsData.setCustomerName(customerName);
			giftZoneContactUsData.setCustomerRemarks(customerRemarks);

			sparGiftZoneFacade.saveGZContactUsFormValue(giftZoneContactUsData);
			response = "success";
		}
		catch (final Exception e)
		{
			response = "failure";
			LOG.error("Error while saving contactUs Details." + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

}
