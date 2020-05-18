/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * This class is used to convert response parameters to PaymentInfo
 *
 * @author rohan_c
 *
 */
public class SparIPGPaymentInfoResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{

	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final PaymentInfoData data = new PaymentInfoData();
		data.setCardAccountNumber(source.get("cardnumber"));
		data.setCardCardType(source.get("ccbrand"));
		data.setCardExpirationMonth(getIntegerForString(source.get("expmonth")));
		data.setCardExpirationYear(getIntegerForString(source.get("expyear")));
		data.setPaymentOption(source.get("paymentMethod"));

		target.setPaymentInfoData(data);
	}

}
