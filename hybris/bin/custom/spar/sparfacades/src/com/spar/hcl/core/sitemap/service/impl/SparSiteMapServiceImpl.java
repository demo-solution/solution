/**
 *
 */
package com.spar.hcl.core.sitemap.service.impl;

import java.util.List;

import com.spar.hcl.core.sitemap.dao.SparSiteMapListDAO;
import com.spar.hcl.core.sitemap.service.SparSiteMapService;
import com.spar.hcl.deliveryslot.model.SparSiteMapModel;


/**
 * @author jitendriya.m
 *
 */
public class SparSiteMapServiceImpl implements SparSiteMapService
{

	private SparSiteMapListDAO sparSiteMapListDao;



	/**
	 * @return the sparSiteMapListDao
	 */
	public SparSiteMapListDAO getSparSiteMapListDao()
	{
		return sparSiteMapListDao;
	}



	/**
	 * @param sparSiteMapListDao
	 *           the sparSiteMapListDao to set
	 */
	public void setSparSiteMapListDao(final SparSiteMapListDAO sparSiteMapListDao)
	{
		this.sparSiteMapListDao = sparSiteMapListDao;
	}



	@Override
	public List<SparSiteMapModel> getAllSiteMapByPageType(final String pageType)
	{
		return sparSiteMapListDao.getAllSiteMapByPageType(pageType);
	}

}
