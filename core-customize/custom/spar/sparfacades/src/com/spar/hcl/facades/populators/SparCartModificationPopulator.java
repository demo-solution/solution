/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.CartModificationPopulator;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;


/**
 * This populator have customization related to BOGO.
 * @author rohan_c
 *
 */
public class SparCartModificationPopulator extends CartModificationPopulator
{
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.CartModificationPopulator#populate(de.hybris.platform
	 * .commerceservices.order.CommerceCartModification,
	 * de.hybris.platform.commercefacades.order.data.CartModificationData)
	 */
	@Override
	public void populate(final CommerceCartModification source, final CartModificationData target)
	{
		// YTODO Auto-generated method stub
		super.populate(source, target);
		target.setBogoAddOrUpdateStatus(source.getBogoAddOrUpdateStatus());
	}
}
