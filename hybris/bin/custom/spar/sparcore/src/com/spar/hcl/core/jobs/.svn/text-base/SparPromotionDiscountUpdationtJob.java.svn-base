/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.promotions.jalo.ProductFixedPricePromotion;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.promotions.model.ProductFixedPricePromotionModel;
import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.spar.hcl.model.promotions.SparWarehousePromotionRestrictionModel;
import com.spar.hcl.promotions.model.SparProductFixedPriceDiscountPromotionModel;
import com.spar.hcl.promotions.model.SparProductPercentDiscountPromotionModel;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;


/**
 * @author ravindra.kr
 *
 */
public class SparPromotionDiscountUpdationtJob extends AbstractJobPerformable
{
	private static final Logger LOG = Logger.getLogger(SparPromotionDiscountUpdationtJob.class);
	SimpleDateFormat dateFormat;
	FlexibleSearchService flexibleSearchService;
	ModelService modelService;
	public static final String SPAR_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		try
		{
			dateFormat = new SimpleDateFormat(SPAR_DATE_FORMAT);
			updateSparProductFixedPriceDiscountPromotion(dateFormat);
			updateProductFixedPricePromotion(dateFormat);
			updateSparProductPercentDiscountPromotion(dateFormat);
			updateProductPercentageDiscountPromotion(dateFormat);
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Promotion Discount updation", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	/**
	 * @param dateFormat
	 */
	private void updateSparProductFixedPriceDiscountPromotion(final SimpleDateFormat dateFormat)
	{
		final Calendar backupCal = Calendar.getInstance();
		final String QUERY = "SELECT {pfp." + SparProductFixedPriceDiscountPromotionModel.PK
				+ "} FROM {SparProductFixedPriceDiscountPromotion as pfp} " + "WHERE {pfp.enabled} = true AND {pfp."
				+ ProductFixedPricePromotion.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";
		LOG.info("In updateSparProductFixedPriceDiscountPromotion, Query : " + QUERY);
		updatePromotionDiscount(QUERY);
	}

	/**
	 * @param dateFormat
	 */
	private void updateProductFixedPricePromotion(final SimpleDateFormat dateFormat)
	{
		final Calendar backupCal = Calendar.getInstance();
		final String QUERY = "SELECT {pfp." + ProductFixedPricePromotionModel.PK + "} FROM {ProductFixedPricePromotion as pfp} "
				+ "WHERE {pfp.enabled} = true AND {pfp." + ProductFixedPricePromotion.ENDDATE + "} < '"
				+ dateFormat.format(backupCal.getTime()) + "'";
		LOG.info("In updateProductFixedPricePromotion, Query : " + QUERY);
		updatePromotionDiscount(QUERY);
	}

	/**
	 * @param dateFormat
	 */
	private void updateSparProductPercentDiscountPromotion(final SimpleDateFormat dateFormat)
	{
		final Calendar backupCal = Calendar.getInstance();
		final String QUERY = "SELECT {pfp." + SparProductPercentDiscountPromotionModel.PK
				+ "} FROM {SparProductPercentDiscountPromotion as pfp} " + "WHERE {pfp.enabled} = true AND {pfp."
				+ ProductFixedPricePromotion.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";
		LOG.info("In updateSparProductPercentDiscountPromotion, Query : " + QUERY);
		updatePromotionDiscount(QUERY);
	}

	/**
	 * @param dateFormat
	 */
	private void updateProductPercentageDiscountPromotion(final SimpleDateFormat dateFormat)
	{
		final Calendar backupCal = Calendar.getInstance();
		final String QUERY = "SELECT {pfp." + ProductPercentageDiscountPromotionModel.PK
				+ "} FROM {ProductPercentageDiscountPromotion as pfp} " + "WHERE {pfp.enabled} = true AND {pfp."
				+ ProductFixedPricePromotion.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";
		LOG.info("In updateProductPercentageDiscountPromotion, Query : " + QUERY);
		updatePromotionDiscount(QUERY);
	}

	/**
	 * @param result
	 */
	private void updatePromotionDiscount(final String QUERY)
	{
		final FlexibleSearchQuery fixedPricePromoQuery = new FlexibleSearchQuery(QUERY);
		final SearchResult<ProductPromotionModel> result = flexibleSearchService.search(fixedPricePromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			Collection<PriceRowModel> priceRowModelList = new ArrayList<PriceRowModel>();
			for (final ProductPromotionModel productPromotionModel : result.getResult())
			{
				int totalProductPromotions = 0;
				List<String> warehouseCodes = new ArrayList<>();
				for (final ProductModel product : productPromotionModel.getProducts())
				{
					priceRowModelList = product.getEurope1Prices();
					final Collection<ProductPromotionModel> promotions = product.getPromotions();
					if (promotions != null && CollectionUtils.isNotEmpty(promotions))
					{
						totalProductPromotions = getProductPromotions(promotions);
					}
					if (CollectionUtils.isNotEmpty(priceRowModelList))
					{
						for (final AbstractPromotionRestrictionModel promoRestr : productPromotionModel.getRestrictions())
						{
							if (promoRestr instanceof SparWarehousePromotionRestrictionModel)
							{
								final SparWarehousePromotionRestrictionModel sparWarehousePromoRestriction = (SparWarehousePromotionRestrictionModel) promoRestr;
								warehouseCodes = getWarehousePromoRestriction(sparWarehousePromoRestriction);
								for (final PriceRowModel priceRowModel : priceRowModelList)
								{
									final SparPriceRowModel sparPriceRowModel = (SparPriceRowModel) priceRowModel;
									final WarehouseModel warehouse = sparPriceRowModel.getWarehouse();
									if (CollectionUtils.isNotEmpty(warehouseCodes) && warehouse != null)
									{
										if (warehouseCodes.contains(warehouse.getCode()) && null != sparPriceRowModel.getPromotionDiscount()
												&& totalProductPromotions == 0)
										{
											sparPriceRowModel.setPromotionDiscount(null);
											getModelService().save(sparPriceRowModel);
										}
									}
								}
							}
						}
					}
					productPromotionModel.setEnabled(Boolean.FALSE);
					getModelService().save(productPromotionModel);
				}
			}
		}
	}

	private List<String> getWarehousePromoRestriction(final SparWarehousePromotionRestrictionModel sparWarehousePromoRestriction)
	{
		final Collection<WarehouseModel> warehouses = sparWarehousePromoRestriction.getWarehouses();
		final List<String> warehouseCodes = new ArrayList<>();
		if (warehouses != null && CollectionUtils.isNotEmpty(warehouses))
		{
			for (final WarehouseModel warehouseModel : warehouses)
			{
				warehouseCodes.add(warehouseModel.getCode());
			}
		}
		return warehouseCodes;
	}

	protected int getProductPromotions(final Collection<ProductPromotionModel> promotions)
	{
		int totalProductPromotions = 0;
		if (promotions != null && CollectionUtils.isNotEmpty(promotions))
		{
			for (final ProductPromotionModel productPromotion : promotions)
			{
				if (productPromotion.getEnabled())
				{
					final Calendar calender = Calendar.getInstance();
					final Date date = calender.getTime();
					final Date currentDate = convertToDateFormat(date, SPAR_DATE_FORMAT);
					final Date startDate = convertToDateFormat(productPromotion.getStartDate(), SPAR_DATE_FORMAT);
					final Date endDate = convertToDateFormat(productPromotion.getEndDate(), SPAR_DATE_FORMAT);
					if (startDate != null && endDate != null && currentDate != null)
					{
						if (endDate.getTime() >= currentDate.getTime())
						{
							totalProductPromotions = totalProductPromotions + 1;
						}
					}
					System.out.println("########## getProductPromotions  totalProductPromotions " + totalProductPromotions);
				}
			}
		}
		return totalProductPromotions;
	}

	private Date convertToDateFormat(final Date date, final String formater)
	{
		Date startDate = null;
		if (date != null)
		{
			final SimpleDateFormat dateFormat = new SimpleDateFormat(formater);
			try
			{
				startDate = dateFormat.parse(dateFormat.format(date));
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
			}
		}

		return startDate;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
