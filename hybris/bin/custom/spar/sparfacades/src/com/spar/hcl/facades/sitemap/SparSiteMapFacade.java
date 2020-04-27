/**
 *
 */
package com.spar.hcl.facades.sitemap;

import java.util.List;

import com.spar.hcl.facades.sparsitemap.data.SparSiteMapData;


/**
 * @author jitendriya.m
 *
 */
public interface SparSiteMapFacade
{
	public List<SparSiteMapData> getSiteMapList(String type);
}
