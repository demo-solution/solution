/**
 *
 */
package com.spar.hcl.parser;

import com.spar.hcl.constants.SparbmecatConstants;



/**
 * Object which holds the value of a parsed &lt;RequestDimension&gt; tag.
 * 
 * @author rohan_c
 * 
 */
public class SparRequestedDimensions
{

	private Integer Height = new Integer(0);
	private Integer Width = new Integer(0);
	private String unitsOfSpecification = "";

	/**
	 * Getter
	 * 
	 * @return the height
	 */
	public Integer getHeight()
	{
		return Height;
	}

	/**
	 * Setter
	 * 
	 * @param height
	 *           the height to set
	 */
	public void setHeight(final Integer height)
	{
		Height = height;
	}

	/**
	 * Getter
	 * 
	 * @return the width
	 */
	public Integer getWidth()
	{
		return Width;
	}

	/**
	 * Setter
	 * 
	 * @param width
	 *           the width to set
	 */
	public void setWidth(final Integer width)
	{
		Width = width;
	}

	/**
	 * Getter
	 * 
	 * @return the unitsOfSpecification
	 */
	public String getUnitsOfSpecification()
	{
		return unitsOfSpecification;
	}

	/**
	 * Setter
	 * 
	 * @param unitsOfSpecification
	 *           the unitsOfSpecification to set
	 */
	public void setUnitsOfSpecification(final String unitsOfSpecification)
	{
		this.unitsOfSpecification = unitsOfSpecification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final String dimension = this.getWidth() + SparbmecatConstants.WIDTH + SparbmecatConstants.CROSS + this.getHeight()
				+ SparbmecatConstants.HEIGHT;
		return dimension;
	}
}
