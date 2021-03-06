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

import com.spar.hcl.core.model.process.SparExpiredPromoArchErrorEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * @author nileshkumar.c
 *
 */
public class SparExpiredPromoArchErrorEmailContext extends AbstractEmailContext<SparExpiredPromoArchErrorEmailProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SparExpiredPromoArchErrorEmailContext.class);

	/** The Constant MAIL_TO_FEEDS_OPERATION. */
	private static final String MAIL_TO_REFUND_REPORT = "mail.to.expired.promo.error";
	private static final String MAIL_CC_REFUND_REPORT = "mail.cc.expired.promo.error";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String EXPORT_DATE = "exportDate";
	private static final String PROMO_TYPE = "promoType";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";

	/** The site config service. */
	@Autowired
	private SiteConfigService siteConfigService;


	@Override
	public void init(final SparExpiredPromoArchErrorEmailProcessModel sparExpiredPromoArchErrorEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");

		super.init(sparExpiredPromoArchErrorEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_REFUND_REPORT));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_REFUND_REPORT));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_REFUND_REPORT).split(",")));
		put(EXPORT_DATE, format1.format(cal.getTime()));
		put(PROMO_TYPE, sparExpiredPromoArchErrorEmailProcessModel.getErrorPromoType());
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
	}

	@Override
	protected BaseSiteModel getSite(final SparExpiredPromoArchErrorEmailProcessModel sparExpiredPromoArchErrorEmailProcessModel)
	{
		return sparExpiredPromoArchErrorEmailProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparExpiredPromoArchErrorEmailProcessModel sparExpiredPromoArchErrorEmailProcessModel)
	{
		return null;
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparExpiredPromoArchErrorEmailProcessModel businessProcessModel)
	{
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
