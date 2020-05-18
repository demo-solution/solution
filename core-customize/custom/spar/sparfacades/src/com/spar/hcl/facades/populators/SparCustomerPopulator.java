/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;

import java.text.SimpleDateFormat;


/**
 * @author madan.d
 *
 */
public class SparCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	final SimpleDateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		populateCustPrimaryMobNumber(source, target);
		populateDateOfBirth(source, target);
		populateWhetherEmployee(source, target);
		populateDateofJoining(source, target);
		populateEmployeecode(source, target);
		target.setIsOTPValidate(source.getIsOTPValidate());
		if(null == source.getLrOptStatus())
		{
			target.setLrOptStatus(Boolean.FALSE);
		}
		else
		{
			target.setLrOptStatus(source.getLrOptStatus());
		}
		if(null == source.getIsEnrolledToLR())
		{
			target.setIsEnrolledToLR(Boolean.FALSE);
		}
		else
		{
			target.setIsEnrolledToLR(source.getIsEnrolledToLR());
		}
		if(null == source.getDisableLRPrompt())
		{
			target.setDisableLRPrompt(Boolean.FALSE);
		}
		else
		{
			target.setDisableLRPrompt(source.getDisableLRPrompt());
		}
		if(null != source.getLoginVia())
		{
			target.setLoginVia(source.getLoginVia());
		}
	}

	private void populateCustPrimaryMobNumber(final CustomerModel source, final CustomerData target)
	{
		if (source.getCustPrimaryMobNumber() != null)
		{
			target.setCustPrimaryMobNumber(source.getCustPrimaryMobNumber());
		}
	}

	private void populateDateOfBirth(final CustomerModel source, final CustomerData target)
	{
		if (source.getDateOfBirth() != null)
		{
			target.setDateOfBirth(source.getDateOfBirth());
		}
	}

	private void populateWhetherEmployee(final CustomerModel source, final CustomerData target)
	{
		if (source.getWhetherEmployee() != null)
		{
			target.setWhetherEmployee(source.getWhetherEmployee());
		}
	}

	private void populateDateofJoining(final CustomerModel source, final CustomerData target)
	{
		if (source.getDateOfJoining() != null)
		{
			target.setDateOfJoining(source.getDateOfJoining());
		}
	}

	private void populateEmployeecode(final CustomerModel source, final CustomerData target)
	{
		if (source.getEmployeeCode() != null)
		{
			target.setEmployeeCode(source.getEmployeeCode());
		}
	}
}
