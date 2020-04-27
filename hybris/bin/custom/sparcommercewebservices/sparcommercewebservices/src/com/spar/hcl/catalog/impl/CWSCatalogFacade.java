package com.spar.hcl.catalog.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.catalog.impl.DefaultCatalogFacade;

import java.util.Set;


public class CWSCatalogFacade extends DefaultCatalogFacade
{

	private static final String SLASH = "/";

	@Override
	public CategoryHierarchyData getCategoryById(final String catalogId, final String catalogVersionId, final String categoryId,
			final PageOption page, final Set<CatalogOption> opts)
	{
		final CategoryHierarchyData data = new CategoryHierarchyData();
		data.setUrl(catalogId + SLASH + catalogVersionId);

		final CatalogVersionModel catalogVersionModel = getProductCatalogVersionModelForBaseSite(catalogId, catalogVersionId);
		final CategoryModel category = getCategoryService().getCategoryForCode(catalogVersionModel, categoryId);

		getCategoryHierarchyPopulator().populate(category, data, opts, page);
		return data;
	}
}
