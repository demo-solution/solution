/**
 *
 */
package com.spar.hcl.core.util;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.product.ProductService;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author jitendriya.m
 *
 */
public class SparTitleResolver extends PageTitleResolver
{

	private ProductService productService;
	private CommerceCategoryService commerceCategoryService;
	private CMSSiteService cmsSiteService;


	/**
	 * @return the productService
	 */
	@Override
	public ProductService getProductService()
	{
		return productService;
	}


	/**
	 * @param productService
	 *           the productService to set
	 */
	@Override
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


	/**
	 * @return the commerceCategoryService
	 */
	@Override
	public CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}


	/**
	 * @param commerceCategoryService
	 *           the commerceCategoryService to set
	 */
	@Override
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}


	/**
	 * @return the cmsSiteService
	 */
	@Override
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}


	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	@Override
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}


	@Override
	public String resolveCategoryPageTitle(final CategoryModel category)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		//final List<CategoryModel> categories = this.getCategoryPath(category);
		//for (final CategoryModel c : categories)
		//{
		if (null != category.getSparCategoryTitle() && StringUtils.isNotEmpty(category.getSparCategoryTitle()))
		{
			stringBuilder.append(category.getSparCategoryTitle()).append(TITLE_WORD_SEPARATOR);
		}
		else
		{
			stringBuilder.append(category.getName()).append(TITLE_WORD_SEPARATOR);
		}

		if (category.getSparTitle() != null)
		{
			stringBuilder.append(category.getSparTitle()).append(TITLE_WORD_SEPARATOR);
		}
		//}

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		if (currentSite != null)
		{
			stringBuilder.append(currentSite.getName());
		}

		return StringEscapeUtils.escapeHtml(stringBuilder.toString());
	}
}
