/**
 *
 */
package com.spar.hcl.core.payment.ipg.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.strategies.impl.AbstractPaymentResponseInterpretationStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Map;


/**
 * This class is used to interpret the IPG response
 * 
 * @author rohan_c
 *
 */
public class SparIPGPaymentResponseInterpretationStrategyImpl extends AbstractPaymentResponseInterpretationStrategy
{


	private Converter<Map<String, String>, CreateSubscriptionResult> sparCreateSubscriptionResultConverter;

	/**
	 * This method is used to invoke respective converters and populators for IPG flow.
	 */
	@Override
	public CreateSubscriptionResult interpretResponse(final Map<String, String> responseParams, final String clientRef,
			final Map<String, PaymentErrorField> errors)
	{
		return getSparCreateSubscriptionResultConverter().convert(responseParams);
	}

	/**
	 * Getter for converter
	 *
	 * @return the sparCreateSubscriptionResultConverter
	 */
	public Converter<Map<String, String>, CreateSubscriptionResult> getSparCreateSubscriptionResultConverter()
	{
		return sparCreateSubscriptionResultConverter;
	}

	/**
	 * Setter for converter
	 *
	 * @param sparCreateSubscriptionResultConverter
	 *           the sparCreateSubscriptionResultConverter to set
	 */
	public void setSparCreateSubscriptionResultConverter(
			final Converter<Map<String, String>, CreateSubscriptionResult> sparCreateSubscriptionResultConverter)
	{
		this.sparCreateSubscriptionResultConverter = sparCreateSubscriptionResultConverter;
	}
}
