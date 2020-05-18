package com.spar.hcl.landmarkreward.webservices.utility;

import de.hybris.platform.util.Config;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * @author ravindra.kr
 *
 */
public class SparLandmarkRewardUtilitySingleton
{
	private static final Logger LOGGER = Logger.getLogger(SparLandmarkRewardUtilitySingleton.class);
	private static final String LANDMARK_CONNECTION_TIMEOUT = "spar.landmarkreward.connectTimeout";
	private static HttpHeaders requestHeaders = null;
	private static URL url = null;

	private SparLandmarkRewardUtilitySingleton()
	{
		//nothing to do
	}

	/**
	 * This method is used to get the HttpHeader object as singleton object
	 *
	 * @return HttpHeaders
	 */

	public static HttpHeaders getHttpHeaders()
	{
		if (requestHeaders == null)
		{
			LOGGER.info("Entered in SparLandmarkRewardUtilitySingleton : getHttpHeaders method");
			final String authKey = Config.getString("spar.landmarkreward.AuthKey", "spar.landmarkreward.AuthKey");
			requestHeaders = new HttpHeaders();
			requestHeaders.add("AuthKey", authKey);
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		}
		LOGGER.info("Exited from getHttpHeaders method : " + requestHeaders);
		return requestHeaders;
	}

	/**
	 * This method is used to get the RestTemplate object as singleton object
	 *
	 * @return RestTemplate
	 */
	public static RestTemplate getRestTemplate()
	{
		RestTemplate restTemplate = new RestTemplate();
 	   HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	   String timeoutMilliseconds = Config.getString(LANDMARK_CONNECTION_TIMEOUT, "2");
		    
	      @SuppressWarnings("boxing")
			 int timeout = (int) ((Double.valueOf(timeoutMilliseconds)) * 1000);
	       LOGGER.info("timeoutMilliseconds : "+timeout);
	       factory.setReadTimeout(timeout);
	       factory.setConnectTimeout(timeout);
	       factory.setConnectionRequestTimeout(timeout);
	       restTemplate.setRequestFactory(factory);
		return restTemplate;
	}

	/**
	 * This method is used to get the URL object as singleton object
	 *
	 * @return URL
	 */
	public static URL getUrl()
	{
		LOGGER.info("Entered in getUrl method");
		try
		{
			if (url == null)
			{
				LOGGER.info("Exited from getUrl method");
				return new URL(Config.getString("spar.landmarkreward.enrollmember.url", "spar.landmarkreward.enrollmember.url"));
			}
		}
		catch (final MalformedURLException msg)
		{
			LOGGER.info("Error on get url from singleton method:::::: " + msg);
		}
		LOGGER.info("Exited from getUrl method");
		return url;
	}
}
