/**
 *
 */
package com.spar.hcl.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.PromotionOrderEntryConsumedPopulator;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.price.service.SparCommercePriceService;


/**
 * This populator extends PromotionOrderEntryConsumedPopulator to populate the flag for combi offer in
 * PromotionOrderEntryConsumedData
 *
 * @author rohan_c
 *
 */
public class SparPromotionOrderEntryConsumedPopulator extends PromotionOrderEntryConsumedPopulator
{
	private static final Logger LOG = Logger.getLogger(SparPromotionOrderEntryConsumedPopulator.class);

	private CommercePriceService commercePriceService;

	private SparCommercePriceService sparCommercePriceService;



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.PromotionOrderEntryConsumedPopulator#populate(de
	 * .hybris.platform.promotions.model.PromotionOrderEntryConsumedModel,
	 * de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData)
	 */
	@Override
	public void populate(final PromotionOrderEntryConsumedModel source, final PromotionOrderEntryConsumedData target)
	{
		super.populate(source, target);
		populateCombiOfferApplicable(source, target);
	}

	/**
	 * This method is used to populate the flag for combi offer in PromotionOrderEntryConsumedData
	 *
	 * @param source
	 * @param target
	 */
	private void populateCombiOfferApplicable(final PromotionOrderEntryConsumedModel source,
			final PromotionOrderEntryConsumedData target)
	{
		if (source.getOrderEntry() != null)
		{
			//final PriceInformation price = getCommercePriceService().getWebPriceForProduct(source.getOrderEntry().getProduct());
			final PriceInformation price = getSparCommercePriceService().getSparWebPriceForProduct(
					source.getOrderEntry().getProduct(), source);
			if (price != null && price.getQualifiers() != null && (price).getQualifiers().get("combiOffer") != null)
			{
				final Boolean isCombiOffer = (Boolean) (price).getQualifiers().get("combiOffer");
				target.setCombiOfferApplicable(BooleanUtils.isTrue(isCombiOffer));
			}
		}
	}

	/**
	 * Getter
	 *
	 * @return the commercePriceService
	 */
	public CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	/**
	 * Setter
	 *
	 * @param commercePriceService
	 *           the commercePriceService to set
	 */
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	/**
	 * @return the sparCommercePriceService
	 */
	public SparCommercePriceService getSparCommercePriceService()
	{
		return sparCommercePriceService;
	}

	/**
	 * @param sparCommercePriceService
	 *           the sparCommercePriceService to set
	 */
	public void setSparCommercePriceService(final SparCommercePriceService sparCommercePriceService)
	{
		this.sparCommercePriceService = sparCommercePriceService;
	}

}
