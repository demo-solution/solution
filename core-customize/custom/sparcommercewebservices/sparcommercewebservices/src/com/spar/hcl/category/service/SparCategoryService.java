package com.spar.hcl.category.service;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


public interface SparCategoryService
{
	public List<ProductModel> findProductsByCategory(final CategoryModel category, final int start, final int count);
}
