/**
 *
 */
package com.spar.hcl.storefront.interceptors.beforecontroller;

import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is used to process the secure checking in Secure Portal
 * 
 * @author valechar
 *
 */
public class AsmRequestProcessor implements SecurePortalRequestProcessor
{
	private String agentSessionAttribute;

	private String asmRequestParameter;

	private String quitAsmRequestUri;

	private SessionService sessionService;


	@Override
	public String getOtherRequestParameters(final HttpServletRequest request)
	{
		String assistedServiceModeRequested = request.getParameter(asmRequestParameter);
		if (request.getRequestURI().endsWith(quitAsmRequestUri))
		{
			assistedServiceModeRequested = "false";
		}

		String requestParam = null;
		if (assistedServiceModeRequested != null)
		{
			requestParam = asmRequestParameter + "=" + assistedServiceModeRequested;
		}

		return requestParam;
	}

	@Override
	public boolean skipSecureCheck()
	{
		// if we find asm agent id in session, that means asm agent login, so we can skip secure checking in secureportal
		final Set<String> allAttributeNames = sessionService.getCurrentSession().getAllAttributes().keySet();
		for (final String name : allAttributeNames)
		{
			if (name.contains(agentSessionAttribute))
			{
				return true;
			}
		}

		return false;
	}

	@Required
	public void setAsmRequestParameter(final String asmRequestParameter)
	{
		this.asmRequestParameter = asmRequestParameter;
	}

	@Required
	public void setQuitAsmRequestUri(final String quitAsmRequestUri)
	{
		this.quitAsmRequestUri = quitAsmRequestUri;
	}

	@Required
	public void setAgentSessionAttribute(final String agentSessionAttribute)
	{
		this.agentSessionAttribute = agentSessionAttribute;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
