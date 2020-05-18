package com.spar.hcl.price.service;

import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;



public interface SparCommercePriceService extends CommercePriceService
{
	PriceInformation getSparWebPriceForProduct(final ProductModel product, final PromotionOrderEntryConsumedModel source);
}