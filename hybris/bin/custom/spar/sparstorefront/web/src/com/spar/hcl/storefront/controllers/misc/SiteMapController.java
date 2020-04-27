/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.storefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spar.hcl.core.enums.SparSitemapPageType;
import com.spar.hcl.facades.sitemap.SparSiteMapFacade;
import com.spar.hcl.storefront.controllers.ControllerConstants;


@Controller
@Scope("tenant")
public class SiteMapController extends AbstractController
{


	@Resource(name = "sparDefaultSiteMapFacade")
	private SparSiteMapFacade sparDefaultSiteMapFacade;

	@RequestMapping(value = "/sitemap.xml", method = RequestMethod.GET, produces = "application/xml")
	public String getSitemapXml(final Model model)
	{
		model.addAttribute("siteMapData", sparDefaultSiteMapFacade.getSiteMapList(SparSitemapPageType.LANDING.getCode()));
		return ControllerConstants.Views.Pages.Misc.MiscSiteMapPage;
	}

	@RequestMapping(value = "/sitemap/category.xml", method = RequestMethod.GET, produces = "application/xml")
	public String getSitemapXmlforCategory(final Model model)
	{
		model.addAttribute("siteMapData", sparDefaultSiteMapFacade.getSiteMapList(SparSitemapPageType.CATEGORY.getCode()));
		return ControllerConstants.Views.Pages.Misc.MiscSiteMapCategorPage;
	}

	@RequestMapping(value = "/sitemap/subcategory.xml", method = RequestMethod.GET, produces = "application/xml")
	public String getSitemapXmlforSubcategory(final Model model)
	{
		model.addAttribute("siteMapData", sparDefaultSiteMapFacade.getSiteMapList(SparSitemapPageType.SUBCATEGORY.getCode()));
		return ControllerConstants.Views.Pages.Misc.MiscSiteMapSubCategoryPage;
	}

	@RequestMapping(value = "/sitemap/product_list.xml", method = RequestMethod.GET, produces = "application/xml")
	public String getSitemapXmlforProduct(final Model model)
	{
		model.addAttribute("siteMapData", sparDefaultSiteMapFacade.getSiteMapList(SparSitemapPageType.PRODUCT.getCode()));
		return ControllerConstants.Views.Pages.Misc.MiscSiteMapProductPage;
	}

	@RequestMapping(value = "/sitemap/toppages.xml", method = RequestMethod.GET, produces = "application/xml")
	public String getSitemapXmlforStatic(final Model model)
	{
		model.addAttribute("siteMapData", sparDefaultSiteMapFacade.getSiteMapList(SparSitemapPageType.STATIC.getCode()));
		return ControllerConstants.Views.Pages.Misc.MiscSiteMapStaticPage;
	}
}
