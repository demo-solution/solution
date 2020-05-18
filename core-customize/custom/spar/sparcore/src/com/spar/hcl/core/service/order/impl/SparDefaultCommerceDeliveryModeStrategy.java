/*
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
 *
 */
package com.spar.hcl.core.service.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryModeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import org.apache.commons.lang.StringUtils;


/**
 * This class is used to set the stored delivery mode for an order for which delivery charge promotion is applied.
 * 
 * @author rohan_c
 *
 */
public class SparDefaultCommerceDeliveryModeStrategy extends DefaultCommerceDeliveryModeStrategy
{

	@Override
	public boolean setDeliveryMode(final CommerceCheckoutParameter parameter)
	{
		final DeliveryModeModel deliveryModeModel = parameter.getDeliveryMode();
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(deliveryModeModel, "Delivery mode model cannot be null");

		setPreiviousDeliveryMode(deliveryModeModel, cartModel);
		cartModel.setDeliveryMode(deliveryModeModel);
		getModelService().save(cartModel);
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setEnableHooks(true);
		commerceCartParameter.setCart(cartModel);
		getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);

		return true;
	}

	/**
	 * This method is used to set the previous delivery mode
	 *
	 * @param deliveryModeModel
	 * @param cartModel
	 */
	private void setPreiviousDeliveryMode(final DeliveryModeModel deliveryModeModel, final CartModel cartModel)
	{
		if (cartModel.getPreviousDeliveryMode() == null)
		{
			cartModel.setPreviousDeliveryMode(deliveryModeModel);
		}
		final String previousDeliveryMode = cartModel.getPreviousDeliveryMode() == null
				|| StringUtils.isEmpty(cartModel.getPreviousDeliveryMode().getCode()) ? "" : cartModel.getPreviousDeliveryMode()
				.getCode();
		if (deliveryModeModel.getCode().equals("pickup") && StringUtils.isNotEmpty(previousDeliveryMode)
				&& !"pickup".equals(previousDeliveryMode))
		{
			cartModel.setPreviousDeliveryMode(deliveryModeModel);
		}
	}

}
