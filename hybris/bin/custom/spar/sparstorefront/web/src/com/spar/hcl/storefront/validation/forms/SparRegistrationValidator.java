/**
 * @author kumari-p
 *
 */
package com.spar.hcl.storefront.validation.forms;

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

import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.RegistrationValidator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.spar.hcl.storefront.forms.SparRegisterForm;


/**
 * Validates registration forms.
 */
@Component("sparRegistrationValidator")
public class SparRegistrationValidator extends RegistrationValidator
{

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	public static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

	//public static final passwordpattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,10}";

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return SparRegisterForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{

		final RegisterForm registerForm = (RegisterForm) object;
		final String email = registerForm.getEmail();
		final String password = registerForm.getPwd();
		final SparRegisterForm sparRegisterForm = (SparRegisterForm) object;

		final Boolean whetherEmployee = (sparRegisterForm.getWhetherEmployee());

		final String employeeCode = sparRegisterForm.getEmployeeCode();



		super.validate(object, errors);
		if (StringUtils.length(password) < 8)
		{
			errors.rejectValue("pwd", "register.password.invalid");

		}
		super.validateEmailAddress(email);
		if (whetherEmployee.booleanValue())
		{
			if (StringUtils.isBlank(employeeCode))
			{
				errors.rejectValue("employeeCode", "register.employeeCode.invalid");
			}
			if (isEmployeeExit(employeeCode))
			{
				errors.rejectValue("employeeCode", "register.employeeCode.alreadyexit");
			}
			if (sparRegisterForm.getDateOfBirth() == null)
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.invalid");
			}

			if (null != sparRegisterForm.getDateOfBirth() && !is18Above(sparRegisterForm.getDateOfBirth()))
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.lessthan18");
			}
			if (sparRegisterForm.getDateOfJoining() == null)
			{
				errors.rejectValue("dateOfJoining", "register.dateOfJoining.invalid");
			}
			else if (null != sparRegisterForm.getDateOfBirth()
					&& sparRegisterForm.getDateOfBirth().after(sparRegisterForm.getDateOfJoining()))
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.after.dateOfJoining.invalid");
			}
			else if (null != sparRegisterForm.getDateOfBirth() && sparRegisterForm.getDateOfBirth().after(new Date()))
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.futureDate.invalid");
			}
			else if (null != sparRegisterForm.getDateOfJoining() && sparRegisterForm.getDateOfJoining().after(new Date()))
			{

				errors.rejectValue("dateOfJoining", "register.dateOfJoining.futureDate.invalid");
			}



		}

		if (sparRegisterForm.getWhetherSubscribedToLandmark().booleanValue() == false)
		{


			errors.rejectValue("whetherSubscribedToLandmark", "register.whetherSubscribedToLandmark.invalid");
		}

	}

	public boolean isEmployeeExit(final String employeeCode)
	{
		final CustomerModel customer = new CustomerModel();
		customer.setEmployeeCode(employeeCode);

		try
		{
			final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);
			if (customerModel != null)
			{
				return true;
			}
		}

		catch (final ModelNotFoundException e)
		{
			return false;
		}
		catch (final AmbiguousIdentifierException e)
		{
			return true;
		}
		return false;
	}

	public boolean isAlphaNumeric(final String s)
	{
		final String pattern = "^[a-zA-Z0-9]*$";
		if (s.matches(pattern))
		{
			return true;
		}
		return false;
	}

	/**
	 * This method is used to verify whether user is over 18 years of age
	 *
	 * @param DOB
	 */
	public boolean is18Above(final Date d)
	{

		final Date dt2 = new Date();

		final int diffInDays = (int) ((dt2.getTime() - d.getTime()) / (1000 * 60 * 60 * 24));

		if (diffInDays < 18 * 365)
		{

			return false;
		}

		return true;
	}


}
