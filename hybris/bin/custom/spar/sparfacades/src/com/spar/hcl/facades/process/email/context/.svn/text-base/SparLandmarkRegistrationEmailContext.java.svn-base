/**
 *
 */
package com.spar.hcl.facades.process.email.context;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparLandmarkRegistrationEmailProcessModel;
import com.spar.hcl.core.model.process.SparNeedHelpEmailProcessModel;
import com.spar.hcl.core.model.process.SparPLMSManualpickOrderEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;
import com.spar.hcl.facades.landmarkreward.LRUserEnrollMemberData;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.impl.SparDefaultLandmarkRewardFacade;


/**
 * @author ravindra.kr
 *
 */
public class SparLandmarkRegistrationEmailContext extends AbstractEmailContext<SparLandmarkRegistrationEmailProcessModel>
{
	private static final Logger LOGGER = Logger.getLogger(SparDefaultLandmarkRewardFacade.class);
	private static final String MAIL_TO_LANDMARK_CUSTOMER_CARE = "mail.to.landmark.customer.care";
	private static final String LANDMARK_ENROLL_MEMBER_DATA = "landmarkEnrollMemberData";
	private static final String MAIL_CC_LANDMARK_CUSTOMER_CARE = "mail.cc.landmark.customer.care";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String CUSTOMER = "customer";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";
	
	@Autowired
	private SiteConfigService siteConfigService;
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;

	@Override
	public void init(final SparLandmarkRegistrationEmailProcessModel landmarkRegistrationEmailProcessModel, final EmailPageModel emailPageModel)
	{
		LOGGER.info("Entering into init() of SparLandmarkRegistrationEmailContext:::::::::::::");
		super.init(landmarkRegistrationEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_LANDMARK_CUSTOMER_CARE));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_LANDMARK_CUSTOMER_CARE));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_LANDMARK_CUSTOMER_CARE).split(",")));
		CustomerModel customerModel = getCustomer(landmarkRegistrationEmailProcessModel);
		put(CUSTOMER, customerModel);
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		LRUserEnrollMemberData enrollMemberData = sparLandmarkRewardFacade.populateLRCustomerData(customerData);
		put(LANDMARK_ENROLL_MEMBER_DATA, enrollMemberData);
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
		LOGGER.info("Exiting from init() of SparLandmarkRegistrationEmailContext:::::::::::::::::");
	}

	@Override
	protected BaseSiteModel getSite(final SparLandmarkRegistrationEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparLandmarkRegistrationEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}
	
	@Override
	protected LanguageModel getEmailLanguage(final SparLandmarkRegistrationEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}
	
	String getEnviromentName(final String enviornment)
	{

		if (enviornment.contains("uat"))
		{
			return SparFacadesConstants.UATENV;
		}
		else if (enviornment.contains("dev"))
		{
			return SparFacadesConstants.DEVENV;
		}
		else if (enviornment.contains("www") || enviornment.contains("WWW"))
		{
			return SparFacadesConstants.PRODENV;
		}
		else if (enviornment.contains("test"))
		{
			return SparFacadesConstants.TESTENV;

		}
		else if (enviornment.contains("newdev"))
		{
			return SparFacadesConstants.NEW_DEV_ENV;

		}
		else if (enviornment.contains("stage"))
		{
			return SparFacadesConstants.STAGE;

		}
		else
		{
			return SparFacadesConstants.LOCALENV;
		}
}
}