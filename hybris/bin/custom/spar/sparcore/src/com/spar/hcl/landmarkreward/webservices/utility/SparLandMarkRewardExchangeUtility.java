/**
 *
 */
package com.spar.hcl.landmarkreward.webservices.utility;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spar.hcl.facades.landmarkreward.SparLRRedemptionDataResult;
import com.spar.hcl.facades.landmarkreward.SparLRUserDetailDataResult;
import com.spar.hcl.facades.landmarkreward.SparLRUserEnrollDataResult;

import de.hybris.platform.util.Config;
/**
 * @author ravindra.kr
 *
 */
public class SparLandMarkRewardExchangeUtility
{

	private static final Logger LOGGER = Logger.getLogger(SparLandMarkRewardExchangeUtility.class);
	private static final String LANDMARK_API_URL_KEY = "spar.landmarkreward.url";
	
	/**
	 * @param customerJSONData
	 * @return
	 */
	public static ResponseEntity<SparLRUserEnrollDataResult> enrollMemberToLR(final String customerJSONData)
	{
		LOGGER.info("Entered in enrollMemberToLR method");
		
		final HttpHeaders requestHeaders = SparLandmarkRewardUtilitySingleton.getHttpHeaders();
		final RestTemplate restTemplate = SparLandmarkRewardUtilitySingleton.getRestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.APPLICATION_OCTET_STREAM));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		String uri=Config.getString(LANDMARK_API_URL_KEY,LANDMARK_API_URL_KEY);
		uri = uri + "/EnrollSparMember";
		HttpEntity<String> requestEntity = new HttpEntity<String>(customerJSONData, requestHeaders);
		LOGGER.info("Sending data to LMS server:::::"+ uri);	
		LOGGER.info("In enrollMemberToLR, customerJSONData :::::"+ customerJSONData);
		try
		{
			return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, SparLRUserEnrollDataResult.class);
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

	
	/**
	 * @param paramJSONData
	 * @return
	 */
	public static ResponseEntity<SparLRUserDetailDataResult> getMemberForLMS(String paramJSONData)
	{
		LOGGER.info("Entered in getMemberForLMS method");
		
		final HttpHeaders requestHeaders = SparLandmarkRewardUtilitySingleton.getHttpHeaders();
		final RestTemplate restTemplate = SparLandmarkRewardUtilitySingleton.getRestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		String uri=Config.getString(LANDMARK_API_URL_KEY,LANDMARK_API_URL_KEY);
		uri = uri + "/GetMemberForLMS";
		HttpEntity<String> requestEntity = new HttpEntity<String>(paramJSONData, requestHeaders);
		LOGGER.info("Sending data to LMS server:::::"+ uri);	
		LOGGER.info("paramJSONData ::::: "+ paramJSONData);	
		try
		{
			return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, SparLRUserDetailDataResult.class);
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
	
	/**
	 * @param paramJSONData
	 * @return
	 */
	public static ResponseEntity<SparLRRedemptionDataResult> redemptionFromLMS(final String paramJSONData)
	{
		LOGGER.info("Entered in getMemberForLMS method");
		
		final HttpHeaders requestHeaders = SparLandmarkRewardUtilitySingleton.getHttpHeaders();
		final RestTemplate restTemplate = SparLandmarkRewardUtilitySingleton.getRestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		String uri=Config.getString(LANDMARK_API_URL_KEY,LANDMARK_API_URL_KEY);
		uri = uri + "/RedemptionFromLMS";
		LOGGER.info("Sending data to LMS server:::::"+ paramJSONData);	
		HttpEntity<String> requestEntity = new HttpEntity<String>(paramJSONData, requestHeaders);
		LOGGER.info("Sending data to LMS server:::::"+ uri);	
		try
		{
			return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, SparLRRedemptionDataResult.class);
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

	/**
	 * @param data
	 * @return
	 */
	public static String createDataAsJson(final Object data)
	{
		LOGGER.info("Entered in createOMSDataAsJson method");
		final ObjectMapper mapper = new ObjectMapper();
		String jsonData = null;
		try
		{
			jsonData = mapper.writeValueAsString(data);
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
