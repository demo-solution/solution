/**
 *
 */
package com.spar.hcl.facades.payment.ipg.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import java.util.Map;

import javax.annotation.Resource;

import com.spar.hcl.core.constants.SparCoreConstants;


/**
 * This class is used convert response paramters to Subscriptio Info
 *
 * @author rohan_c
 *
 */
public class SparSubscriptionInfoResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Resource(name = "acceleratorDigestUtils")
	private AcceleratorDigestUtils digestUtils;
	private static final String SHARED_SECRET = Config.getString(SparCoreConstants.IPGProperties.SHARED_SECRET, "ygAkMSvUb9");

	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SubscriptionInfoData data = new SubscriptionInfoData();
		final String subscriptionId = source.get("oid");
		data.setSubscriptionID(subscriptionId);
		target.setSubscriptionInfoData(data);
	}

	protected String getSubscriptionPublicDigest(final String customValues)
	{
		String result;
		try
		{
			result = digestUtils.getPublicDigest(customValues, SHARED_SECRET);
		}
		catch (final Exception e)
		{
			result = "BzW+Xn0ZgZHeQRcFB6ri";
		}

		return result;
	}
}
