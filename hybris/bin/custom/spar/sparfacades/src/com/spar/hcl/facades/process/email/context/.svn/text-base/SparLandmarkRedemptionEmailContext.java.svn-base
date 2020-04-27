/**
 *
 */
package com.spar.hcl.facades.process.email.context;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparLandmarkRedemptionEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;
import com.spar.hcl.facades.landmarkreward.LRInputParamForRedemptionFromLMSAPI;
import com.spar.hcl.facades.landmarkreward.SparLandmarkRewardFacade;
import com.spar.hcl.facades.landmarkreward.SparValidateLRAmountResultData;
/**
 * @author ravindra.kr
 *
 */
public class SparLandmarkRedemptionEmailContext extends AbstractEmailContext<SparLandmarkRedemptionEmailProcessModel>
{
	private static final Logger LOGGER = Logger.getLogger(SparLandmarkRedemptionEmailContext.class);
	private static final String MAIL_TO_LANDMARK_CUSTOMER_CARE_REDEMPTIOM = "mail.to.landmark.customer.care.redemption";
	private static final String PARAM_REDEMPTION_DATA = "paramForRedemption";
	private static final String MAIL_CC_LANDMARK_CUSTOMER_CARE_REDEMPTIOM = "mail.cc.landmark.customer.care.redemption";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String CUSTOMER = "customer";
	private static final String CART = "cart";
	private static final String STORE_ID = "storeId";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";
	
	@Autowired
	private SiteConfigService siteConfigService;
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "sparLandmarkRewardFacade")
	private SparLandmarkRewardFacade sparLandmarkRewardFacade;
	@Resource(name = "sessionService")
	SessionService sessionService;

	@Override
	public void init(final SparLandmarkRedemptionEmailProcessModel landmarkRedemptionEmailProcessModel, final EmailPageModel emailPageModel)
	{
		LOGGER.info("Entering into init() of SparLandmarkRedemptionEmailContext:::::::::::::");
		super.init(landmarkRedemptionEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_LANDMARK_CUSTOMER_CARE_REDEMPTIOM));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_LANDMARK_CUSTOMER_CARE_REDEMPTIOM));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_LANDMARK_CUSTOMER_CARE_REDEMPTIOM).split(",")));
		CustomerModel customerModel = getCustomer(landmarkRedemptionEmailProcessModel);
		put(CUSTOMER, customerModel);
		CartModel cartModel = getCartForRedemptionEmail(landmarkRedemptionEmailProcessModel);
		put(CART, cartModel);
		put(STORE_ID, cartModel.getOrderWarehouse().getCode());
		//final SparValidateLRAmountResultData calculateLRAmountResultData = (SparValidateLRAmountResultData) sessionService
		//		.getAttribute("calculateLRAmountResultData");
		//LRInputParamForRedemptionFromLMSAPI paramForRedemption = sparLandmarkRewardFacade.populateDataForRedemptionFromLMSAPI(calculateLRAmountResultData);
		//put(PARAM_REDEMPTION_DATA, paramForRedemption);
	/*	CartModel cartmodel = getCartForRedemptionEmail(landmarkRedemptionEmailProcessModel);
		put(CART, cartmodel);
		if(null != cartmodel.getOrderWarehouse())
		{
			put(WAREHOUSE, cartmodel.getOrderWarehouse().getCode());
		}
		else
		{
			put(WAREHOUSE, "");
		}
		put(WAREHOUSE, cartmodel.getOrderWarehouse().getCode());*/
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
		LOGGER.info("Exiting from init() of SparLandmarkRedemptionEmailContext:::::::::::::::::");
	}

	@Override
	protected BaseSiteModel getSite(final SparLandmarkRedemptionEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparLandmarkRedemptionEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}
	
	protected CartModel getCartForRedemptionEmail(final SparLandmarkRedemptionEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getCart();
	}
	
	@Override
	protected LanguageModel getEmailLanguage(final SparLandmarkRedemptionEmailProcessModel businessProcessModel)
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