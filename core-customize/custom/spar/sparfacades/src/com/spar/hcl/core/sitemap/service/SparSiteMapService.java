/**
 *
 */
package com.spar.hcl.core.sitemap.service;

import java.util.List;

import com.spar.hcl.deliveryslot.model.SparSiteMapModel;


/**
 * @author jitendriya.m
 *
 */
public interface SparSiteMapService
{
	List<SparSiteMapModel> getAllSiteMapByPageType(String pageType);
}
