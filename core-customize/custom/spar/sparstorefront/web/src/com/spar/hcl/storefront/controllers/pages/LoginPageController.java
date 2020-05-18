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
package com.spar.hcl.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.servicelayer.session.SessionService;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.validation.Validator;
import com.spar.hcl.facades.constants.SparFacadesConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.storefinder.StoreFinderFacadeInterface;
import com.spar.hcl.storefront.controllers.ControllerConstants;
import com.spar.hcl.storefront.forms.SparRegisterForm;


/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/login")
public class LoginPageController extends SparAbstractLoginPageController
{
	private HttpSessionRequestCache httpSessionRequestCache;

	@Resource(name = "storeFinderFacadeInterface")
	private StoreFinderFacadeInterface storeFinderFacadeInterface;

	@Resource(name = "sessionService")
	SessionService sessionService;

	/* Code change start by sumit */
	private String otpKey;
	private String mobilenum;
	@Resource(name = "sparCustomerFacade")
	SparCustomerFacade sparCustomerFacade;
	@Resource(name = "sparRegistrationViaSocialMediaValidator")
	private Validator sparRegistrationViaSocialMediaValidator;
	
	/**
	 * @return the sparRegistrationViaSocialMediaValidator
	 */
	public Validator getSparRegistrationViaSocialMediaValidator()
	{
		return sparRegistrationViaSocialMediaValidator;
	}
	/* Code change end here */

	@Override
	protected String getView()
	{
		return ControllerConstants.Views.Pages.Account.AccountLoginPage;
	}

	@Override
	protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (httpSessionRequestCache.getRequest(request, response) != null)
		{
			return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
		}
		return "/";
	}

	@Override
	protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId("login");
	}


	@Resource(name = "httpSessionRequestCache")
	public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache)
	{
		this.httpSessionRequestCache = accHttpSessionRequestCache;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String doLogin(@RequestHeader(value = "referer", required = false) final String referer,
			@RequestParam(value = "error", defaultValue = "false") final boolean loginError, final Model model,
			final HttpServletRequest request, final HttpServletResponse response, final HttpSession session)
			throws CMSItemNotFoundException
	{
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
				
		if(null != (String)sessionService.getAttribute("cityName")){
			model.addAttribute("cityName",sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String)sessionService.getAttribute("cityName"));
		}
		
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}
		model.addAttribute("homePageForm", homePageForm);
		if (!loginError)
		{
			storeReferer(referer, request, response);
		}
		return getDefaultLoginPage(loginError, session, model);
	}

	protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response)
	{
		if (StringUtils.isNotBlank(referer) && !StringUtils.endsWith(referer, "/login")
				&& StringUtils.contains(referer, request.getServerName()))
		{
			httpSessionRequestCache.saveRequest(request, response);
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String doRegister(@RequestHeader(value = "referer", required = false) final String referer,
			final SparRegisterForm form, final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (form.getLoginVia() != null
				&& (form.getLoginVia().equalsIgnoreCase(SparFacadesConstants.FACEBOOK) || form.getLoginVia().equalsIgnoreCase(
						SparFacadesConstants.GOOGLE)))
		{
			getSparRegistrationViaSocialMediaValidator().validate(form, bindingResult);
			if (!bindingResult.hasErrors())
			{
				/*if (form.getLoginVia().equalsIgnoreCase(SparFacadesConstants.FACEBOOK))
				{
					form.setPwd(SparFacadesConstants.FACEBOOK);
				}
				else if (form.getLoginVia().equalsIgnoreCase(SparFacadesConstants.GOOGLE))
				{
					form.setPwd(SparFacadesConstants.GOOGLE);
				}*/
				String pwd = Config.getString("social.media.auto.password", "test1234");
				form.setPwd(pwd);
				sessionService.setAttribute("loginVia", form.getLoginVia());
				String fullName = form.getFirstName();
				form.setFirstName(fullName.substring(0, fullName.indexOf(" ")));
				form.setLastName(fullName.substring(fullName.indexOf(" "), fullName.length()));
			}
		}
		else
		{
			getRegistrationValidator().validate(form, bindingResult);
		}
		model.addAttribute("pointOfServiceDatas", storeFinderFacadeInterface.getStoresList());
		model.addAttribute("deliveryTypeEnums", storeFinderFacadeInterface.getDeliveryTypes());
		final HomePageForm homePageForm = new HomePageForm();
		if (null != sessionService.getAttribute("selectedStore"))
		{
			model.addAttribute("selectedStore", sessionService.getAttribute("selectedStore"));
			homePageForm.setStore((String) sessionService.getAttribute("selectedStore"));

		}
		if(null != (String)sessionService.getAttribute("cityName"))
		{
			model.addAttribute("cityName",sessionService.getAttribute("cityName"));
			homePageForm.setDeliveryCityName((String)sessionService.getAttribute("cityName"));
		}
		if (null != sessionService.getAttribute("selectedDeliveryType"))
		{
			model.addAttribute("selectedDeliveryType", sessionService.getAttribute("selectedDeliveryType"));
			homePageForm.setDeliveryType((String) sessionService.getAttribute("selectedDeliveryType"));
			model.addAttribute("deliverySlots", storeFinderFacadeInterface.getDeliverySlots(homePageForm.getDeliveryType()));
		}

		/* Change start by sumit 
		if (StringUtils.isNotBlank(sparCustomerFacade.findPrimaryMobileNumber()))
		{
			if (!sparCustomerFacade.isOTPValidate())
			{
				GlobalMessages.addErrorMessage(model, "phone.verfity.incomplete");

			}
		}
		 Change end here */
		model.addAttribute("homePageForm", homePageForm);
		return processRegisterUserRequest(referer, form, bindingResult, model, request, response, redirectModel);
	}


	/* Code change start by sumit 

	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "mobilenum")
	public Object generateOTP(@RequestParam(value = "mobilenum") final String mobilenum) throws IOException,DuplicateUidException
	{
		this.mobilenum = mobilenum;
		otpKey = RandomStringUtils.randomNumeric(6);
		System.out.println("otpKey:::" + otpKey);
		getmGageOTPResponse(otpKey, mobilenum);

		return otpKey;
	}

	public void getmGageOTPResponse(final String otpKey, final String mobilenum) throws IOException,DuplicateUidException
	{
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		final String sendotpkey = otpKey;
		final String msg = Config.getString("mGage.user.message", "mGage.user.message");
		final String destmobnum = mobilenum;
		final String USER_AGENT = "Mozilla/5.0";
		
		sessionService.setAttribute("OTPKey", "123456");
		
		if(StringUtils.isNotBlank(mobilenum) || null != mobilenum){
			sparCustomerFacade.commitOTPData(mobilenum, new Boolean("false"));
		}
		
		 * final String mGageURL =
		 * "http://www.mgage.solutions/SendSMS/sendmsg.php?uname=SPARHPMK&pass=b$B7x$J@&send=236788&dest=917093618142&msg=hi hello"
		 * ;
		 
		final String mGageURL = "http://www.mgage.solutions/SendSMS/sendmsg.php?uname='" + uname + "'&pass='" + pass
				+ "'&send=345555&dest='" + destmobnum + "'&msg='" + msg + "'";
		

		final URL url = new URL(mGageURL);
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.connect();
		urlConnection.setRequestMethod("GET");
		urlConnection.setRequestProperty("User-Agent", USER_AGENT);
		final int resCode = urlConnection.getResponseCode();
		System.out.println(urlConnection.getContent());
		System.out.println("resCode::" + resCode);

	//	return null;
	}

	@RequestMapping(value = "/otp-generator", method = RequestMethod.GET, params = "userOTP")
	public void validateUserEnteredOTP(@RequestParam(value = "userOTP") final String userenteredotp,
			@RequestHeader(value = "referer", required = false) final String referer, final SparRegisterForm form,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectModel) throws DuplicateUidException,
			CMSItemNotFoundException
	{
		if(sessionService.getAttribute("WRONGOTP")!= null){
			sessionService.removeAttribute("WRONGOTP");
		}
	//	sessionService.setAttribute("userenteredotp", userenteredotp);

		if (sessionService.getAttribute("OTPKey").equals(userenteredotp))
		{
			sparCustomerFacade.commitOTPData(mobilenum, new Boolean("true"));
			sessionService.setAttribute("WRONGOTP", "FALSE");
		}
		else
		{
			sessionService.setAttribute("WRONGOTP", "TRUE");
			model.addAttribute("OTPMISMATCH", "You have entered wrong OTP");
			//return "";
			//return ControllerConstants.Views.Pages.Account.WrongOTPPage;
			//getRegistrationValidator().validate(form, bindingResult);
			//return getView();
			//return ControllerConstants.Views.Pages.Account.AccountRegisterPage;
		}
		//return ControllerConstants.Views.Pages.Account.AccountRegisterPage;
		//return getView();
	}


	*//**
	 * @return the otpKey
	 *//*
	public String getOtpKey()
	{
		return otpKey;
	}

	*//**
	 * @param otpKey
	 *           the otpKey to set
	 *//*
	public void setOtpKey(final String otpKey)
	{
		this.otpKey = otpKey;
	}

	 Code change end here */
}
