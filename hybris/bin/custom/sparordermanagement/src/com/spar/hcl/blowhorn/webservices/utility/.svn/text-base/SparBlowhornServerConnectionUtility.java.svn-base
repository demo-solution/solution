/**
 *
 */
package com.spar.hcl.blowhorn.webservices.utility;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;


/**
 * @author pavan.sr
 *
 */
public class SparBlowhornServerConnectionUtility
{
	private static final Logger LOGGER = Logger.getLogger(SparBlowhornServerConnectionUtility.class);


	/**
	 * This method is used to check the availability of PLMS server
	 *
	 * @return boolean
	 */
	public static boolean blowhornServerAvailability()
	{
		LOGGER.info("Entered in checkAvailabilityofServer method");
		URL url = null;
		HttpURLConnection urlConnection = null;
		try
		{
			url=SparBlowhornOrderSingleton.getUrl();
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			urlConnection.connect();
			final int resCode = urlConnection.getResponseCode();
			LOGGER.info("Blowhorn Server availability status::::::::" + resCode);
			if (resCode == 200)
			{
				LOGGER.info("Exited from checkAvailabilityofServer method");
				return true;
			}
		}
		catch (final MalformedURLException msg)
		{
			LOGGER.error("Issue while connecting to the PLMS Server:::::::" + msg);
			return false;
		}
		catch (final IOException msg)
		{
			LOGGER.error("Issue while connecting to the PLMS Server:::::::" + msg);
			return false;
		}
		finally
		{
			urlConnection.disconnect();
		}
		LOGGER.info("Exited from checkAvailabilityofServer method");
		return false;
	}
}
