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


/**
 * Parses the &lt;Code&gt; tag.Parses the &lt;Product&gt; tag
 */
public class SparCodeTagListener extends DefaultBMECatTagListener
{

	/**
	 * Constructor
	 */
	public SparCodeTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparCodeTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * Process the nodes at the end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		if (SparbmecatConstants.PARTNUMBER_ATTR_ID.equals(getAttribute(SparbmecatConstants.XML.ATTRIBUTE.CODE.SCHEME)))
		{
			return getCharacters();
		}
		return null;
	}

	/**
	 * Get the Tag name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.CODE;
	}
}
