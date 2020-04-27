/**
 *
 */
package com.spar.hcl.core.sitemap.dao;


import java.util.List;

import com.spar.hcl.deliveryslot.model.SparSiteMapModel;


/**
 * @author jitendriya.m
 *
 */
public interface SparSiteMapListDAO
{
	List<SparSiteMapModel> getAllSiteMapByPageType(String pageType);
}
