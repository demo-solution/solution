/**
 *
 */
package com.spar.hcl.storefront.validation.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.storefront.controllers.pages.SparAddressForm;


/**
 *
 */
@Component("sparAddressValidator")
public class SparAddressValidator extends AddressValidator
{
	private static Pattern emailNamePtrn = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	private static final int MAX_FIELD_LENGTH = Config.getInt("sparstorefront.storefront.checkout.email", 255);
	private static final int MIN_PHONE_LENGTH = Config.getInt("sparstorefront.storefront.checkout.phone", 10);
	private static final int MIN_POSTCODE_LENGTH = Config.getInt("sparstorefront.storefront.checkout.pincode", 6);
	public static final Pattern PHONE_REGEX = Pattern.compile("\\d{10}");
	public static final Pattern PIN_REGEX = Pattern.compile("\\d{6}");

	/* Code change start by sumit */
	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	/* Code change end here */

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return SparAddressForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{

		final SparAddressForm sparAddressForm = (SparAddressForm) object;
		if (sparAddressForm.getFirstName().equals("cncPhoneValidation"))
		{
			validateOTPForCnC(errors);
		}
		else
		{
			super.validate(object, errors);
			validateOTP(errors, sparAddressForm);
			validateStandardField(sparAddressForm, errors);
			validateMandatoryFields(errors, sparAddressForm);
			if (sparCustomerFacade.isdateofBirthset() == false)
			{
				if (sparAddressForm.getDateOfBirth() == null)
				{
					errors.rejectValue("dateOfBirth", "register.dateOfBirth.invalid");
				}
				if (null != sparAddressForm.getDateOfBirth() && !is18Above(sparAddressForm.getDateOfBirth()))
				{

					errors.rejectValue("dateOfBirth", "register.dateOfBirth.lessthan18");
				}
			}
			if (null != sparAddressForm.getLongAddress() && !sparAddressForm.getLongAddress().equals("")
					&& !sparAddressForm.getLongAddress().contains(sparAddressForm.getTownCity()))
			{
				errors.rejectValue("longAddress", "storefinder.localityarea.error");
			}
		}

	}


	/**
	 * This method is used to verify whether OTP validation has been done or not
	 *
	 * @param errors
	 * @param sparAddressForm
	 */
	public void validateOTP(final Errors errors, final SparAddressForm sparAddressForm)
	{
		if (!sparCustomerFacade.findRegistrationOTPStatus())
		{
			if (!sparCustomerFacade.findCheckoutOTPStatus())
			{
				errors.rejectValue("phone", "phone.verfity.incomplete");
			}
		}
	}

	/**
	 * This method is used to verify whether OTP validation has been done or not for CNC
	 *
	 * @param errors
	 * @param PickupInStoreForm
	 */
	public void validateOTPForCnC(final Errors errors)
	{
		if (!sparCustomerFacade.findRegistrationOTPStatus())
		{
			if (!sparCustomerFacade.findCheckoutOTPStatus())
			{
				errors.rejectValue("phone", "phone.verfity.incomplete");
			}
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

	/**
	 * @param errors
	 * @param sparAddressForm
	 *           validate email validate phone number validate postcode
	 *
	 */
	protected void validateMandatoryFields(final Errors errors, final SparAddressForm sparAddressForm)
	{
		final String email = sparAddressForm.getEmail();
		if (StringUtils.isEmpty(email))
		{
			errors.rejectValue("email", "address.email.invalid");
		}
		else if (StringUtils.length(email) > MAX_FIELD_LENGTH || !emailNamePtrn.matcher(email).matches())
		{
			errors.rejectValue("email", "address.email.invalid");
		}

		final String phone = sparAddressForm.getPhone();
		if (StringUtils.isEmpty(phone))
		{
			errors.rejectValue("phone", "address.phone.invalid");
		}
		else if (StringUtils.length(phone) < MIN_PHONE_LENGTH || !PHONE_REGEX.matcher(phone).matches())
		{
			errors.rejectValue("phone", "address.phone.invalid");
		}


		final String longAddress = sparAddressForm.getLongAddress();
		if (StringUtils.isEmpty(longAddress))
		{
			errors.rejectValue("longAddress", "address.longAddress.invalid");

		}



		final String postcode = sparAddressForm.getPostcode();
		if (StringUtils.isNotEmpty(postcode))
		{
			if (StringUtils.length(postcode) < MIN_POSTCODE_LENGTH || !PIN_REGEX.matcher(postcode).matches())
			{
				errors.rejectValue("postcode", "address.postcode.invalid");
			}
		}

		/*
		 * if (sparAddressForm.getSparserviceareaid() == null) { errors.rejectValue("area",
		 * "address.area.autoselect.invalid"); }
		 */
	}


	protected void validateStandardField(final SparAddressForm sparAddressForm, final Errors errors)
	{

		validateStringField(sparAddressForm.getArea(), SparAddressField.AREA, MAX_FIELD_LENGTH, errors);

		validateStringField(sparAddressForm.getBuildingName(), SparAddressField.BUILDINGNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(sparAddressForm.getLine2(), SparAddressField.LINE2, MAX_FIELD_LENGTH, errors);


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

	protected enum SparAddressField
	{
		PHONE("phone", "address.phone.invalid"), EMAIL("email", "address.email.invalid"), AREA("area", "address.area.invalid"), BUILDINGNAME(
				"buildingName", "address.buildingname.invalid"), LINE2("line2", "address.line2.invalid"), POSTCODE("postcode",
				"address.pincode.invalid");
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
