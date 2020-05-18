package com.spar.hcl.parser.taglistener;

import de.hybris.bootstrap.xml.ParseAbortException;
import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.parser.BMECatObjectProcessor;
import de.hybris.platform.bmecat.parser.taglistener.BMECatTagListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;
import com.spar.hcl.utils.SparBMECatUtils;


/**
 * Parses the &lt;Message&gt; tag. This listener is root of a hierarchy of listeners that parse BrandBank xml tags. Each
 * listener may call the processor with a value object of the parsed tag.
 * 
 * @author rohan_c
 * 
 */
public class SparBMECatMessageTagListener extends BMECatTagListener
{
	static final Logger log = Logger.getLogger(SparBMECatMessageTagListener.class.getName());

	public SparBMECatMessageTagListener(final TagListener parent)
	{
		super(parent);
	}

	public static class ATTRIBUTES
	{
		public static final String VERSION = "version";
	}

	/**
	 * Creating listeners for the sub elements
	 */
	@Override
	protected Collection createSubTagListeners()
	{
		return Arrays.asList(new TagListener[]
		{ new SparProductTagListener(this) });
	}

	/**
	 * This is a test method and should be removed once the additional attribute from BB determination is finished.
	 */
	@Override
	protected Object processStartElement(final BMECatObjectProcessor processor) throws ParseAbortException
	{
		SparBMECatUtils.additionalCategoryFeatures = new ArrayList<String>();
		return super.processStartElement(processor);
	}

	/**
	 * Processing not at the end of element
	 */
	@Override
	protected Object processEndElement(final BMECatObjectProcessor processor)
	{
		return null;
	}

	/**
	 * Tag name to which this listener is associated to
	 */
	@Override
	public String getTagName()
	{

		return SparbmecatConstants.XML.TAG.MESSAGE;
	}

}
