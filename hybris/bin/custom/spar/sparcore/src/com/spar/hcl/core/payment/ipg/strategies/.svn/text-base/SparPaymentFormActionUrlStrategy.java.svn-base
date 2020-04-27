/**
 *
 */
package com.spar.hcl.core.payment.ipg.strategies;

import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;


/**
 * This interface is used to set the IPG Urls
 * 
 * @author rohan_c
 *
 */
public interface SparPaymentFormActionUrlStrategy extends PaymentFormActionUrlStrategy
{
	/**
	 * This method is used to get the IPG Post URL
	 *
	 * @param clientRef
	 * @return String
	 */
	String getIPGRequestUrl(final String clientRef);

	/**
	 * this method is used to get the current IP of the system, which may be used to identify the request
	 *
	 * @return String
	 */
	String getCurrentClientIpAddress();
}
