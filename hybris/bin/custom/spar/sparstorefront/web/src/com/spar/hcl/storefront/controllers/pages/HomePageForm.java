/**
 * 
 */
package com.spar.hcl.storefront.controllers.pages;

/**
 * @author valechar
 * 
 */
public class HomePageForm
{
	private String store;
	private String deliveryOption;
	private String deliveryType;
	private String deliverySlot;
	
	/*Code change start by sumit*/
	private String deliveryCityName;
	
	/**
	 * @return the deliveryCityName
	 */
	public String getDeliveryCityName()
	{
		return deliveryCityName;
	}

	/**
	 * @param deliveryCityName the deliveryCityName to set
	 */
	public void setDeliveryCityName(String deliveryCityName)
	{
		this.deliveryCityName = deliveryCityName;
	}


	/*Code change end here*/

	
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
	 * @return the deliveryOption
	 */
	public String getDeliveryOption()
	{
		return deliveryOption;
	}

	/**
	 * @param deliveryOption
	 *           the deliveryOption to set
	 */
	public void setDeliveryOption(final String deliveryOption)
	{
		this.deliveryOption = deliveryOption;
	}
}
