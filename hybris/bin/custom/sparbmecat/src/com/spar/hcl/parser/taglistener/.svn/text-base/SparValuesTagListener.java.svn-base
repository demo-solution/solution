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

import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;


/**
 * Parses the &lt;Values&gt; tag
 */
public class SparValuesTagListener extends DefaultBMECatTagListener
{


	/**
	 * Default constructor
	 */
	public SparValuesTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 */
	public SparValuesTagListener(final TagListener parent)
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
		{ new SparValueTagListener(this, SparbmecatConstants.XML.TAG.VALUE) });
	}

	/**
	 * Processing end element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		return getSubTagValue(SparbmecatConstants.XML.TAG.VALUE);
	}

	/**
	 * Getters for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.VALUES;
	}
}
