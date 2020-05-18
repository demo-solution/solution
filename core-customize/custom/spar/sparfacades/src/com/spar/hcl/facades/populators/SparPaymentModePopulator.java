/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.spar.hcl.facades.order.data.PaymentModeData;


/**
 * This class is used to populate PaymentModeData using PaymentModeModel
 * 
 * @author rohan_c
 *
 */
public class SparPaymentModePopulator implements Populator<PaymentModeModel, PaymentModeData>
{

	@Override
	public void populate(final PaymentModeModel source, final PaymentModeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
	}



}
