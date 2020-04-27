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

import com.spar.hcl.core.model.process.SparNeedHelpEmailProcessModel;


/**
 * @author tanveers
 *
 */
public class SPARNeedHelpEmailContext extends AbstractEmailContext<SparNeedHelpEmailProcessModel>
{
	@Autowired
	private SiteConfigService siteConfigService;

	private static final String MAIL_TO_CUSTOMER_CARE = "mail.to.customer.care";
	private static final String MAIL_CC_CUSTOMER_CARE = "mail.cc.customer.care";
	public static final String SELECTED_HELP_TYPE = "helpType";
	private static final String CUSTOMER_NAME = "customerName";
	private static final String CUSTOMER_NUMBER = "customerNumber";
	public static final String DESIRED_TIME = "desiredTime";
	public static final String CUSTOMER_REMARK = "customerRemark";
	private static final String MAIL_CC_KEY = "cc_addresses";

	private static final Logger LOG = Logger.getLogger(SPARNeedHelpEmailContext.class);

	@Override
	public void init(final SparNeedHelpEmailProcessModel sparNeedHelpEmailProcessModel, final EmailPageModel emailPageModel)
	{
		LOG.info("Entering into init() of SPARNeedHelpEmailContext:::::::::::::");
		super.init(sparNeedHelpEmailProcessModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_CUSTOMER_CARE));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_CUSTOMER_CARE));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_CUSTOMER_CARE).split(",")));
		put(SELECTED_HELP_TYPE, sparNeedHelpEmailProcessModel.getHelpType());
		put(CUSTOMER_NAME, sparNeedHelpEmailProcessModel.getCustomerName());
		put(CUSTOMER_NUMBER, sparNeedHelpEmailProcessModel.getCustomerNumber());
		put(DESIRED_TIME, sparNeedHelpEmailProcessModel.getDesiredTime());
		put(CUSTOMER_REMARK, sparNeedHelpEmailProcessModel.getCustomerRemark());
		LOG.info("Exiting from init() of SPARNeedHelpEmailContext:::::::::::::::::");
	}

	@Override
	protected BaseSiteModel getSite(final SparNeedHelpEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparNeedHelpEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparNeedHelpEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}
}