/**
 *
 */
package com.spar.hcl.order.promotion.strategy.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.ProductBOGOFPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.promotions.result.WrappedOrderEntry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.order.promotion.strategy.SparCommerceCartValidateBOGOStrategy;
import com.spar.hcl.promotions.model.SparProductBOGOFPromotionModel;
import com.spar.hcl.promotions.model.SparProductBXGYFPromotionModel;


/**
 * This method is used to have a custom promotion logic
 *
 * @author rohan_c
 *
 */
public class SparCommerceCartValidateBOGOStrategyImpl implements SparCommerceCartValidateBOGOStrategy
{
	private ModelService modelService;
	private PromotionsService promotionService;
	private BaseSiteService baseSiteService;
	private TimeService timeService;

	private final static Logger LOG = Logger.getLogger(SparCommerceCartValidateBOGOStrategyImpl.class.getName());
	/**
	 * This method retrieves the list of promotionResults
	 *
	 * @param promotionsResults
	 * @return List<PromotionResultModel>
	 */
	protected List<PromotionResultModel> getPromotions(final List<PromotionResult> promotionsResults)
	{
		return getModelService().getAll(promotionsResults, new ArrayList<PromotionResultModel>());
	}

	@Override
	public ProductBOGOFPromotionModel getBOGOIfApplicable(final CommerceCartParameter parameters,
			final CommerceCartModification result) throws CommerceCartModificationException
	{
		final CartModel cart = parameters.getCart();
		return getBOGOIfApplicable(cart, result);
	}

	@Override
	public ProductBOGOFPromotionModel getBOGOIfApplicable(final CartModel cart, final CommerceCartModification result)
	{
		final PromotionOrderResults promotionOrderResults = getPromotionService().getPromotionResults(cart);

		final List<WrappedOrderEntry> wrappedOrderEntryList = promotionOrderResults.getEntriesWithPotentialPromotions();
		WrappedOrderEntry potentialPromotionOrderEntry = null;
		if (CollectionUtils.isNotEmpty(wrappedOrderEntryList))
		{
			for (final WrappedOrderEntry wrappedOrderEntry : wrappedOrderEntryList)
			{
				if (null != result && null != result.getEntry() && null != result.getEntry().getPk()
						&& null != wrappedOrderEntry.getBaseOrderEntry()
						&& result.getEntry().getPk().equals(wrappedOrderEntry.getBaseOrderEntry().getPK()))
				{
					potentialPromotionOrderEntry = wrappedOrderEntry;
					break;
				}
			}
		}

		if (null != potentialPromotionOrderEntry)
		{
			final List<PromotionResultModel> promotionResults = getPromotions(potentialPromotionOrderEntry.getPromotionResults());
			if (CollectionUtils.isNotEmpty(promotionResults))
			{
				for (final PromotionResultModel promotion : promotionResults)
				{
					if (promotion.getPromotion() instanceof SparProductBOGOFPromotionModel)
					{
						final SparProductBOGOFPromotionModel orderPromotion = (SparProductBOGOFPromotionModel) (promotion.getPromotion());
						if ((result.getEntry().getQuantity().longValue() % orderPromotion.getQualifyingCount().intValue()) == orderPromotion
								.getQualifyingCount().intValue() - orderPromotion.getFreeCount().intValue())
						{
							return orderPromotion;
						}
					}
					if (promotion.getPromotion() instanceof SparProductBXGYFPromotionModel)
					{
						if (promotion.getPromotion() instanceof SparProductBXGYFPromotionModel)
						{
						final SparProductBOGOFPromotionModel orderPromotion = (SparProductBOGOFPromotionModel) (promotion.getPromotion());
						if ((result.getEntry().getQuantity().longValue() % orderPromotion.getQualifyingCount().intValue()) == orderPromotion
								.getQualifyingCount().intValue() - orderPromotion.getFreeCount().intValue())
						{
							return orderPromotion;
						}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Getter
	 *
	 * @return the promotionService
	 */
	public PromotionsService getPromotionService()
	{
		return promotionService;
	}

	/**
	 * Setter
	 *
	 * @param promotionsService
	 *           the promotionService to set
	 */
	public void setPromotionService(final PromotionsService promotionsService)
	{
		this.promotionService = promotionsService;
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
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Setter
	 *
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Getter
	 *
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Setter
	 *
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}


}
