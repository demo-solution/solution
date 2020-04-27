/**
 *
 */
package com.spar.hcl.plms.webservices.utility;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;


/**
 * @author kumarchoubeys
 *
 */
public class SparPLMSServerConnectionUtility
{
	private static final Logger LOGGER = Logger.getLogger(SparPLMSServerConnectionUtility.class);


	/**
	 * This method is used to check the availability of PLMS server
	 *
	 * @return boolean
	 */
	public static boolean plmsServerAvailability()
	{
		LOGGER.info("Entered in checkAvailabilityofServer method");
		URL url = null;
		HttpURLConnection urlConnection = null;
		try
		{
			url=SparPLMSOrderSingleton.getUrl();
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			urlConnection.connect();
			final int resCode = urlConnection.getResponseCode();
			LOGGER.info("PLMS Server availability status::::::::" + resCode);
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
