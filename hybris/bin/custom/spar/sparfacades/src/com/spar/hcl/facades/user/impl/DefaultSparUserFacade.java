/**
 *
 */
package com.spar.hcl.facades.user.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 *
 */
public class DefaultSparUserFacade extends DefaultUserFacade
{

	private Converter<AddressData, AddressModel> addressReverseConverter;
	private Populator<AddressData, AddressModel> sparAddressReversePopulator;


	@Override
	public void addAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);

		final CustomerModel currentCustomer = getCurrentUserForCheckout();

		final boolean makeThisAddressTheDefault = addressData.isDefaultAddress()
				|| (currentCustomer.getDefaultShipmentAddress() == null && addressData.isVisibleInAddressBook());

		// Create the new address model
		final AddressModel newAddress = getModelService().create(AddressModel.class);
		//getSparAddressReversePopulator().populate(addressData, newAddress);
		getSparAddressReversePopulator().populate(addressData, newAddress);
		//newAddress = addressReverseConverter.convert(addressData);
		// Store the address against the user
		getCustomerAccountService().saveAddressEntry(currentCustomer, newAddress);

		// Update the address ID in the newly created address
		addressData.setId(newAddress.getPk().toString());

		if (makeThisAddressTheDefault)
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, newAddress);
		}

	}


	/**
	 * @return the sparAddressReversePopulator
	 */
	public Populator<AddressData, AddressModel> getSparAddressReversePopulator()
	{
		return sparAddressReversePopulator;
	}


	/**
	 * @param sparAddressReversePopulator
	 *           the sparAddressReversePopulator to set
	 */
	public void setSparAddressReversePopulator(final Populator<AddressData, AddressModel> sparAddressReversePopulator)
	{
		this.sparAddressReversePopulator = sparAddressReversePopulator;
	}


	/**
	 * @param addressReverseConverter
	 *           the addressReverseConverter to set
	 */
	public void setAddressReverseConverter(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}

}
