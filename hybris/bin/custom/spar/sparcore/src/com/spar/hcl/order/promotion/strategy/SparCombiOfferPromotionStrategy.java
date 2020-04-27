/**
 *
 */
package com.spar.hcl.order.promotion.strategy;

import java.util.Collection;


/**
 * This interface is used to get the promotions that are configured for Combi-offer
 *
 * @author rohan_c
 *
 */
public interface SparCombiOfferPromotionStrategy
{
	/**
	 * Getter
	 *
	 * @return the combiPromotionType
	 */
	public Collection<String> getCombiPromotionType();

	/**
	 * Setter
	 *
	 * @param combiPromotionType
	 *           the combiPromotionType to set
	 */
	public void setCombiPromotionType(final Collection<String> combiPromotionType);

}
