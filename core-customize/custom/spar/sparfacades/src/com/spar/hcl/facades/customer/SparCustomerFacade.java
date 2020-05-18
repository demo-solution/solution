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
package com.spar.hcl.facades.customer;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.io.IOException;


/**
 * Register a user with given parameters
 * */
public interface SparCustomerFacade
{
	/**
	 * Register a user with given parameters
	 *
	 * @param registerData
	 *           the user data the user will be registered with
	 * @throws IllegalArgumentException
	 *            if required data is missing
	 * @throws UnknownIdentifierException
	 *            if the title code is invalid
	 * @throws DuplicateUidException
	 *            if the login is not unique
	 */
	void register(RegisterData registerData) throws DuplicateUidException, UnknownIdentifierException, IllegalArgumentException;

	String findUserEmailId();

	void commitOTPData(final String mobilenumber, final Boolean isOTPValidated, final Boolean isRegistrationOTPVerified,
			final Boolean isCheckoutOTPVerified) throws DuplicateUidException;

	boolean isOTPValidate();

	String findPrimaryMobileNumber();

	boolean findRegistrationOTPStatus();

	boolean findCheckoutOTPStatus();

	String findFirstName();

	String findLastName();

	String findCustomerTitle();

	/**
	 * This method is used to get the set wallet for customer
	 */
	CustomerData getCurrentCustomerWallet();

	/**
	 * This method is used to get the set wallet details for customer
	 */
	CustomerData getCustomerWalletDetails();

	public boolean isdateofBirthset();

	void updateCustomerProfile(CustomerData customerData) throws DuplicateUidException;
	boolean isCustomerEmployee();
	
	void getmGageOTPResponse(final String sysGenOTPKey, final String mobilenum)  throws IOException,
	DuplicateUidException;
	
	void sendSMSToUser(final String mobilenum, final String message) throws IOException, DuplicateUidException;
	
	void sendForgetPasswordSMSToUser(final String mobilenum, final String email, final String message) throws IOException, DuplicateUidException; 

	CustomerData getUserForUID(final String emailId);

	void validateEmailAlreadyRegistered(final String email) throws DuplicateUidException;
}
