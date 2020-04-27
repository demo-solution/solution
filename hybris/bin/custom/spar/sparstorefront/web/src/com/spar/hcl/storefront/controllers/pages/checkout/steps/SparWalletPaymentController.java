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
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.core.enums.PaymentModeEnum;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.order.impl.SparDefaultCheckoutFacade;
import com.spar.hcl.facades.payment.impl.SparDefaultPaymentFacade;
import com.spar.hcl.facades.wallet.data.CalculatedWalletData;
import com.spar.hcl.storefront.form.SparSopPaymentDetailsForm;


/**
 * This controller class is used to handle COD payment mode flow
 *
 *
 *
 */
@Controller
@RequestMapping(value = "/checkout/multi/wallet")
public class SparWalletPaymentController extends PaymentMethodCheckoutStepController
{
	@Resource(name = "sparPaymentFacade")
	private SparDefaultPaymentFacade sparDefaultPaymentFacade;

	@Resource(name = "sparDefaultCheckoutFacade")
	private SparDefaultCheckoutFacade sparDefaultCheckoutFacade;

	@Autowired
	private SparCustomerFacade sparCustomerFacade;

	@Resource(name = "sessionService")
	SessionService sessionService;

	/**
	 * This method is used to handle wallet calculation.
	 *
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@ResponseBody
	@RequestMapping(value = "/process", method = RequestMethod.POST)
	@RequireHardLogIn
	public CalculatedWalletData doHandleWalletPayment(final HttpServletRequest request,
			@Valid final SparSopPaymentDetailsForm walletPaymentDetailsForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, NumberFormatException
	{
		final String customerWalletAmount = request.getParameter("customerWalletAmount");
		CalculatedWalletData calculatedWalletData = new CalculatedWalletData();

		final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
		//Call to calculate wallet changes.
		calculatedWalletData = sparDefaultPaymentFacade.changeOnWalletApplied(cartData, Double.parseDouble(customerWalletAmount));
		sessionService.setAttribute("calculatedWalletData", calculatedWalletData);
		request.setAttribute("calculatedWalletData", calculatedWalletData);
		return calculatedWalletData;

	}

	/**
	 * This method is used to handle wallet calculation.
	 *
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.POST)
	@RequireHardLogIn
	public String confirmWalletPayment(final HttpServletRequest request,
			@Valid final SparSopPaymentDetailsForm sparSopPaymentDetailsForm, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException
	{
		final Map<String, String> resultMap = getRequestParameterMap(request);
		setWalletParam(resultMap);
		final CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService
				.getAttribute("calculatedWalletData");
		if (null != calculatedWalletData && null != calculatedWalletData.getOrderWalletAmount()
				&& calculatedWalletData.getOrderWalletAmount().getValue().equals(BigDecimal.valueOf(0.0d)))
		{
			final boolean isWalletApplied = PaymentModeEnum.WALLET.getCode().equals(sparSopPaymentDetailsForm.getPaymentMode());
			final boolean savePaymentInfo = false;
			//Save PaymentMode in CartModel
			getSparDefaultPaymentFacade().setPaymentMode(
					getSparCheckoutFacade().getPaymentModeForCode(PaymentModeEnum.WALLET.getCode()), PaymentStatus.PAID);
			getSparDefaultPaymentFacade().setCalculated(Boolean.valueOf(isWalletApplied));
			this.getSparDefaultPaymentFacade().completeWalletDeliveryCreateSubscription(resultMap, savePaymentInfo, isWalletApplied,
					calculatedWalletData);
			final CartData cartData = sparDefaultCheckoutFacade.getCheckoutCart();
			cartData.setBalanceDue(calculatedWalletData.getOrderWalletAmount());
			model.addAttribute("walletPaymentDetailsForm", new PaymentDetailsForm());
			model.addAttribute("cartData", cartData);
			sessionService.removeAttribute("calculatedWalletData");
			return getCheckoutStep().nextStep();
		}
		else
		{
			return getCheckoutStep().currentStep();
		}
	}

	/**
	 * This method is used to handle wallet calculation.
	 *
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@ResponseBody
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doCancelWalletPayment(final HttpServletRequest request) throws CMSItemNotFoundException, NumberFormatException
	{
		CalculatedWalletData calculatedWalletData = (CalculatedWalletData) sessionService.getAttribute("calculatedWalletData");
		calculatedWalletData = sparDefaultPaymentFacade.refreshWallet(calculatedWalletData);
		request.setAttribute("calculatedWalletData", null);
		sessionService.setAttribute("calculatedWalletData", null);
		sessionService.removeAttribute("calculatedWalletData");
		return null;
	}

	/**
	 * Set COD Parameters
	 *
	 * @param resultMap
	 */
	private void setWalletParam(final Map<String, String> resultMap)
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
