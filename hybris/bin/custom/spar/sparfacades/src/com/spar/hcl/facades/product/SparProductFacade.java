/**
 *
 */
package com.spar.hcl.facades.product;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * @author pavan.sr
 *
 */
public interface SparProductFacade extends ProductFacade
{
	/**
	 * Gets the product data for its variantOptions attribute.
	 * {@link ProductFacade#getProductVariantOptionsData(ProductModel, ProductData)} if you only have the code.
	 *
	 * @param productModel
	 *           the productModel
	 * @param productData
	 *           the productData
	 * @return the {@link ProductData}
	 */
	ProductData getProductVariantOptionsData(List<ProductReferenceData> productReferences, ProductData productData);
}
