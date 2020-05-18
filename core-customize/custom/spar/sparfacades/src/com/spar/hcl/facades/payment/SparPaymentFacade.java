/**
 *
 */
package com.spar.hcl.facades.payment;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;

import java.util.Map;

import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;


/**
 * This interface is used to introduce new methods for SPAR requirement for IPG and COD
 *
 * @author rohan_c
 *
 */
public interface SparPaymentFacade extends PaymentFacade
{

	/**
	 * This method is used to begin the IPG transaction by initializing URLs and request parameters
	 *
	 * @param successResponseUrl
	 * @param failureResponseUrl
	 * @return PaymentData
	 */
	PaymentData beginIPGCreateSubscription(final String successResponseUrl, final String failureResponseUrl,
			final CalculatedWalletData calculatedWalletData);

	/**
	 * This method is used to complete the IPG flow. In case the paymentMode is not CreditCard, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @return PaymentSubscriptionResultData
	 */
	PaymentSubscriptionResultData completeIPGCreateSubscription(final Map<String, String> parameters, final CartData cartData);

	/**
	 * This method is used to complete the COD flow. In case the paymentMode is not COD, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @param isCashOnDeliveryPaymentMode
	 * @return PaymentSubscriptionResultData
	 */
	PaymentSubscriptionResultData completeCashOnDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isCashOnDeliveryPaymentMode, final CartData cartData);

	/**
	 * This method is used to complete the Wallet flow. In case the paymentMode is not COD, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @param isWalletApplied
	 * @return PaymentSubscriptionResultData
	 */
	PaymentSubscriptionResultData completeWalletDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isWalletApplied, final CalculatedWalletData calculatedWallet);

	/**
	 * This method is overridden in case of COD payment mode
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @return PaymentSubscriptionResultData
	 */
	PaymentSubscriptionResultData completeCODCreateSubscription(final Map<String, String> parameters, final boolean saveInAccount,
			final CartData cartData);

	/**
	 * This method is overridden in case of Wallet payment mode
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @return PaymentSubscriptionResultData
	 */
	PaymentSubscriptionResultData completeWalletCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final CalculatedWalletData calculatedWallet);

	/**
	 * This method is used to set Calculated attribute in cartModel for COD flow
	 *
	 * @param value
	 */
	void setCalculated(final Boolean value);

	/**
	 * Getter for CartModel
	 *
	 * @return CartModel
	 */
	CartModel getCart();

	/**
	 * This method is used to set Payment mode in cartModel
	 *
	 * @param paymentMode
	 */
	void setPaymentMode(final PaymentModeModel paymentMode, final PaymentStatus status);

	/**
	 * This method is used to set Balance Due in cartData
	 *
	 * @param cartData
	 */
	CartData setBalanceDue(CartData cartData);

	/**
	 * This method is used to set Initial Balance Due in cartData
	 *
	 * @param cartData
	 */

	CartData setInitialBalanceDue(final CartData cartData);

	/**
	 * This method is used to set wallet and balance due after calculation in cartData
	 *
	 * @param cartData
	 * @param customerWalletAmount
	 */
	CalculatedWalletData changeOnWalletApplied(final CartData cartData, final double customerWalletAmount);

	/**
	 * This method is used to set balance due after calculation in cartData
	 *
	 * @param calculatedWalletData
	 * @param cartData
	 */
	void setBalanceDueInCart(final CalculatedWalletData calculatedWalletData, final CartData cartData);

	CalculatedWalletData refreshWallet(final CalculatedWalletData calculatedWalletData);
	
	PaymentSubscriptionResultData completeLandmarkRewardDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isLandmarkApplied, final SparValidateLRAmountResultData calculateLRAmountResultData);
	
	PaymentSubscriptionResultData completePayTMCreateSubscription(final Map<String, String> parameters, final CartData cartData);
}
