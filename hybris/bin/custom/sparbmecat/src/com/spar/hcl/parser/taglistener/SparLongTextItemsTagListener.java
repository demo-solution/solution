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

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * Parses the &lt;LongTextItems&gt; tag
 */
public class SparLongTextItemsTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparLongTextItemsTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparLongTextItemsTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Creating Listeners for Sub Tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new StringValueTagListener(this, SparbmecatConstants.XML.TAG.TEXT) });
	}

	/**
	 * Processing end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{

		final String attributeName = getAttribute(SparbmecatConstants.XML.ATTRIBUTE.LONG_TEXT_ITEMS.NAME);
		final Object value = getSubTagValue(SparbmecatConstants.XML.TAG.TEXT);
		SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, value, SparbmecatConstants.EMPTY_SPACE);
		return categoryFeature;

	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.LONG_TEXT_ITEMS;
	}
}
