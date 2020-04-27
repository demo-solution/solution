package com.spar.hcl.validator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.spar.hcl.facades.customer.SparCustomerFacade;


public class SparAddressDataValidator implements Validator
{
	private static Pattern emailNamePtrn = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	private static final int MAX_FIELD_LENGTH = Config.getInt("sparstorefront.storefront.checkout.email", 255);
	private static final int MIN_PHONE_LENGTH = Config.getInt("sparstorefront.storefront.checkout.phone", 10);
	private static final int MIN_POSTCODE_LENGTH = Config.getInt("sparstorefront.storefront.checkout.pincode", 6);
	public static final Pattern PHONE_REGEX = Pattern.compile("\\d{10}");
	public static final Pattern PIN_REGEX = Pattern.compile("\\d{6}");
	private static final int MAX_POSTCODE_LENGTH = 10;

	@Resource(name = "sessionService")
	SessionService sessionService;

	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@Override
	public boolean supports(final Class clazz)
	{
		return AddressData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AddressData addressData = (AddressData) object;
		/*
		 * if (addressData.getFirstName().equals("cncPhoneValidation")) { validateOTPForCnC(errors); } else {
		 * validateOTP(errors, addressData);
		 */
		validateStandardField(addressData, errors);
		validateMandatoryFields(errors, addressData);


		/*
		 * if (errors.hasErrors()) { throw new WebserviceValidationException(errors); }
		 */
	}

	/**
	 * This method is used to verify whether OTP validation has been done or not
	 *
	 * @param errors
	 * @param addressData
	 */
	public void validateOTP(final Errors errors, final AddressData addressData)
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
	 * @param addressData
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
	 * @param addressData
	 *           validate email validate phone number validate postcode
	 *
	 */
	protected void validateMandatoryFields(final Errors errors, final AddressData addressData)
	{
		final String email = addressData.getEmail();
		if (StringUtils.isEmpty(email))
		{
			errors.rejectValue("email", "address.email.invalid");
		}
		else if (StringUtils.length(email) > MAX_FIELD_LENGTH || !emailNamePtrn.matcher(email).matches())
		{
			errors.rejectValue("email", "address.email.invalid");
		}

		final String phone = addressData.getPhone();
		if (StringUtils.isEmpty(phone))
		{
			errors.rejectValue("phone", "address.phone.invalid");
		}
		else if (StringUtils.length(phone) < MIN_PHONE_LENGTH || !PHONE_REGEX.matcher(phone).matches())
		{
			errors.rejectValue("phone", "address.phone.invalid");
		}

		final String postalCode = addressData.getPostalCode();
		if (StringUtils.isNotEmpty(postalCode))
		{
			if (StringUtils.length(postalCode) < MIN_POSTCODE_LENGTH || !PIN_REGEX.matcher(postalCode).matches())
			{
				errors.rejectValue("postalCode", "address.postcode.invalid");
			}
		}
		/*
		 * if (sparAddressForm.getSparserviceareaid() == null) { errors.rejectValue("area",
		 * "address.area.autoselect.invalid"); }
		 */
	}

	protected void validateStandardField(final AddressData addressData, final Errors errors)
	{
		validateStringField(addressData.getArea(), SparAddressField.AREA, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getBuildingName(), SparAddressField.BUILDINGNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getLine2(), SparAddressField.LINE2, MAX_FIELD_LENGTH, errors);
		//validateStringField(addressData.getCountry().getIsocode(), SparAddressField.COUNTRY, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getFirstName(), SparAddressField.FIRSTNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getLastName(), SparAddressField.LASTNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getLine1(), SparAddressField.LINE1, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getTown(), SparAddressField.TOWN, MAX_FIELD_LENGTH, errors);
		validateStringField(addressData.getPostalCode(), SparAddressField.POSTALCODE, MAX_POSTCODE_LENGTH, errors);
		validateStringField(addressData.getTitleCode(), SparAddressField.TITLE, MAX_FIELD_LENGTH, errors);
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
				"buildingName", "address.buildingname.invalid"), LINE2("line2", "address.line2.invalid"), POSTALCODE("postalCode",
				"address.postcode.invalid"), TITLE("titleCode", "address.title.invalid"), FIRSTNAME("firstName",
				"address.firstName.invalid"), LASTNAME("lastName", "address.lastName.invalid"), LINE1("line1",
				"address.line1.invalid"), TOWN("town", "address.townCity.invalid"), COUNTRY("countryIso", "address.country.invalid");

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
