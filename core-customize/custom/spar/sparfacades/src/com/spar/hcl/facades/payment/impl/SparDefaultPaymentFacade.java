/**
 *
 */
package com.spar.hcl.facades.payment.impl;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorfacades.payment.impl.DefaultPaymentFacade;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.services.payment.SparPaymentService;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.payment.SparPaymentFacade;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;


/**
 * This class extends DefaultPaymentFacade to handle Payment method COD & IPG.
 *
 */
public class SparDefaultPaymentFacade extends DefaultPaymentFacade implements SparPaymentFacade
{
	@Autowired
	private CartFacade cartFacade;

	@Autowired
	private CartService cartService;

	@Autowired
	private ModelService modelService;
	@Autowired
	private PriceDataFactory priceDataFactory;
	@Autowired
	private CommonI18NService commonI18NService;
	@Autowired
	private SparCustomerFacade sparCustomerFacade;
	@Resource(name = "sessionService")
	SessionService sessionService;

	/**
	 * This method is used to complete the COD flow. In case the paymentMode is not COD, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @param isCashOnDeliveryPaymentMode
	 * @return PaymentSubscriptionResultData
	 */
	@Override
	public PaymentSubscriptionResultData completeCashOnDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isCashOnDeliveryPaymentMode, final CartData cartData)
	{
		if (isCashOnDeliveryPaymentMode)
		{
			return completeCODCreateSubscription(parameters, saveInAccount, cartData);
		}
		else
		{
			return super.completeSopCreateSubscription(parameters, saveInAccount);
		}

	}


	/**
	 * This method is used to complete the Wallet flow. In case the paymentMode is not COD, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @param isWalletApplied
	 * @return PaymentSubscriptionResultData
	 */
	@Override
	public PaymentSubscriptionResultData completeWalletDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isWalletApplied, final CalculatedWalletData calculatedWallet)
	{
		if (isWalletApplied)
		{
			return completeWalletCreateSubscription(parameters, saveInAccount, calculatedWallet);
		}
		else
		{
			return super.completeSopCreateSubscription(parameters, saveInAccount);
		}

	}

	@Override
	public PaymentSubscriptionResultData completeLandmarkRewardDeliveryCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final boolean isLandmarkApplied, final SparValidateLRAmountResultData calculateLRAmountResultData)
	{
		if (isLandmarkApplied)
		{
			return completeLandmarkrewardCreateSubscription(parameters, saveInAccount, calculateLRAmountResultData);
		}
		else
		{
			return super.completeSopCreateSubscription(parameters, saveInAccount);
		}

	}

	
	/**
	 * This method is used to begin the IPG transaction by initializing URLs and request parameters
	 *
	 * @param successResponseUrl
	 * @param failureResponseUrl
	 * @return PaymentData
	 */
	@Override
	public PaymentData beginIPGCreateSubscription(final String successResponseUrl, final String failureResponseUrl,
			final CalculatedWalletData calculatedWalletData)
	{
		final String fullSuccessResponseUrl = getFullResponseUrl(successResponseUrl, true);
		final String fullFailureResponseUrl = getFullResponseUrl(failureResponseUrl, true);
		final String siteName = getCurrentSiteName();

		final CustomerModel customerModel = getCurrentUserForCheckout();
		final AddressModel paymentAddress = getDefaultPaymentAddress(customerModel);
		final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
		return paymentService.beginIPGCreatePaymentSubscription(siteName, fullSuccessResponseUrl, fullFailureResponseUrl,
				customerModel, null, paymentAddress, calculatedWalletData);
	}

	/**
	 * This method is used to complete the IPG flow. In case the paymentMode is not CreditCard, OOTB flow is invoked.
	 *
	 * @param parameters
	 * @return PaymentSubscriptionResultData
	 */
	@Override
	public PaymentSubscriptionResultData completeIPGCreateSubscription(final Map<String, String> parameters,
			final CartData cartData)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
		final CartModel cartModel = this.getCart();
		cartModel.setBalanceDue(Double.valueOf(cartData.getBalanceDue().getValue().doubleValue()));
		if(null != cartData.getPaidByLandmarkReward() && cartData.getPaidByLandmarkReward().doubleValue() > 0)
		{
			cartModel.setPaidByLandmarkReward(cartData.getPaidByLandmarkReward());
		}
		final CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService
				.getAttribute("calculatedWalletData");
		if (null != calculatedWalletData)
		{
			if (null != calculatedWalletData.getOrderWalletAmount())
			{
				cartModel.setPaidByWallet(Double.valueOf(cartData.getTotalPrice().getValue().doubleValue()
						- cartData.getBalanceDue().getValue().doubleValue()));
			}
		}
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = paymentService.completeIPGCreatePaymentSubscription(
				customerModel, parameters, cartModel);
		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}

	/**
	 * This method is overridden in case of COD payment mode
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @return PaymentSubscriptionResultData
	 */
	@Override
	public PaymentSubscriptionResultData completeCODCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final CartData cartData)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
		final CartModel cartModel = this.getCart();
		cartModel.setBalanceDue(Double.valueOf(cartData.getBalanceDue().getValue().doubleValue()));
		
		// need to check if wallet is enabled then only it should be executed
		/*cartModel.setPaidByWallet(Double.valueOf(cartData.getTotalPrice().getValue().doubleValue()
				- cartData.getBalanceDue().getValue().doubleValue()));*/
		if(null != cartData.getPaidByLandmarkReward())
		{
   		cartModel.setPaidByLandmarkReward(cartData.getPaidByLandmarkReward());
		}
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = paymentService.completeCODCreatePaymentSubscription(
				customerModel, saveInAccount, parameters, cartModel);
		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}

	/**
	 * This method is overridden in case of wallet payment mode
	 *
	 * @param parameters
	 * @param saveInAccount
	 * @return PaymentSubscriptionResultData
	 */
	@Override
	public PaymentSubscriptionResultData completeWalletCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final CalculatedWalletData calculatedWallet)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
		customerModel.setTotalWalletAmount(Double.valueOf(calculatedWallet.getCustomerWalletTotal().getValue().doubleValue()));
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = paymentService.completeWalletCreatePaymentSubscription(
				customerModel, saveInAccount, parameters);
		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}
	
	public PaymentSubscriptionResultData completeLandmarkrewardCreateSubscription(final Map<String, String> parameters,
			final boolean saveInAccount, final SparValidateLRAmountResultData calculateLRAmountResultData)
	{
		final CustomerModel customerModel = getCurrentUserForCheckout();
		final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
		final PaymentSubscriptionResultItem paymentSubscriptionResultItem = paymentService.completeLandmarkRewardCreatePaymentSubscription(
				customerModel, saveInAccount, parameters);
		if (paymentSubscriptionResultItem != null)
		{
			return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
		}

		return null;
	}

	/**
	 * This method is used to set Payment mode in cartModel
	 *
	 * @param paymentMode
	 */
	@Override
	public void setPaymentMode(final PaymentModeModel paymentMode, final PaymentStatus status)
	{
		if (getCart() != null)
		{
			this.getCart().setPaymentMode(paymentMode);
			this.getCart().setPaymentStatus(status);
			modelService.save(this.getCart());
		}
	}

	/**
	 * This method is used to set Calculated attribute in cartModel for COD flow
	 *
	 * @param value
	 */
	@Override
	public void setCalculated(final Boolean value)
	{

		if (getCart() != null)
		{
			this.getCart().setCalculated(value);
			modelService.save(this.getCart());
		}

	}

	/**
	 * Getter for CartModel
	 *
	 * @return CartModel
	 */
	@Override
	public CartModel getCart()
	{
		if (cartFacade.hasSessionCart())
		{
			return cartService.getSessionCart();
		}

		return null;
	}

	/**
	 * To set balance in cartData
	 *
	 * @return CartData
	 */
	@Override
	public CartData setBalanceDue(final CartData cartData)
	{
		if (getCart() != null)
		{
			final CartModel cartModel = this.getCart();
			if (null != cartModel.getTotalPrice() && null != cartModel.getPaidByWallet())
			{
				cartModel.setBalanceDue(Double.valueOf(cartModel.getTotalPrice().doubleValue()
						- cartModel.getPaidByWallet().doubleValue()));
				modelService.save(cartModel);
				modelService.refresh(cartModel);
				final PriceData balanceDue = priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(cartModel.getBalanceDue().doubleValue()), commonI18NService.getCurrentCurrency());
				cartData.setBalanceDue(balanceDue);
			}
			else
			{
				cartModel.setBalanceDue(cartModel.getTotalPrice());
				modelService.save(cartModel);
				final PriceData balanceDue = priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(cartModel.getTotalPrice().doubleValue()), commonI18NService.getCurrentCurrency());
				cartData.setBalanceDue(balanceDue);
			}
		}
		return cartData;
	}

	/**
	 * To calculate wallet and balance due for customer
	 *
	 * @param cartData
	 * @param customerWalletAmount
	 * @return CalculatedWalletData
	 */
	@Override
	public CalculatedWalletData changeOnWalletApplied(final CartData cartData, final double customerWalletAmount)
	{
		final CalculatedWalletData calculatedWalletData = new CalculatedWalletData();
		final CustomerData customerData = sparCustomerFacade.getCurrentCustomerWallet();

		PriceData balanceDue = priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(cartData.getBalanceDue().getValue().doubleValue()), commonI18NService.getCurrentCurrency());

		PriceData updatedCustomerWallet = priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(customerData.getPaidByWallet().getValue().doubleValue()), commonI18NService.getCurrentCurrency());

		//		PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0d),
		//				commonI18NService.getCurrentCurrency());

		if (customerWalletAmount > (0.0d) && customerWalletAmount <= customerData.getPaidByWallet().getValue().doubleValue())
		{

			if (customerWalletAmount >= cartData.getBalanceDue().getValue().doubleValue())
			{
				balanceDue = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0d),
						commonI18NService.getCurrentCurrency());
				updatedCustomerWallet = priceDataFactory.create(
						PriceDataType.BUY,
						BigDecimal.valueOf(customerData.getPaidByWallet().getValue().doubleValue()
								- cartData.getBalanceDue().getValue().doubleValue()), commonI18NService.getCurrentCurrency());
				//				paidByWallet = priceDataFactory.create(PriceDataType.BUY,
				//						BigDecimal.valueOf(cartData.getBalanceDue().getValue().doubleValue()), commonI18NService.getCurrentCurrency());
			}
			else if (customerWalletAmount < cartData.getBalanceDue().getValue().doubleValue())
			{
				balanceDue = priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(cartData.getBalanceDue().getValue().doubleValue() - customerWalletAmount),
						commonI18NService.getCurrentCurrency());
				updatedCustomerWallet = priceDataFactory.create(PriceDataType.BUY,
						BigDecimal.valueOf(customerData.getPaidByWallet().getValue().doubleValue() - customerWalletAmount),
						commonI18NService.getCurrentCurrency());
				//				paidByWallet = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(customerWalletAmount),
				//						commonI18NService.getCurrentCurrency());
			}
		}
		//calculatedWalletData.setPaidByWalletAmount(paidByWallet);
		calculatedWalletData.setCustomerWalletTotal(updatedCustomerWallet);
		calculatedWalletData.setOrderWalletAmount(balanceDue);
		return calculatedWalletData;
	}

	/**
	 * To set initial balance due
	 *
	 * @return cartData
	 */
	@Override
	public CartData setInitialBalanceDue(final CartData cartData)
	{
		if (getCart() != null)
		{
			final CartModel cartModel = this.getCart();
			//final PriceData balanceDue = new PriceData();

			cartModel.setBalanceDue(cartModel.getTotalPrice());
			modelService.save(cartModel);
			final PriceData balanceDue = priceDataFactory.create(PriceDataType.BUY,
					BigDecimal.valueOf(cartModel.getTotalPrice().doubleValue()), commonI18NService.getCurrentCurrency());

			// CartData cartData = cartFacade.getSessionCart();
			cartData.setBalanceDue(balanceDue);
		}
		return cartData;
	}

	/**
	 * To set initial balance due
	 *
	 */
	@Override
	public void setBalanceDueInCart(final CalculatedWalletData calculatedWalletData, final CartData cartData)
	{
		final PriceData balanceDue = priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(cartData.getBalanceDue().getValue().doubleValue() - calculatedWalletData.getOrderWalletAmount().getValue().doubleValue()),
				commonI18NService.getCurrentCurrency());
		cartData.setBalanceDue(balanceDue);
	}

	@Override
	public CalculatedWalletData refreshWallet(final CalculatedWalletData calculatedWalletData)
	{

		final CustomerData customerData = sparCustomerFacade.getCurrentCustomerWallet();
		final PriceData updatedCustomerWallet = priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(customerData.getPaidByWallet().getValue().doubleValue()), commonI18NService.getCurrentCurrency());

		//		final PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0d),
		//				commonI18NService.getCurrentCurrency());
		//calculatedWalletData.setPaidByWalletAmount(paidByWallet);
		calculatedWalletData.setCustomerWalletTotal(updatedCustomerWallet);
		//calculatedWalletData.setOrderWalletAmount(balanceDue);
		return calculatedWalletData;
	}
	
	/*@Override
	public SparValidateLRAmountResultData refreshLandmarkReward(final SparValidateLRAmountResultData calculateLRAmountResultData)
	{

		final CustomerData customerData = sparCustomerFacade.getCurrentCustomerWallet();
		final PriceData updatedCustomerWallet = priceDataFactory.create(PriceDataType.BUY,
				BigDecimal.valueOf(customerData.getPaidByWallet().getValue().doubleValue()), commonI18NService.getCurrentCurrency());

		//		final PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0d),
		//				commonI18NService.getCurrentCurrency());
		//calculatedWalletData.setPaidByWalletAmount(paidByWallet);
		calculatedWalletData.setCustomerWalletTotal(updatedCustomerWallet);
		//calculatedWalletData.setOrderWalletAmount(balanceDue);
		return calculatedWalletData;
	}
*/
	
	@Override
	public PaymentSubscriptionResultData completePayTMCreateSubscription(final Map<String, String> parameters,
			final CartData cartData)
	{
			final CustomerModel customerModel = getCurrentUserForCheckout();
			final SparPaymentService paymentService = ((SparPaymentService) getPaymentService());
			final PaymentSubscriptionResultItem paymentSubscriptionResultItem = paymentService.completePayTMCreatePaymentSubscription(
					customerModel, false, parameters);
			if (paymentSubscriptionResultItem != null)
			{
				return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);
			}
			return null;
	}
}
