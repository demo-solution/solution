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


/**
 * Parses the &lt;Headings&gt; tag
 */
public class SparHeadingsTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Constructor
	 */
	public SparHeadingsTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 * @param categoryFeature
	 */
	public SparHeadingsTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Creating Sub Tag listeners
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new StringValueTagListener(this, SparbmecatConstants.XML.TAG.HEADING) });
	}

	/**
	 * Processing end element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		return getSubTagValue(SparbmecatConstants.XML.TAG.HEADING);
	}

	/**
	 * Getter of Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.HEADINGS;
	}
}
