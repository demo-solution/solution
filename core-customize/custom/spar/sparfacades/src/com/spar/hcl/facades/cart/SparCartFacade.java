/**
 *
 */
package com.spar.hcl.facades.cart;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.spar.hcl.facades.deliverySlots.data.DeliverySlotData;
import com.spar.hcl.facades.populators.SparCategoryData;


/**
 * @author nikhil-ku
 *
 */
public interface SparCartFacade extends CartFacade
{
	Map<SparCategoryData, List<OrderEntryData>> groupCartOnCategory(final OrderEntryData entry,
			Map<SparCategoryData, List<OrderEntryData>> catToOrdrEntryMap);

	List<String> checkCartQualifyForOrderLimits();

	/**
	 * @param string
	 */
	boolean setDeliveryMode(String string);

	/**
	 * This method is used to associate warehouse to a cart. This method also performs recalculation of cart if required.
	 *
	 * @param pointOfService
	 */
	void setOrderWarehouse(String pointOfService, boolean isRecaluclationRequired);

	/**
	 * This method is used to associate warehouse to a cart.
	 *
	 * @param pointOfService
	 */
	void setOrderWarehouse(final String pointOfService);

	/**
	 * This method is used to associate point of service to a cart.
	 *
	 * @param pointOfService
	 */
	void setOrderPointOfService(final String pointOfService);

	/**
	 * This method is used for setting the cnc phone number to a cart
	 *
	 * @param cncPhone
	 */
	void setCncPhone(final String cncPhone);

	/**
	 * This method is used to update the cart savings.
	 *
	 * @param cartData
	 */
	void updateCartSavings(final CartData cartData);

	/**
	 * This method is used to reset the cnc phone number after order confirmation through CNC or changing the order flow
	 * from CNC to HD
	 */
	void resetCncPhoneNumber();

	Map<Date, List<DeliverySlotData>> getDeliverySlotsMap(final String deliveryType);
	
	void updateSocialMediaToCart(final String orderVia);
}
