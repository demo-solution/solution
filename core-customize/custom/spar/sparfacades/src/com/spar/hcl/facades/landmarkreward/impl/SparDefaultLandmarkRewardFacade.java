package com.spar.hcl.facades.landmarkreward.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.spar.hcl.core.model.process.SparLandmarkRegistrationEmailProcessModel;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.LRInputParamForGetMemberForLMSAPI;
import com.spar.hcl.facades.landmarkreward.LRInputParamForRedemptionFromLMSAPI;
import com.spar.hcl.facades.landmarkreward.LRUserEnrollMemberData;
import com.spar.hcl.facades.landmarkreward.SparLRRedemptionDataResult;
import com.spar.hcl.facades.landmarkreward.SparLRUserDetailDataResult;
import com.spar.hcl.facades.landmarkreward.SparLRUserEnrollDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.landmarkreward.webservices.utility.SparLandMarkRewardExchangeUtility;


/**
 * @author ravindra.kr
 *
 */
public class SparDefaultLandmarkRewardFacade implements SparLandmarkRewardFacade
{
	private static final Logger LOGGER = Logger.getLogger(SparDefaultLandmarkRewardFacade.class);

	private static final String LANDMARKREWARD_POINTS_AVAILABLE_PLACEHOLDER = "pointsAvailable";
	private static final String SCRIPT_ENGINE = "js";
	private static final String DECIMAL_FORMAT = "#.00";
	private static final String DEFAULT_FORMULA_LRPOINTS_TO_INR = "pointsAvailable*(100/166)";
	private static final String ANDMARKREWARD_POINTS_TO_INR_FORMULA = "spar.landmarkrewardpoints.to.inr.formula";
	private static final String BASESITE = "basesite.uid";
	private static final String SPAR_LANDMARK_REGISTRATION_EMAIL_PROCESS = "sparLandmarkRegistrationEmailProcess";
	 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private static final String LANDMARK_POINTS_BALANCE_CUTOFF_VALUE = "spar.landmark.balance.cutoff.value";
	private static final String LANDMARK_SYSTEM_ACTIVE = "spar.landmarkreward.isActive";
	
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Autowired
	private CartFacade cartFacade;

	@Autowired
	private CartService cartService;

	@Autowired
	private PriceDataFactory priceDataFactory;
	@Autowired
	private CommonI18NService commonI18NService;
	@Autowired
	private SparCustomerFacade sparCustomerFacade;
	@Autowired
	private CheckoutFacade checkoutFacade;
	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparPaymentFacade;
	@Autowired
	private BusinessProcessService businessProcessService;
	@Autowired
	private BaseSiteService baseSiteService;
	@Autowired
	private ConfigurationService configurationService;

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#enrollMember()
	 */
	@SuppressWarnings("static-access")
	@Override
	public SparLRUserEnrollDataResult enrollMember()
	{
		LOGGER.debug("In enrollMember method :");
		ResponseEntity<SparLRUserEnrollDataResult> response = null;
		SparLRUserEnrollDataResult sparLRUserEnrollDataResult = null;
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		if(null != customerData.getDefaultBillingAddress())
		{
   		final LRUserEnrollMemberData lRUserEnrollMemberData = populateLRCustomerData(customerData);
   		final String customerJSONData = SparLandMarkRewardExchangeUtility.createDataAsJson(lRUserEnrollMemberData);
   		LOGGER.debug("customerJSONData :" + customerJSONData);
   
   		boolean landmarkActive = Config.getBoolean(LANDMARK_SYSTEM_ACTIVE, false);
   		if(landmarkActive)
      		{
         		if (StringUtils.isNotBlank(customerJSONData))
         		{
         			response = SparLandMarkRewardExchangeUtility.enrollMemberToLR(customerJSONData);
         		}
      		}
         		updateUserEnrolledToLRStatus();
      		if (null != response)
      		{
      			sparLRUserEnrollDataResult = response.getBody();
      			LOGGER.info("Exited from enrollMember method : "
      					+ SparLandMarkRewardExchangeUtility.createDataAsJson(response.getBody()));
      			if(!sparLRUserEnrollDataResult.isResult())
      			{
      				if(sparLRUserEnrollDataResult.getResultCode().equals("18") || sparLRUserEnrollDataResult.getResultCode().equals("19"))
      				{
      					LOGGER.debug("Email is not triggered due to customer is already enrolled with LR.");
      				}
      				else
      				{
      					sendSparLandmarkRegistrationEmail();
      				}
      			}
      		}
      		else
      		{
      			if(landmarkActive)
         		{
      				sendSparLandmarkRegistrationEmail();
         		}
      		}
		}
		return sparLRUserEnrollDataResult;
	}
	
	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#validateLandmarkRewardPoints(double)
	 */
	@Override
	public SparValidateLRAmountResultData validateLandmarkRewardPoints(final double enteredLRAmount)
	{
		SparValidateLRAmountResultData calculateLRAmountResultData = null;
		String userEnteredLRAmount = String.valueOf(enteredLRAmount);
  		calculateLRAmountResultData = doHandleLRPayment(userEnteredLRAmount);
		return calculateLRAmountResultData;
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#populateLRCustomerData(de.hybris.platform.commercefacades.user.data.CustomerData)
	 */
	@Override
	public LRUserEnrollMemberData populateLRCustomerData(final CustomerData customerData)
	{
		final LRUserEnrollMemberData lRUserEnrollMemberData = new LRUserEnrollMemberData();
		lRUserEnrollMemberData.setLastName(customerData.getLastName());
		lRUserEnrollMemberData.setFirstName(customerData.getFirstName());
		if (null != customerData.getLanguage())
		{
			lRUserEnrollMemberData.setMotherTongue(customerData.getLanguage().getName());
		}
		lRUserEnrollMemberData.setOccupation("");
		lRUserEnrollMemberData.setGender(customerData.getTitle());
		if (null != customerData.getDefaultBillingAddress())
		{
			lRUserEnrollMemberData.setCountry(customerData.getDefaultBillingAddress().getCountry().getName());
			lRUserEnrollMemberData.setAddress1(customerData.getDefaultBillingAddress().getLine1());
			lRUserEnrollMemberData.setAddress2(customerData.getDefaultBillingAddress().getLine2());
			if (null != customerData.getDefaultBillingAddress().getRegion())
			{
				lRUserEnrollMemberData.setAddress3(customerData.getDefaultBillingAddress().getRegion().getName());
				lRUserEnrollMemberData.setState(customerData.getDefaultBillingAddress().getRegion().getName());
			}
			else
			{
				lRUserEnrollMemberData.setAddress3("");
				lRUserEnrollMemberData.setState("");
			}
			lRUserEnrollMemberData.setCity(customerData.getDefaultBillingAddress().getTown());

			lRUserEnrollMemberData.setSuburbanArea(customerData.getDefaultBillingAddress().getArea());
			if(null != customerData.getDefaultBillingAddress().getPostalCode() 
					&& "NA".equalsIgnoreCase(customerData.getDefaultBillingAddress().getPostalCode()))
			{
				lRUserEnrollMemberData.setPinCode("000000");
			}
			else
			{
				lRUserEnrollMemberData.setPinCode(customerData.getDefaultBillingAddress().getPostalCode());
			}
		}
		lRUserEnrollMemberData.setEmailID(customerData.getUid());
		lRUserEnrollMemberData.setMaritalStatus("");
		lRUserEnrollMemberData.setNationality("");
		if (null != customerData.getDateOfBirth())
		{
			final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy");
			lRUserEnrollMemberData.setDateOfBirth(formatter.format(customerData.getDateOfBirth()));
		}
		lRUserEnrollMemberData.setMobileNumber(customerData.getCustPrimaryMobNumber());
		return lRUserEnrollMemberData;
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#getMemberForLMS()
	 */
	@SuppressWarnings("static-access")
	@Override
	public SparLRUserDetailDataResult getMemberForLMS()
	{
		ResponseEntity<SparLRUserDetailDataResult> response = null;
		SparLRUserDetailDataResult sparLRUserDetailDataResult = null;
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final LRInputParamForGetMemberForLMSAPI lRInputParamForGetMemberForLMSAPI = new LRInputParamForGetMemberForLMSAPI();
		lRInputParamForGetMemberForLMSAPI.setMobileNumber(customerData.getCustPrimaryMobNumber());
		lRInputParamForGetMemberForLMSAPI.setCardNumber("");

		final String paramJSONData = SparLandMarkRewardExchangeUtility.createDataAsJson(lRInputParamForGetMemberForLMSAPI);
		LOGGER.debug("customerJSONData :" + paramJSONData);
		boolean landmarkActive = Config.getBoolean(LANDMARK_SYSTEM_ACTIVE, false);
		if(landmarkActive)
		{
   		if (StringUtils.isNotBlank(paramJSONData))
   		{
   			response = SparLandMarkRewardExchangeUtility.getMemberForLMS(paramJSONData);
   		}
		}
		if (null != response)
		{
			sparLRUserDetailDataResult = response.getBody();
			if (sparLRUserDetailDataResult.isResult() && null != sparLRUserDetailDataResult.getPointsAvailable())
			{
				final String lrPointsToInrFormula = Config.getString(ANDMARKREWARD_POINTS_TO_INR_FORMULA,
						DEFAULT_FORMULA_LRPOINTS_TO_INR);
				if (StringUtils.isNotEmpty(lrPointsToInrFormula))
				{
					final String balanceAvailableFormula = lrPointsToInrFormula.replace(LANDMARKREWARD_POINTS_AVAILABLE_PLACEHOLDER,
							sparLRUserDetailDataResult.getPointsAvailable().toString());
					LOGGER.info("balanceAvailableFormula : " + balanceAvailableFormula);
					final ScriptEngineManager manager = new ScriptEngineManager();
					final ScriptEngine engine = manager.getEngineByName(SCRIPT_ENGINE);
					try
					{
						final double lrBalanceCutoffValue = Config.getDouble(LANDMARK_POINTS_BALANCE_CUTOFF_VALUE,0.02);
						LOGGER.info("lrBalanceCutoffValue : " + lrBalanceCutoffValue);
						final DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
						final Object result = engine.eval(balanceAvailableFormula);
						final double balanceAvailable = Double.valueOf(df.format(result)).doubleValue();
						if(balanceAvailable > 0)
						{
							sparLRUserDetailDataResult.setBalanceAvailable(balanceAvailable - lrBalanceCutoffValue);
						}
						else
						{
							sparLRUserDetailDataResult.setBalanceAvailable(balanceAvailable);
						}
						LOGGER.info("balanceAvailable : " + balanceAvailable);
					}
					catch (final ScriptException e)
					{
						LOGGER.error("Expression Evaluation Exception : " + e);
					}
				}
			}
			LOGGER.info("Exited from getMemberForLMS method : "
					+ SparLandMarkRewardExchangeUtility.createDataAsJson(sparLRUserDetailDataResult));
		}
		return sparLRUserDetailDataResult;
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#disableLRPrompt()
	 */
	@Override
	public void disableLRPrompt()
	{
		final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
		customerModel.setDisableLRPrompt(Boolean.TRUE);
		modelService.save(customerModel);
	}
	
	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#changeLROptStatus()
	 */
	@Override
	public void changeLROptStatus()
	{
		LOGGER.info("Entered in changeLROptStatus method : ");
		final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
		customerModel.setLrOptStatus(Boolean.TRUE);
		modelService.save(customerModel);
	}
	
	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#updateUserEnrolledToLRStatus()
	 */
	@Override
	public void updateUserEnrolledToLRStatus()
	{
		LOGGER.info("Entered in updateUserEnrolledToLRStatus method : ");
		final CustomerModel customerModel = (CustomerModel) userService.getCurrentUser();
		customerModel.setIsEnrolledToLR(Boolean.TRUE);
		modelService.save(customerModel);
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#redemptionFromLMS(com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData)
	 */
	@Override
	public SparLRRedemptionDataResult redemptionFromLMS(final SparValidateLRAmountResultData calculateLRAmountResultData)
	{
		ResponseEntity<SparLRRedemptionDataResult> response = null;
		SparLRRedemptionDataResult sparLRRedemptionDataResult = null;
			final LRInputParamForRedemptionFromLMSAPI lRInputParamForRedemptionFromLMSAPI = populateDataForRedemptionFromLMSAPI(calculateLRAmountResultData);
			if(null != lRInputParamForRedemptionFromLMSAPI)
			{
   			final String paramJSONData = SparLandMarkRewardExchangeUtility.createDataAsJson(lRInputParamForRedemptionFromLMSAPI);
   			LOGGER.debug("In redemptionFromLMS method , paramJSONData :" + paramJSONData);
   			boolean landmarkActive = Config.getBoolean(LANDMARK_SYSTEM_ACTIVE, false);
      		if(landmarkActive)
      		{
         			if (StringUtils.isNotBlank(paramJSONData))
         			{
         				response = SparLandMarkRewardExchangeUtility.redemptionFromLMS(paramJSONData);
         			}
      			if (null != response)
      			{
      				sparLRRedemptionDataResult = response.getBody();
      				LOGGER.info("Exited from redemptionFromLMS method : "
      						+ SparLandMarkRewardExchangeUtility.createDataAsJson(sparLRRedemptionDataResult));
      			}
      		}
			}
		return sparLRRedemptionDataResult;
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#populateDataForRedemptionFromLMSAPI(com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData)
	 */
	@Override
	public LRInputParamForRedemptionFromLMSAPI populateDataForRedemptionFromLMSAPI(final SparValidateLRAmountResultData calculateLRAmountResultData)
	{
		CartModel cartModel = cartService.getSessionCart();
		LRInputParamForRedemptionFromLMSAPI lRInputParamForRedemptionFromLMSAPI= null;
		if(null != cartModel && null != cartModel.getOrderWarehouse())
		{
			saveCartInfo(cartModel);
			final DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
   		 lRInputParamForRedemptionFromLMSAPI = new LRInputParamForRedemptionFromLMSAPI();
   		lRInputParamForRedemptionFromLMSAPI.setCardNumber(calculateLRAmountResultData.getCardNumber());
   		decimalFormat.setRoundingMode(RoundingMode.FLOOR);
   	   String redeemAmount = decimalFormat.format(calculateLRAmountResultData.getEnteredLRAmount());
   		lRInputParamForRedemptionFromLMSAPI.setRedeemAmount(Double.valueOf(redeemAmount).doubleValue());
   		lRInputParamForRedemptionFromLMSAPI.setStoreId(cartModel.getOrderWarehouse().getCode());
   		lRInputParamForRedemptionFromLMSAPI.setMessageId(cartModel.getMessageId());
   		lRInputParamForRedemptionFromLMSAPI.setInvoiceNumber(cartModel.getCartId());
		}
		return lRInputParamForRedemptionFromLMSAPI;
	}

	/**
	 * @param cartModel
	 * @return
	 */
	public CartModel saveCartInfo(CartModel cartModel)
	{
		cartModel.setCartId(cartModel.getCode());
		Date currDate = new Date();
		long epoch = currDate.getTime();
      LOGGER.info("epoch time stamp : " + epoch);
		cartModel.setMessageId(cartModel.getCode()+epoch);
		modelService.save(cartModel);
		return cartModel;
	}
	
	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#doHandleLRPayment(java.lang.String)
	 */
	@Override
	public SparValidateLRAmountResultData doHandleLRPayment(final String landmarkRewardAmount)
	{
		LOGGER.info("Entered in SparValidateLRAmountResultData method : ");
		double minimumRedemablePoints = Config.getDouble("spar.landmarkreward.minimum.redemable.balance",100);
		final double enteredLRAmount = Double.parseDouble(landmarkRewardAmount);
		final SparValidateLRAmountResultData validateLRAmountResultData = new SparValidateLRAmountResultData();
		validateLRAmountResultData.setEnteredLRAmount(enteredLRAmount);
		if(enteredLRAmount >= minimumRedemablePoints)
   		{
   		CartData cartData = checkoutFacade.getCheckoutCart();
   		cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
   		if(null != cartData && null != cartData.getBalanceDue())
   		{
   			final SparLRUserDetailDataResult sparLRUserDetailDataResult = getMemberForLMS();
      		if (null != sparLRUserDetailDataResult)
      		{
      			cartData = sparPaymentFacade.setInitialBalanceDue(cartData);
      			final double balanceAvailable = sparLRUserDetailDataResult.getBalanceAvailable();
      			if (enteredLRAmount <= balanceAvailable)
      			{
      				if (enteredLRAmount <= cartData.getBalanceDue().getValue().doubleValue())
      				{
      					final DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
      					validateLRAmountResultData.setIsLRAmountValid(true);
      					double remainingBalanceDue = cartData.getBalanceDue().getValue().doubleValue() - enteredLRAmount;
      					final PriceData formattedRemainingBalanceDue = priceDataFactory.create(PriceDataType.BUY,
      							BigDecimal.valueOf(Double.valueOf(decimalFormat.format(remainingBalanceDue)).doubleValue()),
      							commonI18NService.getCurrentCurrency());
      					validateLRAmountResultData.setRemainingBalanceDue(formattedRemainingBalanceDue);
      					validateLRAmountResultData.setCardNumber(sparLRUserDetailDataResult.getCardNumber());
      				}
      				else
      				{
      					validateLRAmountResultData.setIsLRAmountValid(false);
      					validateLRAmountResultData.setValidationMessage(Config.getString("spar.landmark.balancedue.validation.message",
      							"The value of Landmark Rewards points entered is more than the BALANCE DUE amount. Please enter an amount less than or equal to the BALANCE DUE amount."));
      				}
      			}
      			else
      			{
      				validateLRAmountResultData.setIsLRAmountValid(false);
      				validateLRAmountResultData.setValidationMessage(Config.getString("spar.landmark.amount.validation.message",
      						"The amount entered is more than the available Landmark Rewards points value. Please enter an amount less than or equal to the available points value."));
      			}
      			validateLRAmountResultData.setSubTotal(cartData.getSubTotal());
      			LOGGER.info("validateLRAmountResultData : " + validateLRAmountResultData.toString());
      		}
      		else
      		{
      			validateLRAmountResultData.setIsLRAmountValid(false);
      			validateLRAmountResultData.setValidationMessage(Config.getString("spar.landmark.serverdown.validation.message",
      					"There seems to be a system error and we are unable to spend your Landmark Rewards Points at the moment. Please pay via another payment options."));
      		}
   		}
		}
		else
		{
			validateLRAmountResultData.setIsLRAmountValid(false);
			String ValidationMsg = Config.getString("spar.landmarkreward.minimum.redemable.error.message",
					"The amount entered is less than the minimum point spending value. Please enter an amount more than or equal to Rs.");
			validateLRAmountResultData.setValidationMessage(ValidationMsg + minimumRedemablePoints);
		}
		return validateLRAmountResultData;
	}

	/* (non-Javadoc)
	 * @see com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade#sendSparLandmarkRegistrationEmail()
	 */
	@Override
	public void sendSparLandmarkRegistrationEmail()
	{
		LOGGER.info("sendSparLandmarkRegistrationEmail Start :: ");
		final SparLandmarkRegistrationEmailProcessModel process=(SparLandmarkRegistrationEmailProcessModel)businessProcessService.createProcess
				(SPAR_LANDMARK_REGISTRATION_EMAIL_PROCESS + "-" + "-" + System.currentTimeMillis(), SPAR_LANDMARK_REGISTRATION_EMAIL_PROCESS);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE));
		process.setUser(userService.getCurrentUser());
		process.setSite(baseSite);
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOGGER.debug("sendSparLandmarkRegistrationEmail :: finished");
	}
}