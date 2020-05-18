/**
 *
 */
package com.spar.hcl.promotions.impl;

import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager;

import com.spar.hcl.jalo.SparpromotionsManager;


/**
 * @author rohan_c
 *
 */
public class SparDefaultPromotionsService extends DefaultPromotionsService
{
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.promotions.impl.DefaultPromotionsService#getPromotionsManager()
	 */
	@Override
	protected PromotionsManager getPromotionsManager()
	{
		return SparpromotionsManager.getInstance();
	}
}
