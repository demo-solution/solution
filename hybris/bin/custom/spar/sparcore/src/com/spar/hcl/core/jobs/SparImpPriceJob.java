/**
 *
 */
package com.spar.hcl.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * @author tayal.m
 *
 */
public class SparImpPriceJob extends AbstractJobPerformable
{
	private final String filePathDest = Config.getParameter("sparcore.batch.impex.basefolder");
	private final String filePathSource = Config.getParameter("sparcore.import.price.file.path");
	private final String fileName = "SPAR_ECOM_PRICE_";
	private static final Logger LOG = Logger.getLogger(SparImpPriceJob.class);

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		final Date date = new Date();
		final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final File source = new File(filePathSource);
		final File newFile = new File(filePathSource + File.separator + fileName + dateFormat.format(date) + ".csv");

		if (source.renameTo(newFile))
		{
			LOG.info("File rename success");
		}
		else
		{
			LOG.info("File rename failed");
		}

		final File desc = new File(filePathDest);

		LOG.info("Source Location:" + source.getPath());
		LOG.info("Destination  Location:" + desc.getPath());

		if (source.exists())
		{
			try
			{
				FileUtils.copyDirectory(source, desc);
				FileUtils.cleanDirectory(source);
				//FileUtils.moveDirectory(source, desc);  not working
				//	FileUtils.moveFileToDirectory(source, desc, false);
				LOG.info("File transfered for import of price");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			LOG.info("No File Found");
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
}
