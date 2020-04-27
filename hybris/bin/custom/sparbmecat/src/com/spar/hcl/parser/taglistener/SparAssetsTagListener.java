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

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparMime;


/**
 * Parses the &lt;Assets&gt; tag
 */
public class SparAssetsTagListener extends DefaultBMECatTagListener
{
	/**
	 * This instance variable will hold the list of Mime object associated to the product
	 */
	private ArrayList<SparMime> sparMimeList;

	/**
	 * Default Constructor
	 */
	public SparAssetsTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparAssetsTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * Creating Listeners
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparImageTagListener(this) });
	}

	/*
	 * (non-Javadoc) This method is to process the start element
	 * 
	 * @see
	 * de.hybris.platform.bmecat.parser.taglistener.DefaultBMECatTagListener#processStartElement(de.hybris.platform.bmecat
	 * .parser.BMECatObjectProcessor)
	 */
	@Override
	protected Object processStartElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		sparMimeList = new ArrayList<SparMime>();
		return super.processStartElement(processor);
	}

	/**
	 * Processing at the end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final Object obj = getSubTagValue(SparbmecatConstants.XML.TAG.IMAGE);
		if (obj instanceof SparMime)
		{
			sparMimeList.add((SparMime) obj);
			return sparMimeList;
		}
		else if (obj instanceof ArrayList)
		{
			return obj;
		}
		return null;
	}

	/**
	 * Get the Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.ASSETS;
	}
}
