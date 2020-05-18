/**
 *
 */
package com.spar.hcl.storefront.interceptors.beforecontroller;

import javax.servlet.http.HttpServletRequest;


/**
 * Interface to process the secure checking in Secure Portal
 * 
 * @author valechar
 *
 */
public interface SecurePortalRequestProcessor
{
	/**
	 * get other request parameters
	 *
	 * @param request
	 * @return request parameters
	 */
	public String getOtherRequestParameters(final HttpServletRequest request);

	/**
	 * whether we want to skip secure check in secureportal
	 */
	public boolean skipSecureCheck();
}
