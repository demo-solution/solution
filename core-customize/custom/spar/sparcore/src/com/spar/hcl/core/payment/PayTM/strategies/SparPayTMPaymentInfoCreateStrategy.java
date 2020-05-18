package com.spar.hcl.core.payment.PayTM.strategies;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.core.model.order.payment.PayTMPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * This interface is used to implement PayTM payment info Strategy
 *
 * @author ravindra.kr
 *
 */
public interface SparPayTMPaymentInfoCreateStrategy
{
	PayTMPaymentInfoModel createPayTMPaymentInfo(final PaymentInfoData paymentInfo, final AddressModel billingAddress,
			final CustomerModel customerModel, final boolean saveInAccount);

	PayTMPaymentInfoModel savePayTMSubscription(CustomerModel customerModel, CustomerInfoData customerInfoData,
			PaymentInfoData paymentInfoData, boolean saveInAccount);

}
