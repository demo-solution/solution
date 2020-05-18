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

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparFeedsErrorEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * @author nileshkumar.c
 *
 */
public class SparRMSFeedEmailContext extends AbstractEmailContext<SparFeedsErrorEmailProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SparRMSFeedEmailContext.class);

	/** The Constant MAIL_TO_FEEDS_OPERATION. */
	private static final String MAIL_TO_FEEDS_OPERATION = "mail.to.feeds.operation";

	/** The Constant ATTACHMENT_FILES. */
	public static final String ATTACHMENT_FILES = "attachementFiles";
	public static final String TOEMAILS = "toEmails";

	/** The site config service. */
	@Autowired
	private SiteConfigService siteConfigService;

	/** The Constant ERROR_STRING. */
	public static final String ERROR_STRING = "errorString";
	public static final String INVENTORYFEED = "INVENTORYFEED";
	public static final String SRCFILENAME = "sourceFileName";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String MAIL_CC_RMSFEED = "mail.cc.feeds.operation";



	@Override
	public void init(final SparFeedsErrorEmailProcessModel sparFeedsErrorEmailProcessModel, final EmailPageModel emailPageModel)
	{
		LOG.debug("Inside SparRMSFeedEmailContext: init");
		super.init(sparFeedsErrorEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, "asd asd");// without this ,IllegalStateException:
		put(INVENTORYFEED, "INVENTORYFEED");// Used for marking as inventory feed
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_FEEDS_OPERATION));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_RMSFEED).split(",")));
		final String addresss = (String) get(EMAIL);
		put(TOEMAILS, Arrays.asList(addresss.split(",")));
		put(ATTACHMENT_FILES, sparFeedsErrorEmailProcessModel.getFileAttachments());
		put(SRCFILENAME, sparFeedsErrorEmailProcessModel.getSourceFileName());
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
	}

	@Override
	protected BaseSiteModel getSite(final SparFeedsErrorEmailProcessModel sparFeedsErrorEmailProcessModel)
	{
		return sparFeedsErrorEmailProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparFeedsErrorEmailProcessModel storeFrontCustomerProcessModel)
	{
		return null;
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparFeedsErrorEmailProcessModel businessProcessModel)
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
