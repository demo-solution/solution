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
 * Parses the &lt;NameValue&gt; tag
 */
public class SparNameValueTagListener extends DefaultBMECatTagListener
{

	/**
	 * 
	 */
	private static final String STORAGE_TYPE = "STORAGE TYPE";
	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparNameValueTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparNameValueTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
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
		{ new StringValueTagListener(this, SparbmecatConstants.XML.TAG.NAME),
				new StringValueTagListener(this, SparbmecatConstants.XML.TAG.VALUE) });
	}

	/**
	 * Processing End Element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		String attributeName = getParent().getAttribute(SparbmecatConstants.XML.TAG.NAME);
		if (STORAGE_TYPE.equalsIgnoreCase(attributeName))
		{
			final String value = (String) getSubTagValue(SparbmecatConstants.XML.TAG.VALUE);
			SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, value, SparbmecatConstants.VALUE_DELIMITER);
		}
		else
		{
			attributeName = (String) getSubTagValue(SparbmecatConstants.XML.ATTRIBUTE.NAME_LOOKUPS.NAME);
			final Object value = getSubTagValue(SparbmecatConstants.XML.TAG.VALUE);
			SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, value, SparbmecatConstants.VALUE_DELIMITER
					+ SparbmecatConstants.SPACE);
		}
		return categoryFeature;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.NAME_VALUE;
	}
}
