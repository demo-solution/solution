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
package com.spar.hcl.parser;

import de.hybris.platform.bmecat.parser.Mime;


/**
 * Object which holds the value of a parsed &lt;Image&gt; tag.This class is extending MIME to leverage its instance
 * variable to align it with OOTB
 *
 */
public class SparMime extends Mime
{
	/**
	 * Mime attribute
	 */
	private SparSpecification specification;

	/**
	 * Getter
	 *
	 * @return the specification
	 */
	public SparSpecification getSpecification()
	{
		return specification;
	}

	/**
	 * Setter
	 *
	 * @param specification
	 *           the specification to set
	 */
	public void setSpecification(final SparSpecification specification)
	{
		this.specification = specification;
	}

}
