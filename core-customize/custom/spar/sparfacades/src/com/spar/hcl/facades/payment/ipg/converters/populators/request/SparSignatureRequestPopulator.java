/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.spar.hcl.facades.payment.utils.IPGIntegrationUtil;


/**
 * This method is used to create the PaymentData using CreateSubscriptionRequest.
 *
 * @author rohan_c
 *
 */
public class SparSignatureRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	private static final Logger LOG = Logger.getLogger(SparSignatureRequestPopulator.class);
	private IPGIntegrationUtil integrationUtil;

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final SignatureData signatureData = source.getSignatureData();
		Assert.notNull(signatureData, "signatureData cannot be null");

		final Date date = new Date(System.currentTimeMillis());
		final String merchantId = signatureData.getMerchantID();
		final String currency = signatureData.getCurrency();
		final String amount = String.valueOf(signatureData.getAmount());
		final String sharedSecret = signatureData.getSharedSecret();
		validateParameterNotNull(merchantId, "Merchant ID cannot be null");
		validateParameterNotNull(amount, "Amount cannot be null");
		validateParameterNotNull(currency, "Currency cannot be null");
		try
		{
			// To make sure your transactions are processed correctly, insert the signature data in this order:
			// merchantID, amount, currency, date.
			// The transaction will be declined if any of the data in the signature does not match the data in the order.
			getIntegrationUtil().setParameters(merchantId, sharedSecret, amount, currency, date);
			final String calculatedIPGUtilHash = getIntegrationUtil().createHash();
			final String txnDateTime = getIntegrationUtil().getFormattedSysDate();

			addRequestQueryParam(target, "storename", merchantId);
			addRequestQueryParam(target, "chargetotal", amount);
			addRequestQueryParam(target, "currency", currency);
			addRequestQueryParam(target, "hash", calculatedIPGUtilHash);
			addRequestQueryParam(target, "txndatetime", txnDateTime);
			addRequestQueryParam(target, "hash_algorithm", signatureData.getHash_algorithm());
			addRequestQueryParam(target, "orderId", signatureData.getOid());
			addRequestQueryParam(target, "language", signatureData.getLanguage());
			addRequestQueryParam(target, "mode", signatureData.getMode());
			addRequestQueryParam(target, "txntype", signatureData.getTxntype());
			addRequestQueryParam(target, "timezone", signatureData.getTimezone());
		}
		catch (final Exception e)
		{
			LOG.error("Error inserting IPG Order Page signature", e);
			throw new ConversionException("Error inserting IPG Order Page signature", e);
		}
	}

	/**
	 * Getter
	 *
	 * @return the integrationUtil
	 */
	public IPGIntegrationUtil getIntegrationUtil()
	{
		return integrationUtil;
	}

	/**
	 * Setter
	 *
	 * @param integrationUtil
	 *           the integrationUtil to set
	 */
	public void setIntegrationUtil(final IPGIntegrationUtil integrationUtil)
	{
		this.integrationUtil = integrationUtil;
	}

}
