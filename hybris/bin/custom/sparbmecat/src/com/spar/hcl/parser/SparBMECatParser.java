package com.spar.hcl.parser;

import de.hybris.platform.bmecat.parser.BMECatParser;

import java.util.Map;


/**
 * This class parses the BrandBank xml file. The BB tags are processed by the parser to create the CSV.
 *
 * @author rohan_c
 *
 */
public class SparBMECatParser extends BMECatParser
{
	/**
	 * Default constructor
	 */
	protected SparBMECatParser()
	{
	}

	/**
	 * Parameterized constructor
	 *
	 * @param processor
	 */
	public SparBMECatParser(final SparBMECatObjectProcessor processor)
	{
		this(processor, null, null);
	}

	/**
	 * Parameterized constructor
	 *
	 * @param processor
	 * @param props
	 * @param handler
	 */
	public SparBMECatParser(final SparBMECatObjectProcessor processor, final Map props, final SparBMECatContentHandler handler)
	{
		init(processor, props, handler);
	}

	/**
	 * This method initializes the SPARBMECat Parser.
	 * 
	 * @param processor
	 * @param props
	 * @param handler
	 */
	private void init(final SparBMECatObjectProcessor processor, final Map props, final SparBMECatContentHandler handler)
	{
		createParser(null);

		if (props != null)
		{
			setFeatures(props);
		}
		else
		{
			setDefaultFeatures();
		}
		if (handler == null)
		{
			setContentHandler(new SparBMECatContentHandler(processor));
		}
		else
		{
			setContentHandler(handler);
		}
	}

}
