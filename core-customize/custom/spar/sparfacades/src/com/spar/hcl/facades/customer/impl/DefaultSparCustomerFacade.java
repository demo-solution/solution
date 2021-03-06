/**
 * @author kumari-p
 */
package com.spar.hcl.facades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import com.spar.hcl.core.customeraccount.service.impl.SparDefaultCustomerAccountService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.spar.hcl.deliveryslot.model.WalletDetailsModel;
import com.spar.hcl.facades.customer.SparCustomerFacade;
import com.spar.hcl.facades.populators.SparCustomerPopulator;
import com.spar.hcl.facades.wallet.data.WalletDetailsData;


/**
 * Default implementation for the {@link SparCustomerFacade}.
 */
public class DefaultSparCustomerFacade extends DefaultCustomerFacade implements SparCustomerFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultSparCustomerFacade.class);

	private ModelService modelService;
	private UserService userService;
	private CustomerNameStrategy customerNameStrategy;
	private CommonI18NService commonI18NService;
	private CustomerAccountService customerAccountService;
	private long tokenValiditySeconds;
	@Autowired
	private PriceDataFactory priceDataFactory;
	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "sparCustomerPopulator")
	private SparCustomerPopulator sparCustomerPopulator;
	@Resource(name = "secureTokenService")
	private SecureTokenService secureTokenService;
	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Autowired
	private SparDefaultCustomerAccountService sparDefaultCustomerAccountService;

	@Override
	public void register(final RegisterData registerData) throws DuplicateUidException
	{
		validateParameterNotNullStandardMessage("registerData", registerData);
		Assert.hasText(registerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(registerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(registerData.getLogin(), "The field [Login] cannot be empty");

		final CustomerModel newCustomer = getModelService().create(CustomerModel.class);
		newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
		if (StringUtils.isNotBlank(registerData.getFirstName()) && StringUtils.isNotBlank(registerData.getLastName()))
		{
			newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
			newCustomer.setCustTitle(registerData.getTitleCode());
			newCustomer.setFirstName(registerData.getFirstName());
			newCustomer.setLastName(registerData.getLastName());

		}
		if(null != registerData.getTitleCode())
		{
			final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
			newCustomer.setTitle(title);
		}
		newCustomer.setWhetherEmployee(registerData.getWhetherEmployee());
		newCustomer.setEmployeeCode(registerData.getEmployeeCode());
		newCustomer.setDateOfBirth(registerData.getDateOfBirth());
		newCustomer.setDateOfJoining(registerData.getDateOfJoining());
		newCustomer.setWhetherSubscribedToLandmark(registerData.getWhetherSubscribedToLandmark());

		setUidForRegister(registerData, newCustomer);

		newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());

		//Social Media Integration change start
		newCustomer.setLoginVia(registerData.getLoginVia());
		
		
		if(null != registerData.getLoginVia())
		{
			sparDefaultCustomerAccountService.register(newCustomer, registerData.getPassword());
		}
		else
		{
			getCustomerAccountService().register(newCustomer, registerData.getPassword());
		}
		//getCustomerAccountService().register(newCustomer, registerData.getPassword());
		//Social Media Integration change end
	}


	@Override
	protected void setUidForRegister(final RegisterData registerData, final CustomerModel customer)
	{
		customer.setUid(registerData.getLogin().toLowerCase());
		customer.setOriginalUid(registerData.getLogin());
	}


	@Override
	protected UserService getUserService()
	{
		return userService;
	}


	@Override
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	@Override
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}


	@Override
	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}


	@Override
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	@Override
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	@Override
	protected ModelService getModelService()
	{
		return modelService;
	}


	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}




	@Override
	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}


	@Override
	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}


	/**
	 * This method is used to save OTP status for Registration and Checkout flow.
	 *
	 * @param mobilenumber
	 * @param isOTPValidated
	 * @param isRegistrationOTPVerified
	 * @param isCheckoutOTPVerified
	 * @throws DuplicateUidException
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void commitOTPData(final String mobilenumber, final Boolean isOTPValidated, final Boolean isRegistrationOTPVerified,
			final Boolean isCheckoutOTPVerified) throws DuplicateUidException
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		customer.setIsOTPValidate(isOTPValidated);
		if (StringUtils.isEmpty(customer.getCustPrimaryMobNumber()))
		{
			customer.setCustPrimaryMobNumber(mobilenumber);
		}
		customer.setIsRegistrationOTPVerified(isRegistrationOTPVerified);
		customer.setIsCheckoutOTPVerified(isCheckoutOTPVerified);
		final String titleCode = customer.getTitle() == null ? "" : customer.getTitle().getCode();
		getCustomerAccountService().updateProfile(customer, titleCode, customer.getName(), customer.getLdaplogin());
	}

	/**
	 * This method is used to get the status for OTP in Resiatration flow
	 */
	@Override
	public boolean findRegistrationOTPStatus()
	{
		final CustomerModel customer = getCurrentSessionCustomer();

		if (null != customer.getIsRegistrationOTPVerified())
		{

			if (Boolean.TRUE.equals(customer.getIsRegistrationOTPVerified()))
			{
				return true;
			}
			else
			{
				return false;
			}

		}

		return false;
	}


	/**
	 * This method is used to get the status for OTP in Checkout flow
	 */
	@Override
	public boolean findCheckoutOTPStatus()
	{
		final CustomerModel customer = getCurrentSessionCustomer();

		if (null != customer.getIsCheckoutOTPVerified())
		{

			if (Boolean.TRUE.equals(customer.getIsCheckoutOTPVerified()))
			{
				return true;
			}
			else
			{
				return false;
			}

		}

		return false;
	}


	/**
	 * This method is used to get whether OTP is verified ,this is used to do the validations for OTP in checkout flow
	 */
	@Override
	public boolean isOTPValidate()
	{
		boolean otpFlag = false;
		final CustomerModel customer = getCurrentSessionCustomer();
		if (customer.getIsOTPValidate() != null)
		{
			if (customer.getIsOTPValidate().equals(new Boolean(true)))
			{
				otpFlag = true;
			}
		}
		return otpFlag;
	}


	/**
	 * This method is used to get the primary mobile number for the current user
	 */
	@Override
	public String findPrimaryMobileNumber()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		return customer.getCustPrimaryMobNumber();
	}


	/**
	 * This method is used to get the current user email id to filter the user in customer location
	 */
	@Override
	public String findUserEmailId()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		return customer.getOriginalUid();
	}

	@Override
	public String findFirstName()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		return customer.getFirstName();
	}

	@Override
	public String findLastName()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		return customer.getLastName();

	}

	@Override
	public String findCustomerTitle()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		return customer.getCustTitle();
	}

	/**
	 * This method is used to get the set wallet for customer
	 */
	@Override
	public CustomerData getCurrentCustomerWallet()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		final CustomerData customerData = getCustomerConverter().convert(customer);
		if (null != customer.getTotalWalletAmount() && customer.getTotalWalletAmount().doubleValue() > 0.0)
		{
			final PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY,
					BigDecimal.valueOf(customer.getTotalWalletAmount().doubleValue()), commonI18NService.getCurrentCurrency());
			customerData.setPaidByWallet(paidByWallet);
		}
		else
		{
			final PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(0.0d),
					commonI18NService.getCurrentCurrency());
			customerData.setPaidByWallet(paidByWallet);
		}
		super.getCurrentCustomer();
		return customerData;
	}

	/**
	 * This method is used to get the set wallet details for customer
	 */
	@Override
	public CustomerData getCustomerWalletDetails()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		CustomerData customerData = getCustomerConverter().convert(customer);
		customerData = getCurrentCustomerWallet();
		final List<WalletDetailsData> walletDetailsList = new ArrayList<WalletDetailsData>();
		for (final WalletDetailsModel walletDetails : customer.getWalletDetails())
		{
			final WalletDetailsData walletDetailsData = new WalletDetailsData();
			if (null != walletDetails.getCustomer())
			{
				walletDetailsData.setCustomer(walletDetails.getCustomer().getUid());
			}
			walletDetailsData.setRefundDate(walletDetails.getRefundDate());
			final PriceData paidByWallet = priceDataFactory.create(PriceDataType.BUY,
					BigDecimal.valueOf(walletDetails.getWalletAmount().doubleValue()), commonI18NService.getCurrentCurrency());

			walletDetailsData.setWalletAmount(paidByWallet);
			if (null != walletDetails.getWalletFundingReason())
			{
				walletDetailsData
						.setWalletFundingReason(enumerationService.getEnumerationName(walletDetails.getWalletFundingReason()));
			}
			if (null != walletDetails.getWalletOrder())
			{
				walletDetailsData.setWalletOrder(walletDetails.getWalletOrder().getCode());
			}
			walletDetailsList.add(walletDetailsData);
		}
		customerData.setWalletDetails(walletDetailsList);
		return customerData;
	}

	public boolean isdateofBirthset()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		if (customer.getDateOfBirth() == null || customer.getDateOfBirth().toString().equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void updateCustomerProfile(final CustomerData customerData) throws DuplicateUidException
	{
		final String name = getCustomerNameStrategy().getName(customerData.getFirstName(), customerData.getLastName());
		final CustomerModel customer = getCurrentSessionCustomer();
		customer.setOriginalUid(customerData.getDisplayUid());
		customer.setDateOfBirth(customerData.getDateOfBirth());
		if (null != customerData.getWhetherEmployee() && customerData.getWhetherEmployee().booleanValue())
		{
			customer.setWhetherEmployee(customerData.getWhetherEmployee());
			customer.setEmployeeCode(customerData.getEmployeeCode());
			customer.setDateOfJoining(customerData.getDateOfJoining());
		}
		else
		{
			customer.setWhetherEmployee(customerData.getWhetherEmployee());
		}
		getCustomerAccountService().updateProfile(customer, customerData.getTitleCode(), name, customerData.getUid());

	}
	@Override
	public boolean isCustomerEmployee()
	{
		final CustomerModel customer = getCurrentSessionCustomer();
		if (BooleanUtils.isFalse(customer.getWhetherEmployee()))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void getmGageOTPResponse(final String sysGenOTPKey, final String mobilenum) throws IOException,
	DuplicateUidException
	{
		//this.sysGenOTPKey = sysGenOTPKey;
		URL url = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		final String msg = Config.getString("mGage.user.message", "mGage.user.message");
		final String OTP = sysGenOTPKey;
		LOG.info("OTP generated " + OTP);
		//	model.addAttribute("otpgen", sessionService.getAttribute("sysGenOTPKey"));


		final String OTPmsg = msg.replaceAll("XXXXXX", OTP);

		final String destmobnum = mobilenum;
		final String USER_AGENT = "Mozilla/5.0";
		if (StringUtils.isNotBlank(mobilenum) || null != mobilenum)
		{
			final AddressModel newAddress = new AddressModel();
			newAddress.setPhone1(mobilenum);
			newAddress.setIsOTPValidate(Boolean.FALSE);
		}
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");
		/*
		 * final String mGageURL = mgageURL + uname + "&pass=" + pass + "&send=345555&dest=" + destmobnum + "&msg=" +
		 * URLEncoder.encode(OTPmsg, "UTF-8");
		 */


		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(OTPmsg, "UTF-8");

		LOG.info(" OTP Message *********** " + mGageURL);
		if (sessionService.getAttribute("OTPKey") != null)
		{
			sessionService.removeAttribute("OTPKey");
			sessionService.setAttribute("OTPKey", sysGenOTPKey);
			url = new URL(mGageURL);
		}
		else
		{
			sessionService.setAttribute("OTPKey", sysGenOTPKey);
			url = new URL(mGageURL);
		}

		sendSMS(url);
	}
	
	
	
	@Override
	public void sendSMSToUser(final String mobilenum, final String msg) throws IOException, DuplicateUidException
	{
		URL url = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		//final String msg = Config.getString("mGage.user.changePassword", "mGage.user.changePassword");
		//final String msg = Config.getString("mGage.user.register.message", "mGage.user.register.message");

		final String destmobnum = mobilenum;
		if (StringUtils.isNotBlank(mobilenum) || null != mobilenum)
		{
			final AddressModel newAddress = new AddressModel();
			newAddress.setPhone1(mobilenum);
			newAddress.setIsOTPValidate(Boolean.FALSE);
		}
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");

		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(msg, "UTF-8");

		LOG.info("Request Message *********** " + mGageURL);

		url = new URL(mGageURL);

		sendSMS(url);
	}
	
	@Override
	public void sendForgetPasswordSMSToUser(final String mobilenum, final String email, String msg) throws IOException,
			DuplicateUidException
	{
		URL url = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");

		final CustomerModel customerModel = getUserService().getUserForUID(email.toLowerCase(), CustomerModel.class);
		/*final long timeStamp = new Date().getTime();
		final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
		final String token = secureTokenService.encryptData(data);
		customerModel.setToken(token);
		modelService.save(customerModel);*/
		final String secureURL = siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getBaseSiteForUID("spar"), true,
				"/login/pw/changesms", "token=" + customerModel.getUid());

		msg = msg.replace("https://www.sparindia.com/en/my-account/update-password", secureURL);
		LOG.info(" secureURL - Forget Password Message *********** " + msg);

		final String destmobnum = mobilenum;
		if (StringUtils.isNotBlank(mobilenum) || null != mobilenum)
		{
			final AddressModel newAddress = new AddressModel();
			newAddress.setPhone1(mobilenum);
		}
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");

		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(msg, "UTF-8");

		LOG.info("Request Message *********** " + mGageURL);

		url = new URL(mGageURL);

		sendSMS(url);
	}
	
	



	private void sendSMS(final URL url)
	{
		try
		{
			final String USER_AGENT = "Mozilla/5.0";
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			//urlConnection.connect();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			final int resCode = urlConnection.getResponseCode();
			final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			final StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			LOG.info("response messege for Request" + response);
			LOG.info(" resCode Message *********** " + resCode + "Message" + urlConnection.getResponseMessage());
			urlConnection.disconnect();
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
			LOG.error("Connection Failed", ex);
		}
	}
	
	/**
	 * @return the tokenValiditySeconds
	 */
	public long getTokenValiditySeconds()
	{
		return tokenValiditySeconds;
	}


	/**
	 * @param tokenValiditySeconds
	 *           the tokenValiditySeconds to set
	 */
	public void setTokenValiditySeconds(final long tokenValiditySeconds)
	{
		this.tokenValiditySeconds = tokenValiditySeconds;
	}


	@Override
	public CustomerData getUserForUID(final String uid)
	{
		final CustomerData customerData = new CustomerData();
		final CustomerModel customerModel = getUserService().getUserForUID(uid.toLowerCase(), CustomerModel.class);
		sparCustomerPopulator.populate(customerModel, customerData);
		return customerData;
	}

	@Override
	public void validateEmailAlreadyRegistered(final String email) throws DuplicateUidException
	{
		final boolean flag = getUserService().isUserExisting(email.toLowerCase());
		if (flag == true)
		{
			throw new DuplicateUidException();
		}
	}
}