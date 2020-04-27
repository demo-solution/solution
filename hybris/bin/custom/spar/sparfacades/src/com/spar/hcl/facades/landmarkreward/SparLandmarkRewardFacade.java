package com.spar.hcl.facades.landmarkreward;

import de.hybris.platform.commercefacades.user.data.CustomerData;

/**
 * @author ravindra.kr
 *
 */
public interface SparLandmarkRewardFacade
{
	SparLRUserEnrollDataResult enrollMember();
	SparLRUserDetailDataResult getMemberForLMS();
	void disableLRPrompt();
	SparLRRedemptionDataResult redemptionFromLMS(final SparValidateLRAmountResultData currentLRAmountResultData);
	SparValidateLRAmountResultData doHandleLRPayment(final String landmarkRewardAmount);
	void updateUserEnrolledToLRStatus();
	void changeLROptStatus();
	SparValidateLRAmountResultData validateLandmarkRewardPoints(final double enteredLRAmount);
	LRUserEnrollMemberData populateLRCustomerData(final CustomerData customerData);
	void sendSparLandmarkRegistrationEmail();
	LRInputParamForRedemptionFromLMSAPI populateDataForRedemptionFromLMSAPI(final SparValidateLRAmountResultData calculateLRAmountResultData);
}
