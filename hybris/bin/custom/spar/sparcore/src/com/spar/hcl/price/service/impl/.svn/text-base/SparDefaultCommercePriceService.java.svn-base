/**
 *
 */
package com.spar.hcl.price.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.price.impl.DefaultCommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.price.service.SparCommercePriceService;


/**
 * This class is implemented to render the proper order entry data in order confirmation mail
 *
 * @author kumarchoubeys
 *
 */
public class SparDefaultCommercePriceService extends DefaultCommercePriceService implements SparCommercePriceService
{
	private static final Logger LOG = Logger.getLogger(SparDefaultCommercePriceService.class);

	/**
	 * This method is used to display the proper order entry data in order confirmation mail
	 *
	 * @param product
	 * @param source
	 * @return PriceInformation
	 */

	@Override
	public PriceInformation getSparWebPriceForProduct(final ProductModel product, final PromotionOrderEntryConsumedModel source)
	{
		validateParameterNotNull(product, "Product model cannot be null");
		final List<PriceInformation> prices = getPriceService().getPriceInformationsForProduct(product);
		if (CollectionUtils.isNotEmpty(prices))
		{
			PriceInformation minPriceForLowestQuantity = null;
			for (final PriceInformation price : prices)
			{
				if (null != source.getOrderEntry() && null != source.getOrderEntry().getOrder().getOrderWarehouse())
				{
					final String warehouseCode = source.getOrderEntry().getOrder().getOrderWarehouse().getCode();
					LOG.debug("Price class warehouse code:::" + price.getQualifiers().get("warehouse").toString().substring(0, 5)
							+ "AbstractOrderModel warehouse code:::" + warehouseCode);
					if ((minPriceForLowestQuantity == null || (((Long) minPriceForLowestQuantity.getQualifierValue("minqtd"))
							.longValue() > ((Long) price.getQualifierValue("minqtd")).longValue()))
							&& price.getQualifiers().get("warehouse").toString().substring(0, 5).equals(warehouseCode))
					{
						minPriceForLowestQuantity = price;
					}
				}
			}
			return minPriceForLowestQuantity;
		}
		return null;
	}
}
