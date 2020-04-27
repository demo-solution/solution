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

import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.parser.BMECatObjectProcessor;
import de.hybris.platform.bmecat.parser.taglistener.DefaultBMECatTagListener;
import de.hybris.platform.bmecat.parser.taglistener.StringValueTagListener;

import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparMime;
import com.spar.hcl.parser.SparSpecification;


/**
 * Parses the &lt;Image&gt; tag
 */
public class SparImageTagListener extends DefaultBMECatTagListener
{

	/**
	 * Constructor
	 */
	public SparImageTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparImageTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * Creating Sub Tag Listener
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparSpecificationTagListener(this), new StringValueTagListener(this, SparbmecatConstants.XML.TAG.URL),
				new StringValueTagListener(this, SparbmecatConstants.XML.TAG.THUMBPRINT) });
	}

	/**
	 * Processing end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor)
	{
		final SparMime mime = new SparMime();

		mime.setType(getAttribute(SparbmecatConstants.XML.ATTRIBUTE.IMAGE.MIME_TYPE) == null ? "image/jpeg"
				: getAttribute(SparbmecatConstants.XML.ATTRIBUTE.IMAGE.MIME_TYPE));
		mime.setSource((String) getSubTagValue(SparbmecatConstants.XML.TAG.URL));
		mime.setAlt((String) getSubTagValue(SparbmecatConstants.XML.TAG.THUMBPRINT));
		final SparSpecification sparSpecification = (SparSpecification) getSubTagValue(SparbmecatConstants.XML.TAG.SPECIFICATION);
		mime.setSpecification(sparSpecification);
		return mime;
	}

	/**
	 * Getter of Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.IMAGE;
	}
}
