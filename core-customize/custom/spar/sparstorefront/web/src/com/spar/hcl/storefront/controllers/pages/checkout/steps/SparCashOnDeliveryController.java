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
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.facades.landmarkreward.SparLRRedemptionDataResult;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.voucher.impl.SparDefaultVoucherFacade;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.form.SparSopPaymentDetailsForm;


/**
 * This controller class is used to handle COD payment mode flow
 *
 * @author rohan_c
 *
 */
@Controller
@RequestMapping(value = "/checkout/multi/cod")
public class SparCashOnDeliveryController extends PaymentMethodCheckoutStepController
{
	private static final Logger LOG = Logger.getLogger(SparCashOnDeliveryController.class);
	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;

	@Resource(name = "sparVoucherFacade")
	private SparDefaultVoucherFacade sparVoucherFacade;

	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	/**
	 * This method is used to handle COD Payment Mode
	 *
	 * @param request
	 * @param sparSopPaymentDetailsForm
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@SuppressWarnings("boxing")
	@RequireHardLogIn
	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String doHandleCOD(final HttpServletRequest request, @Valid final SparSopPaymentDetailsForm sparSopPaymentDetailsForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{

		final Map<String, String> resultMap = getRequestParameterMap(request);
		setCashOnDeliveryParam(resultMap);
		final boolean savePaymentInfo = false;
		final CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService
				.getAttribute("calculatedWalletData");
		final SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService
				.getAttribute("calculateLRAmountResultData");
		boolean isCashOnDelivery = PaymentModeEnum.CASHONDELIVERY.getCode().equals(sparSopPaymentDetailsForm.getPaymentMode());
		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();

		if (null != calculateLRAmountResultData)
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
					resultMap.put("ccOrderPostForm", "");
	   			sessionService.setAttribute("redeemAPIErrorMsg",  Config.getString("spar.landmark.serverdown.validation.message",
							"Link to LR system is down. Please pay via other payment modes."));
					return getCheckoutStep().currentStep();
	   		}
	   		else if(!lrRedemptionDataResult.isResult())
	   		{
	   			resultMap.put("ccOrderPostForm", "");
	   			sessionService.setAttribute("redeemAPIErrorMsg",  lrRedemptionDataResult.getMessage());
					return getCheckoutStep().currentStep();
	   		}
				else if(null != currentLRAmountResultData.getRemainingBalanceDue())
				{
					cartData.setBalanceDue(currentLRAmountResultData.getRemainingBalanceDue());
					cartData.setPaidByLandmarkReward(Double.valueOf(cartData.getTotalPrice().getValue().doubleValue()
							- cartData.getBalanceDue().getValue().doubleValue()));
				}
			if (null != cartData.getBalanceDue()
					&& !(cartData.getBalanceDue().getValue().equals(cartData.getTotalPrice().getValue())))
			{
				//Save PaymentMode in CartModel
				getSparDefaultPaymentFacade().setPaymentMode(
						getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.MULTIPAYMENTMODE.getCode()),
						PaymentStatus.PARTPAID);
				getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));
			}
		}
		else if (null != calculatedWalletData)
		{

			if (null != calculatedWalletData.getOrderWalletAmount())
			{
				sparDefaultPaymentFacade.setBalanceDueInCart(calculatedWalletData, cartData);

			}
			if (null != cartData.getBalanceDue()
					&& !(cartData.getBalanceDue().getValue().equals(cartData.getTotalPrice().getValue())))
			{
				//Save PaymentMode in CartModel
				getSparDefaultPaymentFacade().setPaymentMode(
						getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.MULTIPAYMENTMODE.getCode()),
						PaymentStatus.PARTPAID);
				getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));
			}
		}
		else if (CollectionUtils.isNotEmpty(sparVoucherFacade.getSparVouchersForCart())
				&& cartData.getTotalPriceWithTax().getValue().equals(BigDecimal.valueOf(0.0)))
		{
			isCashOnDelivery = true;
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.VOUCHERPAYMENTMODE.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));
			return getCheckoutStep().nextStep();
		}
		else
		{
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.CASHONDELIVERY.getCode()), PaymentStatus.NOTPAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isCashOnDelivery));
		}
		this.getSparDefaultPaymentFacade().completeCashOnDeliveryCreateSubscription(resultMap, savePaymentInfo, isCashOnDelivery,
				cartData);
		sessionService.setAttribute("calculatedWalletData", null);
		sessionService.removeAttribute("calculatedWalletData");
		sessionService.setAttribute("calculateLRAmountResultData", null);
		sessionService.removeAttribute("calculateLRAmountResultData");
		return getCheckoutStep().nextStep();
	}
	
	/**
	 * Set COD Parameters
	 *
	 * @param resultMap
	 */
	private void setCashOnDeliveryParam(final Map<String, String> resultMap)
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
