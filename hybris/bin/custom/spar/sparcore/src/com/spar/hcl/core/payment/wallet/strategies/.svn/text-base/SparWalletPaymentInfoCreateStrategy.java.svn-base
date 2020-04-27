/**
 *
 */
package com.spar.hcl.core.payment.wallet.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.core.model.order.payment.WalletPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * This interface is used to implement COD payment info Strategy
 *
 * @author rohan_c
 *
 */
public interface SparWalletPaymentInfoCreateStrategy
{
	/**
	 * This method is used to create Wallet Payment INFO
	 *
	 * @param paymentInfo
	 * @param billingAddress
	 * @param customerModel
	 * @param saveInAccount
	 * @return CashOnDeliveryPaymentInfoModel
	 */
	WalletPaymentInfoModel createCreditCardPaymentInfo(final PaymentInfoData paymentInfo, final AddressModel billingAddress,
			final CustomerModel customerModel, final boolean saveInAccount);

	/**
	 * This method is used to save Wallet Payment Info
	 *
	 * @param customerModel
	 * @param customerInfoData
	 * @param paymentInfoData
	 * @param saveInAccount
	 * @return CashOnDeliveryPaymentInfoModel
	 */
	WalletPaymentInfoModel saveWalletSubscription(CustomerModel customerModel, CustomerInfoData customerInfoData,
			PaymentInfoData paymentInfoData, boolean saveInAccount);
}
