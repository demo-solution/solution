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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;


/**
 * Parses the &lt;Categorisation&gt; tag
 */
public class SparCategorisationTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default Constructor
	 */
	public SparCategorisationTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparCategorisationTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Create Listeners for Sub Tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new StringValueTagListener(this, SparbmecatConstants.XML.TAG.LEVEL) });
	}

	/**
	 * Processing end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final Object value = getSubTagValue(SparbmecatConstants.XML.TAG.LEVEL);
		processProductCategoriesLevel(value);
		return categoryFeature;
	}

	@SuppressWarnings("unchecked")
	private void processProductCategoriesLevel(final Object value)
	{
		if (value instanceof ArrayList && ((ArrayList<String>) value).size() == 3)
		{
			final ArrayList<String> list = ((ArrayList<String>) value);
			categoryFeature.setCategoryLevel1(list.get(0));
			categoryFeature.setCategoryLevel2(list.get(1));
			categoryFeature.setCategoryLevel3(list.get(2));
		}
	}

	/**
	 * Getter of Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.CATEGORISATION;
	}
}
