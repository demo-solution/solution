/**
 *
 */
package com.spar.hcl.order.promotion.strategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.model.ProductBOGOFPromotionModel;


/**
 * This interface is used to check if BOGO is applicable in the cart.
 *
 * @author rohan_c
 *
 */
public interface SparCommerceCartValidateBOGOStrategy
{
	/**
	 * This method is used to validate if BOGO is applicable in the cart.
	 * 
	 * @param parameters
	 * @param result
	 * @return ProductBOGOFPromotionModel
	 * @throws CommerceCartModificationException
	 */
	public ProductBOGOFPromotionModel getBOGOIfApplicable(final CommerceCartParameter parameters,
			final CommerceCartModification result) throws CommerceCartModificationException;

	/**
	 * This method is used to validate if BOGO is applicable in the cart.
	 * 
	 * @param cart
	 * @param result
	 * @return ProductBOGOFPromotionModel
	 */
	public ProductBOGOFPromotionModel getBOGOIfApplicable(final CartModel cart, final CommerceCartModification result);
}
