/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.product.data.CategoryData;


/**
 * @author nikhil-ku
 *
 */
public class SparCategoryData extends CategoryData
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object abc)
	{
		final CategoryData categoryData = (CategoryData) abc;
		return this.getCode().equals(categoryData.getCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// YTODO Auto-generated method stub
		return this.getCode().hashCode();
	}

}
