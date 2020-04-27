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
import de.hybris.platform.bmecat.parser.taglistener.BooleanValueTagListener;
import de.hybris.platform.bmecat.parser.taglistener.DefaultBMECatTagListener;
import de.hybris.platform.bmecat.parser.taglistener.IntegerValueTagListener;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.model.impl.CachingModelService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.parser.SparRequestedDimensions;
import com.spar.hcl.parser.SparSpecification;


/**
 * Parses the &lt;Specification&gt; tag
 */
public class SparSpecificationTagListener extends DefaultBMECatTagListener
{

	/**
	 * Default constructor
	 */
	public SparSpecificationTagListener()
	{
		super();
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param parent
	 */
	public SparSpecificationTagListener(final TagListener parent)
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
		{ new SparRequestedDimensionsTagListener(this),
				new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.CROP_PADDING),
				new BooleanValueTagListener(this, SparbmecatConstants.XML.TAG.ISCROPPED),
				new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.MAX_SIZE_IN_BYTES),
				new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.QUALITY),
				new IntegerValueTagListener(this, SparbmecatConstants.XML.TAG.RESOLUTION) });
	}

	/**
	 * Process End Element
	 */
	@Override
	public Object processEndElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		final SparSpecification sparSpecification = new SparSpecification();
		final SparRequestedDimensions requestedDimensions = (SparRequestedDimensions) getSubTagValue(SparbmecatConstants.XML.TAG.REQUESTED_DIMENSIONS);
		//		final String mediaFormat = SparBMECatUtils.getSparSupportedMediaFormat(SparBMECatUtils.getMimeConfiguration(), requestedDimensions);
		final String mediaFormat = getSparMediaFormat(requestedDimensions);
		sparSpecification.setMediaFormat(mediaFormat);
		processMediaFormat(mediaFormat);
		sparSpecification.setRequestedDimensions(requestedDimensions);
		sparSpecification.setCropPadding((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.CROP_PADDING));
		sparSpecification.setIsCropped((Boolean) getSubTagValue(SparbmecatConstants.XML.TAG.ISCROPPED));
		sparSpecification.setMaxSizeInBytes((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.MAX_SIZE_IN_BYTES));
		sparSpecification.setQuality((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.QUALITY));
		sparSpecification.setResolution((Integer) getSubTagValue(SparbmecatConstants.XML.TAG.RESOLUTION));
		return sparSpecification;
	}

	/**
	 * This method validates if the media format exist in the Spar System. In case not, then it creates the newly
	 * supported media Format.
	 * 
	 * @param mediaFormat
	 */
	private void processMediaFormat(final String mediaFormat)
	{
		MediaFormatModel format1;
		final MediaService defaultMediaService = Registry.getApplicationContext().getBean(MediaService.class);
		final ModelService defaultModelService = Registry.getApplicationContext().getBean(CachingModelService.class);
		try
		{
			//This is to check if the media exist in Hybris System
			defaultMediaService.getFormat(mediaFormat);
		}
		catch (final UnknownIdentifierException e)
		{
			//if the media format in the hybris system does not exits, then create a new.
			//The new resolution/media format is coming from BB xml and will be created while parsing the xml to support media impex processing.
			format1 = defaultModelService.create(MediaFormatModel.class);
			format1.setQualifier(mediaFormat);
			defaultModelService.saveAll();
		}
	}

	/**
	 * Getter for Tag Name
	 */
	@Override
	public String getTagName()
	{
		return SparbmecatConstants.XML.TAG.SPECIFICATION;
	}

	/**
	 * This method retrieves the mediaFormat for the mime.
	 * 
	 * @param imageDimension
	 * @return resolution
	 */
	private String getSparMediaFormat(final SparRequestedDimensions imageDimension)
	{

		return imageDimension.toString();
	}


}
