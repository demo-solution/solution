package com.spar.hcl.logger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * This file is a custom logger to log errors.
 *
 * @author rohan_c
 *
 */
public class SparLogger
{
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private static Map<String, Logger> m_loggers = new ConcurrentHashMap();
	private static String dir; // Root log directory
	private static Layout layout;
	private static Logger rootLogger = Logger.getRootLogger();

	public static synchronized void logMessage(String jobName, final String message)
	{
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar cal = Calendar.getInstance();
		jobName = jobName + "_" + dateFormat.format(cal.getTime());

		final Logger l = getJobLogger(jobName);
		l.info(message);
	}

	/**
	 * This is a custom logger which stores custom logger based on unique name a log files will be created in root logger
	 * directory
	 *
	 * @param jobName
	 * @return Logger
	 */
	private static Logger getJobLogger(final String jobName)
	{
		Logger logger = m_loggers.get(jobName);
		if (dir == null || layout == null)
		{
			try
			{
				initialize();
			}
			catch (final Exception e)
			{
				rootLogger.info("error getting file appender for custom logger");
				return rootLogger;
			}

		}

		if (logger == null)
		{
			logger = Logger.getLogger(jobName);
			m_loggers.put(jobName, logger);
			logger.setLevel(Level.INFO);
			try
			{
				File file = new File(dir);
				file.mkdirs();
				file = new File(dir, jobName + ".log");
				final FileAppender appender = new FileAppender(layout, file.getAbsolutePath(), false);
				logger.removeAllAppenders();
				logger.addAppender(appender);
				rootLogger.info("file absolute path is " + file.getAbsolutePath());
			}
			catch (final Exception e)
			{
				rootLogger.info("error getting custom logger , return root logger");
				logger = rootLogger;
			}
		}
		return logger;
	}

	/**
	 * this method initializes the logger
	 *
	 * @throws Exception
	 */
	private static void initialize() throws Exception
	{
		@SuppressWarnings("rawtypes")
		final Enumeration enumeration = Logger.getRootLogger().getAllAppenders();
		while (enumeration.hasMoreElements())
		{
			final Appender app = (Appender) enumeration.nextElement();
			if (app instanceof FileAppender)
			{
				layout = app.getLayout();
				final File f = new File(((FileAppender) app).getFile());
				dir = f.getParent();
			}
		}
		if (dir == null)
		{
			throw new Exception("dir is null ");
		}

	}
}
