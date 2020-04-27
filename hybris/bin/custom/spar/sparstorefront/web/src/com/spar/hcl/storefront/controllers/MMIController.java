/**
 *
 */
package com.spar.hcl.storefront.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author pramod-kuma
 *
 */
@Controller
public class MMIController
{
	@Autowired
	MMIUtil mMIUtil;

	@RequestMapping(value = "/autostate", method = RequestMethod.GET)
	@ResponseBody
	public Object autoSuggest(final String q, final String location)
	{

		//       return mMIUtil.cityAutoSuggest(q);
		return mMIUtil.getAutoSuggest(q, location);
	}
}
