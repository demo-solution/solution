/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.facades.customer.impl.SparDefaultNeedHelpFacade;


/**
 * @author tanveers
 *
 */

@Controller
@RequestMapping(value = "/need-help")
public class SparNeedHelpController extends AbstractPageController
{
	@Resource(name = "sparNeedHelpFacade")
	private SparDefaultNeedHelpFacade sparDefaultNeedHelpFacade;

	@ResponseBody
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public void getNeedhelp(@RequestParam("selectedNeedHelpOption") final String selectedNeedHelpOption,
			@RequestParam("customerNameh") final String customerName,
			@RequestParam("customerContacth") final String customerContact,
			@RequestParam("customerDesiredTimeh") final String customerDesiredTime,
			@RequestParam("customerRemarksh") final String customerRemarks) throws CMSItemNotFoundException
	{
		sparDefaultNeedHelpFacade.sendNeedHelpFormValue(selectedNeedHelpOption, customerName, customerContact, customerDesiredTime,
				customerRemarks);
	}

}
