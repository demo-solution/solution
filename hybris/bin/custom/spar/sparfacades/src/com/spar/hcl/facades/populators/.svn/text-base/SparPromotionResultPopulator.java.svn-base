/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.PromotionResultPopulator;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;


/**
 * This populator extends PromotionResultPopulator to add total discount in PromotionResult
 *
 * @author rohan_c
 *
 */
public class SparPromotionResultPopulator extends PromotionResultPopulator
{
	private ModelService modelService;
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;

	/**
	 * This method is overridden to populate Total Discount for a promotion result.
	 *
	 * @see de.hybris.platform.commercefacades.order.converters.populator.PromotionResultPopulator#populate(de.hybris.platform
	 *      .promotions.model.PromotionResultModel, de.hybris.platform.commercefacades.product.data.PromotionResultData)
	 */
	@Override
	public void populate(final PromotionResultModel source, final PromotionResultData target)
	{
		super.populate(source, target);
		populateTotalDiscount(source, target);
	}

	/**
	 * This method is used to populate the total discount.
	 *
	 * @param source
	 * @param target
	 */
	private void populateTotalDiscount(final PromotionResultModel source, final PromotionResultData target)
	{
		final PromotionResult promotionResult = getModelService().getSource(source);
		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
				BigDecimal.valueOf(promotionResult.getTotalDiscount()), getCommonI18NService().getCurrentCurrency().getIsocode());
		target.setTotalDiscount(priceData);
	}

	/**
	 * Getter
	 * 
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Setter
	 * 
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Getter
	 * 
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Setter
	 * 
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Getter
	 * 
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Setter
	 * 
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


}
