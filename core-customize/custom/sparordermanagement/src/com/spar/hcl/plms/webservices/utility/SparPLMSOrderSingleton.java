/**
 *
 */
package com.spar.hcl.plms.webservices.utility;

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
 * @author kumarchoubeys
 *
 */
public class SparPLMSOrderSingleton
{

	private static final Logger LOGGER = Logger.getLogger(SparPLMSOrderSingleton.class);

	private static HttpHeaders requestHeaders = null;
	private static URL url=null;

	private SparPLMSOrderSingleton()
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
			final String user = Config.getString("spar.plms.user", "spar.plms.user");
			final String pass = Config.getString("spar.plms.pass", "spar.plms.pass");
			final String plainCreds = user+":"+pass;
			final byte[] plainCredsBytes = plainCreds.getBytes();
			final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			final String base64Creds = new String(base64CredsBytes);

			requestHeaders = new HttpHeaders();
			requestHeaders.add("Authorization", "Basic " + base64Creds);
			requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
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
				return new URL(Config.getString("spar.plms.rooturl","spar.plms.rooturl"));
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
