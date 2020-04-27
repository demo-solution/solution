/**
 * @author kumarchoubeys
 *
 */


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

import com.spar.hcl.core.model.process.RMSCorruptedPriceFeedDataProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * Velocity context for a customer email.
 */
public class RMSCorruptedPriceFeedDataContext extends AbstractEmailContext<RMSCorruptedPriceFeedDataProcessModel>
{
	private static final Logger LOG = Logger.getLogger(RMSCorruptedPriceFeedDataContext.class);

	/** The Constant MAIL_TO_FEEDS_OPERATION. */
	private static final String MAIL_TO_CORRUPTED_PRICE_FEED = "mail.to.corrupted.price.feed";
	private static final String MAIL_CC_CORRUPTED_PRICE_FEED = "mail.cc.corrupted.price.feed";
	private static final String MAIL_CC_KEY = "cc_addresses";
	private static final String MAIL_ATTACHED_FILE = "attachedFile";
	public static final String ATTACHMENT_FILES = "attachementFiles";
	private static final String RMS_CORRUPTED_PRICE_DATA = "corruptedPriceFeedData";
	private static final String SRCFILENAME = "sourceFileName";
	public static final String TOEMAILS = "toEmails";
	public static final String CORRUPTEDPRICEFEED = "CORRUPTEDPRICEFEED";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static final String TOTAL_COUNT = "totalCount";
	public static final String PROCESSED_COUNT = "processedCount";
	public static final String CORRUPT_COUNT = "corruptCount";
	public static String ENVIORNMENT = "enviornment";

	/** The site config service. */
	@Autowired
	private SiteConfigService siteConfigService;


	@Override
	public void init(final RMSCorruptedPriceFeedDataProcessModel rmsCorruptedPriceFeedDataProcessModel,
			final EmailPageModel emailPageModel)
	{
		LOG.info("Entering into init() of RMSCorruptedPriceFeedDataContext:::::::::::::: ");
		super.init(rmsCorruptedPriceFeedDataProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_CORRUPTED_PRICE_FEED));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_CORRUPTED_PRICE_FEED));
		put(CORRUPTEDPRICEFEED, "CORRUPTEDPRICEFEED");
		final String addresss = (String) get(EMAIL);
		put(TOEMAILS, Arrays.asList(addresss.split(",")));
		put(ATTACHMENT_FILES, rmsCorruptedPriceFeedDataProcessModel.getFileAttachments());
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_CORRUPTED_PRICE_FEED).split(",")));
		put(RMS_CORRUPTED_PRICE_DATA, rmsCorruptedPriceFeedDataProcessModel.getErrorString());
		put(SRCFILENAME, rmsCorruptedPriceFeedDataProcessModel.getSourceFileName());
		put(TOTAL_COUNT, String.valueOf(rmsCorruptedPriceFeedDataProcessModel.getTotalRecords().intValue()));
		put(PROCESSED_COUNT,
				String.valueOf(rmsCorruptedPriceFeedDataProcessModel.getTotalRecords().intValue()
						- rmsCorruptedPriceFeedDataProcessModel.getErrorString().size()));
		put(CORRUPT_COUNT, String.valueOf(rmsCorruptedPriceFeedDataProcessModel.getErrorString().size()));
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));

		LOG.info("Exiting from init() of RMSCorruptedPriceFeedDataContext:::::::::::::::::");
	}

	@Override
	protected BaseSiteModel getSite(final RMSCorruptedPriceFeedDataProcessModel rmsCorruptedPriceFeedDataProcessModel)
	{
		return rmsCorruptedPriceFeedDataProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final RMSCorruptedPriceFeedDataProcessModel rmsCorruptedPriceFeedDataProcessModel)
	{
		return null;
	}

	@Override
	protected LanguageModel getEmailLanguage(final RMSCorruptedPriceFeedDataProcessModel rmsCorruptedPriceFeedDataProcessModel)
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