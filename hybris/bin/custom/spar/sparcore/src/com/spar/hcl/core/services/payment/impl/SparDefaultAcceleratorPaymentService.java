/**
 *
 */
package com.spar.hcl.core.services.payment.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.impl.DefaultAcceleratorPaymentService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CashOnDeliveryPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.LandmarkPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PayTMPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WalletPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.spar.hcl.core.constants.SparCoreConstants.IPGProperties;
import com.spar.hcl.core.enums.SparIPGDecisionEnum;
import com.spar.hcl.core.model.payment.ipg.strategies.SparFailedOnlineTransactionsModel;
import com.spar.hcl.core.payment.PayTM.strategies.SparPayTMPaymentInfoCreateStrategy;
import com.spar.hcl.core.payment.cashondelivery.strategies.SparCashOnDeliveryPaymentInfoCreateStrategy;
import com.spar.hcl.core.payment.ipg.strategies.SparPaymentFormActionUrlStrategy;
import com.spar.hcl.core.payment.ipg.strategies.impl.SparIPGPaymentInfoCreateStrategyImpl;
import com.spar.hcl.core.payment.ipg.strategies.impl.SparIPGPaymentResponseInterpretationStrategyImpl;
import com.spar.hcl.core.payment.landmarkreward.strategies.SparLandmarkRewardPaymentInfoCreateStrategy;
import com.spar.hcl.core.payment.wallet.strategies.SparWalletPaymentInfoCreateStrategy;
import com.spar.hcl.core.services.payment.SparPaymentService;
import com.spar.hcl.core.util.SparIPGResponseHashGenerationUtilService;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;

/**
 * This class extends the OOTB DefaultAcceleratorPaymentService to handle new Payment mode (COD) & Credit Card (IPG)
 *
 * @author rohan_c
 *
 */
public class SparDefaultAcceleratorPaymentService extends DefaultAcceleratorPaymentService implements SparPaymentService
{
	private static final Logger LOG = Logger.getLogger(SparDefaultAcceleratorPaymentService.class);
	private SparCashOnDeliveryPaymentInfoCreateStrategy codPaymentInfoCreateStrategy;
	private SparWalletPaymentInfoCreateStrategy walletPaymentInfoCreateStrategy;
	private SparLandmarkRewardPaymentInfoCreateStrategy landmarkRewardPaymentInfoCreateStrategy;
	private SparIPGPaymentResponseInterpretationStrategyImpl sparIPGPaymentResponseInterpretationStrategy;
	private SparPayTMPaymentInfoCreateStrategy sparPayTMPaymentInfoCreateStrategy;
	private CommerceCheckoutService commerceCheckoutService;
	@Resource(name = "sparIPGResponseHashGenerationUtilService")
	private SparIPGResponseHashGenerationUtilService sparIPGResponseHashGenerationUtilService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource(name = "sessionService")
	SessionService sessionService;

	/**
	 * This method is used to complete the COD mode.
	 *
	 * @param customerModel
	 * @param saveInAccount
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	@Override
	public PaymentSubscriptionResultItem completeCODCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters, final CartModel cartModel)
			throws IllegalArgumentException
	{
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		paymentSubscriptionResult.setSuccess(true);
		paymentSubscriptionResult.setDecision(DecisionsEnum.ACCEPT.name());
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
			Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");

			if (!cartModel.getBalanceDue().equals(cartModel.getTotalPrice()))
			{
				if(null != customerModel && null != customerModel.getTotalWalletAmount() && null != cartModel.getPaidByWallet())
				{
					customerModel.setTotalWalletAmount(Double.valueOf(customerModel.getTotalWalletAmount().doubleValue()
						- cartModel.getPaidByWallet().doubleValue()));
				}
				getModelService().saveAll(cartModel, customerModel);
				final PaymentTransactionEntryModel walletTransactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final WalletPaymentInfoModel walletPaymentInfoModel = getWalletPaymentInfoCreateStrategy().saveWalletSubscription(
						customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);
				saveMultiPaymentTransaction(walletPaymentInfoModel, walletTransactionEntryModel, null, cartModel);
				final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final CashOnDeliveryPaymentInfoModel codPaymentInfoModel = getCodPaymentInfoCreateStrategy().saveCODSubscription(
						customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);

				saveMultiPaymentTransaction(codPaymentInfoModel, transactionEntryModel, null, cartModel);
				getModelService().save(codPaymentInfoModel);
				getModelService().save(walletPaymentInfoModel);
			}
			else
			{
				final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final CashOnDeliveryPaymentInfoModel codPaymentInfoModel = getCodPaymentInfoCreateStrategy().saveCODSubscription(
						customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);

				savePaymentTransaction(codPaymentInfoModel, transactionEntryModel, null);
				getModelService().save(codPaymentInfoModel);
			}
		}
		else
		{
			LOG.error("Cannot create COD transaction. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode() + "\n");
		}
		return paymentSubscriptionResult;
	}

	/**
	 * This method is used to complete the COD mode.
	 *
	 * @param customerModel
	 * @param saveInAccount
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	@Override
	public PaymentSubscriptionResultItem completeWalletCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException
	{
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		paymentSubscriptionResult.setSuccess(true);
		paymentSubscriptionResult.setDecision(DecisionsEnum.ACCEPT.name());
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
			Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");
			final CartModel cartModel = getCartService().getSessionCart();
			cartModel.setPaidByWallet(cartModel.getBalanceDue());
			cartModel.setBalanceDue(Double.valueOf(0.0d));
			getModelService().saveAll(cartModel, customerModel);
			final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy().savePaymentTransactionEntry(
					customerModel, response.getRequestId(), response.getOrderInfoData());
			final WalletPaymentInfoModel walletPaymentInfoModel = getWalletPaymentInfoCreateStrategy().saveWalletSubscription(
					customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);
			savePaymentTransaction(walletPaymentInfoModel, transactionEntryModel, null);

			getModelService().save(walletPaymentInfoModel);
		}
		else
		{
			LOG.error("Cannot create COD transaction. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode() + "\n");
		}
		return paymentSubscriptionResult;
	}

	@Override
	public PaymentSubscriptionResultItem completeLandmarkRewardCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException
	{
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		paymentSubscriptionResult.setSuccess(true);
		paymentSubscriptionResult.setDecision(DecisionsEnum.ACCEPT.name());
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getCustomerInfoData(), "CustomerInfoData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			Assert.notNull(response.getSubscriptionInfoData(), "SubscriptionInfoData cannot be null");
			Assert.notNull(response.getSubscriptionSignatureData(), "SubscriptionSignatureData cannot be null");
			final CartModel cartModel = getCartService().getSessionCart();
			cartModel.setPaidByLandmarkReward(cartModel.getBalanceDue());
			cartModel.setBalanceDue(Double.valueOf(0.0d));
			getModelService().saveAll(cartModel, customerModel);
			final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy().savePaymentTransactionEntry(
					customerModel, response.getRequestId(), response.getOrderInfoData());
			final LandmarkPaymentInfoModel landmarkPaymentInfoModel = getLandmarkRewardPaymentInfoCreateStrategy().savelandmarkRewardSubscription(
					customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);
			savePaymentTransaction(landmarkPaymentInfoModel, transactionEntryModel, null);

			getModelService().save(landmarkPaymentInfoModel);
		}
		else
		{
			LOG.error("Cannot create COD transaction. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode() + "\n");
		}
		return paymentSubscriptionResult;
	}
	
	@Override
	public PaymentData beginIPGCreatePaymentSubscription(final String siteName, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customer, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress, final CalculatedWalletData calculatedWalletData) throws IllegalArgumentException
	{
		final SparPaymentFormActionUrlStrategy paymentFormActionUrlStrategy = ((SparPaymentFormActionUrlStrategy) getPaymentFormActionUrlStrategy());
		final String requestUrl = paymentFormActionUrlStrategy.getIPGRequestUrl(getClientReferenceLookupStrategy()
				.lookupClientReferenceId());

		Assert.notNull(requestUrl, "The IPGRequestUrl cannot be null");

		final CreateSubscriptionRequest request = getCreateSubscriptionRequestStrategy().createSubscriptionRequest(siteName,
				requestUrl, responseUrl, merchantCallbackUrl, customer, cardInfo, paymentAddress);
		
		// Code change for Landmark Reward Points with Credit Card payment
		SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService.getAttribute("calculateLRAmountResultData");
		if (null != calculateLRAmountResultData && null != calculateLRAmountResultData.getRemainingBalanceDue()
				&& calculateLRAmountResultData.getRemainingBalanceDue().getValue().doubleValue() > 0)
		{
			request.getSignatureData().setAmount(
					BigDecimal.valueOf(calculateLRAmountResultData.getRemainingBalanceDue().getValue().doubleValue()));
		}
		
		if (null != calculatedWalletData && null != calculatedWalletData.getOrderWalletAmount()
				&& calculatedWalletData.getOrderWalletAmount().getValue().doubleValue() > 0)
		{
			request.getSignatureData().setAmount(
					BigDecimal.valueOf(calculateLRAmountResultData.getRemainingBalanceDue().getValue().doubleValue() - 
							calculatedWalletData.getOrderWalletAmount().getValue().doubleValue()));
		}
		PaymentData data = getPaymentDataConverter().convert(request);
		if (data == null)
		{
			data = new PaymentData();
			data.setParameters(new HashMap<String, String>());
		}
		LOG.info("Request parameters that are sent to IPG (ICICI) :  \n" + data.getParameters() + "\n");

		return data;
	}

	/**
	 * This method is used to complete the COD mode.
	 *
	 * @param customerModel
	 * @param parameters
	 * @return PaymentSubscriptionResultItem
	 * @throws IllegalArgumentException
	 */
	@Override
	public PaymentSubscriptionResultItem completeIPGCreatePaymentSubscription(final CustomerModel customerModel,
			final Map<String, String> parameters, final CartModel cartModel) throws IllegalArgumentException
	{
		LOG.info("Response parameters that are received from IPG (ICICI): \n" + parameters + "\n");
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getSparIPGPaymentResponseInterpretationStrategy().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		validateParameterNotNull(response, "CreateSubscriptionResult cannot be null");
		validateParameterNotNull(response.getDecision(), "Decision cannot be null");

		paymentSubscriptionResult.setSuccess(SparIPGDecisionEnum.APPROVED.getCode().equalsIgnoreCase(response.getDecision()));
		paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));
		 String calculatedIPGUtilHash="";  
	    try
		{
	   	 if(SparIPGDecisionEnum.APPROVED.getCode().equalsIgnoreCase(response.getDecision()))
	   	 {
   	   	 final DecimalFormat df = new DecimalFormat("#.00");
   			Date processedTxnDate = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss").parse(response.getTxndatetime());
   			LOG.info("formatted with two decimal value of Cart Balance Due value in database : " + df.format(cartModel.getBalanceDue()));
   			sparIPGResponseHashGenerationUtilService.setParameters(getSharedSecret(), response.getApproval_code(), df.format(cartModel.getBalanceDue()), 
   					getHostedOrderPageTestCurrency(), processedTxnDate,  getMerchantId());
   			calculatedIPGUtilHash = sparIPGResponseHashGenerationUtilService.createHash();
   			LOG.info("After Response from ICICI, Calculated Hash : " + calculatedIPGUtilHash);
	   	 }
		}
		catch (Exception e)
		{
			LOG.error("ParseException occured during parsing response.getTxndatetime()");
		}
		if (SparIPGDecisionEnum.APPROVED.getCode().equalsIgnoreCase(response.getDecision()) && StringUtils.isNotEmpty(calculatedIPGUtilHash)
				&& calculatedIPGUtilHash.equals(response.getResponse_hash()) && response.getApproval_code().startsWith("Y:"))
		{
			// in case of ACCEPT we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			if (!cartModel.getBalanceDue().equals(cartModel.getTotalPrice()))
			{
				if(null != customerModel && null != customerModel.getTotalWalletAmount() && null != cartModel.getPaidByWallet())
				{
   				customerModel.setTotalWalletAmount(Double.valueOf(customerModel.getTotalWalletAmount().doubleValue()
   						- cartModel.getPaidByWallet().doubleValue()));
				}
				getModelService().saveAll(cartModel, customerModel);
				final PaymentTransactionEntryModel walletTransactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final WalletPaymentInfoModel walletPaymentInfoModel = getWalletPaymentInfoCreateStrategy().saveWalletSubscription(
						customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), false);
				saveMultiPaymentTransaction(walletPaymentInfoModel, walletTransactionEntryModel, null, cartModel);
				final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final SparIPGPaymentInfoCreateStrategyImpl sparIPGPaymentInfoCreateStrategyImpl = (SparIPGPaymentInfoCreateStrategyImpl) getCreditCardPaymentInfoCreateStrategy();
				final CreditCardPaymentInfoModel cardPaymentInfoModel = sparIPGPaymentInfoCreateStrategyImpl.saveIPGSubscription(
						customerModel, response.getSubscriptionInfoData(), response.getPaymentInfoData(), false);
				paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);
				saveMultiPaymentTransaction(cardPaymentInfoModel, transactionEntryModel, null, cartModel);
				// Check if the subscription has already been validated
				final CCPaySubValidationModel subscriptionValidation = getCreditCardPaymentSubscriptionDao()
						.findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
				if (subscriptionValidation != null)
				{
					cardPaymentInfoModel.setSubscriptionValidated(true);
					getModelService().save(cardPaymentInfoModel);
					getModelService().remove(subscriptionValidation);
				}

			}
			else
			{
				final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy()
						.savePaymentTransactionEntry(customerModel, response.getRequestId(), response.getOrderInfoData());
				final SparIPGPaymentInfoCreateStrategyImpl sparIPGPaymentInfoCreateStrategyImpl = (SparIPGPaymentInfoCreateStrategyImpl) getCreditCardPaymentInfoCreateStrategy();
				final CreditCardPaymentInfoModel cardPaymentInfoModel = sparIPGPaymentInfoCreateStrategyImpl.saveIPGSubscription(
						customerModel, response.getSubscriptionInfoData(), response.getPaymentInfoData(), false);
				paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);
				savePaymentTransaction(cardPaymentInfoModel, transactionEntryModel, getPaymentProvider());
				// Check if the subscription has already been validated
				final CCPaySubValidationModel subscriptionValidation = getCreditCardPaymentSubscriptionDao()
						.findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
				if (subscriptionValidation != null)
				{
					cardPaymentInfoModel.setSubscriptionValidated(true);
					getModelService().save(cardPaymentInfoModel);
					getModelService().remove(subscriptionValidation);
				}
			}
		}
		else
		{
			LOG.error("Cannot create IPG transaction. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode() + "\n");
			
   			SparFailedOnlineTransactionsModel failedOnlineTransactionsModel = (SparFailedOnlineTransactionsModel)getModelService().create(SparFailedOnlineTransactionsModel.class);
   			String currentTimestamp = String.valueOf(new java.util.Date());
   			StringBuilder formattedCartData = new StringBuilder();
   			formattedCartData.append("CartValue="+cartModel.getBalanceDue()+", Calculated Hash="+calculatedIPGUtilHash+", Decision="+response.getDecision()+", Reason Code: "
   					+ response.getReasonCode());
   			failedOnlineTransactionsModel.setFailedOrderNumber(cartModel.getCode() + "_" + currentTimestamp);
   			failedOnlineTransactionsModel.setPaymentGatewayResponse(parameters.toString());
   			failedOnlineTransactionsModel.setPgResponseTime(currentTimestamp);
   			failedOnlineTransactionsModel.setPgCartData(formattedCartData.toString());
   			getModelService().save(failedOnlineTransactionsModel);
		
		}
		return paymentSubscriptionResult;
	}

	/**
	 * This method is used to set the Payment Transaction with all the relevant values like (Order/payment
	 * provider/amount) for COD/IPG
	 *
	 * @param paymentInfo
	 * @param transactionEntryModel
	 * @param paymentProvider
	 */
	private void saveMultiPaymentTransaction(final PaymentInfoModel paymentInfo,
			final PaymentTransactionEntryModel transactionEntryModel, final String paymentProvider, final CartModel cartModel)
	{

		if (null != cartModel && null != transactionEntryModel)
		{
			final PaymentTransactionModel paymentTransaction = transactionEntryModel.getPaymentTransaction();
			paymentTransaction.setOrder(cartModel);
			paymentTransaction.setInfo(paymentInfo);
			paymentTransaction.setPaymentProvider(paymentProvider);
			if (paymentInfo instanceof WalletPaymentInfoModel)
			{
				paymentTransaction.setPlannedAmount(getMultiPaymentAmount(cartModel, paymentInfo));
			}
			else if (paymentInfo instanceof CashOnDeliveryPaymentInfoModel)
			{
				paymentTransaction.setPlannedAmount(getMultiPaymentAmount(cartModel, paymentInfo));
			}
			else if (paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				paymentTransaction.setPlannedAmount(getMultiPaymentAmount(cartModel, paymentInfo));
			}

			paymentTransaction.setCurrency(commonI18NService.getCurrency(cartModel.getCurrency().getIsocode()));
			cartModel.setPaymentInfo(paymentInfo);
			getModelService().saveAll(cartModel, paymentTransaction);
		}
	}

	/**
	 * This method is used to set the Payment Transaction with all the relevant values like (Order/payment
	 * provider/amount) for COD/IPG
	 *
	 * @param paymentInfo
	 * @param transactionEntryModel
	 * @param paymentProvider
	 */
	private void savePaymentTransaction(final PaymentInfoModel paymentInfo,
			final PaymentTransactionEntryModel transactionEntryModel, final String paymentProvider)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (null != cartModel && null != transactionEntryModel)
		{
			final PaymentTransactionModel paymentTransaction = transactionEntryModel.getPaymentTransaction();
			paymentTransaction.setOrder(cartModel);
			paymentTransaction.setInfo(paymentInfo);
			paymentTransaction.setPaymentProvider(paymentProvider);
			paymentTransaction.setPlannedAmount(getOrderAmount(cartModel));
			paymentTransaction.setCurrency(commonI18NService.getCurrency(cartModel.getCurrency().getIsocode()));
			cartModel.setPaymentInfo(paymentInfo);
			getModelService().saveAll(cartModel, paymentTransaction);
		}
	}

	/**
	 * This method is used to get the Amount that has been captured for COD/IPG
	 *
	 * @param cartModel
	 * @return BigDecimal OrderAmount
	 */
	private BigDecimal getOrderAmount(final CartModel cartModel)
	{
		final Double totalPrice = cartModel.getTotalPrice();
		final Double totalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null && cartModel.getStore()
				.getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final BigDecimal totalPriceWithoutTaxBD = new BigDecimal(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		final BigDecimal totalPriceBD = new BigDecimal(totalTax == null ? 0d : totalTax.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
		return totalPriceBD;
	}

	/**
	 * This method is used to get the Amount that has been captured for COD/IPG
	 *
	 * @param cartModel
	 * @return BigDecimal OrderAmount
	 */
	private BigDecimal getMultiPaymentAmount(final CartModel cartModel, final PaymentInfoModel paymentInfo)
	{
		Double totalPrice = Double.valueOf(0.0d);
		if (paymentInfo instanceof CashOnDeliveryPaymentInfoModel)
		{
			totalPrice = cartModel.getBalanceDue();
		}
		if (paymentInfo instanceof WalletPaymentInfoModel)
		{
			totalPrice = cartModel.getPaidByWallet();
		}
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			totalPrice = cartModel.getBalanceDue();
		}

		final Double totalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null && cartModel.getStore()
				.getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final BigDecimal totalPriceWithoutTaxBD = new BigDecimal(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		final BigDecimal totalPriceBD = new BigDecimal(totalTax == null ? 0d : totalTax.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
		return totalPriceBD;
	}

	/**
	 * Getter
	 *
	 * @return String
	 */
	protected String getPaymentProvider()
	{
		return getCommerceCheckoutService().getPaymentProvider();
	}

	/**
	 * Getter
	 *
	 * @return CommerceCheckoutService
	 */
	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	/**
	 * Setter
	 *
	 * @param commerceCheckoutService
	 */
	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}


	/**
	 * Getter
	 *
	 * @return the codPaymentInfoCreateStrategy
	 */
	@Override
	public SparCashOnDeliveryPaymentInfoCreateStrategy getCodPaymentInfoCreateStrategy()
	{
		return codPaymentInfoCreateStrategy;
	}

	/**
	 * Setter
	 *
	 * @param codPaymentInfoCreateStrategy
	 *           the codPaymentInfoCreateStrategy to set
	 */
	@Override
	public void setCodPaymentInfoCreateStrategy(final SparCashOnDeliveryPaymentInfoCreateStrategy codPaymentInfoCreateStrategy)
	{
		this.codPaymentInfoCreateStrategy = codPaymentInfoCreateStrategy;
	}

	/**
	 * @return the walletPaymentInfoCreateStrategy
	 */
	public SparWalletPaymentInfoCreateStrategy getWalletPaymentInfoCreateStrategy()
	{
		return walletPaymentInfoCreateStrategy;
	}

	/**
	 * @param walletPaymentInfoCreateStrategy
	 *           the walletPaymentInfoCreateStrategy to set
	 */
	public void setWalletPaymentInfoCreateStrategy(final SparWalletPaymentInfoCreateStrategy walletPaymentInfoCreateStrategy)
	{
		this.walletPaymentInfoCreateStrategy = walletPaymentInfoCreateStrategy;
	}

	/**
	 * Getter
	 *
	 * @return the sparIPGPaymentResponseInterpretationStrategy
	 */
	@Override
	public SparIPGPaymentResponseInterpretationStrategyImpl getSparIPGPaymentResponseInterpretationStrategy()
	{
		return sparIPGPaymentResponseInterpretationStrategy;
	}

	/**
	 * Setter
	 *
	 * @param sparIPGPaymentResponseInterpretationStrategy
	 *           the sparIPGPaymentResponseInterpretationStrategy to set
	 */
	@Override
	public void setSparIPGPaymentResponseInterpretationStrategy(
			final SparIPGPaymentResponseInterpretationStrategyImpl sparIPGPaymentResponseInterpretationStrategy)
	{
		this.sparIPGPaymentResponseInterpretationStrategy = sparIPGPaymentResponseInterpretationStrategy;
	}

	/**
	 * @return the landmarkRewardPaymentInfoCreateStrategy
	 */
	public SparLandmarkRewardPaymentInfoCreateStrategy getLandmarkRewardPaymentInfoCreateStrategy()
	{
		return landmarkRewardPaymentInfoCreateStrategy;
	}

	/**
	 * @param landmarkRewardPaymentInfoCreateStrategy the landmarkRewardPaymentInfoCreateStrategy to set
	 */
	public void setLandmarkRewardPaymentInfoCreateStrategy(
			SparLandmarkRewardPaymentInfoCreateStrategy landmarkRewardPaymentInfoCreateStrategy)
	{
		this.landmarkRewardPaymentInfoCreateStrategy = landmarkRewardPaymentInfoCreateStrategy;
	}

	/**
	 * @return the sparPayTMPaymentInfoCreateStrategy
	 */
	public SparPayTMPaymentInfoCreateStrategy getSparPayTMPaymentInfoCreateStrategy()
	{
		return sparPayTMPaymentInfoCreateStrategy;
	}

	/**
	 * @param sparPayTMPaymentInfoCreateStrategy the sparPayTMPaymentInfoCreateStrategy to set
	 */
	public void setSparPayTMPaymentInfoCreateStrategy(SparPayTMPaymentInfoCreateStrategy sparPayTMPaymentInfoCreateStrategy)
	{
		this.sparPayTMPaymentInfoCreateStrategy = sparPayTMPaymentInfoCreateStrategy;
	}
	
	protected String getSiteConfigProperty(final String key)
	{
		return getSiteConfigService().getString(key, "");
	}

	/**
	 * Gets the IPG merchant ID.
	 *
	 * @return the IPG merchant ID
	 */
	protected String getMerchantId()
	{
		return getSiteConfigProperty(IPGProperties.MERCHANT_ID);
	}

	protected String getHostedOrderPageTestCurrency()
	{
		return getSiteConfigProperty(IPGProperties.IPG_CURRENCY);
	}
	
	/**
	 * Gets the IPG merchant's shared secret that is used to encrypt and validate connections.
	 *
	 * @return the shared secret downloaded from the IPG Business Centre.
	 */
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(IPGProperties.SHARED_SECRET);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spar.hcl.core.payment.ipg.strategies.SparCreateSubscriptionRequestStrategy#getTimeZone()
	 */
	public String getTimeZone()
	{
		return getSiteConfigProperty(IPGProperties.TIME_ZONE);
	}

	@Override
	public PaymentSubscriptionResultItem completePayTMCreatePaymentSubscription(final CustomerModel customerModel,
			final boolean saveInAccount, final Map<String, String> parameters) throws IllegalArgumentException
	{
		LOG.info("Response parameters that are received from PayTM: \n" + parameters + "\n");
		final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
		paymentSubscriptionResult.setErrors(errors);

		final CreateSubscriptionResult response = getSparIPGPaymentResponseInterpretationStrategy().interpretResponse(parameters,
				getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

		validateParameterNotNull(response, "CreateSubscriptionResult cannot be null");
		validateParameterNotNull(response.getDecision(), "Decision cannot be null");

		paymentSubscriptionResult.setSuccess(SparIPGDecisionEnum.APPROVED.getCode().equalsIgnoreCase(response.getDecision()));
		paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
		paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

		if (SparIPGDecisionEnum.APPROVED.getCode().equalsIgnoreCase(response.getDecision()))
		{
			// in case of APPROVED we should have all these fields filled out
			Assert.notNull(response.getAuthReplyData(), "AuthReplyData cannot be null");
			Assert.notNull(response.getOrderInfoData(), "OrderInfoData cannot be null");
			Assert.notNull(response.getPaymentInfoData(), "PaymentInfoData cannot be null");
			Assert.notNull(response.getSignatureData(), "SignatureData cannot be null");
			final CartModel cartModel = getCartService().getSessionCart();
			cartModel.setBalanceDue(Double.valueOf(0.0d));
			getModelService().saveAll(cartModel, customerModel);
			final PaymentTransactionEntryModel transactionEntryModel = getPaymentTransactionStrategy().savePaymentTransactionEntry(
					customerModel, response.getRequestId(), response.getOrderInfoData());
			final PayTMPaymentInfoModel payTMPaymentInfoModel = getSparPayTMPaymentInfoCreateStrategy().savePayTMSubscription(
					customerModel, response.getCustomerInfoData(), response.getPaymentInfoData(), saveInAccount);
			savePaymentTransaction(payTMPaymentInfoModel, transactionEntryModel, null);

			getModelService().save(payTMPaymentInfoModel);
		}
		else
		{
			LOG.error("Cannot create PayTM transaction. Decision: " + response.getDecision() + " - Reason Code: "
					+ response.getReasonCode() + "\n");
		}
		return paymentSubscriptionResult;
	}
}
