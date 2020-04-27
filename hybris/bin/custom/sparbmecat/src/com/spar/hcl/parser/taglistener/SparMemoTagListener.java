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

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * Parses the &lt;Memo&gt; tag
 */
public class SparMemoTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default Constructor
	 */
	public SparMemoTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparMemoTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Process end element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final String attributeName = getAttribute(SparbmecatConstants.XML.ATTRIBUTE.MEMO.NAME);
		SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, getCharacters());
		return categoryFeature;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.MEMO;
	}
}
