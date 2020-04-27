/**
 *
 */
package com.spar.hcl.order.promotion.strategy.impl;

import java.util.Collection;

import com.spar.hcl.order.promotion.strategy.SparCombiOfferPromotionStrategy;


/**
 * This class is used to get the promotions that are configured for Com-offer
 * 
 * @author rohan_c
 *
 */
public class SparCombiOfferPromotionStrategyImpl implements SparCombiOfferPromotionStrategy
{
	private Collection<String> combiPromotionType;

	/**
	 * Getter
	 *
	 * @return the combiPromotionType
	 */
	@Override
	public Collection<String> getCombiPromotionType()
	{
		return combiPromotionType;
	}

	/**
	 * Setter
	 *
	 * @param combiPromotionType
	 *           the combiPromotionType to set
	 */
	@Override
	public void setCombiPromotionType(final Collection<String> combiPromotionType)
	{
		this.combiPromotionType = combiPromotionType;
	}


}
