package com.spar.hcl.core.service;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.regioncache.CacheController;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.site.BaseSiteService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.spar.hcl.core.model.process.SparFeedsErrorEmailProcessModel;


/**
 *
 *
 */
public abstract class SparAbstractImpexRunnerTask extends AbstractImpexRunnerTask
{

	/** The Logger */
	private static final Logger LOG = Logger.getLogger(SparAbstractImpexRunnerTask.class);
	private static String SPAR_PRODUCT_COMBOS_MULTIPACK_SKUs = "SPAR_PRODUCT_COMBOS_MULTIPACK_SKUs";
	private final String PRICEROW_DELTA_AMOUNT = "promopricerow.duplicate.delta.amount";

	
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

	/** The FILE_SEPERATOR */
	//	private static String FILE_SEPERATOR = "/";

	/** The ZIP_FILE_NAME */
	private static String ZIP_FILE_NAME = "Errorfiles.zip";

	/** The CRONJOB_LOG_FILE_NAME */
	private static String CRONJOB_LOG_FILE_NAME = "CronJobLog.log";

	private static final String SPARfullReIndex = "spar.fullReIndex";
	
	/** Duplicate promotion price row **/
	double deltaAmount = Config.getDouble(PRICEROW_DELTA_AMOUNT, 0.03);
	private final String PROMOTION_PRICE_ROUNDOFF_QUERY = "UPDATE promotionpricerow SET p_price=p_price+"+deltaAmount+" WHERE DATE_FORMAT(createdTS, '%d-%m-%Y') = DATE_FORMAT(NOW(), '%d-%m-%Y') AND (p_price LIKE '%._7' OR p_price LIKE '%._2'  OR p_price LIKE '%._3')";


	/** The ModelService */
	@Autowired
	private ModelService modelService;

	/** The BusinessProcessService */
	@Autowired
	private BusinessProcessService businessProcessService;

	/** The BaseSiteService */
	@Autowired
	private BaseSiteService baseSiteService;

	/** The MediaService */
	@Autowired
	private MediaService mediaService;

	/** The CatalogVersionService */
	@Autowired
	private CatalogVersionService catalogVersionService;

	/** The ConfigurationService */
	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private SetupSolrIndexerService setupSolrIndexerService;

	@Autowired
	private CronJobService cronJobService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	protected CacheController controller;

	/**
	 * CIAbstractImpexRunnerTask constructor
	 */
	public SparAbstractImpexRunnerTask()
	{
		super();
	}

	/**
	 * This method imports the impex generated, and send error if any exception occurs
	 *
	 * @param BatchHeader
	 * @return BatchHeader
	 */
	@Override
	public BatchHeader execute(final BatchHeader header) throws FileNotFoundException
	{
		LOG.info("#####----->>>>>Inside SparAbstractImpexRunnerTask:execute");
		Assert.notNull(header);
		Assert.notNull(header.getEncoding());

		// Setting the csv file for import service
		if (CollectionUtils.isNotEmpty(header.getTransformedFiles()))
		{
			final Session localSession = getSessionService().createNewSession();
			try
			{
				for (final File file : header.getTransformedFiles())
				{
					processFile(file, header);
				}
				if (header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_BUNDLE")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_FIXED_PRICE")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_PERFECT_PARTNER")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_FIXED_PRICE_DISCOUNT")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORY_FIXED_PRICE")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORY_FREEGIFT")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORYITEM_FIXED_PRICE")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORYITEM_PERCENT")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORYORDER_PERCENT_DISCOUNT")
						|| header.getFile().getPath().contains("SPAR_ECOM_PROMOTION_CATEGORYORDER_FIXED_DISCOUNT"))
				{
					roundOffPromotionPriceRowAmount();
				}
			}
			finally
			{
				getSessionService().closeSession(localSession);
			}
		}
		return header;
	}


	@SuppressWarnings("boxing")
	protected void processFile(final File file, final BatchHeader header) throws FileNotFoundException
	{
		LOG.info("HCL Hybris feed file processing starts " + header.getFile().getName());
		final String encoding = header.getEncoding();
		final File sourceFile = header.getFile();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			final ImportConfig config = getImportConfig();
			final ImpExResource resource = new StreamBasedImpExResource(fis, encoding);
			config.setScript(resource);
			final ImportResult importResult = getImportService().importData(config);


			if (sourceFile.getPath().contains("INVENTORY"))
			{
				handlingErrorRows(sourceFile, importResult);


			}
			/*
			 * else if (sourceFile.getPath().contains("PROMOTION")) { handlingErrorRows(sourceFile, importResult);
			 * 
			 * 
			 * }
			 */
			else if (sourceFile.getPath().contains("PRICE"))
			{
				LOG.info("inside price feed method");
				handlingErrorRows(sourceFile, importResult);


			}
			else if (sourceFile.getPath().contains("SPAR_ECOM_BANNER")
					|| sourceFile.getPath().contains(SPAR_PRODUCT_COMBOS_MULTIPACK_SKUs))
			{

				LOG.info("SPAR_ECOM_BANNER Feed or SPAR_PRODUCT_COMBOS_MULTIPACK_SKUs Feed.");

				if (importResult.isError() || importResult.hasUnresolvedLines())
				{
					LOG.info("SPAR_ECOM_BANNER or SPAR_PRODUCT_COMBOS_MULTIPACK_SKUs Feed has errors ");
					handlingErrorRows(sourceFile, importResult);
				}
				else
				{
					executeProductSyncJob();
					//executUpdateIndexJOb();
				}

			}
			else if (sourceFile.getPath().contains("SPAR_ECOM_PRODREFERENCE"))
			{
				LOG.info("SPAR_ECOM_PRODREFERENCE  feed ");
				executeProductSyncJob();
			}
			else if (sourceFile.getPath().contains("SPAR_ECOM_LEGAL_METROLOGY"))
			{
				LOG.info("Sync sparProductCatalog after consuming SPAR_ECOM_LEGAL_METROLOGY feed.");
				executeProductSyncJob();
			}
			else if (sourceFile.getPath().contains("SPAR_ECOM_METATAG_DETAILS"))
			{
				LOG.info("Sync sparProductCatalog after consuming SPAR_ECOM_METATAG_DETAILS feed.");
				executeProductSyncJob();
			}
			else if (importResult.isError() && importResult.hasUnresolvedLines())
			{
				LOG.error(importResult.getUnresolvedLines().getPreview());
			}

		}
		catch (final Exception e)
		{
			LOG.error(" Exception caught at SparAbstractImpexRunnerTask:processFile:", e);
		}
		finally
		{
			IOUtils.closeQuietly(fis);
			LOG.info("HCL Hybris feed file processing ends " + header.getFile().getName());
		}
	}

	public void roundOffPromotionPriceRowAmount()
	{
		LOG.info("In roundOffPromotionPriceRowAmount query : " + PROMOTION_PRICE_ROUNDOFF_QUERY);
		try
		{
			jdbcTemplate.update(PROMOTION_PRICE_ROUNDOFF_QUERY);
			LOG.debug("promotionPriceRow change for duplicateRow - promotiom price row");
			final Collection<CacheRegion> regions = controller.getRegions();
			for (final CacheRegion region : regions)
			{
				controller.clearCache(region);
			}
		}
		catch (final Exception e)
		{
			LOG.error("In RoundOffPromotionPriceRowAmount method, Exception:" + e.getMessage());
		}
	}

	/**
	 * @param sourceFile
	 * @param importResult
	 * @throws FileNotFoundException
	 */
	private void handlingErrorRows(final File sourceFile, final ImportResult importResult) throws FileNotFoundException
	{
		if (importResult.isError() || importResult.hasUnresolvedLines())
		{

			final ImpExImportCronJobModel cronJob = importResult.getCronJob();
			LOG.error("Compressing error file and log file in " + sourceFile.getParentFile() + File.separator
					+ CRONJOB_LOG_FILE_NAME);

			compressErrorFiles(sourceFile, cronJob);
			LOG.error("error occured while import::isError::" + importResult.isError());
			LOG.error("error occured while import::getUnresolvedLines::" + importResult.getUnresolvedLines());
			//initiateFeedsErrorEmailProcess(file, ((GELDefaultImportService) getImportService()).getCsvFile().getName());
			initiateFeedsErrorEmailProcess(sourceFile, sourceFile.getName());

			cleanupCompressedFile(sourceFile);
		}

		/*
		 * else { LOG.info("Begin Solr index cron job after loading inventory feed.");
		 * setupSolrIndexerService.executeSolrIndexerCronJob("sparIndex",
		 * Boolean.valueOf(Config.getParameter(SPARfullReIndex))); LOG.info(
		 * "End of  Solr index cron job after loading inventory feed."); }
		 */

		else if (sourceFile.getName().contains("INVENTORY"))
		{
			LOG.info("###################INDEXING DISABLED FOR INVENTORY FEED###################################");
			//LOG.info("HCL Hybris starting solr indexing for SPAR_ECOM_INVENTORY at " + new Date());
			//setupSolrIndexerService.executeSolrIndexerCronJob("sparIndex", Boolean.valueOf(Config.getParameter(SPARfullReIndex)));
			//LOG.info("HCL Hybris ending solr indexing for SPAR_ECOM_INVENTORY");
		}

		else if (sourceFile.getName().contains("SPAR_ECOM_PRICE"))
		{
			LOG.info("###################INDEXING DISABLED FOR PRICE FEED###################################");
			/*
			 * LOG.info("HCL Hybris starting solr indexing for " + sourceFile.getName() + " at " + new Date());
			 * setupSolrIndexerService.executeSolrIndexerCronJob("sparIndex",
			 * Boolean.valueOf(Config.getParameter(SPARfullReIndex))); LOG.info("HCL Hybris ending solr indexing for " +
			 * sourceFile.getName());
			 */
		}


	}

	/**
	 * This method initiates the email process for sending feeds error.
	 *
	 * @param errorFile
	 * @param csvFileName
	 * @throws FileNotFoundException
	 */

	private void initiateFeedsErrorEmailProcess(final File errorFile, final String csvFileName) throws FileNotFoundException
	{

		LOG.debug("inside SparAbstractImpexRunnerTask:initiateFeedsErrorEmailProcess");

		final SparFeedsErrorEmailProcessModel process = (SparFeedsErrorEmailProcessModel) businessProcessService.createProcess(
				"SparFeedsErrorEmailProcess" + "-" + errorFile.getName() + "-" + System.currentTimeMillis(),
				"SparFeedsErrorEmailProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE_UID));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		process.setFileAttachments(emailAttachmentList(errorFile));
		//process.setErrorString(csvFileName);
		process.setSourceFileName(csvFileName);
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOG.debug("SparFeedsErrorEmailProcess started");
	}

	/**
	 * This method created email attachment list
	 *
	 * @param errorFile
	 * @return List<EmailAttachmentModel>
	 */
	private List<EmailAttachmentModel> emailAttachmentList(final File errorFile)
	{
		final List<EmailAttachmentModel> attachmentsList = new ArrayList<EmailAttachmentModel>();
		EmailAttachmentModel emailAttachmentModel = null;

		DataInputStream stream = null;
		try
		{
			stream = new DataInputStream(new FileInputStream(errorFile.getParentFile() + File.separator + ZIP_FILE_NAME));
		}
		catch (final FileNotFoundException e)
		{
			LOG.error(e);
		}

		emailAttachmentModel = createEmailAttachment(stream, ZIP_FILE_NAME, ZIP_MIME_TYPE);
		attachmentsList.add(emailAttachmentModel);

		return attachmentsList;
	}

	/**
	 * This method creates email attachment
	 *
	 * @param masterDataStream
	 * @param filename
	 * @param mimeType
	 * @return EmailAttachmentModel
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
	 * This method clean ups the compressed file generated for sending emails
	 *
	 * @param errorFile
	 */
	private void cleanupCompressedFile(final File errorFile)
	{
		final File errorFileZip = new File(errorFile.getParentFile() + File.separator + ZIP_FILE_NAME);
		final File logFile = new File(errorFile.getParentFile() + File.separator + CRONJOB_LOG_FILE_NAME);
		if (null != errorFileZip && errorFileZip.exists())
		{
			cleanupHelper.cleanupFile(errorFileZip);
		}
		if (null != logFile && logFile.exists())
		{
			cleanupHelper.cleanupFile(logFile);
		}
	}

	/**
	 * @return the cleanupHelper
	 */
	public CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}

	/**
	 * @param cleanupHelper
	 *           the cleanupHelper to set
	 */
	public void setCleanupHelper(final CleanupHelper cleanupHelper)
	{
		this.cleanupHelper = cleanupHelper;
	}


	/**
	 * This method compress error files
	 *
	 * @param sourceFile
	 * @param cronJob
	 */
	private void compressErrorFiles(final File sourceFile, final ImpExImportCronJobModel cronJob)
	{
		final com.spar.hcl.core.util.ZipUtil zipper = new com.spar.hcl.core.util.ZipUtil();

		// add impexFile to the filesList to be compressed
		final List<File> fileList = new ArrayList<File>();
		fileList.add(sourceFile);

		// add cronjob logs file to the filesList to be compressed
		final File logFile = new File(sourceFile.getParentFile() + File.separator + CRONJOB_LOG_FILE_NAME);
		PrintWriter logWriter = null;
		try
		{
			logWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile))));
		}
		catch (final FileNotFoundException ex)
		{
			LOG.error(ex);
		}
		logWriter.write(cronJob.getLogText());
		logWriter.flush();
		logWriter.close();

		fileList.add(logFile);

		// compress impex file and cron job logs file
		try
		{
			zipper.compressFiles(fileList, sourceFile.getParentFile() + File.separator + ZIP_FILE_NAME);

		}
		catch (final IOException ioe)
		{
			LOG.error(ioe);
		}
	}

	private void executeProductSyncJob()
	{

		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Online");
		catalogVersionService.setSessionCatalogVersion("sparProductCatalog", "Staged");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("sparProductCatalog", "Staged");
		final List<SyncItemJobModel> synchronizations = catalogVersion.getSynchronizations();
		for (final SyncItemJobModel syncItemJob : synchronizations)
		{
			final SyncItemCronJobModel cronJob = modelService.create(CatalogVersionSyncCronJobModel.class);
			cronJob.setLogToDatabase(Boolean.FALSE);
			cronJob.setLogToFile(Boolean.FALSE);
			cronJob.setForceUpdate(Boolean.FALSE);
			cronJob.setJob(syncItemJob);
			modelService.save(cronJob);
			modelService.refresh(cronJob);
			LOG.info("Generating cronjob  : " + cronJob.getCode() + " to synchronize the catalog : " + "sparProductCatalog");
			cronJobService.performCronJob(cronJob, true);
		}
	}

	/**
	 *
	 */
	private void executUpdateIndexJOb()
	{
		//defaultSetupSyncJobService.executeCatalogSyncJob("sparProductCatalog");
		LOG.info("Begin Solr index cron job after loading SPAR_ECOM_MULTIPLE_MEDIAS feed.");
		setupSolrIndexerService.executeSolrIndexerCronJob("sparIndex", Boolean.valueOf(Config.getParameter(SPARfullReIndex)));
		LOG.info("End of  Solr index cron job after loading SPAR_ECOM_MULTIPLE_MEDIAS feed.");

	}


}
