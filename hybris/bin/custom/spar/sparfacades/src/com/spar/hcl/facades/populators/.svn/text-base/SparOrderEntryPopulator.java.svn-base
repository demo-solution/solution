/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.order.promotion.strategy.SparCombiOfferPromotionStrategy;


/**
 * This populator is used to set information in OrderEntryData
 *
 * @author rohan_c
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class SparOrderEntryPopulator<SOURCE extends AbstractOrderEntryModel, TARGET extends OrderEntryData> implements
		Populator<SOURCE, TARGET>
{
	private PromotionsService promotionsService;
	private ModelService modelService;
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	private CommonI18NService commonI18NService;
	private PriceDataFactory priceDataFactory;
	private SparCombiOfferPromotionStrategy sparCombiOfferPromotionStrategy;



	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		populateOrderEntrySavings(source, target);
	}

	/**
	 * This method populates the Savings in PriceData in OrderEntryData
	 *
	 * @param orderEntryData
	 */
	private void populateOrderEntrySavings(final AbstractOrderEntryModel orderEntryModel, final OrderEntryData orderEntryData)
	{
		if (null != orderEntryData.getQuantity())
		{
			final ProductData productData = orderEntryData.getProduct();
			final BigDecimal productSavings = (null != productData.getSavings() && productData.getSavings().getValue().doubleValue() != 0) ? productData
					.getSavings().getValue() : BigDecimal.ZERO;
			final BigDecimal orderEntryQty = new BigDecimal(orderEntryData.getQuantity().toString());
			BigDecimal orderEntrySavings = new BigDecimal(0.0);
			if(orderEntryData.getBasePrice().getValue().doubleValue() == 0.0 && orderEntryModel.getBasePrice().doubleValue() == 0.0)
			{
				orderEntrySavings = BigDecimal.valueOf(orderEntryModel.getSavings().doubleValue());
			}
			else
			{
				if(orderEntryData.getBasePrice().getValue().doubleValue() > 0.0 && orderEntryModel.getBasePrice().doubleValue() > 0.0)
				{
   				orderEntrySavings = (orderEntryData.getBasePrice().getValue().multiply(orderEntryQty))
   					.subtract(orderEntryData.getTotalPrice().getValue());
				}
			}
			final PromotionOrderResults promotionResult = getPromotionsService().getPromotionResults(orderEntryModel.getOrder());
			final boolean isCombiOffer = doesCombiOfferPromotionExistForOrderEntry(promotionResult.getAppliedProductPromotions(),
					orderEntryData.getEntryNumber());

			if(orderEntryData.getBasePrice().getValue().doubleValue() == 0.0 && orderEntryModel.getBasePrice().doubleValue() == 0.0)
			{
				final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderEntrySavings,
						getCommonI18NService().getCurrentCurrency());
				orderEntryData.setSavings(priceData);
			}
			else
			{
   			// if the order entry have promotion applied and doesnot have combioffer
   			if (BigDecimal.ZERO.equals(productSavings) && !BigDecimal.ZERO.equals(orderEntrySavings))
   			{
   				final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, orderEntrySavings,
   						getCommonI18NService().getCurrentCurrency());
   				orderEntryData.setSavings(priceData);
   			}
   			// if the order entry do not have promotion applied
   			else
   			{
   				final BigDecimal savings = productSavings.multiply(orderEntryQty);
   				final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, savings,
   						getCommonI18NService().getCurrentCurrency());
   				orderEntryData.setSavings(priceData);
   			}
			}
			//if the orderentry has combioffer applied to it.
			if (isCombiOffer)
			{
				orderEntryData.setCombiOfferApplied(isCombiOffer);
				orderEntryModel.setCombiOffer(isCombiOffer);
				getModelService().save(orderEntryModel);
			}
		}
	}

	/**
	 * Extracts (and converts to POJOs) promotions from given results.
	 *
	 * @param promotionsResults
	 * @return List<PromotionResultData>
	 */
	protected List<PromotionResultData> getPromotions(final List<PromotionResult> promotionsResults)
	{
		final ArrayList<PromotionResultModel> promotionResultModels = getModelService().getAll(promotionsResults,
				new ArrayList<PromotionResultModel>());
		return Converters.convertAll(promotionResultModels, getPromotionResultConverter());
	}

	/**
	 * This method checks if combi offer is applied in the cart.
	 *
	 * @param productPromotions
	 * @param entryNumberToFind
	 * @return boolean
	 */
	protected boolean doesCombiOfferPromotionExistForOrderEntry(final List<PromotionResult> productPromotions,
			final Integer entryNumberToFind)
	{

		final List<PromotionResultData> appliedOrderPromotions = getPromotions(productPromotions);
		for (final PromotionResultData productPromotion : appliedOrderPromotions)
		{
			final List<PromotionOrderEntryConsumedData> consumedEntries = productPromotion.getConsumedEntries();
			if (consumedEntries != null && !consumedEntries.isEmpty())
			{
				for (final PromotionOrderEntryConsumedData consumedEntry : consumedEntries)
				{
					if (entryNumberToFind.equals(consumedEntry.getOrderEntryNumber()))
					{
						if (consumedEntry.isCombiOfferApplicable())
						{
							for (final String promotionType : getSparCombiOfferPromotionStrategy().getCombiPromotionType())
							{
								if (productPromotion.getPromotionData().getPromotionType().equals(promotionType))
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
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
	 * @return PromotionsService
	 */
	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
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
	 * @return the promotionResultConverter
	 */
	public Converter<PromotionResultModel, PromotionResultData> getPromotionResultConverter()
	{
		return promotionResultConverter;
	}

	/**
	 * Setter
	 *
	 * @param promotionResultConverter
	 *           the promotionResultConverter to set
	 */
	public void setPromotionResultConverter(final Converter<PromotionResultModel, PromotionResultData> promotionResultConverter)
	{
		this.promotionResultConverter = promotionResultConverter;
	}

	/**
	 * Getter
	 *
	 * @return the sparCombiOfferPromotionStrategy
	 */
	public SparCombiOfferPromotionStrategy getSparCombiOfferPromotionStrategy()
	{
		return sparCombiOfferPromotionStrategy;
	}

	/**
	 * Setter
	 *
	 * @param sparCombiOfferPromotionStrategy
	 *           the sparCombiOfferPromotionStrategy to set
	 */
	public void setSparCombiOfferPromotionStrategy(final SparCombiOfferPromotionStrategy sparCombiOfferPromotionStrategy)
	{
		this.sparCombiOfferPromotionStrategy = sparCombiOfferPromotionStrategy;
	}


}
