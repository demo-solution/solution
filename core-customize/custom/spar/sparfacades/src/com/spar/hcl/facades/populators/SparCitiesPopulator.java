/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.spar.hcl.core.model.service.delivery.SparCitiesModel;
import com.spar.hcl.facades.service.delivery.data.SparCitiesData;


/**
 * @author tanveers
 *
 */
public class SparCitiesPopulator implements Populator<SparCitiesModel, SparCitiesData>
{
	@Override
	public void populate(final SparCitiesModel source, final SparCitiesData target) throws ConversionException
	{
		if (null != source)
		{
			target.setCity(source.getCity());
			target.setStatus(source.getStatus());
		}
	}
}
