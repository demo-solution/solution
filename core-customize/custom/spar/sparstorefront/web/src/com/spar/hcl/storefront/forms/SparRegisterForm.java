/**
 *
 */
package com.spar.hcl.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * @author kumari-p
 *
 */
public class SparRegisterForm extends RegisterForm
{
	private String employeeCode;
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date dateOfBirth;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date dateOfJoining;
	private Boolean whetherSubscribedToPromotion;
	private Boolean whetherSubscribedToLandmark;
	private Boolean whetherEmployee;
	private String loginVia;
	private String token;
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
	 * @return the whetherSubscribedToPromotion
	 */
	public Boolean getWhetherSubscribedToPromotion()
	{
		return whetherSubscribedToPromotion;
	}

	/**
	 * @param whetherSubscribedToPromotion
	 *           the whetherSubscribedToPromotion to set
	 */
	public void setWhetherSubscribedToPromotion(final Boolean whetherSubscribedToPromotion)
	{
		this.whetherSubscribedToPromotion = whetherSubscribedToPromotion;
	}

	/**
	 * @return the whetherSubscribedToLandmark
	 */
	public Boolean getWhetherSubscribedToLandmark()
	{
		return whetherSubscribedToLandmark;
	}

	/**
	 * @param whetherSubscribedToLandmark
	 *           the whetherSubscribedToLandmark to set
	 */
	public void setWhetherSubscribedToLandmark(final Boolean whetherSubscribedToLandmark)
	{
		this.whetherSubscribedToLandmark = whetherSubscribedToLandmark;
	}

	/**
	 * @return the loginVia
	 */
	public String getLoginVia()
	{
		return loginVia;
	}

	/**
	 * @param loginvia the loginVia to set
	 */
	public void setLoginVia(String loginvia)
	{
		this.loginVia = loginvia;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}
}
