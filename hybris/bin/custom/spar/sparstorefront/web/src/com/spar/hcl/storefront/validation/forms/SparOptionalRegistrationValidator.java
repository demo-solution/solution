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
package com.spar.hcl.storefront.validation.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.storefront.controllers.pages.SparAddressForm;


/**
 * Validator for address forms. Enforces the order of validation
 */
@Component("sparOptionalRegistrationValidator")
public class SparOptionalRegistrationValidator implements Validator
{
	private static final int MAX_FIELD_LENGTH = 255;
	private static final int MAX_POSTCODE_LENGTH = 10;
	private static final int MAX_PHONE_LENGTH = 10;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return AddressForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SparAddressForm addressForm = (SparAddressForm) object;
		validateStandardFields(addressForm, errors);
		if (sparCustomerFacade.isdateofBirthset() == false)
		{
			if (addressForm.getDateOfBirth() == null)
			{
				errors.rejectValue("dateOfBirth", "register.dateOfBirth.invalid");
			}
			if (null != addressForm.getDateOfBirth() && !is18Above(addressForm.getDateOfBirth()))
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.lessthan18");
			}
		}
		/*
		 * if (addressForm.getSparserviceareaid() == null) { errors.rejectValue("area",
		 * "address.area.autoselect.invalid"); }
		 */
	}

	protected void validateStandardFields(final SparAddressForm addressForm, final Errors errors)
	{
		validateStringField(addressForm.getPhone(), SparAddressField.PHONE, MAX_PHONE_LENGTH, errors);



		validateStringField(addressForm.getTownCity(), SparAddressField.TOWN, MAX_FIELD_LENGTH, errors);

		validateStringField(addressForm.getArea(), SparAddressField.AREA, MAX_FIELD_LENGTH, errors);

		validateStringField(addressForm.getPostcode(), SparAddressField.POSTCODE, MAX_POSTCODE_LENGTH, errors);


		validateStringField(addressForm.getBuildingName(), SparAddressField.BUILDNAME, MAX_FIELD_LENGTH, errors);

		validateStringField(addressForm.getLine2(), SparAddressField.LINE2, MAX_FIELD_LENGTH, errors);


		validateStringField(addressForm.getLine1(), SparAddressField.LINE1, MAX_FIELD_LENGTH, errors);






	}

	protected static void validateStringField(final String sparAddressField, final SparAddressField fieldType,
			final int maxFieldLength, final Errors errors)
	{
		if (sparAddressField == null || StringUtils.isEmpty(sparAddressField)
				|| (StringUtils.length(sparAddressField) > maxFieldLength))
		{
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
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


	protected enum SparAddressField
	{
		PHONE("phone", "address.phone.invalid"), DOB("dateOfBirth", "address.dob.invalid"), TOWN("townCity",
				"address.townCity.invalid"), AREA("area", "address.area.invalid"), POSTCODE("postcode", "address.postcode.invalid"), BUILDNAME(
				"buildingName", "address.buildingname.invalid"), LINE1("line1", "address.line1.invalid"), LINE2("line2",
				"address.line2.invalid");

		private final String fieldKey;
		private final String errorKey;

		private SparAddressField(final String fieldKey, final String errorKey)
		{
			this.fieldKey = fieldKey;
			this.errorKey = errorKey;
		}

		public String getFieldKey()
		{
			return fieldKey;
		}

		public String getErrorKey()
		{
			return errorKey;
		}
	}
}
