/**
 *
 */
package com.spar.hcl.core.search.solrfacetsearch.provider.impl;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.CategoryCodeValueProvider;
/**
 * @author ravindra.kr
 */
public class SparCategoryNameValueProvider extends CategoryCodeValueProvider
{
	@Override
	protected Object getPropertyValue(final Object model)
	{
		CategoryModel category = (CategoryModel) model;
		if(!category.getCode().startsWith("9"))
		{
			return getPropertyValue(model, "name");
		}
		return null;
	}
}
