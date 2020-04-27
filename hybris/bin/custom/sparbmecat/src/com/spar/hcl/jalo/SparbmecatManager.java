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
package com.spar.hcl.jalo;

import de.hybris.platform.core.Registry;

import org.apache.log4j.Logger;

import com.spar.hcl.constants.SparbmecatConstants;



/**
 * This is the extension manager of the Sparbmecat extension.
 */
public class SparbmecatManager extends GeneratedSparbmecatManager
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(SparbmecatManager.class.getName());

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SparbmecatManager getInstance()
	{
		return (SparbmecatManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SparbmecatConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public SparbmecatManager() // NOPMD
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of SparbmecatManager called.");
		}
	}
}
