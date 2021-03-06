/**
 *
 */
package com.spar.hcl.blowhorn.webservices.utility;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spar.hcl.facades.blowhorn.SparBlowhornCancelledOrderDataResponse;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataResponse;

import de.hybris.platform.util.Config;


/**
 * @author pavan.sr
 *
 */
public class SparBlowhornOrderTransferUtility
{

	private static final Logger LOGGER = Logger.getLogger(SparBlowhornOrderTransferUtility.class);
	private static final String BLOWHORN_SYSTEM_ACTIVE = "spar.blowhorn.isActive";
	
	/**
	 * This method is used to create the resttemplate object used to send the json data to plms
	 *
	 * @param omsJsonData
	 * @return ResponseEntity
	 */

	public static ResponseEntity<SparBlowhornShipAddressOrderDataResponse> sendOMSDataToBlowhorn(final String omsJsonData, final String apiUrl, HttpMethod httpMethod)
	{
		boolean isConnected = SparBlowhornServerConnectionUtility.blowhornServerAvailability();
		boolean activeFlage = Config.getBoolean(BLOWHORN_SYSTEM_ACTIVE, false);
   		if(isConnected && activeFlage)
      	{
			LOGGER.info("Entered in sendOMSDataToBlowhorn method");
			
			final HttpHeaders requestHeaders = SparBlowhornOrderSingleton.getHttpHeaders();
			final RestTemplate restTemplate = SparBlowhornOrderSingleton.getRestTemplate();
			MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
			mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
			restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
			LOGGER.info("Shipment API resource URL :::::::::"+ apiUrl);	
			LOGGER.debug("Shipment API Header data  ::::::::"+ requestHeaders);
			LOGGER.debug("Shipment API request string  :::::"+ omsJsonData);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(omsJsonData, requestHeaders);
			LOGGER.info("requestEntity sending to blowhorn server:::::"+ omsJsonData);	
			
			try
			{
				ResponseEntity<SparBlowhornShipAddressOrderDataResponse> entity = restTemplate.exchange(apiUrl, httpMethod, requestEntity, SparBlowhornShipAddressOrderDataResponse.class);
				LOGGER.info("Please chceck the server connection:::" +entity.getStatusCode());
				return entity;
			}
			catch(ResourceAccessException msg)
			{
				LOGGER.info("Please chceck the server connection 1:::" +msg);
				return null;
			}
			catch(HttpClientErrorException msg)
			{
				LOGGER.info("RestClientException 2 msg.getMessage():::" +msg.getMessage());
				LOGGER.info("RestClientException 2 msg.getMessage():::" +msg.getResponseBodyAsString());
				return null;
			}
			catch(Exception msg)
			{
				LOGGER.info("RestClientException 3 :::" +msg.getMessage());
				return null;
			}
      	}
   		else
   		{
   			return null;
   		}
	}
	
	
	public static ResponseEntity<SparBlowhornCancelledOrderDataResponse> sendCancelledOMSDataToBlowhorn(final String omsJsonData, final String apiUrl, HttpMethod httpMethod)
	{
		boolean isConnected = SparBlowhornServerConnectionUtility.blowhornServerAvailability();
		boolean activeFlage = Config.getBoolean(BLOWHORN_SYSTEM_ACTIVE, false);
   		if(isConnected && activeFlage)
      	{
			LOGGER.info("Entered in sendCancelledOMSDataToBlowhorn method");
			
			final HttpHeaders requestHeaders = SparBlowhornOrderSingleton.getHttpHeaders();
			final RestTemplate restTemplate = SparBlowhornOrderSingleton.getRestTemplate();
			MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
			mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
			restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
			LOGGER.info("Shipment API resource URL :::::::::"+ apiUrl);	
			LOGGER.debug("Shipment API Header data  ::::::::"+ requestHeaders);
			LOGGER.debug("Shipment API request string  :::::"+ omsJsonData);
			
			HttpEntity<String> requestEntity = new HttpEntity<String>(omsJsonData, requestHeaders);
			LOGGER.info("requestEntity sending to blowhorn server:::::"+ omsJsonData);	
			
			try
			{
				ResponseEntity<SparBlowhornCancelledOrderDataResponse> entity = restTemplate.exchange(apiUrl, httpMethod, requestEntity, SparBlowhornCancelledOrderDataResponse.class);
				LOGGER.info("Please chceck the server connection:::" +entity.getStatusCode());
				return entity;
			}
			catch(ResourceAccessException msg)
			{
				LOGGER.info("Please chceck the server connection 1:::" +msg);
				return null;
			}
			catch(HttpClientErrorException msg)
			{
				LOGGER.info("RestClientException 2 msg.getMessage():::" +msg.getMessage());
				LOGGER.info("RestClientException 2 msg.getMessage():::" +msg.getResponseBodyAsString());
				return null;
			}
			catch(Exception msg)
			{
				LOGGER.info("RestClientException 3 :::" +msg.getMessage());
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
