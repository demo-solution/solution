/**
 *
 */
package com.spar.hcl.facades.sitemap.impl;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import javax.annotation.Resource;

import com.spar.hcl.core.sitemap.service.SparSiteMapService;
import com.spar.hcl.deliveryslot.model.SparSiteMapModel;
import com.spar.hcl.facades.sitemap.SparSiteMapFacade;
import com.spar.hcl.facades.sparsitemap.data.SparSiteMapData;


/**
 * @author jitendriya.m
 *
 */
public class SparSiteMapFacadeImpl implements SparSiteMapFacade
{

	private Converter<SparSiteMapModel, SparSiteMapData> sparSiteMapConverter;

	@Resource(name = "sparSiteMapListService")
	private SparSiteMapService sparSiteMapListService;


	/**
	 * @return the sparSiteMapConverter
	 */
	public Converter<SparSiteMapModel, SparSiteMapData> getSparSiteMapConverter()
	{
		return sparSiteMapConverter;
	}

	/**
	 * @param sparSiteMapConverter
	 *           the sparSiteMapConverter to set
	 */
	public void setSparSiteMapConverter(final Converter<SparSiteMapModel, SparSiteMapData> sparSiteMapConverter)
	{
		this.sparSiteMapConverter = sparSiteMapConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.facades.sitemap.SparSiteMapFacade#getSiteMapList(java.lang.String)
	 */
	@Override
	public List<SparSiteMapData> getSiteMapList(final String type)
	{
		final List<SparSiteMapModel> allSiteMapModelList = sparSiteMapListService.getAllSiteMapByPageType(type);

		return Converters.convertAll(allSiteMapModelList, getSparSiteMapConverter());
	}

}
