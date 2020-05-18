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
import com.spar.hcl.parser.SparProductCategoryFeature;


/**
 * Parses the &lt;ItemTypeGroup&gt; tag
 */
public class SparItemTypeGroupTagListener extends DefaultBMECatTagListener
{
	SparProductCategoryFeature categoryFeature;

	/**
	 * Default Constructor
	 */
	public SparItemTypeGroupTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 * @param categoryFeature
	 */
	public SparItemTypeGroupTagListener(final TagListener parent, final SparProductCategoryFeature categoryFeature)
	{
		super(parent);
		this.categoryFeature = categoryFeature;
	}

	/**
	 * Creating Listeners for Sub tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparStatementsTagListener(this, categoryFeature), new SparMemoTagListener(this, categoryFeature),
				new SparNameLookUpsTagListener(this, categoryFeature), new SparNameTextItemsTagListener(this, categoryFeature),
				new SparLongTextItemsTagListener(this, categoryFeature), new SparNameTextLookupsTagListener(this, categoryFeature),
				new SparTextualNutritionTagListener(this, categoryFeature) });
	}

	/**
	 * Processing end of element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{

		return categoryFeature;
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.ITEM_TYPE_GROUP;
	}
}
