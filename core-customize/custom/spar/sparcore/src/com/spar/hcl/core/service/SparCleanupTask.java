/**
 *
 */
package com.spar.hcl.core.service;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupTask;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author jitendriya.m
 *
 */
public class SparCleanupTask extends CleanupTask
{
	/** The Logger */
	private static final Logger LOG = Logger.getLogger(SparCleanupTask.class);
	private static final String SPARfullReIndex = "spar.fullReIndex";

	private CleanupHelper cleanupHelper;

	/** The ModelService */
	@Autowired
	private ModelService modelService;

	/** The CatalogVersionService */
	@Autowired
	private CatalogVersionService catalogVersionService;

	@Autowired
	private CronJobService cronJobService;

	@Autowired
	private SetupSolrIndexerService setupSolrIndexerService;

	@Override
	public BatchHeader execute(final BatchHeader header)
	{

		if (header.getFile().getName().contains("SPAR_ECOM_MULTIPLE_MEDIAS"))
		{
			executeProductSyncJob();
			executUpdateIndexJOb();
		}
		cleanupHelper.cleanup(header, false);

		return null;
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
	 * @return the cleanupHelper
	 */
	@Override
	protected CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}


	/**
	 *
	 */
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
