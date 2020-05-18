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
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparProductCategoryFeature;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * Parses the &lt;NameValueText&gt; tag
 */
public class SparNameValueTextTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparNameValueTextTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 * @param categoryFeature
	 */
	public SparNameValueTextTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Create listeners for Sub Tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new StringValueTagListener(this, SparbmecatConstants.XML.TAG.NAME),
				new StringValueTagListener(this, SparbmecatConstants.XML.TAG.VALUE),
				new StringValueTagListener(this, SparbmecatConstants.XML.TAG.TEXT) });
	}


	/**
	 * Process end element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final boolean isSubElementParsable = isIdAttributeEquals24();
		final String attributeName = getAttributeName(isSubElementParsable);
		final String value = (String) getSubTagValue(SparbmecatConstants.XML.TAG.TEXT) + SparbmecatConstants.SPACE
				+ (String) getSubTagValue(SparbmecatConstants.XML.TAG.VALUE);
		SparBMECatUtils.processCategoryFeature(categoryFeature, attributeName, value, SparbmecatConstants.VALUE_DELIMITER);

		return value;

	}

	/**
	 * This method is used to get the Attribute Name from Element/Sub Element depending on if parent element attribute Id
	 * =24.
	 *
	 * @param isSubElementParsable
	 * @return String
	 */
	private String getAttributeName(final boolean isSubElementParsable)
	{
		String attributeName;
		//If the attribute ID="24" which means Main element has to be parsed
		if (isSubElementParsable)
		{
			attributeName = (String) getSubTagValue(SparbmecatConstants.XML.TAG.NAME);
		}
		else
		{
			attributeName = getParent().getAttribute(SparbmecatConstants.XML.ATTRIBUTE.NAME_TEXT_LOOKUPS.NAME);

		}
		return attributeName;
	}

	/**
	 * This method is used to determine whether the id attribute of parent element is equal to 24 or not. If its equal
	 * then Main element is parsed, otherwise Sub-Element is parsed
	 *
	 * @return boolean
	 */
	private boolean isIdAttributeEquals24()
	{
		final String idAttribute = getParent().getAttribute(SparbmecatConstants.NAME_TEXT_LOOKUPS_ID_ATTRIBUTE);
		boolean isSubElementParsable = true;
		//If the attribute ID="24" which means Main element has to be parsed
		if (null != idAttribute)
		{
			isSubElementParsable = !Config.getString(SparbmecatConstants.NAME_TEXT_LOOKUPS_ID,
					SparbmecatConstants.NAME_TEXT_LOOKUPS_ID_VALUE).equals(idAttribute);
		}
		return isSubElementParsable;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.NAME_VALUE_TEXT;
	}
}
