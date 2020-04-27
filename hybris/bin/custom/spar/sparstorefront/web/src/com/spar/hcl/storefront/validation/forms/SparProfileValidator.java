/**
 *
 */
package com.spar.hcl.storefront.validation.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.storefront.forms.SparUpdateProfileForm;


/**
 * @author madan.d
 *
 */
@Component("sparProfileValidator")
public class SparProfileValidator extends ProfileValidator
{
	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SparUpdateProfileForm profileForm = (SparUpdateProfileForm) object;
		final String title = profileForm.getTitleCode();
		final String firstName = profileForm.getFirstName();
		final String lastName = profileForm.getLastName();
		final Boolean whetherEmployee = profileForm.getWhetherEmployee();
		final String employeeCode = profileForm.getEmployeeCode();

		final Date dateOfJoining = profileForm.getDateOfJoining();

		final Date dateOfbirth = profileForm.getDateOfBirth();
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		System.out.print("customer");
		System.out.print(currentCustomerData.getEmployeeCode());
		if (StringUtils.isEmpty(title))
		{
			errors.rejectValue("titleCode", "profile.title.invalid");
		}
		else if (StringUtils.length(title) > 255)
		{
			errors.rejectValue("titleCode", "profile.title.invalid");
		}

		if (StringUtils.isBlank(firstName))
		{
			errors.rejectValue("firstName", "profile.firstName.invalid");
		}
		else if (StringUtils.length(firstName) > 255)
		{
			errors.rejectValue("firstName", "profile.firstName.invalid");
		}

		if (StringUtils.isBlank(lastName))
		{
			errors.rejectValue("lastName", "profile.lastName.invalid");
		}
		else if (StringUtils.length(lastName) > 255)
		{
			errors.rejectValue("lastName", "profile.lastName.invalid");
		}
		if (sparCustomerFacade.isdateofBirthset() == false)
		{
			if (dateOfbirth == null)
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.invalid");
			}

			if (null != dateOfbirth && !is18Above(dateOfbirth))
			{

				errors.rejectValue("dateOfBirth", "register.dateOfBirth.lessthan18");
			}
		}

		if (null != whetherEmployee && whetherEmployee.booleanValue())
		{
			if (StringUtils.isBlank(employeeCode))
			{
				errors.rejectValue("employeeCode", "register.employeeCode.invalid");
			}
			if (null == currentCustomerData.getEmployeeCode())
			{
				if (isEmployeeExit(employeeCode))
				{
					errors.rejectValue("employeeCode", "register.employeeCode.alreadyexit");
				}
			}

			if (currentCustomerData.getEmployeeCode() != null)
			{
				if (!currentCustomerData.getEmployeeCode().equals(employeeCode))
				{
					if (isEmployeeExit(employeeCode))
					{
						errors.rejectValue("employeeCode", "register.employeeCode.alreadyexit");
					}
				}
			}

			if (dateOfJoining == null)
			{
				errors.rejectValue("dateOfJoining", "register.dateOfJoining.invalid");
			}
		}


	}

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

}
