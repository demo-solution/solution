/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * This method is used to convert the IPG response to SignatureResult
 * 
 * @author rohan_c
 *
 */
public class SparSignatureResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SignatureData data = new SignatureData();
		data.setAmount(getBigDecimalForString(source.get("chargetotal")));
		data.setCurrency(source.get("currency"));
		data.setMerchantID(source.get("merchantID"));

		target.setSignatureData(data);
	}

}
