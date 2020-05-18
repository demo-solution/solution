/**
 * This Class is an extension of DefaultCommerceUpdateCartEntryStrategy to change condition from entryNewQuantity == maxOrderQuantityset DeliveryMethod to entryNewQuantity > maxOrderQuantity.
 *  
 * @author ravindra.kr
 *
 */
package com.spar.hcl.order.strategy.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import javax.annotation.Resource;

import com.spar.hcl.promotions.model.SparProductBXGYFPromotionModel;




public class SparDefaultCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
{
	@Resource(name = "promotionsService")
	private PromotionsService promotionsService;
	
	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		// Now work out how many that leaves us with on this entry
		final long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;

		final ModelService modelService = getModelService();

		if (entryNewQuantity <= 0)
		{
			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(entryToUpdate.getProduct());
			
			if(null != entryToUpdate.getProduct().getPromotions()){
				for(final ProductPromotionModel promotionModel : entryToUpdate.getProduct().getPromotions()){
					if(promotionModel instanceof SparProductBXGYFPromotionModel){
						final Collection<ProductModel> productModels = ((SparProductBXGYFPromotionModel) promotionModel).getFreeProducts();
						if(null != productModels && productModels.size() > 0){
							for(final ProductModel productModel : productModels){
								for(final AbstractOrderEntryModel entryModel : cartModel.getEntries()){
									if(entryModel.getProduct().getCode().equals(productModel.getCode())){
										modelService.remove(entryModel);
										break;
									}
								}
							}
						}
					}
				}
			}
			// The allowed new entry quantity is zero or negative
			// just remove the entry
			modelService.remove(entryToUpdate);
			modelService.refresh(cartModel);
			normalizeEntryNumbers(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(cartModel);

			// Return an empty modification
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setEntry(entry);
			modification.setQuantity(0);
			// We removed all the quantity from this row
			modification.setQuantityAdded(-entryToUpdate.getQuantity().longValue());

			if (newQuantity == 0)
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
			// Adjust the entry quantity to the new value
			entryToUpdate.setQuantity(Long.valueOf(entryNewQuantity));
			modelService.save(entryToUpdate);
			modelService.refresh(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(cartModel);
			modelService.refresh(entryToUpdate);

			// Return the modification data
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setQuantityAdded(actualAllowedQuantityChange);
			modification.setEntry(entryToUpdate);
			modification.setQuantity(entryNewQuantity);

			if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity > maxOrderQuantity.longValue())
			{
				modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
			}
			else if (newQuantity == entryNewQuantity)
			{
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			else
			{
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}

			return modification;
		}
	}
}
