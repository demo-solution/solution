/**
 *
 */
package com.spar.hcl.plms.webservices.utility;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;

import de.hybris.platform.util.Config;


/**
 * @author kumarchoubeys
 *
 */
public class SparPLMSOrderTransferUtility
{

	private static final Logger LOGGER = Logger.getLogger(SparPLMSOrderTransferUtility.class);
	private static final String PLMS_SYSTEM_ACTIVE = "spar.plms.isActive";
	
	/**
	 * This method is used to create the resttemplate object used to send the json data to plms
	 *
	 * @param omsJsonData
	 * @return ResponseEntity
	 */

	public static ResponseEntity<SparPlmsOrderDataResponse> sendOMSDataToPLMS(final String omsJsonData)
	{
		boolean plmsActive = Config.getBoolean(PLMS_SYSTEM_ACTIVE, false);
   		if(plmsActive)
      	{
			LOGGER.info("Entered in sendOMSDataToPLMS method");
			
			final HttpHeaders requestHeaders = SparPLMSOrderSingleton.getHttpHeaders();
			final RestTemplate restTemplate = SparPLMSOrderSingleton.getRestTemplate();
			MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
			mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
			restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
			final String plmsUri=Config.getString("spar.plms.rooturl","spar.plms.rooturl");
	
			HttpEntity<String> requestEntity = new HttpEntity<String>(omsJsonData, requestHeaders);
			LOGGER.info("Sending data to plms server:::::"+ plmsUri);	
			try
			{
				return restTemplate.exchange(plmsUri, HttpMethod.POST, requestEntity, SparPlmsOrderDataResponse.class);
			}
			catch(ResourceAccessException msg)
			{
				LOGGER.info("Please chceck the server connection:::" +msg);
				return null;
			}
			catch(RestClientException msg)
			{
				LOGGER.info("RestClientException :::" +msg);
				return null;
			}
      	}
   		else
   		{
   			return null;
   		}
	}



	/**
	 * This method is used to create the Json data for the Order Data object
	 *
	 * @param orderData
	 * @return String
	 */
	public static String createOMSDataAsJson(final Object orderData)
	{
		LOGGER.info("Entered in createOMSDataAsJson method");
		final ObjectMapper mapper = new ObjectMapper();
		String jsonData = null;
		try
		{
			jsonData = mapper.writeValueAsString(orderData);
			if (null != jsonData)
			{
				return jsonData;
			}
		}
		catch (final IOException msg)
		{
			LOGGER.error("error while converting into json data" + msg);
		}
		
		return StringUtils.EMPTY;
	}

}
