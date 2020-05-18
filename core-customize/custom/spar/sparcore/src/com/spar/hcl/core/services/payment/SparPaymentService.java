/**
 *
 */
package com.spar.hcl.core.services.payment;

import de.hybris.platform.acceleratorservices.payment.PaymentService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;

import com.spar.hcl.core.payment.cashondelivery.strategies.SparCashOnDeliveryPaymentInfoCreateStrategy;
import com.spar.hcl.core.payment.ipg.strategies.impl.SparIPGPaymentResponseInterpretationStrategyImpl;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;


/**
 * this interface is used to handle request and response for IPG.
 *
 * @author rohan_c
 *
 */
public interface SparPaymentService extends PaymentService
{
	/**
	 * This method is used to begin the IPG transaction by initializing URLs and request parameters.
	 *
	 * @param siteName
	 * @param responseUrl
	 * @param merchantCallbackUrl
	 * @param customer
	 * @param cardInfo
	 * @param paymentAddress
	 * @return PaymentData
	 * @throws IllegalArgumentException
	 */
	PaymentData beginIPGCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress, final CalculatedWalletData calculatedWalletData) throws IllegalArgumentException;

	/**
	 * This method is used to save the COD PI and carry forward the order processing flow.
	 *
	 * @param customerModel
	 * @param saveInAccount
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	PaymentSubscriptionResultItem completeCODCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters, final CartModel cartModel)
			throws IllegalArgumentException;

	/**
	 * This method is used to save the Wallet PI and carry forward the order processing flow.
	 *
	 * @param customerModel
	 * @param saveInAccount
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	PaymentSubscriptionResultItem completeWalletCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException;


	/**
	 * This method is used for saving IPG payment instructions. Post successful flow, order processing is carried out.
	 *
	 * @param customerModel
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	PaymentSubscriptionResultItem completeIPGCreatePaymentSubscription(final CustomerModel customerModel,
			final Map<String, String> parameters, final CartModel cartModel) throws IllegalArgumentException;


	/**
	 * Getter
	 *
	 * @return the codPaymentInfoCreateStrategy
	 */
	SparCashOnDeliveryPaymentInfoCreateStrategy getCodPaymentInfoCreateStrategy();

	/**
	 * Setter
	 *
	 * @param codPaymentInfoCreateStrategy
	 *           the codPaymentInfoCreateStrategy to set
	 */
	void setCodPaymentInfoCreateStrategy(final SparCashOnDeliveryPaymentInfoCreateStrategy codPaymentInfoCreateStrategy);

	/**
	 * Getter
	 *
	 * @return the sparIPGPaymentResponseInterpretationStrategy
	 */
	SparIPGPaymentResponseInterpretationStrategyImpl getSparIPGPaymentResponseInterpretationStrategy();

	/**
	 * Setter
	 *
	 * @param sparIPGPaymentResponseInterpretationStrategy
	 *           the sparIPGPaymentResponseInterpretationStrategy to set
	 */
	void setSparIPGPaymentResponseInterpretationStrategy(
			final SparIPGPaymentResponseInterpretationStrategyImpl sparIPGPaymentResponseInterpretationStrategy);
	
	PaymentSubscriptionResultItem completeLandmarkRewardCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException;
	
	PaymentSubscriptionResultItem completePayTMCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException;
}
