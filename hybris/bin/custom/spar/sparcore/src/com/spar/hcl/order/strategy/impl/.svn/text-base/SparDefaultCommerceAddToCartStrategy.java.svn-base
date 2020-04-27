/**
 *
 */
package com.spar.hcl.order.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import javax.annotation.Resource;

import com.spar.hcl.core.enums.DeliveryTypeEnum;
import com.spar.hcl.core.service.cart.SparCartService;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;


/**
 * This Class is an extension of DefaultCommerceAddToCartStrategy to set DeliveryMethod on AddtoCart. it also
 * initializes orderPointOfService and OrderWarehouse attributes in the cart.
 *
 * @author rohan_c
 *
 */
public class SparDefaultCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{

	private DeliveryService deliveryService;

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy#addToCart(de.hybris.platform.
	 * commerceservices.service.data.CommerceCartParameter)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{

		CommerceCartModification modification = null;

		try
		{
			this.beforeAddToCart(parameter);
			validateAddToCart(parameter);

			final CartModel cartModel = parameter.getCart();
			final ProductModel productModel = parameter.getProduct();
			final long quantityToAdd = parameter.getQuantity();
			final UnitModel unit = parameter.getUnit();
			final boolean forceNewEntry = parameter.isCreateNewEntry();
			final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();

			try
			{
				getProductService().getProductForCode(productModel.getCode());
			}
			catch (final UnknownIdentifierException e)
			{
				modification = new CommerceCartModification();
				modification.setStatusCode(CommerceCartModificationStatus.UNAVAILABLE);
				modification.setQuantityAdded(0);
				modification.setQuantity(quantityToAdd);
				final CartEntryModel entry = new CartEntryModel();
				entry.setProduct(productModel);
				modification.setEntry(entry);
				return modification;
			}

			UnitModel orderableUnit = unit;

			if (orderableUnit == null)
			{
				try
				{
					orderableUnit = getProductService().getOrderableUnit(productModel);
				}
				catch (final ModelNotFoundException e)
				{
					throw new CommerceCartModificationException(e.getMessage(), e);
				}
			}

			// So now work out what the maximum allowed to be added is (note that this may be negative!)
			final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
					deliveryPointOfService);
			final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();
			final long cartLevel = checkCartLevel(productModel, cartModel, deliveryPointOfService);
			final long cartLevelAfterQuantityChange = actualAllowedQuantityChange + cartLevel;

			if (actualAllowedQuantityChange > 0)
			{
				// We are allowed to add items to the cart
				CartEntryModel cartEntryModel = null;

				if (deliveryPointOfService == null)
				{
					// Modify the cart
					cartEntryModel = getCartService().addNewEntry(cartModel, productModel, actualAllowedQuantityChange, orderableUnit,
							APPEND_AS_LAST, !forceNewEntry);
				}
				else
				{
					// Find the entry to modify
					final Integer entryNumber = getEntryForProductAndPointOfService(cartModel, productModel, deliveryPointOfService);

					// Modify the cart
					cartEntryModel = getCartService().addNewEntry(cartModel, productModel, actualAllowedQuantityChange, orderableUnit,
							entryNumber.intValue(), (entryNumber.intValue() < 0) ? false : !forceNewEntry);

					if (cartEntryModel != null)
					{
						cartEntryModel.setDeliveryPointOfService(deliveryPointOfService);
					}
				}

				getModelService().save(cartEntryModel);
				setDeliveryMode(cartModel);
				setOrderPointOfServiceAndWarehouse(cartModel);
				//Cart Calculation is commented due to improve performance as mini cart on mouse hover is deprecated in SPAR
				//getCommerceCartCalculationStrategy().calculateCart(cartModel);
				getModelService().save(cartEntryModel);

				// Create the card modification result
				modification = new CommerceCartModification();
				modification.setQuantityAdded(actualAllowedQuantityChange);
				modification.setQuantity(quantityToAdd);

				if (cartEntryModel != null)
				{
					modification.setEntry(cartEntryModel);
				}

				// Are we able to add the quantity we requested?
				if (isMaxOrderQuantitySet(maxOrderQuantity) && (actualAllowedQuantityChange < quantityToAdd)
						&& (cartLevelAfterQuantityChange >= maxOrderQuantity.longValue()))
				{
					modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
				}
				else if (actualAllowedQuantityChange == quantityToAdd)
				{
					modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
				}
				else
				{
					modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
				}

				return modification;
			}
			else
			{
				// Not allowed to add any quantity, or maybe even asked to reduce the quantity
				// Do nothing!
				modification = new CommerceCartModification();

				if (isMaxOrderQuantitySet(maxOrderQuantity) && (cartLevelAfterQuantityChange >= maxOrderQuantity.longValue()))
				{
					modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
				}
				else
				{
					modification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
				}
				modification.setQuantityAdded(0);
				modification.setQuantity(quantityToAdd);
				final CartEntryModel entry = new CartEntryModel();
				entry.setProduct(productModel);
				entry.setDeliveryPointOfService(deliveryPointOfService);
				modification.setEntry(entry);
				return modification;
			}
		}
		finally
		{
			this.afterAddToCart(parameter, modification);
		}
	}

	/**
	 * This method sets the deliveryModel in the order
	 *
	 * @param cartModel
	 */
	protected void setDeliveryMode(final CartModel cartModel)
	{
		final String selectedType = sessionService.getAttribute("selectedType");

		if (null != selectedType)
		{
			if (DeliveryTypeEnum.HD.getCode().equalsIgnoreCase(selectedType))
			{
				setDeliveryMode("standard-gross", cartModel);
			}

			else if (DeliveryTypeEnum.CNC.getCode().equalsIgnoreCase(selectedType))
			{
				setDeliveryMode("pickup", cartModel);
			}

		}
	}

	/**
	 * This method sets the deliveryModel in the order
	 *
	 * @param deliveryModeCode
	 * @param cartModel
	 * @return boolean
	 */
	public boolean setDeliveryMode(final String deliveryModeCode, final CartModel cartModel)
	{

		validateParameterNotNullStandardMessage("deliveryModeCode", deliveryModeCode);

		if (cartModel != null)
		{

			final DeliveryModeModel deliveryModeModel = getDeliveryService().getDeliveryModeForCode(deliveryModeCode);
			if (deliveryModeModel != null)
			{
				final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
				parameter.setEnableHooks(true);
				parameter.setCart(cartModel);
				parameter.setDeliveryMode(deliveryModeModel);
				return getCartService().setDeliveryMode(parameter);
			}

		}
		return false;
	}

	/**
	 * This method saves the POS and warehouse in orderPointOfService and OrderWarehouse attribute of abstractOrderModel
	 *
	 * @param cartModel
	 */
	public void setOrderPointOfServiceAndWarehouse(final CartModel cartModel)
	{
		if (null != cartModel)
		{
			final String pointOfService = sessionService.getAttribute("selectedStore");
			cartModel.setOrderPointOfService(storeFinderServiceInterface.getPosStore(pointOfService));
			cartModel.setOrderWarehouse(storeFinderServiceInterface.getWarehouse(pointOfService));
			getCartService().saveOrder(cartModel);
		}
	}

	@Override
	protected SparCartService getCartService()
	{
		return (SparCartService) super.getCartService();
	}

	public DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

}
