/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.v2.helper;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.spar.hcl.util.ws.SearchQueryCodec;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@Component
public class ProductsHelper extends AbstractHelper
{
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "cwsSearchQueryCodec")
	private SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

	//@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'DTO',#query,#currentPage,#pageSize,#sort,#fields)")
	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
			final String fields)
	{
		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return dataMapper.map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return dataMapper.map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	//@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'Data',#query,#currentPage,#pageSize,#sort)")
	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
			final int pageSize, final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = createPageableData(currentPage, pageSize, sort);

		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = productSearchFacade.textSearch(
				solrSearchStateConverter.convert(searchQueryData), pageable);
		return sourceResult;
	}

	@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'DTO',#query,#categoryId,#currentPage,#pageSize,#sort,#fields)")
	public ProductCategorySearchPageWsDTO searchProductsForCategory(final String query, final int currentPage, final int pageSize,
			final String sort, final String fields, final String categoryId)
	{
		if (null != categoryId && !(categoryId.isEmpty()))
		{
			final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> sourceResult = searchProductsForCategory(
					query, currentPage, pageSize, sort, categoryId);
			return dataMapper.map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}
		return null;

	}

	@Cacheable(value = "productSearchCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'Data',#query,#categoryId,#currentPage,#pageSize,#sort)")
	public ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchProductsForCategory(final String query,
			final int currentPage, final int pageSize, final String sort, final String categoryId)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = createPageableData(currentPage, pageSize, sort);

		final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> sourceResult = productSearchFacade
				.categorySearch(categoryId, solrSearchStateConverter.convert(searchQueryData), pageable);
		//final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> sourceResult = productSearchFacade
		//		.categorySearch(categoryId);
		return sourceResult;
	}


}
