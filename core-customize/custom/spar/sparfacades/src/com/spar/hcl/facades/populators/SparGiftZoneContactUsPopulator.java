/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.spar.hcl.core.model.service.giftzone.SparGiftZoneContactUsModel;
import com.spar.hcl.facades.service.delivery.data.SparGiftZoneContactUsData;


/**
 * @author pavan.sr
 *
 */
public class SparGiftZoneContactUsPopulator implements Populator<SparGiftZoneContactUsData, SparGiftZoneContactUsModel>
{
	@Override
	public void populate(final SparGiftZoneContactUsData source, final SparGiftZoneContactUsModel target)
			throws ConversionException
	{
		if (null != source)
		{
			target.setCompanyName(source.getCompanyName());
			target.setCustomerCity(source.getCustomerCity());
			target.setCustomerContact(source.getCustomerContact());
			target.setCustomerEmail(source.getCustomerEmail());
			target.setCustomerName(source.getCustomerName());
			target.setCustomerRemarks(source.getCustomerRemarks());

		}
	}
}
