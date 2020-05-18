/**
 *
 */
package com.spar.hcl.core.payment.ipg.strategies;

import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * This interface is used to implement IPG payment info Strategy
 *
 * @author rohan_c
 *
 */
public interface SparIPGPaymentInfoCreateStrategy
{
	/**
	 * This method is used to create IPG patment info
	 *
	 * @param subscriptionInfo
	 * @param paymentInfo
	 * @param customerModel
	 * @param saveInAccount
	 * @return CreditCardPaymentInfoModel
	 */
	CreditCardPaymentInfoModel createIPGCreditCardPaymentInfo(SubscriptionInfoData subscriptionInfo,
			final PaymentInfoData paymentInfo, final CustomerModel customerModel, final boolean saveInAccount);

	/**
	 * This method is used to save the response from IPG as part if subscription.
	 *
	 * @param customerModel
	 * @param subscriptionInfo
	 * @param paymentInfoData
	 * @param saveInAccount
	 * @return CreditCardPaymentInfoModel
	 */
	CreditCardPaymentInfoModel saveIPGSubscription(CustomerModel customerModel, SubscriptionInfoData subscriptionInfo,
			PaymentInfoData paymentInfoData, boolean saveInAccount);
}
