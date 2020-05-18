/**
 *
 */
package com.spar.hcl.core.service.cart;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.Collection;
import java.util.List;



/**
 * @author nikhil-ku
 *
 */
public interface SparCartService extends CartService
{
	/**
	 * Returns total amount that can be paid by food coupon for all the OrderEntries.
	 *
	 * @param cartModel
	 * @return Double
	 */
	Double getOrderEntriesTotalPaidByFoodCoupon(final CartModel cartModel);

	CategoryModel getLastLevelCategoryOnProduct(String productCode);

	CategoryModel getFirstLevelCategory(final Collection<CategoryModel> categoryModels);

	List<String> isCartQualifyForOrderLimits(final Double orderValue, final DeliveryModeModel deliveryMode);

	boolean setDeliveryMode(CommerceCheckoutParameter parameter);

	List<CategoryModel> getCategoryChain(final String productCode) throws IllegalArgumentException;

	double getOrderEntryDiscountTotalForCombi(final Integer entryNumber, final PromotionOrderResults promotionOrderResults);

	public CategoryModel getSecondLevelCategoryOnProduct(final CatalogVersionModel catalogVersionModel, final String productCode);

	public CategoryModel getSecondLevelCategory(final Collection<CategoryModel> categoryModels);

}
