/*
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package com.spar.hcl.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.spar.hcl.constants.SparordermanagementConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SparordermanagementManager extends GeneratedSparordermanagementManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( SparordermanagementManager.class.getName() );
	
	public static final SparordermanagementManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SparordermanagementManager) em.getExtension(SparordermanagementConstants.EXTENSIONNAME);
	}
	
}
