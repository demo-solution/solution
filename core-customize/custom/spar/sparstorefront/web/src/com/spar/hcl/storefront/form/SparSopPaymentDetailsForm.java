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
package com.spar.hcl.storefront.form;

import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;

import java.util.HashMap;
import java.util.Map;


/**
 * This is an extended for of SopPaymentDetailsForm to support Payment Modes and parameters for IPG
 *
 * @author rohan_c
 *
 */
public class SparSopPaymentDetailsForm extends SopPaymentDetailsForm
{
	private String paymentMode;
	private Double paidByWallet;
	private Double paidByLandmarkReward;
	
	/**
	 * @return the paidByLandmarkReward
	 */
	public Double getPaidByLandmarkReward()
	{
		return paidByLandmarkReward;
	}

	/**
	 * @param paidByLandmarkReward the paidByLandmarkReward to set
	 */
	public void setPaidByLandmarkReward(Double paidByLandmarkReward)
	{
		this.paidByLandmarkReward = paidByLandmarkReward;
	}

	/**
	 * @return the paidByWallet
	 */
	public Double getPaidByWallet()
	{
		return paidByWallet;
	}

	/**
	 * @param paidByWallet
	 *           the paidByWallet to set
	 */
	public void setPaidByWallet(final Double paidByWallet)
	{
		this.paidByWallet = paidByWallet;
	}

	/**
	 * This method is overridden for IPG request parameters
	 */
	@Override
	public Map<String, String> getSignatureParams()
	{
		if (getParameters() != null)
		{
			final Map<String, String> signatureParams = new HashMap<String, String>();
			signatureParams.put("txntype", getParameters().get("txntype"));
			signatureParams.put("timezone", getParameters().get("timezone"));
			signatureParams.put("txndatetime", getParameters().get("txndatetime"));
			signatureParams.put("hash", getParameters().get("hash"));
			signatureParams.put("currency", getParameters().get("currency"));
			signatureParams.put("storename", getParameters().get("storename"));
			signatureParams.put("chargetotal", getParameters().get("chargetotal"));
			signatureParams.put("language", getParameters().get("language"));
			signatureParams.put("orderId", getParameters().get("orderId"));
			signatureParams.put("hash_algorithm", getParameters().get("hash_algorithm"));
			signatureParams.put("mode", getParameters().get("mode"));
			signatureParams.put("responseSuccessURL", getParameters().get("orderPage_receiptResponseURL"));
			signatureParams.put("responseFailURL", getParameters().get("orderPage_declineResponseURL"));

			return signatureParams;
		}
		return null;
	}

	/**
	 * Getter
	 *
	 * @return the paymentMode
	 */
	public String getPaymentMode()
	{
		return paymentMode;
	}

	/**
	 * Setter
	 *
	 * @param paymentMode
	 *           the paymentMode to set
	 */
	public void setPaymentMode(final String paymentMode)
	{
		this.paymentMode = paymentMode;
	}


}
