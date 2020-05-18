/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.spar.hcl.deliveryslot.model.SparSiteMapModel;
import com.spar.hcl.facades.sparsitemap.data.SparSiteMapData;


/**
 * @author jitendriya.m
 *
 */
public class SparSiteMapPopulator implements Populator<SparSiteMapModel, SparSiteMapData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SparSiteMapModel source, final SparSiteMapData target) throws ConversionException
	{
		if (null != source)
		{
			target.setCode(source.getCode());
			target.setLocation(source.getLocation());
			target.setChangefreq(source.getChangefreq());
			target.setPriority(source.getPriority());
			target.setPageType(source.getPageType().getCode());
			target.setVisible(source.getVisible());
			target.setModifiedTime(source.getModifiedtime());
		}
	}

}
