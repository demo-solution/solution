/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author tanveers
 */
public class SparAddressPopulator extends AddressPopulator
{
	//private Converter<SparServiceAreaModel, SparServiceAreaData> serviceAreaConverter;

	@Override
	public void populate(final AddressModel source, final AddressData target) throws ConversionException
	{
		super.populate(source, target);
		target.setEmail(source.getEmail());
		target.setDateOfBirth(source.getDateOfBirth());
		target.setArea(source.getDistrict());
		target.setBuildingName(source.getBuilding());
		target.setLandmark(source.getAppartment());
		target.setLongAddress(source.getLongAddress());
		target.setMappedStore(source.getMappedStore());

		/*
		 * target.setDefaultStore(source.getDefaultStore()); target.setDefaultCncCenter(source.getDefaultCncCenter()); if
		 * (null != source.getSparServiceArea()) { final SparServiceAreaData sparServiceAreaData =
		 * getServiceAreaConverter().convert(source.getSparServiceArea()); target.setSparServiceArea(sparServiceAreaData);
		 * }
		 */
	}

	/**
	 * @return the serviceAreaConverter
	 */
	/*
	 * public Converter<SparServiceAreaModel, SparServiceAreaData> getServiceAreaConverter() { return
	 * serviceAreaConverter; }
	 *//**
	 * @param serviceAreaConverter
	 *           the serviceAreaConverter to set
	 */
	/*
	 * public void setServiceAreaConverter(final Converter<SparServiceAreaModel, SparServiceAreaData>
	 * serviceAreaConverter) { this.serviceAreaConverter = serviceAreaConverter; }
	 */
}
