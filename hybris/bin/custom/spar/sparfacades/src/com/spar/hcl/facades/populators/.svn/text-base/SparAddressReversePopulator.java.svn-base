/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;



/**
 * @author tanveers
 *
 */
public class SparAddressReversePopulator extends AddressReversePopulator
{

	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		super.populate(addressData, addressModel);
		addressModel.setEmail(addressData.getEmail());
		addressModel.setDateOfBirth(addressData.getDateOfBirth());
		addressModel.setDistrict(addressData.getArea());
		addressModel.setBuilding(addressData.getBuildingName());
		addressModel.setAppartment(addressData.getLandmark());
		addressModel.setLongAddress(addressData.getLongAddress());
		addressModel.setMappedStore(addressData.getMappedStore());
		/*
		 * addressModel.setDefaultStore(addressData.getDefaultStore());
		 * addressModel.setDefaultCncCenter(addressData.getDefaultCncCenter()); if (null !=
		 * addressData.getSparServiceArea()) { final SparServiceAreaModel sparServiceAreaModel = new
		 * SparServiceAreaModel(); sparServiceAreaModel.setAreaId(addressData.getSparServiceArea().getAreaId());
		 * addressModel.setSparServiceArea(getFlexibleSearchService().getModelByExample(sparServiceAreaModel)); }
		 */
	}
}
