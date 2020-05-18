package com.spar.hcl.parser.taglistener;

import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.parser.taglistener.BMECatTagListener;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;


/**
 * Parses the &lt;GetUnsentProductDataResult&gt; tag. This listener is root of a hierarchy of listeners that parse
 * BrandBank xml tags. Each listener may call the processor with a value object of the parsed tag.
 * 
 * @author rohan_c
 * 
 */
public class SparGetUnsentProductDataResultTagListener extends BMECatTagListener
{
	static final Logger log = Logger.getLogger(SparGetUnsentProductDataResultTagListener.class.getName());

	public SparGetUnsentProductDataResultTagListener(final TagListener parent)
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
		{ new SparBMECatMessageTagListener(this) });
	}


	/**
	 * Tag name to which this listener is associated to
	 */
	@Override
	public String getTagName()
	{

		return SparbmecatConstants.XML.TAG.GET_UNSENT_PRODUCT_DATA_RESULT;
	}

}
