package com.spar.hcl.parser;

import de.hybris.bootstrap.xml.XMLContentHandler;

import org.apache.log4j.Logger;

import com.spar.hcl.parser.taglistener.SparGetUnsentProductDataResponseTagListener;


/**
 * This class handles the parsed Brand Bank xml elements. Its implements basic tag processing and binds the root element
 * to the {@link hcl.spar.bmecat.parser.taglistener.SparGetUnsentProductDataResponseTagListener}. This listener is root
 * of a hierarchy of listeners each of them parsing a BB xml tag. Each listener may call the processor with a values
 * object of the parsed tag.
 * 
 * @author rohan_c
 * 
 */
public class SparBMECatContentHandler extends XMLContentHandler
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SparBMECatContentHandler.class.getName());

	protected final SparBMECatObjectProcessor processor;

	/**
	 * Setting the SPARBMECatContent Handler and ObjectProcessor
	 * 
	 * @param processor
	 */
	public SparBMECatContentHandler(final SparBMECatObjectProcessor processor)
	{
		super(new SparGetUnsentProductDataResponseTagListener(null), processor);
		this.processor = processor;
	}

}
