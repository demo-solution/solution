/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparDelayFeedNotificationEmailProcessModel;




public class SparDelayFeedNotificationJob extends AbstractJobPerformable
{
	private static final Logger LOG = Logger.getLogger(SparDelayFeedNotificationJob.class);
	public static final String ARCHIVE_LOCATION_KEY = "sparcore.import.archive.file.path";

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private ConfigurationService configurationService;

	@SuppressWarnings("deprecation")
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		LOG.info("SparDelayFeedNotification :: perform :: start");


		try
		{
			if (isEligibleForSendingMail())
			{
				LOG.info("price feed file not  found and sending email notification.");
				sendDelayEmailNotification();

			}
			LOG.info("SparDelayFeedNotification :: perform :: finished");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during Order Export", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	private void sendDelayEmailNotification()
	{
		final SparDelayFeedNotificationEmailProcessModel process = (SparDelayFeedNotificationEmailProcessModel) businessProcessService
				.createProcess("SparDelayFeedNotificationEmailProcess" + "-" + "-" + System.currentTimeMillis(),
						"SparDelayFeedNotificationEmailProcess");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				"basesite.uid"));
		process.setSite(baseSite);
		process.setStore(baseSite.getStores().get(0));
		//process.setDailyRefundResults(dailyRefundResultList);
		modelService.saveAll();
		businessProcessService.startProcess(process);
		LOG.debug("SparDelayFeedNotification :: sendSparDelayFeedNotification :: finished");
	}


	boolean isEligibleForSendingMail() throws ParseException
	{


		// this decide wheter to send email or not on the basis of directory archive.
		//filter the price file with today date first and also u can use date range then if does not found send mail.


		final String path = Config.getParameter(ARCHIVE_LOCATION_KEY);
		final List<String> priceFeedsToday = getArchiceFiles(path);
		if (priceFeedsToday.isEmpty())
		{


			return true;
		}
		else
		{
			return false;
		}
	}

	public static List<String> getArchiceFiles(final String logLocation) throws ParseException
	{
		final File logDirectory = new File(logLocation);
		String[] files = new String[] {};


		final SimpleDateFormat todayDate = new SimpleDateFormat("yyyyMMdd");//dd/MM/yyyy
		final Date now = new Date();
		final String todayDateStr = todayDate.format(now);

		if (logDirectory.isDirectory())
		{
			files = logDirectory.list(new FilenameFilter()
			{

				@Override
				public boolean accept(final File dir, final String name)
				{
					final String[] parts = name.split("_");
					final String lastElement = parts[parts.length - 1];
					final String fileDate = lastElement.substring(0, Math.min(lastElement.length(), 8));
					return name.startsWith("SPAR_ECOM_PRICE") && StringUtils.equals(todayDateStr, fileDate);
				}
			});
		}
		return Arrays.asList(files);
	}
}
