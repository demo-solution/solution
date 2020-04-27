package com.spar.hcl.category.service.impl;

import de.hybris.platform.category.constants.CategoryConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.FlexibleSearchUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.spar.hcl.category.service.SparCategoryService;


public class SparCategoryServiceImpl extends AbstractBusinessService implements SparCategoryService
{
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	@SuppressWarnings("deprecation")
	public List<ProductModel> findProductsByCategory(final CategoryModel category, final int start, final int count)
	{
		final Map params = new HashMap();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT {p:").append(ProductModel.PK).append("} ");
		stringBuilder.append("FROM {").append(ProductModel._TYPECODE).append(" AS p ");
		stringBuilder.append("JOIN ").append(CategoryConstants.Relations.CATEGORYPRODUCTRELATION).append(" AS l ");
		stringBuilder.append("ON {l:").append(LinkModel.TARGET).append("}={p:").append(ProductModel.PK).append("} } ");

		final Collection<CategoryModel> cats = new ArrayList<CategoryModel>();
		cats.add(category);

		final String inPart = FlexibleSearchUtils.buildOracleCompatibleCollectionStatement(//
				"{l:" + LinkModel.SOURCE + "} IN (?cat)",//
				"cat", "AND", cats, params//
				);//

		stringBuilder.append("WHERE ").append(inPart);
		stringBuilder.append(" ORDER BY {p:name}");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(stringBuilder.toString());
		query.setStart(start);
		query.setCount(count);
		query.setNeedTotal(true);
		query.addQueryParameters(params);
		final SearchResult<ProductModel> search = flexibleSearchService.search(query);

		return search.getResult();
	}
}
