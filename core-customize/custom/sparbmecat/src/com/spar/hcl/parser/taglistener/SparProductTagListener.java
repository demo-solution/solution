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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparMime;
import com.spar.hcl.parser.SparProductCategoryFeature;



/**
 * Parses the &lt;Product&gt; tag
 */
public class SparProductTagListener extends DefaultBMECatTagListener
{
	SparProductCategoryFeature categoryFeature;

	/**
	 * Default constructor
	 */
	public SparProductTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 *
	 * @param parent
	 */
	public SparProductTagListener(final TagListener parent)
	{
		super(parent);
	}

	/**
	 * Creating listeners for Sub tags
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparIdentityTagListener(this, categoryFeature), new SparAssetsTagListener(this),
				new SparDataTagListener(this, categoryFeature) });
	}

	/**
	 * Process Start Element
	 */
	@Override
	protected Object processStartElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		this.categoryFeature = new SparProductCategoryFeature();
		return null;
	}

	/**
	 * Process End Elements
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{

		processAssetElement();
		/*
		 * notify processor for Category Features - Product-Classification
		 */
		processor.process(this, categoryFeature);

		return null;

	}

	/**
	 * This method is used to process the Asset element to handle Different Mime objects coming for a product
	 */
	@SuppressWarnings("unchecked")
	private void processAssetElement()
	{
		if (null != getSubTagValue(SparbmecatConstants.XML.TAG.ASSETS))
		{
			final ArrayList<SparMime> mimeList = (ArrayList<SparMime>) getSubTagValue(SparbmecatConstants.XML.TAG.ASSETS);
			categoryFeature.setMimeList(mimeList);

			final ArrayList<String> mediaContainerValueList = new ArrayList<String>();
			for (final SparMime mime : mimeList)
			{
				final String mediaFormat = mime.getSpecification().getMediaFormat();
				//Product name is same as partNumber
				final String fileName = categoryFeature.getPartNumber();
				String file = File.separator + mediaFormat + File.separator + fileName + "." + SparbmecatConstants.MIME_TYPE;
				// This is done to support Unix/Linux environment (Higher Environment)
				file = FilenameUtils.separatorsToUnix(file);
				mediaContainerValueList.add(file);
			}
			categoryFeature.setMediaContainerValue(String.join(SparbmecatConstants.VALUE_DELIMITER, mediaContainerValueList));
		}
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.PRODUCT;
	}
}
