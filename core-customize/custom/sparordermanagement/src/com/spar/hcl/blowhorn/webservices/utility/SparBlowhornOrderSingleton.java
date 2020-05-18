/**
 *
 */
package com.spar.hcl.blowhorn.webservices.utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.util.Config;


/**
 * @author pavan.sr
 *
 */
public class SparBlowhornOrderSingleton
{

	private static final Logger LOGGER = Logger.getLogger(SparBlowhornOrderSingleton.class);

	private static HttpHeaders requestHeaders = null;
	private static URL url=null;

	private SparBlowhornOrderSingleton()
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
			LOGGER.info("Entered in getHttpHeaders method");
			final String api_key = Config.getString("spar.blowhorn.api_key", "spar.blowhorn.api_key");
			
			requestHeaders = new HttpHeaders();
			requestHeaders.set("api_key", api_key);
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			
		}
		LOGGER.info("Exited from getHttpHeaders method");
		return requestHeaders;
	}

	

	/**
	 * This method is used to get the RestTemplate object as singleton object
	 *
	 * @return RestTemplate
	 */
	public static RestTemplate getRestTemplate()
	{
		return new RestTemplate();
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
			if(url == null)
			{
				LOGGER.info("Exited from getUrl method");
				return new URL(Config.getString("spar.blowhorn.rooturl","https://test.blowhorn.com/api/orders/shipment"));
			}
		} 
		catch (MalformedURLException msg) 
		{
			LOGGER.info("Error on get url from singleton method:::::: "+msg);
		}
		LOGGER.info("Exited from getUrl method");
		return url;
	}
}
