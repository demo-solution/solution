/**
 *
 */
package com.spar.hcl.parser.taglistener;

import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.parser.taglistener.StringValueTagListener;


/**
 *
 */
public class SparValueTagListener extends StringValueTagListener
{

	/**
	 * @param parent
	 * @param tagName
	 */
	public SparValueTagListener(final TagListener parent, final String tagName)
	{
		super(parent, tagName);
	}

	public SparValueTagListener(final TagListener parent, final String tagName, final boolean typed)
	{
		super(parent, tagName, typed);
	}

	@Override
	public Object getValue()
	{
		final Object value = getCharacters();
		return value == null ? "" : value;
	}
}
