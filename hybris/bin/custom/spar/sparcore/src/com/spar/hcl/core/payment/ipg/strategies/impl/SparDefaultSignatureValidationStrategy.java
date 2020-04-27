/**
 *
 */
package com.spar.hcl.core.payment.ipg.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl.DefaultSignatureValidationStrategy;

import com.spar.hcl.core.constants.SparCoreConstants;


/**
 * this class is used to get the shared secret for IPG
 *
 * @author rohan_c
 *
 */
public class SparDefaultSignatureValidationStrategy extends DefaultSignatureValidationStrategy
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl.DefaultSignatureValidationStrategy#
	 * getSharedSecret()
	 */
	@Override
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(SparCoreConstants.IPGProperties.SHARED_SECRET);
	}
}
