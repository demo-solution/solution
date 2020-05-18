/**
 *
 */
package com.spar.hcl.facades.customer;



/**
 * @author tanveers
 *
 */
public interface SparNeedHelpFacade
{
	void sendNeedHelpFormValue(final String selectedNeedHelpOption, final String customerName, final String customerContact,
			final String customerDesiredTime, final String customerRemarks);
}
