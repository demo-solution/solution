/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.promotions.jalo.ProductFixedPricePromotion;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.promotions.model.ProductBundlePromotionModel;
import de.hybris.platform.promotions.model.ProductFixedPricePromotionModel;
import de.hybris.platform.promotions.model.ProductOneToOnePerfectPartnerPromotionModel;
import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVWriter;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.WeakArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.model.process.SparExpiredPromoArchErrorEmailProcessModel;
import com.spar.hcl.model.promotions.SparWarehousePromotionRestrictionModel;
import com.spar.hcl.promotions.model.SparProductBOGOFPromotionModel;
import com.spar.hcl.promotions.model.SparProductFixedPriceDiscountPromotionModel;
import com.spar.hcl.promotions.model.SparProductPercentDiscountPromotionModel;


/**
 * @author tanveers
 *
 */
public class SparExpiredPromotionArchiveJob extends AbstractJobPerformable
{
	private static final Logger LOG = Logger.getLogger(SparExpiredPromotionArchiveJob.class);

	private final String filePathDest = Config.getParameter("sparcore.sparpromotionexportcronjob.export.path");
	private final String jobInterval = Config.getParameter("sparcore.sparpromotionexportcronjob.job.interval");
	private final String bogoPromotionFileName = "BOGO_PROMOTION_";
	private final String bundlePromotionFileName = "BUNDLE_PROMOTION_";
	private final String fixedPricePromotionFileName = "FIXED_PRICE_PROMOTION_";
	private final String percentagePromotionFileName = "PERCENTAGE_PROMOTION_";
	private final String perfectPartnerPromotionFileName = "PERFECT_PARTNER_PROMOTION_";
	private final String percentDiscountPromotionFileName = "SPAR_PERCENT_DISCOUNT_";
	private final String fixedPriceDiscountPromotionFileName = "SPAR_FIXED_PRICE_DISCOUNT_";


	// CSVWriter settings
	private static char commentchar = CSVConstants.DEFAULT_COMMENT_CHAR;
	private static char fieldcommaseperator = ';';
	private static char fieldsemiseperator = ';';
	private static char lineseperator = ';';
	private static char textseperator = CSVConstants.DEFAULT_QUOTE_CHARACTER;

	// stores all CSVWriter, output map
	private Map<String, MyCSVWriter> outputWriters;

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	public static final String SPAR_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

	public static final String SPAR_EXPORT_DATE_FORMAT = "yyyy-MM-dd";

	//Delimiters
	public static final String UNDERSCORE_DELIMETER = "_";
	public static final String DATE_SEPARATOR = " TO ";
	public static final String FILE_EXTENSION = ".csv";

	//Date date;
	SimpleDateFormat dateFormat;
	SimpleDateFormat exportdateFormat;
	String voucherCode = "";
	Double vouchervalue;

	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		outputWriters = new HashMap<String, MyCSVWriter>();
		dateFormat = new SimpleDateFormat(SPAR_DATE_FORMAT);
		exportdateFormat = new SimpleDateFormat(SPAR_EXPORT_DATE_FORMAT);
		final Calendar backupCal = Calendar.getInstance();
		boolean isJobError = false;

		final File file = new File(filePathDest);
		if (!file.exists())
		{
			file.mkdir();
		}

		try
		{
			archiveFixedPricePromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export for ProductFixedPricePromotion ", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("ProductFixedPricePromotion" + e.getMessage());
		}

		try
		{
			archiveBOGOPromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("SparProductBOGOFPromotion");
		}

		try
		{
			archiveBundlePromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("ProductBundlePromotion");
		}

		try
		{
			archivePercentageDiscountPromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("ProductPercentageDiscountPromotion");
		}
		try
		{
			archivePerfectPartnerPromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("ProductOneToOnePerfectPartnerPromotion");
		}
		try
		{
			archiveSparPercentDiscountPromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("SparProductPercentDiscountPromotion");
		}
		try
		{
			archiveSparFixedPriceDiscountPromotion(backupCal);
		}
		catch (final IOException e)
		{
			LOG.error("Exception occurred during promotion Export", e);
			isJobError = true;
			sendExpiredPromoArchErrorEmail("SparProductFixedPriceDiscountPromotion");
		}

		if (isJobError)
		{
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	public void archiveFixedPricePromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_FIXED_PRICE_PROMO = "SELECT {pfp." + ProductFixedPricePromotionModel.PK
				+ "} from {ProductFixedPricePromotion as pfp} where {pfp." + ProductFixedPricePromotion.ENDDATE + "} < '"
				+ dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery fixedPricePromoQuery = new FlexibleSearchQuery(QUERY_FOR_FIXED_PRICE_PROMO);
		final SearchResult<ProductFixedPricePromotionModel> result = flexibleSearchService.search(fixedPricePromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{

			final String fileName = getFileNamePattern(fixedPricePromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();

			// Setting headers for the export file
			final String lineHeader = "####" + ProductFixedPricePromotionModel.PK + fieldcommaseperator
					+ ProductFixedPricePromotionModel.CODE + fieldcommaseperator + ProductFixedPricePromotionModel.CREATIONTIME
					+ fieldcommaseperator + ProductFixedPricePromotionModel.DESCRIPTION + fieldcommaseperator
					+ ProductFixedPricePromotionModel.ENABLED + fieldcommaseperator + ProductFixedPricePromotionModel.ENDDATE
					+ fieldcommaseperator + ProductFixedPricePromotionModel.MESSAGEFIRED + fieldcommaseperator
					+ ProductFixedPricePromotionModel.MODIFIEDTIME + fieldcommaseperator + ProductFixedPricePromotionModel.NAME
					+ fieldcommaseperator + ProductFixedPricePromotionModel.PRIORITY + fieldcommaseperator + "pricePK"
					+ fieldcommaseperator + ProductFixedPricePromotionModel.STARTDATE + fieldcommaseperator
					+ ProductFixedPricePromotionModel.TITLE + fieldcommaseperator + "products" + fieldcommaseperator + "prices"
					+ fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);


			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final ProductFixedPricePromotionModel fpp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> promoPriceRowList = new ArrayList<String>();
				final List<String> promoPriceRowPKList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final PromotionPriceRowModel promoPriceRow : fpp.getProductFixedUnitPrice())
				{
					promoPriceRowList.add(String.valueOf(promoPriceRow.getPrice().intValue()));
					promoPriceRowPKList.add(promoPriceRow.getPk().toString());
				}
				for (final ProductModel product : fpp.getProducts())
				{
					productList.add(product.getCode());
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : fpp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = fpp.getPk().toString() + fieldcommaseperator + fpp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getCreationtime()) + fieldcommaseperator + fpp.getDescription()
						+ fieldcommaseperator + fpp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getEndDate()) + fieldcommaseperator + fpp.getMessageFired(Locale.ENGLISH)
						+ fieldcommaseperator + exportdateFormat.format(fpp.getModifiedtime()) + fieldcommaseperator
						+ fpp.getName(Locale.ENGLISH) + fieldcommaseperator + fpp.getPriority().intValue() + fieldcommaseperator
						+ StringUtils.join(promoPriceRowPKList, ",") + fieldcommaseperator
						+ exportdateFormat.format(fpp.getStartDate()) + fieldcommaseperator + fpp.getTitle() + fieldcommaseperator
						+ StringUtils.join(productList, ",") + fieldcommaseperator + StringUtils.join(promoPriceRowList, ",")
						+ fieldcommaseperator + StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archivePercentageDiscountPromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_PERCENT_DISCOUNT_PROMO = "SELECT {pdp." + ProductPercentageDiscountPromotionModel.PK
				+ "} from {ProductPercentageDiscountPromotion as pdp} where {pdp." + ProductPercentageDiscountPromotionModel.ENDDATE
				+ "} < '" + dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery percentDiscountPromoQuery = new FlexibleSearchQuery(QUERY_FOR_PERCENT_DISCOUNT_PROMO);
		final SearchResult<ProductPercentageDiscountPromotionModel> result = flexibleSearchService
				.search(percentDiscountPromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(percentagePromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + ProductPercentageDiscountPromotionModel.PK + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.CODE + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.CREATIONTIME + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.DESCRIPTION + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.ENABLED + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.ENDDATE + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.MESSAGEFIRED + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.MODIFIEDTIME + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.NAME + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.PRIORITY + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.STARTDATE + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.TITLE + fieldcommaseperator
					+ ProductPercentageDiscountPromotionModel.PERCENTAGEDISCOUNT + fieldcommaseperator + "products"
					+ fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final ProductPercentageDiscountPromotionModel pdp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final ProductModel product : pdp.getProducts())
				{
					productList.add(product.getCode());
				}
				//Collecting all the restriction for cleanup andof warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : pdp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = pdp.getPk().toString() + fieldcommaseperator + pdp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(pdp.getCreationtime()) + fieldcommaseperator + pdp.getDescription()
						+ fieldcommaseperator + pdp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(pdp.getEndDate()) + fieldcommaseperator + pdp.getMessageFired(Locale.ENGLISH)
						+ fieldcommaseperator + exportdateFormat.format(pdp.getModifiedtime()) + fieldcommaseperator
						+ pdp.getName(Locale.ENGLISH) + fieldcommaseperator + pdp.getPriority() + fieldcommaseperator
						+ exportdateFormat.format(pdp.getStartDate()) + fieldcommaseperator + pdp.getTitle() + fieldcommaseperator
						+ pdp.getPercentageDiscount().doubleValue() + fieldcommaseperator + StringUtils.join(productList, ",")
						+ fieldcommaseperator + StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archiveBOGOPromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_BOGO_PROMO = "SELECT {bgp." + SparProductBOGOFPromotionModel.PK
				+ "} from {SparProductBOGOFPromotion as bgp} where {bgp." + SparProductBOGOFPromotionModel.ENDDATE + "} < '"
				+ dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery bogoPromoQuery = new FlexibleSearchQuery(QUERY_FOR_BOGO_PROMO);
		final SearchResult<SparProductBOGOFPromotionModel> result = flexibleSearchService.search(bogoPromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(bogoPromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + SparProductBOGOFPromotionModel.PK + fieldcommaseperator
					+ SparProductBOGOFPromotionModel.CODE + fieldcommaseperator + SparProductBOGOFPromotionModel.CREATIONTIME
					+ fieldcommaseperator + SparProductBOGOFPromotionModel.DESCRIPTION + fieldcommaseperator
					+ SparProductBOGOFPromotionModel.ENABLED + fieldcommaseperator + SparProductBOGOFPromotionModel.ENDDATE
					+ fieldcommaseperator + SparProductBOGOFPromotionModel.MESSAGECOULDHAVEFIRED + fieldcommaseperator
					+ SparProductBOGOFPromotionModel.MESSAGEFIRED + fieldcommaseperator + SparProductBOGOFPromotionModel.MODIFIEDTIME
					+ fieldcommaseperator + SparProductBOGOFPromotionModel.QUALIFYINGCOUNT + fieldcommaseperator
					+ SparProductBOGOFPromotionModel.PRIORITY + fieldcommaseperator + SparProductBOGOFPromotionModel.STARTDATE
					+ fieldcommaseperator + SparProductBOGOFPromotionModel.TITLE + fieldcommaseperator + "products"
					+ fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final SparProductBOGOFPromotionModel bgp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final ProductModel product : bgp.getProducts())
				{
					productList.add(product.getCode());
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : bgp.getRestrictions())
				{
					final Collection<WarehouseModel> warehouses = ((SparWarehousePromotionRestrictionModel) promoRestr)
							.getWarehouses();
					if (!warehouses.isEmpty())
					{
						for (final WarehouseModel warehouse : warehouses)
						{
							warehousesCodeList.add(warehouse.getCode());
						}
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = bgp.getPk().toString() + fieldcommaseperator + bgp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(bgp.getCreationtime()) + fieldcommaseperator + bgp.getDescription()
						+ fieldcommaseperator + bgp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(bgp.getEndDate()) + fieldcommaseperator
						+ bgp.getMessageCouldHaveFired(Locale.ENGLISH) + fieldcommaseperator + bgp.getMessageFired(Locale.ENGLISH)
						+ fieldcommaseperator + exportdateFormat.format(bgp.getModifiedtime()) + fieldcommaseperator
						+ bgp.getQualifyingCount().intValue() + fieldcommaseperator + bgp.getPriority().intValue()
						+ fieldcommaseperator + exportdateFormat.format(bgp.getStartDate()) + fieldcommaseperator + bgp.getTitle()
						+ fieldcommaseperator + StringUtils.join(productList, ",") + fieldcommaseperator
						+ StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archiveBundlePromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_BUNDLE_PROMO = "SELECT {bp." + ProductBundlePromotionModel.PK
				+ "} from {ProductBundlePromotion as bp} where {bp." + ProductBundlePromotionModel.ENDDATE + "} < '"
				+ dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery bundlePromoQuery = new FlexibleSearchQuery(QUERY_FOR_BUNDLE_PROMO);
		final SearchResult<ProductBundlePromotionModel> result = flexibleSearchService.search(bundlePromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(bundlePromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + ProductBundlePromotionModel.PK + fieldcommaseperator
					+ ProductBundlePromotionModel.CODE + fieldcommaseperator + ProductBundlePromotionModel.CREATIONTIME
					+ fieldcommaseperator + ProductBundlePromotionModel.DESCRIPTION + fieldcommaseperator
					+ ProductBundlePromotionModel.ENABLED + fieldcommaseperator + ProductBundlePromotionModel.ENDDATE
					+ fieldcommaseperator + ProductBundlePromotionModel.MESSAGECOULDHAVEFIRED + fieldcommaseperator
					+ ProductBundlePromotionModel.MESSAGEFIRED + fieldcommaseperator + ProductBundlePromotionModel.MODIFIEDTIME
					+ fieldcommaseperator + ProductBundlePromotionModel.PRIORITY + fieldcommaseperator
					+ ProductBundlePromotionModel.STARTDATE + fieldcommaseperator + ProductBundlePromotionModel.TITLE
					+ fieldcommaseperator + "products" + fieldcommaseperator + "prices" + fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final ProductBundlePromotionModel bp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> bundlePricesList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final ProductModel product : bp.getProducts())
				{
					productList.add(product.getCode());
				}
				for (final PromotionPriceRowModel bundlePrices : bp.getBundlePrices())
				{
					bundlePricesList.add(String.valueOf(bundlePrices.getPrice().intValue()));
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : bp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = bp.getPk().toString() + fieldcommaseperator + bp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(bp.getCreationtime()) + fieldcommaseperator + bp.getDescription()
						+ fieldcommaseperator + bp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(bp.getEndDate()) + fieldcommaseperator + bp.getMessageCouldHaveFired(Locale.ENGLISH)
						+ fieldcommaseperator + bp.getMessageFired(Locale.ENGLISH) + fieldcommaseperator
						+ exportdateFormat.format(bp.getModifiedtime()) + fieldcommaseperator + bp.getPriority().intValue()
						+ fieldcommaseperator + exportdateFormat.format(bp.getStartDate()) + fieldcommaseperator + bp.getTitle()
						+ fieldcommaseperator + StringUtils.join(productList, ",") + fieldcommaseperator
						+ StringUtils.join(bundlePricesList, ",") + fieldcommaseperator + StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archivePerfectPartnerPromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_PERFECT_PARTNER_PROMO = "SELECT {pp." + ProductOneToOnePerfectPartnerPromotionModel.PK
				+ "} from {ProductOneToOnePerfectPartnerPromotion as pp} where {pp."
				+ ProductOneToOnePerfectPartnerPromotionModel.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery perfectPartnerPromoQuery = new FlexibleSearchQuery(QUERY_FOR_PERFECT_PARTNER_PROMO);
		final SearchResult<ProductOneToOnePerfectPartnerPromotionModel> result = flexibleSearchService
				.search(perfectPartnerPromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(perfectPartnerPromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + ProductOneToOnePerfectPartnerPromotionModel.PK + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.CODE + fieldcommaseperator + "products" + fieldcommaseperator
					+ "prices" + fieldcommaseperator + ProductOneToOnePerfectPartnerPromotionModel.CREATIONTIME + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.DESCRIPTION + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.DETAILSURL + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.ENABLED + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.ENDDATE + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.MESSAGECOULDHAVEFIRED + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.MESSAGEFIRED + fieldcommaseperator + "partnerCode"
					+ fieldcommaseperator + ProductOneToOnePerfectPartnerPromotionModel.MODIFIEDTIME + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.PRIORITY + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.STARTDATE + fieldcommaseperator
					+ ProductOneToOnePerfectPartnerPromotionModel.TITLE + fieldcommaseperator + "products" + fieldcommaseperator
					+ "prices" + fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final ProductOneToOnePerfectPartnerPromotionModel pp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> bundlePricesList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final ProductModel product : pp.getProducts())
				{
					productList.add(product.getCode());
				}
				for (final PromotionPriceRowModel bundlePrices : pp.getBundlePrices())
				{
					bundlePricesList.add(String.valueOf(bundlePrices.getPrice().intValue()));
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : pp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = pp.getPk().toString() + fieldcommaseperator + pp.getCode() + fieldcommaseperator
						+ pp.getBaseProduct().getCode() + fieldcommaseperator + StringUtils.join(bundlePricesList, ",")
						+ fieldcommaseperator + exportdateFormat.format(pp.getCreationtime()) + fieldcommaseperator
						+ pp.getDescription() + fieldcommaseperator + pp.getDetailsURL() + fieldcommaseperator
						+ pp.getEnabled().booleanValue() + fieldcommaseperator + exportdateFormat.format(pp.getEndDate())
						+ fieldcommaseperator + pp.getMessageCouldHaveFired(Locale.ENGLISH) + fieldcommaseperator
						+ pp.getMessageFired(Locale.ENGLISH) + fieldcommaseperator + pp.getPartnerProduct().getCode()
						+ fieldcommaseperator + exportdateFormat.format(pp.getModifiedtime()) + fieldcommaseperator
						+ pp.getPriority().intValue() + fieldcommaseperator + exportdateFormat.format(pp.getStartDate())
						+ fieldcommaseperator + pp.getTitle() + fieldcommaseperator + StringUtils.join(productList, ",")
						+ fieldcommaseperator + StringUtils.join(bundlePricesList, ",") + fieldcommaseperator
						+ StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archiveSparPercentDiscountPromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_PERCENT_DISCOUNT_PROMO = "SELECT {ppd." + SparProductPercentDiscountPromotionModel.PK
				+ "} from {SparProductPercentDiscountPromotion as ppd} where {ppd."
				+ SparProductPercentDiscountPromotionModel.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery percentDiscountPromoQuery = new FlexibleSearchQuery(QUERY_FOR_PERCENT_DISCOUNT_PROMO);
		final SearchResult<SparProductPercentDiscountPromotionModel> result = flexibleSearchService
				.search(percentDiscountPromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(percentDiscountPromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + SparProductPercentDiscountPromotionModel.PK + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.CODE + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.CREATIONTIME + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.DESCRIPTION + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.ENABLED + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.ENDDATE + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.MESSAGEFIRED + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.MESSAGECOULDHAVEFIRED + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.MODIFIEDTIME + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.NAME + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.PRIORITY + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.STARTDATE + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.TITLE + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.QUALIFYINGCOUNT + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.PRODUCTS + fieldcommaseperator
					+ SparProductPercentDiscountPromotionModel.PERCENTAGEDISCOUNT + fieldcommaseperator + "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final SparProductPercentDiscountPromotionModel fpp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final ProductModel product : fpp.getProducts())
				{
					productList.add(product.getCode());
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : fpp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}
				String lineData = fpp.getPk().toString() + fieldcommaseperator + fpp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getCreationtime()) + fieldcommaseperator + fpp.getDescription()
						+ fieldcommaseperator + fpp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getEndDate()) + fieldcommaseperator + fpp.getMessageFired(Locale.ENGLISH)
						+ fieldcommaseperator + fpp.getMessageCouldHaveFired(Locale.ENGLISH) + fieldcommaseperator
						+ exportdateFormat.format(fpp.getModifiedtime()) + fieldcommaseperator + fpp.getName(Locale.ENGLISH)
						+ fieldcommaseperator + fpp.getPriority().intValue() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getStartDate()) + fieldcommaseperator + fpp.getTitle() + fieldcommaseperator
						+ fpp.getQualifyingCount().intValue() + fieldcommaseperator + StringUtils.join(productList, ",")
						+ fieldcommaseperator + fpp.getPercentageDiscount() + fieldcommaseperator
						+ StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	public void archiveSparFixedPriceDiscountPromotion(final Calendar backupCal) throws IOException
	{
		final String QUERY_FOR_FIXED_PRICE_DISCOUNT_PROMO = "SELECT {pfp." + SparProductFixedPriceDiscountPromotionModel.PK
				+ "} from {SparProductFixedPriceDiscountPromotion as pfp} where {pfp."
				+ SparProductFixedPriceDiscountPromotionModel.ENDDATE + "} < '" + dateFormat.format(backupCal.getTime()) + "'";

		final FlexibleSearchQuery fixedPricePromoQuery = new FlexibleSearchQuery(QUERY_FOR_FIXED_PRICE_DISCOUNT_PROMO);
		final SearchResult<SparProductFixedPriceDiscountPromotionModel> result = flexibleSearchService.search(fixedPricePromoQuery);

		if (result.getResult() != null & !result.getResult().isEmpty())
		{
			final String fileName = getFileNamePattern(fixedPriceDiscountPromotionFileName, backupCal) + FILE_EXTENSION;
			final MyCSVWriter wr = exportCSV(fileName);
			Map<Integer, Object> mapLine = new HashMap<Integer, Object>();
			final WeakArrayList<AbstractPromotionRestrictionModel> restrToRemoveList = new WeakArrayList<AbstractPromotionRestrictionModel>();
			// Setting headers for the export file
			final String lineHeader = "####" + SparProductFixedPriceDiscountPromotionModel.PK + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.CODE + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.CREATIONTIME + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.DESCRIPTION + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.ENABLED + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.ENDDATE + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.MESSAGEFIRED + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.MESSAGECOULDHAVEFIRED + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.MODIFIEDTIME + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.NAME + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.PRIORITY + fieldcommaseperator + "pricePK" + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.STARTDATE + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.TITLE + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.QUALIFYINGCOUNT + fieldcommaseperator
					+ SparProductFixedPriceDiscountPromotionModel.PRODUCTS + fieldcommaseperator + "prices" + fieldcommaseperator
					+ "warehouses";
			mapLine.put(Integer.valueOf(0), lineHeader);
			wr.write(mapLine);

			mapLine = new HashMap<Integer, Object>();
			int i = 1;
			for (final SparProductFixedPriceDiscountPromotionModel fpp : result.getResult())
			{
				final List<String> productList = new ArrayList<String>();
				final List<String> promoPriceRowList = new ArrayList<String>();
				final List<String> promoPriceRowPKList = new ArrayList<String>();
				final List<String> warehousesCodeList = new ArrayList<String>();
				for (final PromotionPriceRowModel promoPriceRow : fpp.getBundlePrices())
				{
					promoPriceRowList.add(String.valueOf(promoPriceRow.getPrice().intValue()));
					promoPriceRowPKList.add(promoPriceRow.getPk().toString());
				}
				for (final ProductModel product : fpp.getProducts())
				{
					productList.add(product.getCode());
				}
				//Collecting all the restriction for cleanup and warehouses
				for (final AbstractPromotionRestrictionModel promoRestr : fpp.getRestrictions())
				{
					for (final WarehouseModel warehouse : ((SparWarehousePromotionRestrictionModel) promoRestr).getWarehouses())
					{
						warehousesCodeList.add(warehouse.getCode());
					}
					restrToRemoveList.add(promoRestr);
				}

				String lineData = fpp.getPk().toString() + fieldcommaseperator + fpp.getCode() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getCreationtime()) + fieldcommaseperator + fpp.getDescription()
						+ fieldcommaseperator + fpp.getEnabled().booleanValue() + fieldcommaseperator
						+ exportdateFormat.format(fpp.getEndDate()) + fieldcommaseperator + fpp.getMessageFired(Locale.ENGLISH)
						+ fieldcommaseperator + fpp.getMessageCouldHaveFired(Locale.ENGLISH) + fieldcommaseperator
						+ exportdateFormat.format(fpp.getModifiedtime()) + fieldcommaseperator + fpp.getName(Locale.ENGLISH)
						+ fieldcommaseperator + fpp.getPriority().intValue() + fieldcommaseperator
						+ StringUtils.join(promoPriceRowPKList, ",") + fieldcommaseperator
						+ exportdateFormat.format(fpp.getStartDate()) + fieldcommaseperator + fpp.getTitle() + fieldcommaseperator
						+ fpp.getQualifyingCount() + fieldcommaseperator + StringUtils.join(productList, ",") + fieldcommaseperator
						+ StringUtils.join(promoPriceRowList, ",") + fieldcommaseperator + StringUtils.join(warehousesCodeList, ",");
				lineData = lineData.replace("null", "");
				mapLine.put(Integer.valueOf(i), lineData);
				i++;
				wr.write(mapLine);
			}
			wr.close();

			// Removing the archived data
			modelService.removeAll(result.getResult());
			modelService.removeAll(restrToRemoveList);
			modelService.saveAll();
		}
	}

	private String getFileNamePattern(final String fileName, final Calendar backupCal)
	{
		final String toDate = exportdateFormat.format(backupCal.getTime());
		final int jobIntervalVal = Integer.parseInt(jobInterval);
		final Calendar backExportCal = Calendar.getInstance();
		backExportCal.add(Calendar.DATE, -jobIntervalVal);
		final String fromDate = exportdateFormat.format(backExportCal.getTime());
		return fileName + fromDate + DATE_SEPARATOR + toDate;
	}

	private void sendExpiredPromoArchErrorEmail(final String errorPromoType)
	{
		final SparExpiredPromoArchErrorEmailProcessModel process = (SparExpiredPromoArchErrorEmailProcessModel) businessProcessService
				.createProcess("SparExpiredPromoArchErrorEmailProcess" + "-" + "-" + System.currentTimeMillis(),
						"SparExpiredPromoArchErrorEmailProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				"basesite.uid"));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		process.setErrorPromoType(errorPromoType);
		modelService.saveAll();
		businessProcessService.startProcess(process);
		LOG.info("sendExpiredPromoArchErrorEmail completed.!!!!!");
	}

	/**
	 * This method gets the CSV writer with the specified fileName
	 *
	 * @param fileName
	 *           the used filename
	 * @return a CSVWriter (here MyCSVWriter) for the given filename. The Encoding is set to UTF-8 and the file is bound
	 *         to the CSVWriter.
	 */
	protected MyCSVWriter exportCSV(final String fileName)
	{
		MyCSVWriter csvWriter = outputWriters.get(fileName);
		if (csvWriter == null)
		{
			try
			{
				csvWriter = new MyCSVWriter(new File(filePathDest + File.separator + fileName), encoding);
				outputWriters.put(fileName, csvWriter);
			}
			catch (final UnsupportedEncodingException e)
			{
				LOG.info("Exporting CSV faces UnsupportedEncodingException");
			}
			catch (final FileNotFoundException e)
			{
				LOG.info("Exporting CSV faces FileNotFoundException");
			}
		}
		return csvWriter;
	}

	/**
	 * Extended CSVWriter which keeps the given file in a link.
	 *
	 */
	protected static class MyCSVWriter extends CSVWriter
	{
		private final File file;

		/**
		 * Parameterized constructor
		 *
		 * @param file
		 * @param encoding
		 * @throws UnsupportedEncodingException
		 * @throws FileNotFoundException
		 */
		MyCSVWriter(final File file, final String encoding) throws UnsupportedEncodingException, FileNotFoundException
		{
			super(file, encoding, false);
			this.file = file;
		}

		@Override
		public String createCSVLine(final Map fields)
		{
			final BitSet cells = new BitSet();
			int max = 0;

			int str;
			for (final Iterator sb = fields.entrySet().iterator(); sb.hasNext(); max = max < str ? str : max)
			{
				final Entry i = (Entry) sb.next();
				str = ((Integer) i.getKey()).intValue();
				if (str < 0)
				{
					throw new IllegalArgumentException("cell index < 0 (got " + str + "=>" + i.getValue() + ")");
				}

				cells.set(str);
			}

			final StringBuilder arg6 = new StringBuilder();

			for (int arg7 = 0; arg7 <= max; ++arg7)
			{
				if (arg7 > 0)
				{
					arg6.append(super.getFieldseparator());
				}

				if (cells.get(arg7))
				{
					final String arg8 = (String) fields.get(Integer.valueOf(arg7));
					if (arg8 != null)
					{
						arg6.append(this.createCSVField(arg8));
					}
				}
			}

			return arg6.toString();
		}

		private String createCSVField(final String fielddata)
		{
			if (fielddata != null && fielddata.length() != 0)
			{
				final HashSet specials = new HashSet(Arrays.asList(CSVConstants.LINE_SEPARATORS));
				specials.add(Character.toString(super.getFieldseparator()));
				specials.add(super.getLinebreak());
				final StringBuilder buf = new StringBuilder(fielddata);

				return buf.toString();
			}
			else
			{
				return "";
			}
		}

		/**
		 * Getter for File
		 *
		 * @return the file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Delete File
		 *
		 * @return boolean
		 */
		public boolean deleteFile()
		{
			return this.file.delete();
		}

	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Override
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


}
