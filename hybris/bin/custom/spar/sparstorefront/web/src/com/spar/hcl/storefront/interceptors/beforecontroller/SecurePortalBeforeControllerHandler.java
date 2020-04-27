/**
 *
 */
package com.spar.hcl.storefront.interceptors.beforecontroller;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.method.HandlerMethod;

import com.spar.hcl.storefront.interceptors.BeforeControllerHandler;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * This class is used as a beforeController to SecureCheck the request. In case the user is anonymous, it redirects the
 * user to Login page.
 * 
 * @author valechar
 * 
 */
@SuppressWarnings("deprecation")
public class SecurePortalBeforeControllerHandler implements BeforeControllerHandler
{

	private static final String SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

	private static final String REFERER = "referer";

	private String defaultLoginUri;

	private String checkoutLoginUri;

	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private RedirectStrategy redirectStrategy;

	private static final Logger LOG = Logger.getLogger(SecurePortalBeforeControllerHandler.class);

	private UserService userService;

	private CMSSiteService cmsSiteService;

	private Set<String> unsecuredUris;

	private Set<String> controlUris;

	private SecurePortalRequestProcessor requestProcessor;

	private transient RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.yacceleratorstorefront.interceptors.BeforeControllerHandler#beforeController(javax.servlet.http
	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)
	 */
	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response,
			final HandlerMethod handler) throws Exception
	{
		boolean returning = true;

		//Make sure that if the user does not have secure token in session, we invalidate the session and website asks for login again.
		if (!requestProcessor.skipSecureCheck())
		{
			final UserModel user = userService.getCurrentUser();
			final boolean isUserAnonymous = userService.isAnonymousUser(user);

			final String otherRequestParam = requestProcessor.getOtherRequestParameters(request);

			if (isUriPartOfSet(request, unsecuredUris))
			{
				if (!isUserAnonymous && !isUriPartOfSet(request, controlUris))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("User is already authenticated and accessing a safe-mapping, hence a useless operation. Redirect to home page instead.");
					}

					redirect(request, response, getRedirectUrlIfAuthenticated(otherRequestParam));
					returning = false;
				}
				else
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("The request URI '%s' does not require security. Interceptor is done.",
								request.getRequestURI()));
					}
				}
			}
			else if (isUserAnonymous && isNotLoginRequest(request))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Anonymous user is accessing the URI '%s' which is secured. Redirecting to login page.",
							request.getRequestURI()));
				}

				saveRequestCacheAttribute(request, otherRequestParam);

				redirect(request, response, getRedirectUrl(defaultLoginUri, true, otherRequestParam));
				returning = false;
			}
		}

		return returning;
	}

	/**
	 * This method is used to save the Home Page URL in the httpSessionRequestCache
	 * 
	 * @param request
	 * @param otherRequestParam
	 */
	@SuppressWarnings("unused")
	private void saveRequestCacheAttribute(final HttpServletRequest request, final String otherRequestParam)
	{
		if (requestMatcher.matches(request))
		{
			final DefaultSavedRequest savedRequest = new DefaultSavedRequest(request, new PortResolverImpl())
			{
				private final String referer = request.getHeader(REFERER);
				private final String contextPath = request.getContextPath();

				@Override
				public String getRedirectUrl()
				{
					return getRedirectUrlIfAuthenticated(otherRequestParam);
				}
			};
			if (request.getSession(false) != null)
			{
				request.getSession().setAttribute(SPRING_SECURITY_SAVED_REQUEST, savedRequest);
			}
		}
	}


	protected boolean isUriPartOfSet(final HttpServletRequest request, final Set<String> inputSet)
	{

		for (final String input : inputSet)
		{

			final AntPathRequestMatcher matcher = new AntPathRequestMatcher(input);
			if (matcher.matches(request))
			{
				return true;
			}

		}

		return false;

	}

	protected String getRedirectUrlIfAuthenticated(final String otherParameters)
	{
		return getRedirectUrl("/", true, otherParameters);
	}

	protected void redirect(final HttpServletRequest request, final HttpServletResponse response, final String targetUrl)
	{

		try
		{

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Redirecting to url '%s'.", targetUrl));
			}

			redirectStrategy.sendRedirect(request, response, targetUrl);

		}
		catch (final IOException ex)
		{
			LOG.error("Unable to redirect.", ex);
		}

	}

	protected String getRedirectUrl(final String mapping, final boolean secured, final String otherParameters)
	{
		if (otherParameters != null)
		{
			return siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), secured, mapping,
					otherParameters);
		}

		return siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), secured, mapping);
	}

	/**
	 * @return the defaultLoginUri
	 */
	public String getDefaultLoginUri()
	{
		return defaultLoginUri;
	}

	/**
	 * @param defaultLoginUri
	 *           the defaultLoginUri to set
	 */
	@Required
	public void setDefaultLoginUri(final String defaultLoginUri)
	{
		this.defaultLoginUri = defaultLoginUri;
	}

	/**
	 * @return the checkoutLoginUri
	 */
	public String getCheckoutLoginUri()
	{
		return checkoutLoginUri;
	}

	/**
	 * @param checkoutLoginUri
	 *           the checkoutLoginUri to set
	 */
	@Required
	public void setCheckoutLoginUri(final String checkoutLoginUri)
	{
		this.checkoutLoginUri = checkoutLoginUri;
	}

	protected boolean isNotLoginRequest(final HttpServletRequest request)
	{
		return !request.getRequestURI().contains(defaultLoginUri) || request.getRequestURI().contains(checkoutLoginUri);
	}

	/**
	 * @return the siteBaseUrlResolutionService
	 */
	public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	/**
	 * @param siteBaseUrlResolutionService
	 *           the siteBaseUrlResolutionService to set
	 */
	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}


	/**
	 * @return the redirectStrategy
	 */
	public RedirectStrategy getRedirectStrategy()
	{
		return redirectStrategy;
	}

	/**
	 * @param redirectStrategy
	 *           the redirectStrategy to set
	 */
	public void setRedirectStrategy(final RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	@Required
	public void setUnsecuredUris(final Set<String> unsecuredUris)
	{
		this.unsecuredUris = unsecuredUris;
	}

	@Required
	public void setControlUris(final Set<String> controlUris)
	{
		this.controlUris = controlUris;
	}

	/**
	 * @return the requestProcessor
	 */
	public SecurePortalRequestProcessor getRequestProcessor()
	{
		return requestProcessor;
	}

	/**
	 * @param requestProcessor
	 *           the requestProcessor to set
	 */
	public void setRequestProcessor(final SecurePortalRequestProcessor requestProcessor)
	{
		this.requestProcessor = requestProcessor;
	}

}
