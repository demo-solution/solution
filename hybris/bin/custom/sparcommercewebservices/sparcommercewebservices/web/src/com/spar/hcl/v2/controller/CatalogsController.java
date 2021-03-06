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
package com.spar.hcl.v2.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CatalogsData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogVersionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CategoryHierarchyWsDTO;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.webservicescommons.mapping.impl.FieldSetBuilderContext;


/**
 *
 * @pathparam catalogId Catalog identifier
 * @pathparam catalogVersionId Catalog version identifier
 * @pathparam categoryId Category identifier
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/catalogs")
public class CatalogsController extends BaseController
{
	protected static final Logger LOG = Logger.getLogger(CatalogsController.class);
	private static final Set<CatalogOption> OPTIONS;
	static
	{
		OPTIONS = getOptions();
	}

	@Resource(name = "cwsCatalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "fieldSetBuilder")
	private FieldSetBuilder fieldSetBuilder;
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "pointOfServiceValidator")
	private Validator pointOfServiceValidator;
	

	/**
	 * Returns all catalogs with versions defined for the base store.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return All catalogs defined for the base store.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CatalogListWsDTO getCatalogs(@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<CatalogData> catalogDataList = catalogFacade.getAllProductCatalogsForCurrentSite(OPTIONS);
		final CatalogsData catalogsData = new CatalogsData();
		catalogsData.setCatalogs(catalogDataList);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrecyLevel(catalogDataList));
		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(CatalogListWsDTO.class, DataMapper.FIELD_PREFIX, fields,
				context);

		final CatalogListWsDTO catalogListDto = dataMapper.map(catalogsData, CatalogListWsDTO.class, fieldSet);
		return catalogListDto;
	}


	/**
	 * Returns a information about a catalog based on its ID, along with versions defined for the current base store.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Catalog structure
	 */
	@RequestMapping(value = "/{catalogId}", method = RequestMethod.GET)
	@ResponseBody
	public CatalogWsDTO getCatalog(@PathVariable final String catalogId,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CatalogData catalogData = catalogFacade.getProductCatalogForCurrentSite(catalogId, OPTIONS);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCatalogData(catalogData));
		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(CatalogWsDTO.class, DataMapper.FIELD_PREFIX, fields, context);

		final CatalogWsDTO dto = dataMapper.map(catalogData, CatalogWsDTO.class, fieldSet);
		return dto;
	}

	/**
	 * Returns information about catalog version that exists for the current base store.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about catalog version
	 */
	@RequestMapping(value = "/{catalogId}/{catalogVersionId}", method = RequestMethod.GET)
	@ResponseBody
	public CatalogVersionWsDTO getCatalogVersion(@PathVariable final String catalogId,
			@PathVariable final String catalogVersionId, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		LOG.info("Start getCatalogVersion() of CatalogsController:::::::" + new Date());
		final Set<CatalogOption> optsForCatalogId = new HashSet<>();
		optsForCatalogId.add(CatalogOption.BASIC);
		optsForCatalogId.add(CatalogOption.CATEGORIES);
		optsForCatalogId.add(CatalogOption.SUBCATEGORIES);

		final CatalogVersionData catalogVersionData = catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogId,
				catalogVersionId, optsForCatalogId);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCatalogVersionData(catalogVersionData));
		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(CatalogVersionWsDTO.class, DataMapper.FIELD_PREFIX, fields,
				context);

		final CatalogVersionWsDTO dto = dataMapper.map(catalogVersionData, CatalogVersionWsDTO.class, fieldSet);
		LOG.info("End getCatalogVersion() of CatalogsController:::::::" + new Date());
		return dto;
	}

	/**
	 * Returns information about category that exists in a catalog version available for the current base store.
	 *
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Information about category
	 */
	@RequestMapping(value = "/{catalogId}/{catalogVersionId}/categories/{categoryId}", method = RequestMethod.GET)
	@ResponseBody
	public CategoryHierarchyWsDTO getCategories(@PathVariable final String catalogId, @PathVariable final String catalogVersionId,
			@PathVariable final String categoryId, @RequestParam(required = true) final String pickupStore,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(defaultValue = "DEFAULT,totalNumber,pageSize,numberOfPages,currentPage") final String fields)
	{
		LOG.info("Start getCategories() of CatalogsController:::::::" + new Date());
		final Set<CatalogOption> optsForCategories = new HashSet<>();
		optsForCategories.add(CatalogOption.BASIC);
		optsForCategories.add(CatalogOption.CATEGORIES);
		optsForCategories.add(CatalogOption.SUBCATEGORIES);
		//optsForCategories.add(CatalogOption.PRODUCTS);
		sessionService.setAttribute("sparProductListWS", Boolean.FALSE);

		if (StringUtils.isNotEmpty(pickupStore))
		{
			validate(pickupStore, "pickupStore", pointOfServiceValidator);
			sessionService.setAttribute("selectedPickUpStore", pickupStore);
		}

		final PageOption page = PageOption.createForPageNumberAndPageSize(currentPage, Integer.MAX_VALUE);
		final CategoryHierarchyData categoryHierarchyData = catalogFacade.getCategoryById(catalogId, catalogVersionId, categoryId,
				page, optsForCategories);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCategoryHierarchyData(1, categoryHierarchyData));
		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(CategoryHierarchyWsDTO.class, DataMapper.FIELD_PREFIX, fields,
				context);

		final CategoryHierarchyWsDTO dto = dataMapper.map(categoryHierarchyData, CategoryHierarchyWsDTO.class, fieldSet);

		final List<CategoryHierarchyWsDTO> lstSubcategories = dto.getSubcategories();
		
		if (null != lstSubcategories && lstSubcategories.size() > 0)
		{
			Collections.sort(lstSubcategories, (o1, o2) -> Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId()));
			dto.setSubcategories(lstSubcategories);
			for (final CategoryHierarchyWsDTO dto2 : lstSubcategories)
			{
				final List<CategoryHierarchyWsDTO> lstSubcategories2 = dto2.getSubcategories();
				if(null != lstSubcategories2 && lstSubcategories2.size() > 0)
				{
					Collections.sort(lstSubcategories2, (obj1, obj2) -> Integer.parseInt(obj1.getId()) - Integer.parseInt(obj2.getId()));
					dto2.setSubcategories(lstSubcategories2);
					for (final CategoryHierarchyWsDTO categoryHierarchyWsDTO2 : lstSubcategories2)
					{
						LOG.debug("Sorted subcategories :::::::::::::::::::" + categoryHierarchyWsDTO2.getId());
					}
				}
			}
		}

		LOG.info("End getCategories() of CatalogsController:::::::" + new Date());
		return dto;
	}

	private static Set<CatalogOption> getOptions()
	{
		final Set<CatalogOption> opts = new HashSet<>();
		opts.add(CatalogOption.BASIC);
		opts.add(CatalogOption.CATEGORIES);
		opts.add(CatalogOption.SUBCATEGORIES);
		return opts;
	}

	private int countRecurrecyLevel(final List<CatalogData> catalogDataList)
	{
		int recurrencyLevel = 1;
		int value;
		for (final CatalogData catalog : catalogDataList)
		{
			value = countRecurrencyForCatalogData(catalog);
			if (value > recurrencyLevel)
			{
				recurrencyLevel = value;
			}
		}
		return recurrencyLevel;
	}

	private int countRecurrencyForCatalogData(final CatalogData catalog)
	{
		int retValue = 1;
		int value;
		for (final CatalogVersionData version : catalog.getCatalogVersions())
		{
			value = countRecurrencyForCatalogVersionData(version);
			if (value > retValue)
			{
				retValue = value;
			}
		}
		return retValue;
	}

	private int countRecurrencyForCatalogVersionData(final CatalogVersionData catalogVersion)
	{
		int retValue = 1;
		int value;
		for (final CategoryHierarchyData hierarchy : catalogVersion.getCategoriesHierarchyData())
		{
			value = countRecurrencyForCategoryHierarchyData(1, hierarchy);
			if (value > retValue)
			{
				retValue = value;
			}
		}
		return retValue;
	}

	private int countRecurrencyForCategoryHierarchyData(int currentValue, final CategoryHierarchyData hierarchy)
	{
		currentValue++;
		int retValue = currentValue;
		int value;
		for (final CategoryHierarchyData subcategory : hierarchy.getSubcategories())
		{
			value = countRecurrencyForCategoryHierarchyData(currentValue, subcategory);
			if (value > retValue)
			{
				retValue = value;
			}
		}
		return retValue;
	}
}
