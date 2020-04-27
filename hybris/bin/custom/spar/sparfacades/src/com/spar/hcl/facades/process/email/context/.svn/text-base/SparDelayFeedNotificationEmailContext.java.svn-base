/**
 *
 */
package com.spar.hcl.facades.process.email.context;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparDelayFeedNotificationEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * @author nikhil.kumar
 *
 */
public class SparDelayFeedNotificationEmailContext extends AbstractEmailContext<SparDelayFeedNotificationEmailProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SparDelayFeedNotificationEmailContext.class);

	/** The Constant MAIL_TO_FEEDS_OPERATION. */
	private static final String MAIL_TO_DELAY_FEED = "mail.to.delay.feed";
	private static final String MAIL_CC_DELAY_FEED = "mail.cc.delay.feed";
	private static final String MAIL_CC_KEY = "cc_addresses";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";

	private static final String TODAY_DATE = "todayDate";
	public static String ENVIORNMENT = "enviornment";

	/** The site config service. */
	@Autowired
	private SiteConfigService siteConfigService;


	@Override
	public void init(final SparDelayFeedNotificationEmailProcessModel sparDelayFeedNotificationEmail,
			final EmailPageModel emailPageModel)
	{
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		final SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");

		super.init(sparDelayFeedNotificationEmail, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_DELAY_FEED));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_DELAY_FEED));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_DELAY_FEED).split(",")));
		//	put(DAIILY_REFUND_RESULTS, sparDailyRefundReportEmailProcessModel.getDailyRefundResults());
		put(TODAY_DATE, format1.format(cal.getTime()));
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getSite(de.hybris.platform.
	 * processengine.model.BusinessProcessModel)
	 */
	@Override
	protected BaseSiteModel getSite(final SparDelayFeedNotificationEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getSite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform
	 * .processengine.model.BusinessProcessModel)
	 */
	@Override
	protected CustomerModel getCustomer(final SparDelayFeedNotificationEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getEmailLanguage(de.hybris.platform
	 * .processengine.model.BusinessProcessModel)
	 */
	@Override
	protected LanguageModel getEmailLanguage(final SparDelayFeedNotificationEmailProcessModel businessProcessModel)
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
