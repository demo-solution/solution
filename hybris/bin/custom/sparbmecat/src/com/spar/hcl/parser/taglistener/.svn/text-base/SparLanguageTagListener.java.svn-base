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
 * Parses the &lt;Language&gt; tag
 */
public class SparLanguageTagListener extends DefaultBMECatTagListener
{

	SparProductCategoryFeature categoryFeature;

	/**
	 * Default Constructor
	 */
	public SparLanguageTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparLanguageTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
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
		{ new SparItemTypeGroupTagListener(this, categoryFeature),
				new StringValueTagListener(this, SparbmecatConstants.XML.TAG.DESCRIPTION),
				new SparCategorisationsTagListener(this, categoryFeature) });
	}

	/**
	 * Processing end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final Object value = getSubTagValue(SparbmecatConstants.XML.TAG.DESCRIPTION);
		final String val = SparBMECatUtils
				.processSubElement(value, SparbmecatConstants.SPACE + SparbmecatConstants.VALUE_DELIMITER);
		categoryFeature.setDescription(val);
		categoryFeature.getCategoryFeaturesMap().put(SparbmecatConstants.XML.TAG.DESCRIPTION.toUpperCase(), val);
		return categoryFeature;
	}

	/**
	 * Getter of Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.LANGUAGE;
	}
}
