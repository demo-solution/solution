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
import de.hybris.platform.bmecat.parser.taglistener.IntegerValueTagListener;

import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparRequestedDimensions;


/**
 * Parses the &lt;RequestedDimensions&gt; tag
 */
public class SparRequestedDimensionsTagListener extends DefaultBMECatTagListener
{
	/**
	 * Default constructor
	 */
	public SparRequestedDimensionsTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparRequestedDimensionsTagListener(final TagListener parent)
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
		{ new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.HEIGHT),
				new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.WIDTH) });
	}

	/**
	 * Process End Elements
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final SparRequestedDimensions requestedDimensions = new SparRequestedDimensions();

		requestedDimensions.setHeight((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.HEIGHT));
		requestedDimensions.setWidth((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.WIDTH));
		requestedDimensions.setUnitsOfSpecification(getAttribute(SparbmecatConstants.XML.ATTRIBUTE.REQUESTED_DIMENSIONS.UNITS));
		return requestedDimensions;

	}

	/**
	 * Getter for Tag Element
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.REQUESTED_DIMENSIONS;
	}
}
