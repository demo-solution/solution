/**
 *
 */
/*
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.service.delivery.data.SparServiceAreaData;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;


 *//**
 * @author tanveers
 *
 */
/*

@Controller
@Scope("tenant")
@RequestMapping(value = "/spar-store-finder")
public class SparStoreLocatorPageContorller
{
protected static final Logger LOG = Logger.getLogger(SparStoreLocatorPageContorller.class);

@Resource(name = "storeFinderFacadeInterface")
private StoreFinderFacadeInterface storeFinderFacadeInterface;

@Resource(name = "sessionService")
SessionService sessionService;

@Resource(name = "sparCustomerFacade")
SparCustomerFacade sparCustomerFacade;

@RequestMapping(method = RequestMethod.GET)
public String getSparStoreServiceAreaForCity(@RequestParam("selectedCityName") final String selectedCityName,
		@RequestParam("area") final String area, final Model model) throws CMSItemNotFoundException
{
	final List<SparServiceAreaData> serviceAreaForCity = getStoreFinderFacadeInterface().getSparServiceAreaForCity(
			selectedCityName, area);
	model.addAttribute("ServiceArea", serviceAreaForCity);
	return ControllerConstants.Views.Pages.StoreFinder.SparStoreFinderSearchPage;
}

@RequestMapping(value = "/spar-delivery-slots", method = RequestMethod.GET)
public String getDeliverySlotsForStorePOS(@RequestParam("selectedCityName") final String selectedCityName,
		@RequestParam("defaultStore") final String defaultStore, 
		@RequestParam("defaulStoreCenter") final String defaulStoreCenter,
		@RequestParam("hdActive") final String hdActive,
		final Model model) throws CMSItemNotFoundException
{
	final List<PointOfServiceData> posForCitySelected = sparPosFinder(selectedCityName,"POS");
		if(!defaultStore.isEmpty())
		{
			final Map dSlotsPOS = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("CNC", defaultStore);
			model.addAttribute("DSSLOTSPOS", dSlotsPOS);
			
			final Map dSlotsStores = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("HD", defaultStore);
      	model.addAttribute("DSSLOTSSTORES", dSlotsStores);
		}
		else if(!defaulStoreCenter.isEmpty()){
			final Map dSlotsPOS = storeFinderFacadeInterface.getDeliverySlotsForStorePOS("CNC", defaulStoreCenter);
			model.addAttribute("DSSLOTSPOS", dSlotsPOS);
		}
	model.addAttribute("defaultStore", defaultStore);
	model.addAttribute("POS", posForCitySelected);
	model.addAttribute("hdActive", hdActive);
	final String userCurrentEmailID = sparCustomerFacade.findUserEmailId();
	model.addAttribute("USEREMAILID", userCurrentEmailID);
	return ControllerConstants.Views.Pages.StoreFinder.SparDeliverySlotsForStore;
}

public List<PointOfServiceData> sparPosFinder(final String selectedCityName, final String posType)
{
	final List<PointOfServiceData> posForCitySelected = storeFinderFacadeInterface.getPosForCity(selectedCityName, posType);
	return posForCitySelected;
}

@RequestMapping(value = "/spar-service-area", method = RequestMethod.GET)
public String getSparStoreServiceArea(final Model model) throws CMSItemNotFoundException
{
	final List<SparServiceAreaData> serviceArea = getStoreFinderFacadeInterface().getSparServiceArea();
	model.addAttribute("ServiceArea", serviceArea);
	return ControllerConstants.Views.Pages.StoreFinder.SparStoreFinderSearchPage;
}

 *//**
 * @return the storeFinderFacadeInterface
 */
/*
public StoreFinderFacadeInterface getStoreFinderFacadeInterface()
{
return storeFinderFacadeInterface;
}

 *//**
 * @param storeFinderFacadeInterface
 *           the storeFinderFacadeInterface to set
 */
/*
 * public void setStoreFinderFacadeInterface(final StoreFinderFacadeInterface storeFinderFacadeInterface) {
 * this.storeFinderFacadeInterface = storeFinderFacadeInterface; } }
 */
