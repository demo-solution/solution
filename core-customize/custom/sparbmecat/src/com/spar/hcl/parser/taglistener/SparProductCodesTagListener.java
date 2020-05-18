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

import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;


/**
 * Parses the &lt;ProductCodes&gt; tag
 */
public class SparProductCodesTagListener extends DefaultBMECatTagListener
{
	private static final Logger log = Logger.getLogger(SparProductCodesTagListener.class.getName());
	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparProductCodesTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparProductCodesTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Creating listeners for Sub Tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparCodeTagListener(this) });
	}


	/**
	 * Processing End Element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		if (null != getSubTagValue(SparbmecatConstants.XML.TAG.CODE))
		{
			if (null == getSubTagValue(SparbmecatConstants.XML.TAG.CODE))
			{
				categoryFeature.setPartNumber("");
				log.error("Missing Product Code: " + SparbmecatConstants.XML.TAG.CODE);
			}
			else
			{
				categoryFeature.setPartNumber((String) getSubTagValue(SparbmecatConstants.XML.TAG.CODE));
			}
		}

		return categoryFeature;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.PRODUCT_CODES;
	}
}
