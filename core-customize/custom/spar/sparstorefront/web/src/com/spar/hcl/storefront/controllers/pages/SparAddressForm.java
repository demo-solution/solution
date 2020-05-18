/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


/**
 *
 */
public class SparAddressForm extends AddressForm
{
	private String email;
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date dateOfBirth;
	private String area;
	private String buildingName;
	private String landmark;
	private String deliveryType;
	private Boolean OTP;
	//Added for google location change
	private String longAddress;


	//private Integer sparserviceareaid;


	/**
	 * @return the sparserviceareaid
	 */
	/*
	 * public Integer getSparserviceareaid() { return sparserviceareaid; }
	 *//**
	 * @param sparserviceareaid
	 *           the sparserviceareaid to set
	 */
	/*
	 * public void setSparserviceareaid(final Integer sparserviceareaid) { this.sparserviceareaid = sparserviceareaid; }
	 */

	/**
	 * @return the oTP
	 */
	public Boolean getOTP()
	{
		return OTP;
	}

	/**
	 * @param oTP
	 *           the oTP to set
	 */
	public void setOTP(final Boolean oTP)
	{
		OTP = oTP;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
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
	 * @return the area
	 */
	public String getArea()
	{
		return area;
	}

	/**
	 * @param area
	 *           the area to set
	 */
	public void setArea(final String area)
	{
		this.area = area;
	}

	/**
	 * @return the buildingName
	 */
	public String getBuildingName()
	{
		return buildingName;
	}

	/**
	 * @param buildingName
	 *           the buildingName to set
	 */
	public void setBuildingName(final String buildingName)
	{
		this.buildingName = buildingName;
	}

	/**
	 * @return the landmark
	 */
	public String getLandmark()
	{
		return landmark;
	}

	/**
	 * @param landmark
	 *           the landmark to set
	 */
	public void setLandmark(final String landmark)
	{
		this.landmark = landmark;
	}

	/**
	 * @return the deliveryType
	 */
	public String getDeliveryType()
	{
		return deliveryType;
	}

	/**
	 * @param deliveryType
	 *           the deliveryType to set
	 */
	public void setDeliveryType(final String deliveryType)
	{
		this.deliveryType = deliveryType;
	}

	/**
	 * @return the longAddress
	 */
	public String getLongAddress()
	{
		return longAddress;
	}

	/**
	 * @param longAddress
	 *           the longAddress to set
	 */
	public void setLongAddress(final String longAddress)
	{
		this.longAddress = longAddress;
	}



}
