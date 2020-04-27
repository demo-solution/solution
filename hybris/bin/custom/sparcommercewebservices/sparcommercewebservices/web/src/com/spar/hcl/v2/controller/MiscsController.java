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
package com.spar.hcl.v2.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.dto.SparCitiesWsDTO;
import com.spar.hcl.dto.SparCityListWsDTO;
import com.spar.hcl.dto.SparMobileAppVersionWsDTO;
import com.spar.hcl.facades.service.delivery.data.SparCitiesData;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.mobileAppVersion.service.SparMobileAppVersionService;
import com.spar.hcl.order.data.CardTypeDataList;
import com.spar.hcl.storesession.data.CurrencyDataList;
import com.spar.hcl.storesession.data.LanguageDataList;
import com.spar.hcl.user.data.CountryDataList;
import com.spar.hcl.user.data.TitleDataList;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.storesession.CurrencyListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.storesession.LanguageListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.TitleListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;


/**
 * Misc Controller
 */
@Controller
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
public class MiscsController extends BaseController
{
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;
	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;
	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;
	@Autowired
	private SparMobileAppVersionService sparMobileAppVersionService;

	/**
	 * Lists all available languages (all languages used for a particular store). If the list of languages for a base
	 * store is empty, a list of all languages available in the system will be returned.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of languages
	 */
	@RequestMapping(value = "/{baseSiteId}/languages", method = RequestMethod.GET)
	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getLanguages',#fields)")
	@ResponseBody
	public LanguageListWsDTO getLanguages(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final LanguageDataList dataList = new LanguageDataList();
		dataList.setLanguages(storeSessionFacade.getAllLanguages());
		return dataMapper.map(dataList, LanguageListWsDTO.class, fields);
	}

	/**
	 * Lists all available currencies (all usable currencies for the current store).If the list of currencies for stores
	 * is empty, a list of all currencies available in the system is returned.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of currencies
	 */
	@RequestMapping(value = "/{baseSiteId}/currencies", method = RequestMethod.GET)
	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCurrencies',#fields)")
	@ResponseBody
	public CurrencyListWsDTO getCurrencies(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CurrencyDataList dataList = new CurrencyDataList();
		dataList.setCurrencies(storeSessionFacade.getAllCurrencies());
		return dataMapper.map(dataList, CurrencyListWsDTO.class, fields);
	}

	/**
	 * Lists all supported delivery countries for the current store. The list is sorted alphabetically.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List supported delivery countries.
	 */
	@RequestMapping(value = "/{baseSiteId}/deliverycountries", method = RequestMethod.GET)
	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getDeliveryCountries',#fields)")
	@ResponseBody
	public CountryListWsDTO getDeliveryCountries(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CountryDataList dataList = new CountryDataList();
		dataList.setCountries(checkoutFacade.getDeliveryCountries());
		return dataMapper.map(dataList, CountryListWsDTO.class, fields);
	}

	/**
	 * Lists all localized titles.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of titles
	 */
	@RequestMapping(value = "/{baseSiteId}/titles", method = RequestMethod.GET)
	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getTitles',#fields)")
	@ResponseBody
	public TitleListWsDTO getTitles(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final TitleDataList dataList = new TitleDataList();
		dataList.setTitles(userFacade.getTitles());
		return dataMapper.map(dataList, TitleListWsDTO.class, fields);
	}

	/**
	 * Lists supported payment card types.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of card types
	 */
	@RequestMapping(value = "/{baseSiteId}/cardtypes", method = RequestMethod.GET)
	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCardTypes',#fields)")
	@ResponseBody
	public CardTypeListWsDTO getCardTypes(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CardTypeDataList dataList = new CardTypeDataList();
		dataList.setCardTypes(checkoutFacade.getSupportedCardTypes());
		return dataMapper.map(dataList, CardTypeListWsDTO.class, fields);
	}

	/**
	 * Returns the list of Supported Cities
	 *
	 */
	@RequestMapping(value = "/{baseSiteId}/cities", method = RequestMethod.GET)
	//	@Cacheable(value = "miscsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCardTypes',#fields)")
	@ResponseBody
	public SparCityListWsDTO getCities(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<SparCitiesData> dataList = storeFinderFacadeInterface.getSparCities();
		final SparCityListWsDTO cityDtoList = new SparCityListWsDTO();
		final List<SparCitiesWsDTO> cityDTO = new ArrayList<SparCitiesWsDTO>();
		for (final SparCitiesData data : dataList)
		{
			cityDTO.add(dataMapper.map(data, SparCitiesWsDTO.class, fields));
		}
		cityDtoList.setCityList(cityDTO);
		return cityDtoList;
	}
	
	@RequestMapping(value = "/{baseSiteId}/updateVersion", method = RequestMethod.POST)
	@ResponseBody
	public SparMobileAppVersionWsDTO updateVersion(@RequestParam(required = true) final String versionNumber,
			@RequestParam(required = true) final Boolean forceUpgrade, 
			@RequestParam(required = true) final Boolean recommendUpgrade,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		SparMobileAppVersionWsDTO versionWsDTO =  new SparMobileAppVersionWsDTO();
		if(StringUtils.isEmpty(versionNumber))
		{
			versionWsDTO.setMessage("Version number is mandatory.");
		}
		else
		{
   		versionWsDTO.setVersionNumber(versionNumber);
   		versionWsDTO.setForceUpgrade(forceUpgrade);
   		versionWsDTO.setRecommendUpgrade(recommendUpgrade);
   		sparMobileAppVersionService.updateVersion(versionWsDTO);
   		versionWsDTO.setMessage("Data Saved Successfully.");
		}
		return versionWsDTO;
	}
	
	@RequestMapping(value = "/{baseSiteId}/versionCheck", method = RequestMethod.GET)
	@ResponseBody
	public SparMobileAppVersionWsDTO versionCheck(@RequestParam(required = true) final String appVersion,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		SparMobileAppVersionWsDTO versionWsDTO = new SparMobileAppVersionWsDTO();
			versionWsDTO = sparMobileAppVersionService.versionCheck(appVersion);
			if(null == versionWsDTO)
			{
				versionWsDTO.setMessage("Given Version number doesn't exist.");
			}
		
		return versionWsDTO;
	}
}
