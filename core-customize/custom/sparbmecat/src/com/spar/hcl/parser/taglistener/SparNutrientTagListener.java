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
import de.hybris.platform.bmecat.parser.taglistener.StringValueTagListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.spar.hcl.constants.SparbmecatConstants;


/**
 * Parses the &lt;Nutrient&gt; tag
 */
public class SparNutrientTagListener extends DefaultBMECatTagListener
{


	/**
	 * Default Constructor
	 */
	public SparNutrientTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 */
	public SparNutrientTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * Creating Listeners for Sub Tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparValuesTagListener(this), new StringValueTagListener(this, SparbmecatConstants.XML.TAG.NAME) });
	}

	/**
	 * Processing End Element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final String nameValue = (String) getSubTagValue(SparbmecatConstants.XML.TAG.NAME);
		if (null == nameValue)
		{
			return null;
		}
		final Object value = getSubTagValue(SparbmecatConstants.XML.TAG.VALUES);
		final Map<String, Object> nutrientMap = new HashMap<String, Object>();
		nutrientMap.put(nameValue, value);
		return nutrientMap;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.NUTRIENT;
	}
}
