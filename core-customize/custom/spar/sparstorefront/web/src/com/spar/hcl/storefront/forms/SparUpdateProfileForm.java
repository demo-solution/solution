/**
 *
 */
package com.spar.hcl.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateProfileForm;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * @author madan.d
 *
 */
public class SparUpdateProfileForm extends UpdateProfileForm
{


	private Boolean whetherEmployee;
	private String employeeCode;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date dateOfJoining;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date dateOfBirth;

	/**
	 * @return the whetherEmployee
	 */
	public Boolean getWhetherEmployee()
	{
		return whetherEmployee;
	}

	/**
	 * @param whetherEmployee
	 *           the whetherEmployee to set
	 */
	public void setWhetherEmployee(final Boolean whetherEmployee)
	{
		this.whetherEmployee = whetherEmployee;
	}

	/**
	 * @return the employeeCode
	 */
	public String getEmployeeCode()
	{
		return employeeCode;
	}

	/**
	 * @param employeeCode
	 *           the employeeCode to set
	 */
	public void setEmployeeCode(final String employeeCode)
	{
		this.employeeCode = employeeCode;
	}

	/**
	 * @return the dateOfJoining
	 */
	public Date getDateOfJoining()
	{
		return dateOfJoining;
	}

	/**
	 * @param dateOfJoining
	 *           the dateOfJoining to set
	 */

	public void setDateOfJoining(final Date dateOfJoining)
	{
		this.dateOfJoining = dateOfJoining;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *           the dateOfBirth to set
	 */
	public void setDateOfBirth(final Date dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}



}
