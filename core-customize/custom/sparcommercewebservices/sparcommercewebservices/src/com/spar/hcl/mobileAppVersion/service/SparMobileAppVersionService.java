package com.spar.hcl.mobileAppVersion.service;

import com.spar.hcl.dto.SparMobileAppVersionWsDTO;

/**
 * @author ravindra.kr
 *
 */
public interface SparMobileAppVersionService
{
	public void updateVersion(final SparMobileAppVersionWsDTO versionWsDTO);

	public SparMobileAppVersionWsDTO versionCheck(final String appVersion);
}
