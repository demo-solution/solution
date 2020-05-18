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

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.landmarkreward.SparLRRedemptionDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.storefront.controllers.pages.SparRegisterOptionalPageController;
import com.spar.hcl.storefront.form.SparSopPaymentDetailsForm;
/**
 * @author ravindra.kr
 *
 */
@Controller
@RequestMapping(value = "/checkout/multi/landmarkreward")
public class SparLandmarkRewardPaymentController extends PaymentMethodCheckoutStepController
{
	protected static final Logger LOG = Logger.getLogger(SparRegisterOptionalPageController.class);
	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;

	@Autowired
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "sessionService")
	SessionService sessionService;
	
	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	@SuppressWarnings("boxing")
	@ResponseBody
	@RequestMapping(value = "/process", method = RequestMethod.GET)
	@RequireHardLogIn
	public SparValidateLRAmountResultData doHandleLRPayment(@RequestParam(value = "landmarkRewardAmount") final String landmarkRewardAmount) 
	{
		SparValidateLRAmountResultData validateLRAmountResultData = sparLandmarkRewardFacade.doHandleLRPayment(landmarkRewardAmount);
		if(null != validateLRAmountResultData && validateLRAmountResultData.isIsLRAmountValid())
		{
			sessionService.setAttribute("calculateLRAmountResultData", validateLRAmountResultData);
		}
		return validateLRAmountResultData;
	}

	@SuppressWarnings("boxing")
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	@RequireHardLogIn
	public String confirmLandmarkRewardPayment(final HttpServletRequest request,
			@Valid final SparSopPaymentDetailsForm sparSopPaymentDetailsForm , final Model model)
			throws CMSItemNotFoundException
	{
		final Map<String, String> resultMap = getRequestParameterMap(request);
		final SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService.getAttribute("calculateLRAmountResultData");
		if(null != calculateLRAmountResultData)
		{
   		SparValidateLRAmountResultData currentLRAmountResultData = sparLandmarkRewardFacade.validateLandmarkRewardPoints(calculateLRAmountResultData.getEnteredLRAmount());
   		if(!currentLRAmountResultData.isIsLRAmountValid())
   		{
   			resultMap.put("ccOrderPostForm", "");
   			sessionService.setAttribute("redeemAPIErrorMsg", currentLRAmountResultData.getValidationMessage());
   			return getCheckoutStep().currentStep();
   		}
   		
   		SparLRRedemptionDataResult lrRedemptionDataResult = sparLandmarkRewardFacade.redemptionFromLMS(currentLRAmountResultData);
   		LOG.info(" lrRedemptionDataResult : " +lrRedemptionDataResult);
   		if(null == lrRedemptionDataResult)
   		{
   			sessionService.setAttribute("redeemAPIErrorMsg",  Config.getString("spar.landmark.serverdown.validation.message",
						"Link to LR system is down. Please pay via other payment modes."));
   			return getCheckoutStep().currentStep();
   		}
   		else if(!lrRedemptionDataResult.isResult())
   		{
   			sessionService.setAttribute("redeemAPIErrorMsg",  lrRedemptionDataResult.getMessage());
   			return getCheckoutStep().currentStep();
   		}
		}
		setLandmarkRewardParam(resultMap);
		
		if (null != calculateLRAmountResultData && calculateLRAmountResultData.getEnteredLRAmount() > 0)
		{
			final boolean isLandmarkRewardApplied = PaymentModeEnum.LANDMARKREWARD.getCode().equals(sparSopPaymentDetailsForm.getPaymentMode());
			final boolean savePaymentInfo = false;
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.LANDMARKREWARD.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isLandmarkRewardApplied));
			this.getSparDefaultPaymentFacade().completeLandmarkRewardDeliveryCreateSubscription(resultMap, savePaymentInfo, isLandmarkRewardApplied,
					calculateLRAmountResultData);
			final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
			cartData.setBalanceDue(calculateLRAmountResultData.getRemainingBalanceDue());
			model.addAttribute("walletPaymentDetailsForm", new PaymentDetailsForm());
			model.addAttribute("cartData", cartData);
			sessionService.removeAttribute("calculateLRAmountResultData");
			return getCheckoutStep().nextStep();
		}
		else
		{
			model.addAttribute("redeemAPIErrorMsg", Config.getString("spar.landmark.serverdown.validation.message",
					"Link to LR system is down. Please pay via other payment modes."));
			return getCheckoutStep().currentStep();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doCancelLandmarkRewardPayment(final HttpServletRequest request) throws CMSItemNotFoundException, NumberFormatException
	{
		//SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService.getAttribute("calculateLRAmountResultData");
		//SparValidateLRAmountResultData = sparDefaultPaymentFacade.refreshWallet(calculateLRAmountResultData);
		request.setAttribute("calculateLRAmountResultData", null);
		sessionService.setAttribute("calculateLRAmountResultData", null);
		sessionService.removeAttribute("calculateLRAmountResultData");
		return null;
	}
	
	/**
	 * Set COD Parameters
	 *
	 * @param resultMap
	 */
	private void setLandmarkRewardParam(final Map<String, String> resultMap)
	{
		resultMap.put("reasonCode", "100");
		resultMap.put("decision", "ACCEPT");
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
