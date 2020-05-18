/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.spar.hcl.v2.controller;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.oauth2.token.provider.HybrisOAuthTokenServices;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;

@Controller
@RequestMapping(value = "/{baseSiteId}/socialMedia")
@CacheControl(directive = CacheControlDirective.NO_STORE)
public class SocialLoginController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(SocialLoginController.class);

	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;
	
	@Autowired
	private HybrisOAuthTokenServices tokenServices;

	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/isExistingUser", method = RequestMethod.GET)
	@ResponseBody
	public UserWsDTO isExistingUser(@RequestParam(value = "email_id", defaultValue = StringUtils.EMPTY) final String email_id,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CustomerData customerData = sparCustomerFacade.getUserForUID(email_id);
		final UserWsDTO dto = dataMapper.map(customerData, UserWsDTO.class, fields);
		return dto;
	}
	
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/getOAuthAccessToken", method = RequestMethod.GET)
	@ResponseBody
	public OAuth2AccessToken getOAuthAccessToken(@RequestParam(value = "email_id", defaultValue = StringUtils.EMPTY) final String email_id,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		LOG.info("In getOAuthAccessToken, email_id : "+ email_id);
   	 HashMap<String, String> authorizationParameters = new HashMap<String, String>();
       authorizationParameters.put("scope", "basic");
       authorizationParameters.put("username", email_id);
       authorizationParameters.put("client_id", "mobile_client");
       authorizationParameters.put("grant_type", "password");
   
       Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
       authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMERMANAGERGROUP"));
   
       Set<String> responseType = new HashSet<String>();
       responseType.add("password");
       Set<String> scopes = new HashSet<String>();
      scopes.add("basic");
     // scopes.add("write");
   
       OAuth2Request authorizationRequest = new OAuth2Request(authorizationParameters, "mobile_client",authorities, true,scopes, null, "",
               responseType, null);
       User userPrincipal = new User(email_id, "", true, true, true, true, authorities);
       UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
       OAuth2Authentication authenticationRequest = new OAuth2Authentication(authorizationRequest, authenticationToken);
       authenticationRequest.setAuthenticated(true);
       
       OAuth2AccessToken accessToken = tokenServices.createAccessToken(authenticationRequest);
       return accessToken;
	}
}
