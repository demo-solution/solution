/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.storefront.controllers.pages.checkout.steps;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.core.model.process.SparLandmarkRedemptionEmailProcessModel;
import com.spar.hcl.facades.landmarkreward.SparLRRedemptionDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.form.SparSopPaymentDetailsForm;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;


/**
 * This class is used to handle response from IPG and is an extension of PaymentMethodCheckoutStepController
 *
 * @author rohan_c
 *
 */
@Controller
@RequestMapping(value = "/checkout/multi/ipg")
public class SparIPGResponseController extends PaymentMethodCheckoutStepController
{
	private static final Logger LOG = LoggerFactory.getLogger(SparIPGResponseController.class);
	
	public static final String IPG_REDIRECT_URL_ERROR = REDIRECT_PREFIX + "/checkout/multi/ipg/error";
	private static final String BASESITE = "basesite.uid";
	private static final String SPAR_LANDMARK_REDEMPTION_EMAIL_PROCESS = "sparLandmarkRedemptionEmailProcess";
	

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;
	
	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private BusinessProcessService businessProcessService;
	
	@Autowired
	private BaseSiteService baseSiteService;
	
	@Autowired
	private ModelService modelService;
	
	@Resource(name = "userService")
	private UserService userService;
	
	@Autowired
	private CartService cartService;

	/**
	 * This method is used to handle IPG Response (Success/failure)
	 *
	 * @param request
	 * @param ccPaymentDetailsForm
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/response", method = RequestMethod.POST)
	@RequireHardLogIn
	@SuppressWarnings({ "unused", "boxing" })
	public String doHandleIpgResponse(final HttpServletRequest request,
			@Valid final SparSopPaymentDetailsForm ccPaymentDetailsForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		boolean isLRPaymentEnable = false;
		setModelAttributesFromSession(model);
		final Map<String, String> resultMap = getRequestParameterMap(request);
		
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		
		final SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService
				.getAttribute("calculateLRAmountResultData");
		if(null != calculateLRAmountResultData)
		{
			if (null != calculateLRAmountResultData.getRemainingBalanceDue())
			{
				isLRPaymentEnable = true;
				cartData.setBalanceDue(calculateLRAmountResultData.getRemainingBalanceDue());
				cartData.setPaidByLandmarkReward(Double.valueOf(cartData.getTotalPrice().getValue().doubleValue()
						- cartData.getBalanceDue().getValue().doubleValue()));
			}
		}
   		
		final CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService
				.getAttribute("calculatedWalletData");
		
		if (null != calculatedWalletData)
		{
			if (null != calculatedWalletData.getOrderWalletAmount())
			{
				sparDefaultPaymentFacade.setBalanceDueInCart(calculatedWalletData, cartData);
			}
		}
		final PaymentSubscriptionResultData paymentSubscriptionResultData = this.getSparDefaultPaymentFacade()
				.completeIPGCreateSubscription(resultMap, cartData);
		if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null
				&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			doHandleIPGSuccessResponse(paymentSubscriptionResultData, cartData);
			if(isLRPaymentEnable)
			{
   			SparLRRedemptionDataResult lrRedemptionDataResult = sparLandmarkRewardFacade.redemptionFromLMS(calculateLRAmountResultData);
      		LOG.info(" lrRedemptionDataResult : " +lrRedemptionDataResult);
      		if(null == lrRedemptionDataResult || !lrRedemptionDataResult.isResult())
      		{
      			sendLandmarkRedemptionEmail();
      		}
			}
		}
		else
		{
			sessionService.setAttribute("calculatedWalletData", null);
			// IPG Error
			LOG.error("Failed to create IPG subscription. Error occurred while contacting external payment services.");
			return IPG_REDIRECT_URL_ERROR + "/?decision=" + paymentSubscriptionResultData.getDecision() + "&reasonCode="
					+ paymentSubscriptionResultData.getResultCode();

		}
		sessionService.setAttribute("calculatedWalletData", null);
		return getCheckoutStep().nextStep();
	}

	/**
	 * This method is used to handle success response from IPG
	 *
	 * @param paymentSubscriptionResultData
	 */
	public void doHandleIPGSuccessResponse(final PaymentSubscriptionResultData paymentSubscriptionResultData,
			final CartData cartData)
	{
		final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();
		if (getUserFacade().getCCPaymentInfos(true).size() <= 1)
		{
			getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
		}
		if (null != cartData.getBalanceDue() && !(cartData.getBalanceDue().getValue().equals(cartData.getTotalPrice().getValue())))
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.MULTIPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(Boolean.TRUE));
		}
		else
		{
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.CREDITCARD.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.TRUE);
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
	}

	/**
	 * This method is used to handle error response from IPG
	 *
	 * @param decision
	 * @param reasonCode
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String doHandleIPGResponseError(@RequestParam(required = true) final String decision,
			@RequestParam(required = true) final String reasonCode, final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		enterStep(model, redirectAttributes);

		model.addAttribute("decision", decision);
		model.addAttribute("reasonCode", reasonCode);

		if (!Config.getString("ipg.response.failed.error", "5993").equals(reasonCode))
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.ipg.addPaymentDetails.generalError");
		}
		return ControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
	}
	
	@SuppressWarnings("boxing")
	private void sendLandmarkRedemptionEmail()
	{
		LOG.info("sendLandmarkRedemptionEmail :: Start");
		final SparLandmarkRedemptionEmailProcessModel process=(SparLandmarkRedemptionEmailProcessModel)businessProcessService.createProcess
				(SPAR_LANDMARK_REDEMPTION_EMAIL_PROCESS + "-" + "-" + System.currentTimeMillis(), SPAR_LANDMARK_REDEMPTION_EMAIL_PROCESS);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE));
		CustomerModel customer = (CustomerModel) userService.getCurrentUser();
		
		CartModel cartModel = cartService.getSessionCart();
		Date currDate = new Date();
		long epoch = currDate.getTime();
		LOG.info("epoch time stamp : " + epoch);
		cartModel.setMessageId(cartModel.getCode()+epoch);
		cartModel.setCartId(cartModel.getCode());
		//cartModel.setPaidByLandmarkReward(cartData.getPaidByLandmarkReward());
		modelService.save(cartModel);
		process.setSite(baseSite);
		process.setUser(customer);
		process.setCart(cartModel);
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOG.info("sendLandmarkRedemptionEmail :: finished");
	}

	/**
	 * Getter
	 *
	 * @return the sparDefaultPaymentFacade
	 */
	public SparDefaultPaymentFacade getSparDefaultPaymentFacade()
	{
		return sparDefaultPaymentFacade;
	}

	/**
	 * Setter
	 *
	 * @param sparDefaultPaymentFacade
	 *           the sparDefaultPaymentFacade to set
	 */
	public void setSparDefaultPaymentFacade(final SparDefaultPaymentFacade sparDefaultPaymentFacade)
	{
		this.sparDefaultPaymentFacade = sparDefaultPaymentFacade;
	}

}
