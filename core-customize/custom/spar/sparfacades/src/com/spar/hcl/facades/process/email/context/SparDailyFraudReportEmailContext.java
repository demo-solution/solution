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

import com.spar.hcl.core.model.process.SparDailyFraudReportEmailProcessModel;




/**
 * @author nileshkumar.c
 *
 */
public class SparDailyFraudReportEmailContext extends AbstractEmailContext<SparDailyFraudReportEmailProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SparDailyFraudReportEmailContext.class);

	/** The Constant MAIL_TO_FEEDS_OPERATION. */
	private static final String MAIL_TO_FRAUD_REPORT = "mail.to.fraud.report";
	private static final String DAIILY_FRAUD_RESULTS = "dailyFraudResults";
	private static final String MAIL_CC_FRAUD_REPORT = "mail.cc.fraud.report";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String EXPORT_DATE = "exportDate";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";

	/** The site config service. */
	@Autowired
	private SiteConfigService siteConfigService;


	@Override
	public void init(final SparDailyFraudReportEmailProcessModel sparFraudReportEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");

		super.init(sparFraudReportEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_FRAUD_REPORT));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_FRAUD_REPORT));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_FRAUD_REPORT).split(",")));
		put(DAIILY_FRAUD_RESULTS, sparFraudReportEmailProcessModel.getSparDailyFraud());
		put(EXPORT_DATE, format1.format(cal.getTime()));
		//final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		//put(ENVIORNMENT, getEnviromentName(enviornment));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getSite(de.hybris.platform.
	 * processengine.model.BusinessProcessModel)
	 */
	@Override
	protected BaseSiteModel getSite(final SparDailyFraudReportEmailProcessModel sparFraudReportEmailProcessModel)
	{
		// YTODO Auto-generated method stub
		return sparFraudReportEmailProcessModel.getSite();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform.
	 * processengine.model.BusinessProcessModel)
	 */
	@Override
	protected CustomerModel getCustomer(final SparDailyFraudReportEmailProcessModel businessProcessModel)
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
	protected LanguageModel getEmailLanguage(final SparDailyFraudReportEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}



}
