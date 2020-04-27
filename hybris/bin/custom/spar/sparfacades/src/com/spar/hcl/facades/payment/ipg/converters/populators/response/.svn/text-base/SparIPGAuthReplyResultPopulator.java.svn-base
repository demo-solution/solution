/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * This class is used to convert response parameters to AuthReplyData
 *
 * @author rohan_c
 *
 */
public class SparIPGAuthReplyResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final AuthReplyData data = new AuthReplyData();
		final Integer reasonCode = getIntegerForString(source.get("fail_rc"));
		data.setCcAuthReplyAmount(getBigDecimalForString(source.get("chargetotal")));
		//As per IPG, ipgTransactionId is the authcode
		data.setCcAuthReplyAuthorizationCode(source.get("ipgTransactionId"));
		// As per IPG this is the IST date and time.
		data.setCcAuthReplyAuthorizedDateTime(source.get("txndatetime"));
		data.setCcAuthReplyProcessorResponse(source.get("processor_response_code"));
		data.setCcAuthReplyCvCode(source.get("approval_code"));
		data.setCcAuthReplyReasonCode(reasonCode == null ? new Integer(0) : reasonCode);
		data.setCvnDecision(Boolean.valueOf(StringUtils.equalsIgnoreCase(source.get("status"), "APPROVED")));

		target.setAuthReplyData(data);
	}

}
