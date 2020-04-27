/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

/**
 * @author tanveers
 */

public class DeliverySlotForm
{
	private String store;
	private String deliveryType;
	private String deliverySlot;
	private String deliveryMethod;

	/**
	 * @return the store
	 */
	public String getStore()
	{
		return store;
	}

	/**
	 * @param store
	 *           the store to set
	 */
	public void setStore(final String store)
	{
		this.store = store;
	}

	/**
	 * @return the deliveryType
	 */
	public String getDeliveryType()
	{
		return deliveryType;
	}

	/**
	 * @param deliveryType
	 *           the deliveryType to set
	 */
	public void setDeliveryType(final String deliveryType)
	{
		this.deliveryType = deliveryType;
	}

	/**
	 * @return the deliverySlot
	 */
	public String getDeliverySlot()
	{
		return deliverySlot;
	}

	/**
	 * @param deliverySlot
	 *           the deliverySlot to set
	 */
	public void setDeliverySlot(final String deliverySlot)
	{
		this.deliverySlot = deliverySlot;
	}

	/**
	 * @return the deliveryMethod
	 */
	public String getDeliveryMethod()
	{
		return deliveryMethod;
	}

	/**
	 * @param deliveryMethod
	 *           the deliveryMethod to set
	 */
	public void setDeliveryMethod(final String deliveryMethod)
	{
		this.deliveryMethod = deliveryMethod;
	}

}
