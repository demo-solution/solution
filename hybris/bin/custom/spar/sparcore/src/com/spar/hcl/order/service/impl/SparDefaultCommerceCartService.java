/**
 *
 */
package com.spar.hcl.order.service.impl;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.ProductBOGOFPromotionModel;
import de.hybris.platform.util.Config;

import java.util.List;

import com.spar.hcl.order.promotion.strategy.SparCommerceCartValidateBOGOStrategy;


/**
 * This service class is extended using DefaultCommerceCartService to customize the BOGO add to bag or update cart item
 * behaviour.
 *
 * @author rohan_c
 *
 */
public class SparDefaultCommerceCartService extends DefaultCommerceCartService
{
	private static final String BOGO_PROMOTION_ENABLE = "BOGO_PROMOTION_ENABLE";
	private static final String ORDER_ENTRY_THRESHHOLD = "ORDER_ENTRY_THRESHHOLD";
	SparCommerceCartValidateBOGOStrategy commerceCartValidateBOGOStrategy;
	ProductService productService;
	CartService cartService;

	/**
	 * This method is used to customize the BOGO add to cart behaviour.
	 *
	 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService#addToCart(de.hybris.platform.
	 *      commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final boolean isBogoCustomFlowEnabled = Config.getBoolean(BOGO_PROMOTION_ENABLE, true);
		CommerceCartModification cartModification = super.addToCart(parameter);
		if (isBogoCustomFlowEnabled)
		{
			cartModification = addBOGOPromotionIfApplicable(parameter, cartModification);
		}
		return cartModification;
	}

	/**
	 * This method is used to customize the BOGO add to cart behaviour.
	 *
	 * @param parameter
	 * @param cartModification
	 * @throws CommerceCartModificationException
	 */
	private CommerceCartModification addBOGOPromotionIfApplicable(final CommerceCartParameter parameter,
			final CommerceCartModification cartModification) throws CommerceCartModificationException
	{
		final ProductBOGOFPromotionModel bogoPromotion = getCommerceCartValidateBOGOStrategy().getBOGOIfApplicable(parameter,
				cartModification);

		if (CommerceCartModificationStatus.SUCCESS.equals(cartModification.getStatusCode()) && null != bogoPromotion
				&& !isOrderEntryQtyThresholdExceeded(cartModification, bogoPromotion,parameter.getProduct()))
		{
			final ProductModel product = parameter.getProduct();
			final CartModel cartModel = parameter.getCart();
			final CommerceCartParameter bogoParameters = new CommerceCartParameter();
			bogoParameters.setEnableHooks(true);
			bogoParameters.setCart(cartModel);
			bogoParameters.setQuantity(bogoPromotion.getFreeCount().intValue());
			bogoParameters.setProduct(product);
			bogoParameters.setUnit(product.getUnit());
			bogoParameters.setCreateNewEntry(false);
			final CommerceCartModification bogoCartModification = super.addToCart(bogoParameters);
			cartModification.setBogoAddOrUpdateStatus(bogoCartModification.getStatusCode());
		}
		return cartModification;
	}

	/**
	 * This method is used to customize the BOGO update cart items behaviour. (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService#updateQuantityForCartEntry(de.hybris
	 *      .platform.commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException
	{
		final AbstractOrderEntryModel orderEntry = getEntryForNumber(parameters.getCart(), (int) parameters.getEntryNumber());
		final long beforeUpdateQty = orderEntry.getQuantity().longValue();
		CommerceCartModification cartModification = super.updateQuantityForCartEntry(parameters);
		final boolean isBogoCustomFlowEnabled = Config.getBoolean(BOGO_PROMOTION_ENABLE, true);
		if (isBogoCustomFlowEnabled)
		{
			final CartModel cartModel = parameters.getCart();
			cartModification = updateBOGOPromotionIfApplicable(cartModel, cartModification);
		}
		return cartModification;

	}

	/**
	 * This method is used to customize the BOGO update cart items behaviour.
	 *
	 * @param cartModel
	 * @param cartModification
	 * @throws CommerceCartModificationException
	 */
	private CommerceCartModification updateBOGOPromotionIfApplicable(final CartModel cartModel,
			final CommerceCartModification cartModification) throws CommerceCartModificationException
	{
		final ProductBOGOFPromotionModel bogoPromotion = getCommerceCartValidateBOGOStrategy().getBOGOIfApplicable(cartModel,
				cartModification);
		final AbstractOrderEntryModel orderEntry = cartModification.getEntry();

		if (CommerceCartModificationStatus.SUCCESS.equals(cartModification.getStatusCode()) && null != bogoPromotion
				&& !isOrderEntryQtyThresholdExceeded(cartModification, bogoPromotion, orderEntry.getProduct()))
		{
			final long entryNumber = orderEntry.getEntryNumber().longValue();
			final CommerceCartParameter bogoParameters = new CommerceCartParameter();
			bogoParameters.setEnableHooks(true);
			bogoParameters.setCart(cartModel);
			bogoParameters.setEntryNumber(entryNumber);
			bogoParameters.setQuantity(cartModification.getEntry().getQuantity().longValue()
					+ bogoPromotion.getFreeCount().intValue());
			final CommerceCartModification bogoCartModification = super.updateQuantityForCartEntry(bogoParameters);
			cartModification.setBogoAddOrUpdateStatus(bogoCartModification.getStatusCode());
		}
		return cartModification;
	}

	/**
	 * This method is used to check if order entry threshold is not exceeded in case of BOGO.
	 *
	 * @param cartModification
	 * @param bogoPromotion
	 * @return boolean
	 */
	protected boolean isOrderEntryQtyThresholdExceeded(final CommerceCartModification cartModification,
			final ProductBOGOFPromotionModel bogoPromotion,ProductModel product)
	{
		//final long orderEntryThreshhold = Config.getLong(ORDER_ENTRY_THRESHHOLD, 25);
		@SuppressWarnings("boxing")
		final long orderEntryThreshhold = product.getMaxOrderQuantity() != null ? product.getMaxOrderQuantity().longValue() : null;
		
		final AbstractOrderEntryModel orderEntry = cartModification.getEntry();
		final long beforeUpdateQty = orderEntry.getQuantity().longValue() + bogoPromotion.getFreeCount().intValue();
		return beforeUpdateQty > orderEntryThreshhold ? true : false;
	}

	/**
	 * This method is used to get the order entry model using entry number.
	 *
	 * @param order
	 * @param entryNumber
	 * @return AbstractOrderEntryModel
	 */
	protected AbstractOrderEntryModel getEntryForNumber(final AbstractOrderModel order, final int entryNumber)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf(entryNumber);
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * This method is overridden to change the behaviour for an order item that qualifies for BOGO. (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService#mergeCarts(de.hybris.platform.core.model
	 *      .order.CartModel, de.hybris.platform.core.model.order.CartModel, java.util.List)
	 */
	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		super.mergeCarts(fromCart, toCart, modifications);
		final boolean isBogoCustomFlowEnabled = Config.getBoolean(BOGO_PROMOTION_ENABLE, true);
		if (isBogoCustomFlowEnabled)
		{
			for (final CommerceCartModification cartModification : modifications)
			{
				try
				{
					updateBOGOPromotionIfApplicable(toCart, cartModification);
				}
				catch (final CommerceCartModificationException e)
				{
					throw new CommerceCartMergingException("Cannot merge cart to itself!");
				}
			}
		}
	}

	/**
	 * Getter
	 *
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Setter
	 *
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Getter
	 *
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * Setter
	 *
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * Getter
	 *
	 * @return the commerceCartValidateBOGOStrategy
	 */
	public SparCommerceCartValidateBOGOStrategy getCommerceCartValidateBOGOStrategy()
	{
		return commerceCartValidateBOGOStrategy;
	}

	/**
	 * Setter
	 *
	 * @param commerceCartValidateBOGOStrategy
	 *           the commerceCartValidateBOGOStrategy to set
	 */
	public void setCommerceCartValidateBOGOStrategy(final SparCommerceCartValidateBOGOStrategy commerceCartValidateBOGOStrategy)
	{
		this.commerceCartValidateBOGOStrategy = commerceCartValidateBOGOStrategy;
	}


}
