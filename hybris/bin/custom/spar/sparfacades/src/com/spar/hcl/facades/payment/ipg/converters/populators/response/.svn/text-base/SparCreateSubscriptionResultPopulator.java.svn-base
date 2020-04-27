/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;

import java.util.Map;


/**
 * This class is an extension of AbstractResultPopulator to convert response parameters into Subscription object (map)
 *
 * @author rohan_c
 *
 */
public class SparCreateSubscriptionResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	/**
	 * This method is overridden to populate AuthReplyData
	 */
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target)
	{
		//Validate parameters and related data
		validateParameterNotNull(source, "Parameter source (Map<String, String>) cannot be null");
		validateParameterNotNull(target, "Parameter target (CreateSubscriptionResult) cannot be null");

		final Integer reasonCode = getIntegerForString(source.get("fail_rc"));
		target.setDecision(source.get("status"));
		target.setReasonCode(reasonCode == null ? new Integer(0) : reasonCode);
		target.setRequestId(source.get("ipgTransactionId"));
		target.setApproval_code(source.get("approval_code"));
		target.setResponse_hash(source.get("response_hash"));
		target.setTxndatetime(source.get("txndatetime"));
		final AuthReplyData authReplyData = new AuthReplyData();
		authReplyData.setCvnDecision(Boolean.TRUE);
		target.setAuthReplyData(authReplyData);

	}
}
