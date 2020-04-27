/**
 *
 */
package com.spar.hcl.core.payment.cashondelivery.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.core.model.order.payment.CashOnDeliveryPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * This interface is used to implement COD payment info Strategy
 *
 * @author rohan_c
 *
 */
public interface SparCashOnDeliveryPaymentInfoCreateStrategy
{
	/**
	 * This method is used to create COD Payment INFO
	 *
	 * @param paymentInfo
	 * @param billingAddress
	 * @param customerModel
	 * @param saveInAccount
	 * @return CashOnDeliveryPaymentInfoModel
	 */
	CashOnDeliveryPaymentInfoModel createCreditCardPaymentInfo(final PaymentInfoData paymentInfo,
			final AddressModel billingAddress, final CustomerModel customerModel, final boolean saveInAccount);

	/**
	 * This method is used to save COD Payment Info
	 *
	 * @param customerModel
	 * @param customerInfoData
	 * @param paymentInfoData
	 * @param saveInAccount
	 * @return CashOnDeliveryPaymentInfoModel
	 */
	CashOnDeliveryPaymentInfoModel saveCODSubscription(CustomerModel customerModel, CustomerInfoData customerInfoData,
			PaymentInfoData paymentInfoData, boolean saveInAccount);
}
