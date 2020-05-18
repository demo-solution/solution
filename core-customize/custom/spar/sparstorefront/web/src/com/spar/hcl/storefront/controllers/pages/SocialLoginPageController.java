/**
 *
 */
package com.spar.hcl.storefront.controllers.pages;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.facades.customer.SparCustomerFacade;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * @author ravindra.kr
 *
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/socialMediaLogin")
public class SocialLoginPageController extends AbstractPageController
{
	
	private static final Logger LOG = LoggerFactory.getLogger(SocialLoginPageController.class);
	
	@Autowired
	private CustomerFacade customerFacade;

	@Resource(name = "autoLoginStrategy")
	private AutoLoginStrategy autoLoginStrategy;

	@Resource(name = "sparCustomerFacade")
	private SparCustomerFacade sparCustomerFacade;

	@Autowired
	private UserService userService;
	
	@Resource
	AuthenticationManager authenticationManager;
	
	@Resource
	SessionService sessionService;
	
	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;
	
	@Autowired
	private UserDetailsService coreUserDetailsService;
	
	@Autowired
	private DeviceDetectionFacade deviceDetectionFacade;
	
	@Autowired
	private RememberMeServices rememberMeServices;
	/*
	 * @Autowired private RilCookieUtility customCookie; private HttpSessionRequestCache httpSessionRequestCache;
	 *//**
	 * @return the customCookie
	 */
	/*
	 * public RilCookieUtility getCustomCookie() { return customCookie; }
	 *//**
	 * @param customCookie
	 *           the customCookie to set
	 */
	/*
	 * public void setCustomCookie(final RilCookieUtility customCookie) { this.customCookie = customCookie; }
	 */

	protected AutoLoginStrategy getAutoLoginStrategy()
	{
		return autoLoginStrategy;
	}

	@RequestMapping(value = "/checkUser", method = RequestMethod.GET)
	@ResponseBody
	public String CheckUser(@RequestParam(value = "email_id", defaultValue = StringUtils.EMPTY) final String email_id,
			@RequestParam(value = "token_id", defaultValue = StringUtils.EMPTY) String token,
			@RequestParam(value = "login_media", defaultValue = StringUtils.EMPTY) final String loginVia, final Model model,
			final RedirectAttributes redirectAttributes, final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			sparCustomerFacade.validateEmailAlreadyRegistered(email_id);
		}
		catch (final DuplicateUidException e)
		{
			try
			{
				 Cookie cookie=new Cookie("user",email_id);
				 response.addCookie(cookie);
				 final Collection<? extends GrantedAuthority> customerAuthorities = coreUserDetailsService.loadUserByUsername(email_id).getAuthorities();
		       final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email_id, cookie.getValue(), customerAuthorities);
		       authentication.setDetails(new WebAuthenticationDetails(request));
		       UserDetails userDetails = coreUserDetailsService.loadUserByUsername(email_id);
		       User user = UserManager.getInstance().getUserByLogin(userDetails.getUsername());
		       JaloSession.getCurrentSession().setUser(user);
		       SecurityContextHolder.getContext().setAuthentication(authentication);
		       getCustomerFacade().loginSuccess();
		       getGuidCookieStrategy().setCookie(request, response);
		       rememberMeServices.loginSuccess(request, response, authentication);
		       LOG.debug("Calling device detection facade.");
		       deviceDetectionFacade.initializeRequest(request);
			}
			catch (final Exception ex)
			{
				final String errormsg = getMessageSource()
						.getMessage("social.login.error", null, getI18nService().getCurrentLocale());
				return errormsg;
			}
			if (null != request.getHeader("Referer") && request.getHeader("Referer").contains("/login/checkout"))
			{
				return "/login/checkout";
			}
			else
			{
				return "/login";
			}
		}
		final String errormsg = getMessageSource()
				.getMessage("social.register.new.user", null, getI18nService().getCurrentLocale());
		return errormsg;
	}

	/**
	 * @return the guidCookieStrategy
	 */
	public GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	/**
	 * @param guidCookieStrategy the guidCookieStrategy to set
	 */
	public void setGuidCookieStrategy(GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	

	/*
	 * @RequestMapping(value = "/CheckUserPost", method = RequestMethod.POST) public String checkUserPost(final
	 * RilfnlRegisterForm form, final BindingResult bindingResult, final Model model, final RedirectAttributes
	 * redirectAttributes, final HttpServletRequest request, final HttpServletResponse response) throws
	 * CMSItemNotFoundException { getCustomCookie().createNameQuantityCookie(response); return REDIRECT_PREFIX +
	 * getSuccessRedirect(request, response); }
	 * 
	 * protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) { if
	 * (httpSessionRequestCache.getRequest(request, response) != null) { return
	 * httpSessionRequestCache.getRequest(request, response).getRedirectUrl(); } return "/cart"; }
	 * 
	 * @Resource(name = "httpSessionRequestCache") public void setHttpSessionRequestCache(final HttpSessionRequestCache
	 * accHttpSessionRequestCache) { this.httpSessionRequestCache = accHttpSessionRequestCache; }
	 */
}
