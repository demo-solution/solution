/**
 *
 */
package com.spar.hcl.core.service;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.PromotionPriceRowModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;
import de.hybris.platform.util.CSVWriter;
import de.hybris.platform.util.Config;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.SparVariantProductModel;
import com.spar.hcl.core.model.process.RMSCorruptedPriceFeedDataProcessModel;
import com.spar.hcl.core.model.process.SparFeedsErrorEmailProcessModel;
import com.spar.hcl.core.util.ZipUtil;
import com.spar.hcl.sparpricefactory.enums.BenefitTypeEnum;
import com.spar.hcl.sparpricefactory.model.SparPriceRowModel;


/**
 * @author anuj_mittal
 *
 */
public class SparImpexTransformerTask extends ImpexTransformerTask
{
	private static final Logger LOG = Logger.getLogger(SparImpexTransformerTask.class);
	private static final String PRICEROW_END_TIME = "sparcore.product.priceRow.endTime";
	private static final String QUERY_FOR_PRODUCT_TO_REMOVE="select {p.pk} from {Product as p} where {p.code}= ?productCode";
	private static final String PRICEROW_DELTA_AMOUNT = "promopricerow.duplicate.delta.amount";
	StockLevelModel stockLevelModel = null;
	/** The CleanupHelper */
	private CleanupHelper cleanupHelper;

	/** The BASESITE_UID */
	private static String BASESITE_UID = "basesite.uid";

	/** The ACTIVE_CATALOG_VERSION */
	private static String ACTIVE_CATALOG_VERSION = "active.catalog.version";

	/** The ACTIVE_CATALOG_NAME */
	private static String ACTIVE_CATALOG_NAME = "active.catalog.name";

	/** The ZIP_MIME_TYPE */
	private static String ZIP_MIME_TYPE = "application/zip";

	/** The ZIP_MIME_TYPE */
	private static String ZIP_MIME_TYPE_FOR_CORRUPT_PRICE_DATA = "text/csv";

	/** The FILE_SEPERATOR */
	//private static String FILE_SEPERATOR = "/";

	/** The ZIP_FILE_NAME */
	private static String ZIP_FILE_NAME = "Errorfiles.zip";

	/** The CRONJOB_LOG_FILE_NAME */
	private static String CRONJOB_LOG_FILE_NAME = "CronJobLog.log";
	private SessionService sessionService;
	private ImportService importService;

	private Map<Integer, List> corruptedRow;

	private Map<Integer, List> invalidRow;

	//private Map<Integer, List> invalidServiceRow;

	private List<String> corruptedRowData;

	//private List<String> invalidServiceData;

	private List<String> invalidRowData;
	//private List<PointOfServiceModel> allStores;

	private Integer totalRecords = 0;

	// stores all CSVWriter, output map
	private Map<String, MyCSVWriter> outputWriters;

	private final String filePathDest = Config.getParameter("sparcore.delta.feed.export.path");

	// encoding constant
	private final String encoding = CSVConstants.DEFAULT_ENCODING;

	public static final String SPAR_EXPORT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SPAR_DETLA_EXPORT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

	private static final String INVENTORY_EXPIRE_TIME = "sparcore.product.inventory.expireTime";

	@Resource(name = "modelService")
	ModelService modelService;


	final StockLevelModel model = null;
	@Autowired
	private EventService eventService;
	@Autowired
	private BaseStoreService baseStoreService;
	@Autowired
	private BaseSiteService baseSiteService;
	@Autowired
	private CommonI18NService commonI18NService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private UserService userService;


	/** The MediaService */
	@Autowired
	private MediaService mediaService;

	/** The CatalogVersionService */
	@Autowired
	private CatalogVersionService catalogVersionService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "productService")
	private ProductService productService;
	
	@Autowired
	private SearchRestrictionService searchRestrictionService;

	@Override
	protected boolean convertFile(final BatchHeader header, final File file, final File impexFile, final ImpexConverter converter)
			throws UnsupportedEncodingException, FileNotFoundException
	{
		boolean result = false;

		CSVReader csvReader = null;
		PrintWriter writer = null;
		PrintWriter errorWriter = null;
		MyCSVWriter deltaWriter = null;
		Date date = null;
		SimpleDateFormat dateFormat = null;
		final SimpleDateFormat promotiondateFormat = new SimpleDateFormat("dd-MMM-yy");
		String strDate = null;
		Integer count = 0;
		corruptedRow = new HashMap<Integer, List>();
		//invalidServiceRow = new HashMap<Integer, List>();
		invalidRow = new HashMap<Integer, List>();
		/*
		 * allStores =
		 * getBaseSiteService().getBaseSiteForUID(configurationService.getConfiguration().getString("basesite.uid"))
		 * .getStores().get(0).getPointsOfService();
		 */

		//final Map allStoreMap = new HashMap();
		try
		{
			/*
			 * allStores.forEach((stores) -> { allStoreMap.put(stores.getName().toString(), "true"); });
			 */
			csvReader = createCsvReader(file);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(impexFile), getEncoding())));
			writer.println(getReplacedHeader(header, converter));

			date = new Date();
			dateFormat = new SimpleDateFormat("MM/dd/yyyy H:MM:SS a");
			strDate = dateFormat.format(date);
			date = promotiondateFormat.parse(promotiondateFormat.format(date));
			totalRecords = 0;
			outputWriters = new HashMap<String, MyCSVWriter>();
			boolean isDeltaHeaderCreated = false;
			while (csvReader.readNextLine())
			{

				totalRecords++;
				final Map<Integer, String> row = csvReader.getLine();
				//if (file.getName().contains(configurationService.getConfiguration().getString(CUSTOMER_FEED_FILE_NAME)))
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE"))
				{
					try
					{

						if ((row.get(4).equals("") || row.get(5).equals("") || StringUtils.isEmpty(row.get(6))
								|| StringUtils.isEmpty(row.get(7)) || row.get(9).equals("") || row.get(11).equals("")
								|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)) || (!StringUtils
								.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(5));
							//final Date promotionstartDate = promotiondateFormat.parse(row.get(6));
							//final Date promotionendDate = promotiondateFormat.parse(row.get(7));
							if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.pricelist.message", "Price List is empty for promotion"));
							}
							else if (row.get(5).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Fixed price promotion"));
							}
							else if (StringUtils.isEmpty(row.get(6))
									|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(7))
									|| (!StringUtils.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							if (file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE_DISCOUNT"))
							{
								if (row.get(11).equals(""))
								{
									LOG.info("Applicable warehouse is empty or missing for the promotion");
									invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
											"Applicable warehouse is empty or missing for the promotion"));
								}
							}
							else
							{
								if (row.get(9).equals(""))
								{
									LOG.info("Applicable warehouse is empty or missing for the promotion");
									invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
											"Applicable warehouse is empty or missing for the promotion"));
								}
							}



							invalidRow.put(count, invalidRowData);
							continue;
						}
						
						if (null != row.get(4) && null != row.get(3) && getDulpicatePromotionPriceRow(row.get(3), row.get(4)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(4)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(4, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
						calulatePromotionDiscountForFixedPricePromotions(row, file);
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(5));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}

					//removeDulpicatePromotionPriceRow(row.get(3), row.get(4));

				}
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_BOGO"))
				{
					try
					{
						final Date promotionstartDate = promotiondateFormat.parse(row.get(7));
						final Date promotionendDate = promotiondateFormat.parse(row.get(8));
						if ((row.get(3).equals("") || Integer.valueOf(row.get(3)).intValue() <= 1) || row.get(4).equals("")
								|| Integer.valueOf(row.get(4)).intValue() <= 0 || row.get(6).equals("") || row.get(8).equals("")
								|| promotionendDate.before(date) || row.get(7).equals("") || row.get(10).equals("")
								|| promotionstartDate.before(date))
						{

							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(6));
							if (row.get(3).equals("") || Integer.valueOf(row.get(3)).intValue() <= 1)
							{
								invalidRowData.add(Config.getString("promotion.qualifyingcount.message",
										"Qualifying Count is either zero or empty for promotion"));
							}
							else if (row.get(4).equals("") || Integer.valueOf(row.get(4)).intValue() <= 0)
							{
								invalidRowData.add(Config.getString("promotion.freecount.message",
										"Freecount is either zero or empty for promotion"));
							}
							else if (row.get(6).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Product List is empty for promotion"));
							}
							else if (row.get(7).equals("") || promotionstartDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"StartDate date is empty or before the current date"));
							}
							else if (row.get(8).equals("") || promotionendDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}
							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
					}
					catch (final ParseException p)
					{
						LOG.info("Inside Parse");
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_BUNDLE"))
				{

					try
					{
						final Date promotionstartDate = promotiondateFormat.parse(row.get(7));
						final Date promotionendDate = promotiondateFormat.parse(row.get(8));
						if ((StringUtils.isEmpty(row.get(6)) || row.get(7).equals("") || promotionstartDate.before(date)
								|| row.get(8).equals("") || row.get(10).equals("") || promotionendDate.before(date)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(6));

							if (row.get(6).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Bundle Promotion"));
							}
							else if (row.get(7).equals("") || promotionstartDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"StartDate date is empty or before the current date"));
							}
							else if (row.get(8).equals("") || promotionendDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}
							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
						
						if (null != row.get(5) && null != row.get(4) && getDulpicatePromotionPriceRow(row.get(4), row.get(5)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(5)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(5, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
						//removeDulpicatePromotionPriceRow(row.get(4),row.get(5));
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}

				}

				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_PERCENT"))
				{
					try
					{

						if ((row.get(2).equals("") || row.get(4).equals("") || StringUtils.isEmpty(row.get(5))
								|| StringUtils.isEmpty(row.get(6)) || row.get(8).equals("")
								|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)) || (!StringUtils
								.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(4));
							/*
							 * final Date promotionstartDate = promotiondateFormat.parse(row.get(5)); final Date
							 * promotionendDate = promotiondateFormat.parse(row.get(6));
							 */
							if (row.get(2).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.discount.messgae", "Discount field is empty"));
							}
							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Percentage Promotion"));
							}
							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(6))
									|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}
							else if (row.get(8).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
						calulatePromotionDiscountForPercentPromotions(row, file);
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(4));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}

				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CUSTOM_PERCENT_DISCOUNT"))
				{
					try
					{

						if ((row.get(2).equals("") || row.get(4).equals("") || StringUtils.isEmpty(row.get(5))
								|| StringUtils.isEmpty(row.get(6)) || row.get(8).equals("") || row.get(10).equals("")
								|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)) || (!StringUtils
								.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(4));
							//final Date promotionstartDate = promotiondateFormat.parse(row.get(5));
							//final Date promotionendDate = promotiondateFormat.parse(row.get(6));
							if (row.get(2).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.discount.messgae", "Discount field is empty"));
							}
							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Percentage Promotion"));
							}
							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(6))
									|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
						calulatePromotionDiscountForPercentPromotions(row, file);
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(4));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}

				}
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_PERFECT_PARTNER"))
				{
					try
					{
						final Date promotionstartDate = promotiondateFormat.parse(row.get(8));
						final Date promotionendDate = promotiondateFormat.parse(row.get(9));
						if ((row.get(3).equals("") || row.get(7).equals("") || row.get(8).equals("") || row.get(11).equals("")
								|| promotionstartDate.before(date) || row.get(9).equals("") || promotionendDate.before(date)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(7));
							if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Perfect Partner Product"));
							}
							else if (row.get(7).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.actualproductlist.messgae",
										"Actual Product List is empty for PERFECT PARTNER Promotion"));
							}
							else if (row.get(8).equals("") || promotionstartDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (row.get(9).equals("") || promotionendDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}
							else if (row.get(11).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
						if (null != row.get(5) && null != row.get(4) && getDulpicatePromotionPriceRow(row.get(4), row.get(5)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(5)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(5, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(7));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}
				if (file.getName().contains("SPAR_ECOM_PRODUCT"))
				{
					catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Staged");
					//final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("sparProductCatalog", "Staged");
					final FlexibleSearchQuery queryForProducts = new FlexibleSearchQuery(QUERY_FOR_PRODUCT_TO_REMOVE);
					//queryForProducts.addQueryParameter("cv", catalogVersionModel);
					queryForProducts.addQueryParameter("productCode", row.get(7).toString());
					LOG.debug("query to fetch product to be removed :" + queryForProducts);
					searchRestrictionService.disableSearchRestrictions();
					final SearchResult<List<?>> resultForProducts = flexibleSearchService.search(queryForProducts);
					if (resultForProducts.getResult() != null && !resultForProducts.getResult().isEmpty())
					{
						ProductModel productModel = (ProductModel) resultForProducts.getResult().get(0);
						if(null != productModel.getApprovalStatus() && productModel.getApprovalStatus().equals(ArticleApprovalStatus.CHECK))
						{
							try
							{
								SparVariantProductModel sparVariantProduct  = (SparVariantProductModel) productModel;
								LOG.debug("sparVariantProduct code :" + sparVariantProduct.getCode());
							}
							catch(ClassCastException ex)
							{
								LOG.error("Product is removed during product feed import. Product Id :"+productModel.getCode()+", Exception :" + ex.getMessage());
								modelService.removeAll(resultForProducts.getResult());
								modelService.saveAll();
								
							}
						}
					}
					searchRestrictionService.enableSearchRestrictions();
					if (row.get(4).isEmpty())
					{
						LOG.warn("4th position of 3rd level category is blank ! Set the 2nd position to 4th position.");
						row.put(4, row.get(2));
					}
					if (null == row.get(16) || row.get(16).isEmpty())
					{
						LOG.warn("put 25 as maximum order quantity");
						row.put(16, "25");
					}

				}


				if (file.getName().contains("SPAR_ECOM_PRICE"))
				{
					WarehouseModel warehouseDB = null;
					SparPriceRowModel priceDB = null;

					// in case PriceRow does not have END_DATE from RMS, then default it to any future date (configurable in sparcore- project.properties.)
					if (null == row.get(3) || "".equals(row.get(3)))
					{
						final String endTime = Config.getString(PRICEROW_END_TIME, "31-Dec-30");
						row.put(3, endTime);
					}

					if (Double.valueOf(row.get(5)).doubleValue() < 0)
					{
						LOG.debug("Negative CSP found for Item Code: " + row.get(0));
						continue;
					}

					if (Double.valueOf(row.get(7)).doubleValue() < 0)
					{
						LOG.debug("Negative MRP found for Item Code: " + row.get(0));
						continue;
					}

					/**** Start :: End date check ****/
					try
					{
						catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
						catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Staged");
						final SparPriceRowModel price = new SparPriceRowModel();
						final WarehouseModel warehouse = new WarehouseModel();
						warehouse.setCode(row.get(4));
						warehouseDB = flexibleSearchService.getModelByExample(warehouse);
						price.setProductId(row.get(0));
						if (StringUtils.isNotEmpty(row.get(1)))
						{
							price.setCurrency(commonI18NService.getCurrency(row.get(1)));
						}
						price.setWarehouse(warehouseDB);
						//price.setUnit(unitDB);
						modelService.attach(price);
						priceDB = flexibleSearchService.getModelByExample(price);
						modelService.detach(price);

						if (null != priceDB)
						{
							if (StringUtils.isNotEmpty(row.get(3)))
							{
								if (null != priceDB.getEndTime())
								{
									if (new Date(row.get(3)).before(date) && priceDB.getEndTime().before(date))
									{
										LOG.debug("Skip past end date for Item Code: " + row.get(0) + " warehouse " + row.get(4));
										continue;
									}
								}
							}
						}
					}
					catch (final Exception e)
					{
						LOG.error("Exception while getting price row for Item :" + row.get(0) + " warehouse " + row.get(4)
								+ ":: Exception " + e.getMessage());
					}

					/**** End :: End date check ****/

					if (Boolean.valueOf(row.get(17)).booleanValue() || Boolean.valueOf(row.get(18)).booleanValue())
					{
						LOG.debug("RMS Price data to be corrected  for Item Code: " + row.get(0) + ", REGULAR_OFFER =" + row.get(17)
								+ ", COMBI_OFFER =" + row.get(18));
						LOG.debug("CSP: " + Double.valueOf(row.get(7)).doubleValue());
						LOG.debug("MRP: " + Double.valueOf(row.get(7)).doubleValue());
						row.put(5, row.get(7));
					}

					else if (Double.valueOf(row.get(5)).doubleValue() > Double.valueOf(row.get(7)).doubleValue())
					{
						LOG.debug("RMS Price data to be corrected  for CSP :: Item Code: " + row.get(0));
						LOG.debug("CSP: " + Double.valueOf(row.get(5)).doubleValue());
						LOG.debug("MRP: " + Double.valueOf(row.get(7)).doubleValue());
						row.put(5, row.get(7));
					}

					if ((row.get(9)).equals("N")
							&& (Boolean.valueOf(row.get(17)).booleanValue() || Boolean.valueOf(row.get(18)).booleanValue())
							|| (Boolean.valueOf(row.get(16)).booleanValue() && !Boolean.valueOf(row.get(17)).booleanValue() && !Boolean
									.valueOf(row.get(18)).booleanValue())
							|| ((row.get(9)).equals("Y") && !Boolean.valueOf(row.get(17)).booleanValue() && !Boolean
									.valueOf(row.get(18)).booleanValue()))
					{
						count++;
						corruptedRowData = new ArrayList<String>();
						corruptedRowData.add(row.get(4));
						corruptedRowData.add(row.get(0));

						if ((row.get(9)).equals("N")
								&& (Boolean.valueOf(row.get(17)).booleanValue() || Boolean.valueOf(row.get(18)).booleanValue()))
						{
							if (Boolean.valueOf(row.get(17)).booleanValue() && Boolean.valueOf(row.get(18)).booleanValue())
							{
								corruptedRowData.add(Config.getString("spar.promotion.promocheck.regularoffer.combioffer",
										"Promo Check is N But Regular and Combi offer are True"));
							}
							else if (Boolean.valueOf(row.get(17)).booleanValue())
							{
								corruptedRowData.add(Config.getString("spar.promotion.promocheck.regularoffer",
										"Promo Check is N But Regular Offer Is True"));
							}
							else if (Boolean.valueOf(row.get(18)).booleanValue())
							{
								corruptedRowData.add(Config.getString("spar.promotion.promocheck.combioffer",
										"Promo Check is N But Combi Offer Is True"));
							}
						}

						else if ((row.get(9)).equals("Y") && !Boolean.valueOf(row.get(17)).booleanValue()
								&& !Boolean.valueOf(row.get(18)).booleanValue())
						{
							corruptedRowData.add(Config.getString("spar.promotion.promocheck.true.regularoffer.combioffer",
									"Promo Check is Y But Regular and Combi offer are False"));
						}

						else if (Boolean.valueOf(row.get(16)).booleanValue() && !Boolean.valueOf(row.get(17)).booleanValue()
								&& !Boolean.valueOf(row.get(18)).booleanValue())
						{

							corruptedRowData.add(Config.getString("spar.promotion.bestdeal.regularoffer.combioffer",
									"Best Deal is True But Regular and Combi Offer are False"));
						}
						corruptedRow.put(count, corruptedRowData);
						continue;
					}

					if (isPriceFieldUpdated(row, promotiondateFormat, priceDB))
					{
						/*if (LOG.isDebugEnabled())
						{*/
							LOG.debug("No changes found in feed for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						//}
						continue;
					}
				}
				if (file.getName().contains("SPAR_ECOM_INVENTORY"))
				{
					row.put(4, strDate);
					if (isInventoryFieldUpdated(row))
					{
						if (LOG.isDebugEnabled())
						{
							LOG.debug("No changes found in feed for Item Code: " + row.get(0) + " warehouse " + row.get(1));
						}
						continue;
					}
				}
				/*
				 * if (file.getName().contains("SERVICE_AREA_DATA")) {
				 *
				 * allStores.forEach((stores) -> { matchStore = storeContainsInStoresList(row.get(4), stores.getName()); });
				 *
				 * boolean matchStore = false;
				 *
				 * if (allStoreMap.get(row.get(4)) != null) { matchStore =
				 * Boolean.valueOf(allStoreMap.get(row.get(4)).toString()); }
				 *
				 * if (Boolean.valueOf(row.get(7)).booleanValue() && row.get(4).equals("") ||
				 * Boolean.valueOf(row.get(7)).booleanValue() && !matchStore) { count++; invalidServiceData = new
				 * ArrayList<String>(); invalidServiceData.add(row.get(0)); invalidServiceData.add(row.get(2)); if
				 * (Boolean.valueOf(row.get(7)).booleanValue() && row.get(4).equals("")) {
				 * LOG.info("Home Delivery Service Location has empty Mapped Store for Service location Area with Area Id:"
				 * + row.get(0)); invalidServiceData.add(Config.getString("service.data.feed.error.message",
				 * "Home Delivery Service Location has empty Mapped Store for Service location Area"));
				 *
				 * } if (Boolean.valueOf(row.get(7)).booleanValue() && !matchStore) {
				 * LOG.info("Home Delivery Service Location has incorrect Mapped Store for Service location Area with Area Id:"
				 * + row.get(0)); invalidServiceData.add(Config.getString("service.data.feed.error.message.incorrectStore",
				 * "Home Delivery Service Location has incorrect Mapped Store for Service location Area")); }
				 * invalidServiceRow.put(count, invalidServiceData); LOG.info(count); LOG.info(invalidServiceData);
				 * LOG.info(invalidServiceRow); continue; } }
				 */
				if (file.getName().startsWith("WALLET_REFUND"))
				{
					if (null != row.get(0) && !(row.get(0).isEmpty()))
					{
						final CustomerModel customer = (CustomerModel) userService.getUserForUID(row.get(0));
						customer.setTotalWalletAmount(customer.getTotalWalletAmount() + Double.valueOf(row.get(1)));
						modelService.save(customer);
					}
				}

				if (file.getName().startsWith("SPAR_EMPLOYEE_DISCOUNT"))
				{
					if (null != row.get(0) && !(row.get(0).isEmpty()))
					{
						final CustomerModel customer = new CustomerModel();
						customer.setEmployeeCode(row.get(0));
						try
						{
							final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);
							if (customerModel != null)
							{
								customerModel.setCOemployeeCode(row.get(0));
								customerModel.setCOActive(Boolean.valueOf(row.get(1)));
								customerModel.setCOemployeediscountamount(Double.valueOf(row.get(2)));
								customerModel.setCOdateOfJoining(promotiondateFormat.parse(row.get(3)));
								customerModel.setCOdateOfBirth(promotiondateFormat.parse(row.get(4)));
								modelService.save(customerModel);
							}
						}
						catch (final ModelNotFoundException me)
						{
							LOG.info("ModelNotFoundException");
							continue;

						}
					}
				}


				if (file.getName().startsWith("SPAR_ECOM_PRODREFERENCE"))
				{
					if ((null != row.get(0) && !(row.get(0).isEmpty())) && (null != row.get(1) && !(row.get(1).isEmpty())))
					{
						if (StringUtils.equals(row.get(0), row.get(1)))
						{
							LOG.debug("Source and target product should not be same");
							continue;
						}
					}
					else
					{
						LOG.debug("Found empty source or targer items in feed");
						continue;
					}
					if (null == row.get(5) || (row.get(5).isEmpty()))
					{
						row.put(5, strDate);
					}
				}
				
				if (file.getName().startsWith("SPAR_ECOM_LEGAL_METROLOGY"))
				{
					try
					{
						if (StringUtils.isNotBlank(row.get(0)) || !row.get(0).equals(""))
						{
							final ProductModel product = new ProductModel();
							product.setCode(row.get(0));
							catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
							catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Staged");
							final ProductModel productDB = flexibleSearchService.getModelByExample(product);
							if (null == productDB)
							{
								LOG.info("Product not found in DB ::: " + row.get(0));
								continue;
							}
						}

						if (StringUtils.isEmpty(row.get(0)) || StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(2))
								|| StringUtils.isEmpty(row.get(3)) || StringUtils.isEmpty(row.get(4)) || StringUtils.isEmpty(row.get(5))
								|| StringUtils.isEmpty(row.get(6)) || StringUtils.isEmpty(row.get(7)) || StringUtils.isEmpty(row.get(8))
								|| StringUtils.isEmpty(row.get(9)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(0));
							if (StringUtils.isEmpty(row.get(0)) || row.get(0).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.code", "Item code is empty"));
								continue;
							}
							if (StringUtils.isEmpty(row.get(1)) || row.get(1).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.ean", "Produce (EAN) is empty"));
							}
							if (StringUtils.isEmpty(row.get(2)) || row.get(2).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.brand", "Product brand is empty"));
							}
							if (StringUtils.isEmpty(row.get(3)) || row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.manufacture", "Manufacture is empty"));
							}
							if (StringUtils.isEmpty(row.get(4)) || row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.importedBy", "Imported By is empty"));
							}
							if (StringUtils.isEmpty(row.get(5)) || row.get(5).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.netQuantity", "Net Quantity is empty"));
							}
							if (StringUtils.isEmpty(row.get(6)) || row.get(6).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.size", "Size is empty"));
							}
							if (StringUtils.isEmpty(row.get(7)) || row.get(7).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.bestBefore", "Best Before date is empty"));
							}
							if (StringUtils.isEmpty(row.get(8)) || row.get(8).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.usedBy", "used By is empty"));
							}
							if (StringUtils.isEmpty(row.get(9)) || row.get(9).equals(""))
							{
								invalidRowData.add(Config.getString("legal.metrology.customercare", "Customer care detail is empty"));
							}
							invalidRow.put(count, invalidRowData);

						}
					}
					catch (final Exception e)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(0));
						invalidRowData.add(Config.getString("legal.metrology.itemNotFound", "Product not found"));
						invalidRow.put(count, invalidRowData);
						LOG.error("Exception found in SPAR_ECOM_LEGAL_METROLOGY. Message = " + e.getMessage());
						continue;
					}
				}
				


				// New Order level promotion - 1
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORY_FIXED_PRICE"))
				{
					try
					{
						if ((StringUtils.isEmpty(row.get(4)) || StringUtils.isEmpty(row.get(5)) || StringUtils.isEmpty(row.get(10))
								|| StringUtils.isEmpty(row.get(11))
								|| (!StringUtils.isEmpty(row.get(4)) && promotiondateFormat.parse(row.get(4)).before(date)) || (!StringUtils
								.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							if (row.get(7).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.threshold.message",
										"Order threshold total is empty"));
							}
							else if (row.get(8).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.discountprice.message",
										"Order discount price is empty"));
							}
							else if (row.get(10).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.category.message", "Category is empty"));
							}
							else if (row.get(11).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.warehouse.message", "Warehouse is empty"));
							}
							else if (StringUtils.isEmpty(row.get(4))
									|| (!StringUtils.isEmpty(row.get(4)) && promotiondateFormat.parse(row.get(4)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}
						if (null != row.get(7) && null != row.get(6) && getDulpicatePromotionPriceRow(row.get(6), row.get(7)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(7)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(7, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
						if (null != row.get(8) && null != row.get(6) && getDulpicatePromotionPriceRow(row.get(6), row.get(8)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(8)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(8, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
					//removeDulpicatePromotionPriceRow(row.get(6), row.get(7));
					//removeDulpicatePromotionPriceRow(row.get(6), row.get(8));
				}

				// New Order level promotion - 2
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORY_FREEGIFT"))
				{
					try
					{
						if ((StringUtils.isEmpty(row.get(4)) || StringUtils.isEmpty(row.get(5)) || StringUtils.isEmpty(row.get(10))
								|| StringUtils.isEmpty(row.get(11))
								|| (!StringUtils.isEmpty(row.get(4)) && promotiondateFormat.parse(row.get(4)).before(date)) || (!StringUtils
								.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							if (row.get(7).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.threshold.message",
										"Order threshold total is empty"));
							}
							else if (row.get(8).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.giftproduct.message",
										"Gift product Id is empty"));
							}
							else if (row.get(10).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.category.message", "Category is empty"));
							}
							else if (row.get(11).equals(""))
							{
								invalidRowData.add(Config.getString("order.promotion.empty.warehouse.message", "Warehouse is empty"));
							}
							else if (StringUtils.isEmpty(row.get(4))
									|| (!StringUtils.isEmpty(row.get(4)) && promotiondateFormat.parse(row.get(4)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}
						if (null != row.get(7) && null != row.get(6) && getDulpicatePromotionPriceRow(row.get(6), row.get(7)))
                  {
                        String price = String.valueOf((Double.parseDouble(row.get(7)) - Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03)));
                        row.put(7, price);
                        LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = "+price);
                  }
						//removeDulpicatePromotionPriceRow(row.get(6), row.get(7));
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}
				
				// Meta tag details 
				if (file.getName().startsWith("SPAR_ECOM_METATAG_DETAILS"))
				{
					try
					{
						if ((StringUtils.isEmpty(row.get(0)) || StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(2)) || StringUtils
								.isEmpty(row.get(3))))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							if (row.get(0).equals(""))
							{
								invalidRowData.add(Config.getString("order.metatag.category.message", "Category code is empty"));
							}
							else if (row.get(1).equals(""))
							{
								invalidRowData.add(Config.getString("order.metatag.sparTitle.message", "Spar Title is empty"));
							}
							else if (row.get(2).equals(""))
							{
								invalidRowData.add(Config.getString("order.metatag.sparCategoryTitle.message",
										"Spar category title is empty"));
							}
							else if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("order.metatag.description.message",
										"Spar category description is empty"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
					}
					catch (final Exception p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add("Please check meta tag details");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}
				
				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORYITEM_FIXED_PRICE"))
				{
					try
					{

						if (StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(3)) || StringUtils.isEmpty(row.get(4))
								|| StringUtils.isEmpty(row.get(5)) || StringUtils.isEmpty(row.get(6))
								|| (!StringUtils.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date))
								|| (!StringUtils.isEmpty(row.get(8)) && promotiondateFormat.parse(row.get(8)).before(date))
								|| StringUtils.isEmpty(row.get(9)) || StringUtils.isEmpty(row.get(10)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(6));

							if (row.get(1).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.category.msg", "Category is empty for promotion"));
							}
							else if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.currency.msg", "Currency is empty for promotion"));
							}
							else if (row.get(4).equals(""))
							{
								invalidRowData
										.add(Config.getString("promotion.productprice.msg", "Product price is empty for promotion"));
							}
							else if (row.get(5).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.categorythresholg.msg",
										"Category threshold value is empty for promotion"));
							}
							else if (row.get(6).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.productlist.message",
										"Products List is empty for Fixed price promotion"));
							}
							else if (StringUtils.isEmpty(row.get(7))
									|| (!StringUtils.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(8))
									|| (!StringUtils.isEmpty(row.get(8)) && promotiondateFormat.parse(row.get(8)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}

						if (null != row.get(4) && null != row.get(3) && getDulpicatePromotionPriceRow(row.get(3), row.get(4)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(4)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(4, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}

						if (null != row.get(5) && null != row.get(3) && getDulpicatePromotionPriceRow(row.get(3), row.get(5)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(5)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(5, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}

					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}


				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORYITEM_PERCENT"))
				{
					try
					{

						if (StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(3)) || StringUtils.isEmpty(row.get(4))
								|| StringUtils.isEmpty(row.get(5)) || StringUtils.isEmpty(row.get(6))
								|| (!StringUtils.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date))
								|| (!StringUtils.isEmpty(row.get(8)) && promotiondateFormat.parse(row.get(8)).before(date))
								|| StringUtils.isEmpty(row.get(9)) || StringUtils.isEmpty(row.get(10)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(6));

							if (row.get(1).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.category.msg", "Category is empty for promotion"));
							}
							else if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.discount.msg", "Discount is empty for promotion"));
							}

							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.currency.msg", "Currency is empty for promotion"));
							}
							else if (row.get(5).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.threshold.msg", "Threshold total is empty for promotion"));
							}

							else if (row.get(6).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.product.msg", "Product is empty for promotion"));
							}

							else if (StringUtils.isEmpty(row.get(7))
									|| (!StringUtils.isEmpty(row.get(7)) && promotiondateFormat.parse(row.get(7)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(8))
									|| (!StringUtils.isEmpty(row.get(8)) && promotiondateFormat.parse(row.get(8)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}

							else if (row.get(10).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}

						if (null != row.get(4) && null != row.get(5) && getDulpicatePromotionPriceRow(row.get(4), row.get(5)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(5)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(5, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}
					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}


				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_BXGY"))
				{
					try
					{
						final Date promotionstartDate = promotiondateFormat.parse(row.get(5));
						final Date promotionendDate = promotiondateFormat.parse(row.get(6));
						if ((Integer.valueOf(row.get(1)).intValue() <= 1) || (Integer.valueOf(row.get(2)).intValue() <= 1)
								|| row.get(4).equals("") || row.get(8).equals("") || row.get(9).equals("")
								|| promotionendDate.before(date) || row.get(5).equals("") || row.get(6).equals("")
								|| promotionstartDate.before(date))
						{

							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(4));
							if (row.get(1).equals("") || Integer.valueOf(row.get(1)).intValue() <= 1)
							{
								invalidRowData.add(Config.getString("promotion.qualifyingcount.message",
										"Qualifying Count is either zero or empty for promotion"));
							}
							else if (row.get(2).equals("") || Integer.valueOf(row.get(2)).intValue() <= 1)
							{
								invalidRowData.add(Config.getString("promotion.freecount.message",
										"Free Count is either zero or empty for promotion"));
							}

							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.priority.message", "Product List is empty for promotion"));
							}

							else if (row.get(5).equals("") || promotionstartDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.startdate.message",
										"StartDate date is empty or before the current date"));
							}
							else if (row.get(6).equals("") || promotionendDate.before(date))
							{
								invalidRowData.add(Config.getString("promotion.enddate.message",
										"End date is empty or before the current date"));
							}
							else if (row.get(8).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}
							else if (row.get(9).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.freeProduct.message",
										"Free Product is empty for promotion"));
							}
							invalidRow.put(count, invalidRowData);
							continue;
						}
					}
					catch (final ParseException p)
					{
						LOG.info("Inside Parse");
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(4));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}

				if (file.getName().startsWith("SPAR_PRODUCT_ONLINEOFFLINESKUS_MAPPING"))
				{
					try
					{
						if (StringUtils.isEmpty(row.get(0)) || StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(2)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(1));
							if (StringUtils.isEmpty(row.get(0)) || row.get(0).equals(""))
							{
								invalidRowData.add(Config.getString("spar.offlineonlinesku.onlinesku.validation.message", "onlineSKU is empty"));
							}
							if (StringUtils.isEmpty(row.get(1)) || row.get(1).equals(""))
							{
								invalidRowData.add(Config.getString("spar.offlineonlinesku.offlinesku.validation.message", "offlineSKU is empty"));
							}
							if (StringUtils.isEmpty(row.get(2)) || row.get(2).equals(""))
							{
								invalidRowData.add(Config.getString("spar.offlineonlinesku.grammageRatio.validation.message", "grammageRatio is empty"));
							}
							invalidRow.put(count, invalidRowData);
						}
					}
					catch (final Exception e)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(1));
						invalidRowData.add(Config.getString("spar.offlineonlinesku.itemNotFound", "Product not found"));
						invalidRow.put(count, invalidRowData);
						LOG.error("Exception found in SPAR_PRODUCT_ONLINEOFFLINESKUS_MAPPING. Message = " + e.getMessage());
						continue;
					}
				}
				
				// Order Threshold :: Category level promotion :: Fixed discount

				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORYORDER_FIXED_DISCOUNT"))
				{
					try
					{

						if (StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(2)) || StringUtils.isEmpty(row.get(3))
								|| StringUtils.isEmpty(row.get(4))
								|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date))
								|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date))
								|| StringUtils.isEmpty(row.get(8)) || StringUtils.isEmpty(row.get(9)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(8));

							if (row.get(1).equals(""))
							{
								invalidRowData.add(Config
										.getString("promotion.ordercategory.priority", "Priority is empty for promotion"));
							}
							else if (row.get(2).equals(""))
							{
								invalidRowData.add(Config
										.getString("promotion.ordercategory.currency", "Currency is empty for promotion"));
							}
							else if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.threshold",
										"Threshold price is empty for promotion"));
							}
							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.discount",
										"Discount value is empty for promotion"));
							}

							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.startdate",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(6))
									|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.enddate",
										"End date is empty or before the current date"));
							}

							else if (row.get(8).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							else if (row.get(9).equals(""))
							{
								LOG.info("Category is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable Category is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}

						if (null != row.get(3) && null != row.get(2) && getDulpicatePromotionPriceRow(row.get(2), row.get(3)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(3)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(3, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}

						if (null != row.get(4) && null != row.get(2) && getDulpicatePromotionPriceRow(row.get(2), row.get(4)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(4)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(4, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}

					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}


				if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CATEGORYORDER_PERCENT_DISCOUNT"))
				{
					try
					{

						if (StringUtils.isEmpty(row.get(1)) || StringUtils.isEmpty(row.get(2)) || StringUtils.isEmpty(row.get(3))
								|| StringUtils.isEmpty(row.get(4))
								|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date))
								|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date))
								|| StringUtils.isEmpty(row.get(8)) || StringUtils.isEmpty(row.get(9)))
						{
							count++;
							invalidRowData = new ArrayList<String>();
							invalidRowData.add(row.get(0));
							invalidRowData.add(row.get(8));

							if (row.get(1).equals(""))
							{
								invalidRowData.add(Config
										.getString("promotion.ordercategory.priority", "Priority is empty for promotion"));
							}
							else if (row.get(2).equals(""))
							{
								invalidRowData.add(Config
										.getString("promotion.ordercategory.currency", "Currency is empty for promotion"));
							}
							else if (row.get(3).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.threshold",
										"Threshold price is empty for promotion"));
							}
							else if (row.get(4).equals(""))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.discount",
										"Discount value is empty for promotion"));
							}

							else if (StringUtils.isEmpty(row.get(5))
									|| (!StringUtils.isEmpty(row.get(5)) && promotiondateFormat.parse(row.get(5)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.startdate",
										"Start Date date is empty or before the current date"));
							}
							else if (StringUtils.isEmpty(row.get(6))
									|| (!StringUtils.isEmpty(row.get(6)) && promotiondateFormat.parse(row.get(6)).before(date)))
							{
								invalidRowData.add(Config.getString("promotion.ordercategory.enddate",
										"End date is empty or before the current date"));
							}

							else if (row.get(8).equals(""))
							{
								LOG.info("Applicable warehouse is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable warehouse is empty or missing for the promotion"));
							}

							else if (row.get(9).equals(""))
							{
								LOG.info("Category is empty or missing for the promotion");
								invalidRowData.add(Config.getString("promotion.warehouses.validation.message",
										"Applicable Category is empty or missing for the promotion"));
							}

							invalidRow.put(count, invalidRowData);
							continue;
						}

						if (null != row.get(3) && null != row.get(2) && getDulpicatePromotionPriceRow(row.get(2), row.get(3)))
						{
							final String price = String.valueOf((Double.parseDouble(row.get(3)) - Config.getDouble(
									PRICEROW_DELTA_AMOUNT, 0.03)));
							row.put(3, price);
							LOG.info("promotionPriceRow change for duplicateRow - temp promotiom price row = " + price);
						}

					}
					catch (final ParseException p)
					{
						count++;
						invalidRowData = new ArrayList<String>();
						invalidRowData.add(row.get(0));
						invalidRowData.add(row.get(6));
						invalidRowData.add("Start date or End Date is empty ");
						invalidRow.put(count, invalidRowData);
						continue;
					}
				}
				
				if (converter.filter(row))
				{
					try
					{
						writer.println(converter.convert(row, header.getSequenceId()));
						result = true;

						Map<Integer, Object> mapLine = null;
						String lineHeader = StringUtils.EMPTY;

						if (file.getName().contains("SPAR_ECOM_INVENTORY"))
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("Spar Hybris creating validated SPAR_ECOM_INVENTORY csv to store");
							}
							final File fileLocation = new File(filePathDest);
							if (!fileLocation.exists())
							{
								fileLocation.mkdir();
							}

							final String fileName = file.getName();
							deltaWriter = exportCSV(fileName, "INVENTORY");
							if (!isDeltaHeaderCreated)
							{
								mapLine = new HashMap<Integer, Object>();
								lineHeader = "ITEM;LOCATION;AVAILABLE_STOCK;ITEM_LIVE_IND";
								mapLine.put(Integer.valueOf(0), lineHeader);
								deltaWriter.write(mapLine);
								isDeltaHeaderCreated = true;
							}
							row.remove(Integer.valueOf(4));
							deltaWriter.write(row);
						}
						else if (file.getName().contains("SPAR_ECOM_PRICE"))
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("Spar Hybris creating validated SPAR_ECOM_PRICE csv to store");
							}
							final File fileLocation = new File(filePathDest);
							if (!fileLocation.exists())
							{
								fileLocation.mkdir();
							}

							final String fileName = file.getName();
							deltaWriter = exportCSV(fileName, "PRICE");
							if (!isDeltaHeaderCreated)
							{
								mapLine = new HashMap<Integer, Object>();
								lineHeader = "ITEM;CURRENCY;EFFECTIVE_DATE;END_DATE;LOCATION;SELLING_RETAIL;SELLING_UOM;UNIT_MRP;BARCODE;PROMO_CHECK;VAT %;TAX_VALUE;BENEFIT_TYPE;PRODUCT_ON_BOGO;PROMO_MESSAGE;ASSOCIATED_FREE_PRODUCT;BEST_DEAL;REGULAR_OFFER;COMBI_OFFER;PERCENTAGE_DISCOUNT;RANKING";
								mapLine.put(Integer.valueOf(0), lineHeader);
								deltaWriter.write(mapLine);
								isDeltaHeaderCreated = true;
							}
							deltaWriter.write(row);
						}
					}
					catch (final IllegalArgumentException exc)
					{
						errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
					}
					/*
					 * catch (final Exception e) { LOG.info("Into the catch"); corruptedRowData = new ArrayList<String>();
					 * count++; corruptedRowData.add(row.get(0)); corruptedRowData.add("");
					 * corruptedRowData.add(Arrays.toString(e.getStackTrace())); corruptedRow.put(count, corruptedRowData);
					 * LOG.info("Catch is completed"); }
					 */

				}
			}
		}
		catch (final Exception e)
		{
			LOG.info("error" + e.toString());
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(errorWriter);
			closeQuietly(csvReader);
			try
			{
				deltaWriter.closeQuietly();
			}
			catch (final Exception e)
			{
				LOG.info("Exception closing output writer");
			}
		}
		return result;
	}

	/**
	 * @param row
	 * @param file
	 */
	private void calulatePromotionDiscountForFixedPricePromotions(Map<Integer, String> row, final File file)
	{
		LOG.info("calulatePromotionDiscountForFixedPricePromotions, Promo Code : "+row.get(0));
		if(null == row.get(8) || (null != row.get(8) && row.get(8).toString().equalsIgnoreCase("TRUE")))
		{
   		List<String> warehouseList = new ArrayList<String>();
   		if (file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE_DISCOUNT"))
   		{  
   			warehouseList = Arrays.asList(StringUtils.split(row.get(11).toString(), ","));
   		}
   		else if(file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE"))
   		{
   			warehouseList = Arrays.asList(StringUtils.split(row.get(9).toString(), ","));
   		}
   		
   		WarehouseModel warehouseDB = null;
   		SparPriceRowModel priceDB = null;
   		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
   		final SparPriceRowModel price = new SparPriceRowModel();
   		final WarehouseModel warehouse = new WarehouseModel();
   		
   		for(String whouse : warehouseList)
   		{
   			try
      		{
   				priceDB = modelService.create(SparPriceRowModel.class);
        			warehouse.setCode(whouse.trim());
         		warehouseDB = flexibleSearchService.getModelByExample(warehouse);
         		
         		price.setProductId(row.get(5));
         		if (StringUtils.isNotEmpty(row.get(3)))
         		{
         			price.setCurrency(commonI18NService.getCurrency(row.get(3)));
         		}
         		price.setWarehouse(warehouseDB);
         		modelService.attach(price);
      		
      			priceDB = flexibleSearchService.getModelByExample(price);
      			modelService.detach(price);
      			
      			if (null != priceDB)
      			{
      				try
      				{
      					double promotionPercentage = 0.0;
      					if (file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE_DISCOUNT"))
      					{  
      						double benifitPerItem = Double.valueOf(row.get(4)).doubleValue()/ Double.valueOf(row.get(10)).doubleValue();
            				promotionPercentage = (benifitPerItem/priceDB.getUnitMRP().doubleValue()) * 100;
      					}
      					else if (file.getName().startsWith("SPAR_ECOM_PROMOTION_FIXED_PRICE"))
      					{
            				double benifitForCustomer = priceDB.getUnitMRP().doubleValue() - Double.valueOf(row.get(4)).doubleValue();
            				promotionPercentage = (benifitForCustomer/priceDB.getUnitMRP().doubleValue()) * 100;
      					}
      					double formattedPromotionPercentage = Math.round(promotionPercentage);
         				priceDB.setPromotionDiscount(Double.valueOf(formattedPromotionPercentage));
         				LOG.info("Promotion Discount Saved For : ");
         				modelService.save(priceDB);
         				LOG.info("Saved For ProductId : "+ priceDB.getProductId());
         				LOG.info("Saved Promotion Discount : "+priceDB.getPromotionDiscount().doubleValue());
         				LOG.info("Saved For warehouse : "+ priceDB.getWarehouse().getCode());
      				}
      				catch(Exception ex)
      				{
      					LOG.error("In calulatePromotionDiscountForFixedPricePromotions method, Exception occured during percent calculation for price row.");
      					LOG.error("Promotion Code of Feed : "+row.get(0));
      				}
      				priceDB = null;
      			}
      		}
      		catch(ModelNotFoundException ex)
         		{
         			LOG.error("In calulatePromotionDiscountForFixedPricePromotions Method, ModelNotFoundException : "+ ex);
         		}
      		}
			}
		}

	/**
	 * @param row
	 * @param file
	 */
	private void calulatePromotionDiscountForPercentPromotions(Map<Integer, String> row, final File file)
	{
		LOG.info("calulatePromotionDiscountForPercentPromotions, Promo Code : "+row.get(0));
		if(null == row.get(7) || (null != row.get(7) && row.get(7).toString().equalsIgnoreCase("TRUE")))
		{
   		List<String> warehouseList = new ArrayList<String>();
   		if (file.getName().startsWith("SPAR_ECOM_PROMOTION_CUSTOM_PERCENT_DISCOUNT"))
   		{  
   			warehouseList = Arrays.asList(StringUtils.split(row.get(10).toString(), ","));
   		}
   		else if(file.getName().startsWith("SPAR_ECOM_PROMOTION_PERCENT"))
   		{
   			warehouseList = Arrays.asList(StringUtils.split(row.get(8).toString(), ","));
   		}
   		
   		WarehouseModel warehouseDB = null;
   		SparPriceRowModel priceDB = null;
   		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
   		final SparPriceRowModel price = new SparPriceRowModel();
   		final WarehouseModel warehouse = new WarehouseModel();
   		
   		for(String whouse : warehouseList)
   		{
   			try
      		{
   				priceDB = modelService.create(SparPriceRowModel.class);
        			warehouse.setCode(whouse.trim());
         		warehouseDB = flexibleSearchService.getModelByExample(warehouse);
         		
         		price.setProductId(row.get(4));
         		price.setCurrency(commonI18NService.getCurrency("INR"));
         		price.setWarehouse(warehouseDB);
         		modelService.attach(price);
           		
      			priceDB = flexibleSearchService.getModelByExample(price);
      			modelService.detach(price);
      			
      			if (null != priceDB)
      			{
      	   		try
      	   		{
      	   			priceDB.setPromotionDiscount(Double.valueOf(row.get(2)));
      	   			LOG.info("Promotion Discount Saved For : ");
         				modelService.save(priceDB);
         				LOG.info("Saved For ProductId : "+ priceDB.getProductId());
         				LOG.info("Saved Promotion Discount : "+priceDB.getPromotionDiscount().doubleValue());
         				LOG.info("Saved For warehouse : "+ priceDB.getWarehouse().getCode());
      				}
      				catch(ModelSavingException ex)
      				{
      					LOG.error("In calulatePromotionDiscountForPercentPromotions method, ModelSavingException.");
      					LOG.error("Promotion Code of Feed : "+row.get(0));
      				}
      	   		priceDB = null;
      			}
      		}
      		catch(ModelNotFoundException ex)
         		{
         			LOG.error("In calulatePromotionDiscountForPercentPromotions Method, ModelNotFoundException : "+ ex);
         		}
      		}
			}
		}	
	/**
	 * This method invokes the ImpexTransformerTask execute() method and after that if error occurs in transformation,
	 * invoked the email process for sending email
	 *
	 * @param: BatchHeader
	 */
	@Override
	public BatchHeader execute(final BatchHeader header) throws UnsupportedEncodingException, FileNotFoundException
	{
		LOG.info("########## Inside SparImpexTransformerTask:execute");
		BatchHeader locHeader = null;
		super.setCleanupHelper(this.cleanupHelper);
		locHeader = super.execute(header);
		// send email notification for inventory error files
		final File sourceFile = header.getFile();
		if (sourceFile.getPath().contains("INVENTORY"))
		{
			final File errorFile = getErrorFile(sourceFile);
			//final File errorFile = getErrorFile(source);
			// in content line no. and in attachment logs and source file
			if (null != errorFile && errorFile.exists())
			{
				compressErrorFiles(sourceFile);
				initiateFeedsErrorEmailProcess(sourceFile, sourceFile.getName());
				cleanupCompressedFile(sourceFile);
			}
		}

		if (corruptedRow.size() > 0)
		{
			initiateCorruptedRMSFeedErrorEmailProcess(header.getFile(), header.getFile().getName(), corruptedRow);
		}
		/*
		 * if (invalidServiceRow.size() > 0) { LOG.info("going to initiatecorruptedServiceDataFeedError");
		 * initiatecorruptedServiceDataFeedError(header.getFile(), header.getFile().getName(), invalidServiceRow); }
		 */
		if (invalidRow.size() > 0)
		{
			LOG.info("goint to initiatecorruptedRMSFeedError");
			initiateCorruptedRMSFeedErrorEmailProcess(header.getFile(), header.getFile().getName(), invalidRow);
		}
		LOG.info(locHeader);
		return locHeader;
	}

	private boolean isPriceFieldUpdated(final Map<Integer, String> row, final SimpleDateFormat priceDateFormat,
			final SparPriceRowModel priceDB)
	{
		boolean flag = Boolean.FALSE;
		final SparPriceRowModel price = new SparPriceRowModel();

		try
		{
			final String promoCheckRow = row.get(9);
			final boolean promoCheckFlag = promoCheckRow.equals("Y") ? true : false;

			if (null != priceDB)
			{
				// Comparing CSP
				if (StringUtils.isNotEmpty(row.get(5)))
				{
					if (null != priceDB.getPrice() && (priceDB.getPrice().doubleValue() == Double.parseDouble(row.get(5))))
					{
						LOG.debug("CSP found same for item : " + row.get(0) + " warehouse " + row.get(4));
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("CSP diff found for item : " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				// Comparing MRP
				if (StringUtils.isNotEmpty(row.get(7)))
				{
					if (null != priceDB.getUnitMRP() && (priceDB.getUnitMRP().doubleValue() == Double.parseDouble(row.get(7))))
					{
						LOG.debug("MRP found same for item : " + row.get(0) + " warehouse " + row.get(4));
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("MRP diff found for item : " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				// Comparing EndDate
				if (StringUtils.isNotEmpty(row.get(3)))
				{
					if (null != priceDB.getEndTime()
							&& StringUtils.equals(priceDateFormat.format(priceDB.getEndTime()),
									priceDateFormat.format(new Date(row.get(3)))))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("EndTime Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (null != priceDB.getEndTime())
					{
						LOG.debug("EndTime diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
					else
					{
						flag = Boolean.TRUE;
					}
				}

				// Comparing Benefit type data
				if (StringUtils.isNotEmpty(row.get(12)))
				{
					final BenefitTypeEnum benefitTypeEnum = enumerationService.getEnumerationValue(BenefitTypeEnum.class, row.get(12));
					if (StringUtils.equalsIgnoreCase(priceDB.getBenefitType().getCode(), benefitTypeEnum.getCode()))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Benefit Type Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (null == priceDB.getBenefitType())
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Benefit Type diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}



				// Comparing Promo_check
				if (null != priceDB.getCheckForPromotion())
				{
					if (StringUtils.isNotEmpty(row.get(9)) && promoCheckFlag == priceDB.getCheckForPromotion().booleanValue())
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("promo check Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}

					// Comparing ProductOnBOGO
					if (null != priceDB.getProductOnBogo())
					{
						if (StringUtils.isNotEmpty(row.get(13))
								&& Boolean.valueOf(row.get(13)).booleanValue() == priceDB.getProductOnBogo().booleanValue())
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("BOGO Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}
					else
					{
						if (StringUtils.isEmpty(row.get(13)))
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("BOGO diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}

					// Comparing BestDeal
					if (null != priceDB.getBestDeal())
					{
						if (StringUtils.isNotEmpty(row.get(16))
								&& Boolean.valueOf(row.get(16)).booleanValue() == priceDB.getBestDeal().booleanValue())
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Best Deal diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}
					else
					{
						if (StringUtils.isEmpty(row.get(16)))
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Best deal diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}

					// Comparing RegularOffer
					if (null != priceDB.getRegularOffer())
					{
						if (StringUtils.isNotEmpty(row.get(17))
								&& Boolean.valueOf(row.get(17)).booleanValue() == priceDB.getRegularOffer().booleanValue())
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Regular Offer Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}
					else
					{
						if (StringUtils.isEmpty(row.get(17)))
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Regular offer diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}

					// Comparing CombiOffer
					if (null != priceDB.getCombiOffer())
					{
						if (StringUtils.isNotEmpty(row.get(18))
								&& Boolean.valueOf(row.get(18)).booleanValue() == priceDB.getCombiOffer().booleanValue())
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Combi offer Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}
					else
					{
						if (StringUtils.isEmpty(row.get(18)))
						{
							flag = Boolean.TRUE;
						}
						else
						{
							LOG.debug("Combi offer diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
							return false;
						}
					}

				}
				else
				{
					if (StringUtils.isEmpty(row.get(9)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Promo check diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				if (StringUtils.isNotEmpty(priceDB.getSparBarcode()))
				{
					if (StringUtils.isNotEmpty(row.get(8)) && StringUtils.equalsIgnoreCase(row.get(8), priceDB.getSparBarcode()))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Barcode Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (StringUtils.isEmpty(row.get(8)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Barcode diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				if (StringUtils.isNotEmpty(priceDB.getPromoMessage(Locale.ENGLISH)))
				{
					if (StringUtils.isNotEmpty(row.get(14))
							&& StringUtils.equalsIgnoreCase(row.get(14), priceDB.getPromoMessage(Locale.ENGLISH)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Promo msg Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (StringUtils.isEmpty(row.get(14)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Promo msg diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				if (null != priceDB.getAssociatedFreeProduct())
				{
					if (StringUtils.isNotEmpty(row.get(15))
							&& Long.valueOf(row.get(15)).longValue() == priceDB.getAssociatedFreeProduct().longValue())
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Associated pr Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (StringUtils.isEmpty(row.get(15)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Associated pr diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				if (null != priceDB.getDiscount())
				{
					if (StringUtils.isNotEmpty(row.get(19))
							&& Double.valueOf(row.get(19)).doubleValue() == priceDB.getDiscount().doubleValue())
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Discount Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (StringUtils.isEmpty(row.get(19)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Discount diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}

				if (null != priceDB.getPopularity())
				{
					if (StringUtils.isNotEmpty(row.get(20))
							&& Integer.valueOf(row.get(20)).intValue() == priceDB.getPopularity().intValue())
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Popularity Diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
				else
				{
					if (StringUtils.isEmpty(row.get(20)))
					{
						flag = Boolean.TRUE;
					}
					else
					{
						LOG.debug("Popularity diff found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
						return false;
					}
				}
			}
		}
		catch (final Exception e)
		{
			flag = Boolean.FALSE;
			LOG.debug("No record found for Item Code: " + row.get(0) + " warehouse " + row.get(4));
		}
		modelService.detach(price);
		LOG.debug("Final flag value " + flag + " for Item Code: " + row.get(0) + " warehouse " + row.get(4));
		return flag;
	}

	private boolean isInventoryFieldUpdated(final Map<Integer, String> row)
	{
		boolean flag = Boolean.FALSE;
		final StockLevelModel stockLevel = new StockLevelModel();

		try
		{
			final WarehouseModel warehouse = new WarehouseModel();
			warehouse.setCode(row.get(1));
			final WarehouseModel warehouseDB = flexibleSearchService.getModelByExample(warehouse);

			catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
			catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Staged");

			stockLevel.setProductCode(row.get(0));
			stockLevel.setWarehouse(warehouseDB);

			modelService.attach(stockLevel);
			final StockLevelModel stockLevelDB = flexibleSearchService.getModelByExample(stockLevel);
			modelService.detach(stockLevel);
			if (null != stockLevelDB)
			{
				if (null != stockLevelDB.getRmsModifiedTime()
						&& ((new Date()).getTime() - stockLevelDB.getRmsModifiedTime().getTime()) > Config.getLong(
								INVENTORY_EXPIRE_TIME, 4) * 1000 * 60 * 60 * 24)
				{
					LOG.debug("RMS modified date is older than " + Config.getLong(INVENTORY_EXPIRE_TIME, 4) + " days, for Item Code: "
							+ row.get(0) + " warehouse " + row.get(1));
					return false;
				}

				if (stockLevelDB.getAvailable() == Integer.valueOf(row.get(2)).intValue())
				{
					flag = Boolean.TRUE;
				}
				else
				{
					LOG.debug("Available diff found for Item Code: " + row.get(0) + " warehouse " + row.get(1));
					return false;
				}

			}


		}
		catch (final Exception e)
		{
			flag = Boolean.FALSE;
			LOG.debug("No record found for Item Code: " + row.get(0) + " warehouse " + row.get(1));
		}
		modelService.detach(stockLevel);
		LOG.debug("Final flag value " + flag + " for Item Code: " + row.get(0) + " warehouse " + row.get(1));
		return flag;
	}

	/* Code change start by sumit for promotion story */
	private void initiateCorruptedRMSFeedErrorEmailProcess(final File csvFIle, final String csvFileName, final Map corruptedRow)
			throws FileNotFoundException
	{
		LOG.info("Entering into initiateCorruptedRMSFeedErrorEmailProcess:::::");
		final RMSCorruptedPriceFeedDataProcessModel process = (RMSCorruptedPriceFeedDataProcessModel) businessProcessService
				.createProcess("RMSCorruptedPriceFeedDataProcess" + "-" + csvFIle.getName() + "-" + System.currentTimeMillis(),
						"RMSCorruptedPriceFeedDataProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				"basesite.uid"));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		process.setFileAttachments(emailAttachmentListForCorruptedPrice(csvFIle));
		process.setErrorString(corruptedRow);
		process.setSourceFileName(csvFileName);
		process.setTotalRecords(totalRecords);
		try
		{
			modelService.saveAll();
			businessProcessService.startProcess(process);
		}
		catch (final Exception e)
		{
			LOG.info("Exception ::::::::::::" + e.getMessage());
		}

		LOG.info("corruptedRowData::::::::::::" + corruptedRow);
		LOG.debug("RMSCorruptedPriceFeedDataProcessModel :: RMSCorruptedPriceFeedDataProcessModel :: finished");
		LOG.info("Exiting from initiateCorruptedRMSFeedErrorEmailProcess::::::");

	}

	/* Code change start by Saif for offline database */
	/*
	 * private void initiatecorruptedServiceDataFeedError(final File csvFIle, final String csvFileName, final Map
	 * corruptedRow) throws FileNotFoundException { LOG.info("Entering into initiatecorruptedServiceDataFeedError:::::");
	 * final RMSCorruptedPriceFeedDataProcessModel process = (RMSCorruptedPriceFeedDataProcessModel)
	 * businessProcessService .createProcess("RMSCorruptedPriceFeedDataProcess" + "-" + csvFIle.getName() + "-" +
	 * System.currentTimeMillis(), "RMSCorruptedPriceFeedDataProcess");
	 * 
	 * final BaseSiteModel baseSite =
	 * baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString( "basesite.uid"));
	 * process.setSite(baseSite); process.setStore(baseSite.getStores().get(0));
	 * process.setFileAttachments(emailAttachmentListForCorruptedPrice(csvFIle)); process.setErrorString(corruptedRow);
	 * process.setSourceFileName(csvFileName); modelService.saveAll(); businessProcessService.startProcess(process);
	 * LOG.info("corruptedRowData::::::::::::" + corruptedRow);
	 * LOG.debug("ServiceLocationCorruptedDataProcessModel :: ServiceLocationCorruptedDataProcessModel :: finished");
	 * LOG.info("Exiting from initiatecorruptedServiceDataFeedError::::::"); }
	 */

	/* Change end here */
	/**
	 * This method initiates the email process for sending feeds error.
	 *
	 * @param errorFile
	 * @param csvFileName
	 * @throws FileNotFoundException
	 */

	private void initiateFeedsErrorEmailProcess(final File errorFile, final String csvFileName) throws FileNotFoundException
	{

		LOG.debug("inside SparImpexTransformerTask:initiateFeedsErrorEmailProcess");

		final SparFeedsErrorEmailProcessModel process = (SparFeedsErrorEmailProcessModel) businessProcessService.createProcess(
				"SparFeedsErrorEmailProcess" + "-" + errorFile.getName() + "-" + System.currentTimeMillis(),
				"SparFeedsErrorEmailProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE_UID));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));

		process.setFileAttachments(emailAttachmentList(errorFile));
		process.setErrorString(csvFileName);
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOG.debug("SparFeedsErrorEmailProcess started");
	}

	/**
	 *
	 * @return the Boolean Value if Store Name matches with Store Name in feed
	 */
	/*
	 * public boolean storeContainsInStoresList(final String storeName, final String listOfStores) { if
	 * (!storeName.equals("")) { return storeName.equals(listOfStores); } else { return true; } }
	 */

	/**
	 * @return the cleanupHelper
	 */
	@Override
	public CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}

	/**
	 * @param cleanupHelper
	 *           the cleanupHelper to set
	 */
	@Override
	public void setCleanupHelper(final CleanupHelper cleanupHelper)
	{
		this.cleanupHelper = cleanupHelper;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the importService
	 */
	public ImportService getImportService()
	{
		return importService;
	}

	/**
	 * @param importService
	 *           the importService to set
	 */
	public void setImportService(final ImportService importService)
	{
		this.importService = importService;
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
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * @return the corruptedRow
	 */
	public Map<Integer, List> getCorruptedRow()
	{
		return corruptedRow;
	}

	/**
	 * @param corruptedRow
	 *           the corruptedRow to set
	 */
	public void setCorruptedRow(final Map<Integer, List> corruptedRow)
	{
		this.corruptedRow = corruptedRow;
	}

	/**
	 * This method created email attachments list
	 *
	 * @param errorFile
	 * @return List<EmailAttachmentModel>
	 * @throws FileNotFoundException
	 */
	private List<EmailAttachmentModel> emailAttachmentList(final File errorFile) throws FileNotFoundException
	{

		final List<EmailAttachmentModel> attachmentsList = new ArrayList<EmailAttachmentModel>();
		EmailAttachmentModel emailAttachmentModel = null;

		final DataInputStream stream = new DataInputStream(new FileInputStream(errorFile.getParentFile() + File.separator
				+ ZIP_FILE_NAME));

		emailAttachmentModel = createEmailAttachment(stream, ZIP_FILE_NAME, ZIP_MIME_TYPE);
		attachmentsList.add(emailAttachmentModel);

		return attachmentsList;
	}


	private List<EmailAttachmentModel> emailAttachmentListForCorruptedPrice(final File sourceFile) throws FileNotFoundException
	{

		final List<EmailAttachmentModel> attachmentsList = new ArrayList<EmailAttachmentModel>();
		EmailAttachmentModel emailAttachmentModel = null;

		final DataInputStream stream = new DataInputStream(new FileInputStream(sourceFile.getPath()));

		emailAttachmentModel = createEmailAttachment(stream, sourceFile.getName(), ZIP_MIME_TYPE_FOR_CORRUPT_PRICE_DATA);
		attachmentsList.add(emailAttachmentModel);

		return attachmentsList;
	}

	private boolean getDulpicatePromotionPriceRow(final String currency, final String value)
   {
			LOG.info("In getDulpicatePromotionPriceRow : ");
			LOG.info("currency : "+currency);
			LOG.info("value : "+value);
         try
         {
               CurrencyModel currencyModelDB = null;
               List<PromotionPriceRowModel> priceRowModelsDB = null;
               catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
               final PromotionPriceRowModel promoPrice = new PromotionPriceRowModel();
               final CurrencyModel currencymodel = new CurrencyModel();
               currencymodel.setIsocode(currency);
               currencyModelDB = flexibleSearchService.getModelByExample(currencymodel);

               promoPrice.setCurrency(currencyModelDB);
               promoPrice.setPrice(Double.valueOf(value));

               modelService.attach(promoPrice);
               priceRowModelsDB = flexibleSearchService.getModelsByExample(promoPrice);

               if (null != priceRowModelsDB && priceRowModelsDB.size() >= 1)
               {
                     return true;
               }
               modelService.detach(promoPrice);
         }
         catch (final Exception e)
         {
               LOG.error("Exception occured for PromotionPricerow for value " + currency + ":" + value + ", Exception is = "
                           + e.getMessage());
         }
         return false;
   }


	/**
	 * This method creates email attachment
	 *
	 * @param masterDataStream
	 * @param filename
	 * @param mimeType
	 * @return
	 */
	private EmailAttachmentModel createEmailAttachment(final DataInputStream masterDataStream, final String filename,
			final String mimeType)
	{


		final EmailAttachmentModel attachment = modelService.create(EmailAttachmentModel.class);
		attachment.setCode(filename + "-" + System.currentTimeMillis());
		attachment.setMime(mimeType);
		attachment.setRealFileName(filename);

		attachment.setCatalogVersion(catalogVersionService.getCatalogVersion(
				configurationService.getConfiguration().getString(ACTIVE_CATALOG_NAME), configurationService.getConfiguration()
						.getString(ACTIVE_CATALOG_VERSION)));

		modelService.save(attachment);


		final MediaFolderModel mediaFolderModel = mediaService.getRootFolder();
		mediaService.setStreamForMedia(attachment, masterDataStream, filename, mimeType, mediaFolderModel);

		return attachment;
	}

	/**
	 * This method clean ups the generated compressed file.
	 *
	 * @param errorFile
	 */
	private void cleanupCompressedFile(final File errorFile)
	{
		final File errorFileZip = new File(errorFile.getParentFile() + File.separator + ZIP_FILE_NAME);
		if (errorFileZip.exists())
		{
			getCleanupHelper().cleanupFile(errorFileZip);
		}
	}

	/**
	 * This method compress error files
	 *
	 * @param errorFile
	 */
	private void compressErrorFiles(final File errorFile)
	{
		final ZipUtil zipper = new ZipUtil();

		final List<File> fileList = new ArrayList<File>();
		fileList.add(errorFile);

		try
		{
			zipper.compressFiles(fileList, errorFile.getParentFile() + File.separator + ZIP_FILE_NAME);

		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage());
		}
	}

	/**
	 * This method gets the CSV writer with the specified fileName
	 *
	 * @param fileName
	 *           the used filename
	 * @return a CSVWriter (here MyCSVWriter) for the given filename. The Encoding is set to UTF-8 and the file is bound
	 *         to the CSVWriter.
	 */
	protected MyCSVWriter exportCSV(final String fileName, final String fileType)
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

}
