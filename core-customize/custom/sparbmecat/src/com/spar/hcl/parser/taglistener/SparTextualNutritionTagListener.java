/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.parser.taglistener;

import de.hybris.bootstrap.xml.ParseAbortException;
import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.parser.BMECatObjectProcessor;
import de.hybris.platform.bmecat.parser.taglistener.DefaultBMECatTagListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * Parses the &lt;TextualNutrition&gt; tag
 */
public class SparTextualNutritionTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparTextualNutritionTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 * @param categoryFeature
	 */
	public SparTextualNutritionTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Creating Listeners for Sub tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparHeadingsTagListener(this, categoryFeature), new SparNutrientTagListener(this) });
	}

	/**
	 * Process End Element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{

		final String attributeName = getAttribute(SparbmecatConstants.XML.ATTRIBUTE.NAME_LOOKUPS.NAME);
		final Object nutrients = getSubTagValue(SparbmecatConstants.XML.TAG.NUTRIENT);
		final Object headings = getSubTagValue(SparbmecatConstants.XML.TAG.HEADINGS);
		String nutritionValue = "";
		if (headings instanceof String)
		{
			nutritionValue = processNutrientElementData((String) headings, nutrients, -1);
		}
		else if (headings instanceof ArrayList)
		{
			int position = 0;
			for (final String heading : (ArrayList<String>) headings)
			{
				nutritionValue = nutritionValue + "#&" + processNutrientElementData(heading, nutrients, position);
				position++;
			}
		}
		SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, nutritionValue, SparbmecatConstants.VALUE_DELIMITER
				+ SparbmecatConstants.SPACE);
		return categoryFeature;

	}

	protected String processNutrientElementData(final String heading, final Object nutrients, final int position)
	{
		String nutritionValue = null;
		if (nutrients instanceof Map)
		{
			String nutrientsValue = "";
			final Map<String, Object> nutrientsMap = (Map<String, Object>) nutrients;
			for (final String nutrientName : nutrientsMap.keySet())
			{
				final Object nutientValue = nutrientsMap.get(nutrientName);
				if (nutientValue instanceof String)
				{
					nutrientsValue = nutrientsValue + nutrientName + ":" + nutrientsMap.get(nutrientName) + ",";
				}
				else if (nutientValue instanceof ArrayList)
				{
					final List<String> nutientValueList = (ArrayList<String>) nutientValue;
					final String value = nutientValueList.get(position) == null || nutientValueList.get(position).isEmpty() ? ""
							: nutientValueList.get(position);
					nutrientsValue = nutrientsValue + nutrientName + ":" + value + ",";
				}
			}

			nutrientsValue = nutrientsValue.substring(0, nutrientsValue.length() - 1);
			nutritionValue = heading + ":" + nutrientsValue;
		}

		return nutritionValue;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.TEXTUAL_NUTRITION;
	}
}
