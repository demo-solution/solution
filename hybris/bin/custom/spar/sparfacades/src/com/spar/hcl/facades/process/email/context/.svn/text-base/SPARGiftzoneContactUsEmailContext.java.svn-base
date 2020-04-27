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

import com.spar.hcl.core.model.process.SparGiftzoneContactUsEmailProcessModel;


/**
 * @author p
 *
 */
public class SPARGiftzoneContactUsEmailContext extends AbstractEmailContext<SparGiftzoneContactUsEmailProcessModel>
{
	@Autowired
	private SiteConfigService siteConfigService;

	private static final String MAIL_TO_CUSTOMER_CARE = "mail.to.customer.care";
	private static final String MAIL_CC_CUSTOMER_CARE = "mail.cc.customer.care";
	private static final String CUSTOMER_NAME = "customerName";
	private static final String CUSTOMER_EMAIL = "customerEmail";
	private static final String CUSTOMER_CONTACT = "customerContact";
	private static final String COMPANY_NAME = "companyName";
	private static final String CUSTOMER_CITY = "customerCity";
	private static final String CUSTOMER_REMARKS = "customerRemarks";
	private static final String MAIL_CC_KEY = "cc_addresses";

	private static final Logger LOG = Logger.getLogger(SPARGiftzoneContactUsEmailContext.class);

	@Override
	public void init(final SparGiftzoneContactUsEmailProcessModel contactUsModel, final EmailPageModel emailPageModel)
	{
		LOG.info("Entering into init() of SPARGiftzoneContactUsEmailContext:::::::::::::");
		super.init(contactUsModel, emailPageModel);
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_CUSTOMER_CARE));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_CUSTOMER_CARE));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_CUSTOMER_CARE).split(",")));


		put(CUSTOMER_NAME, contactUsModel.getCustomerName());
		put(CUSTOMER_EMAIL, contactUsModel.getCustomerEmail());
		put(CUSTOMER_CONTACT, contactUsModel.getCustomerContact());
		put(COMPANY_NAME, contactUsModel.getCompanyName());
		put(CUSTOMER_CITY, contactUsModel.getCustomerCity());
		put(CUSTOMER_REMARKS, contactUsModel.getCustomerRemarks());
		LOG.info("Exiting from init() of SPARGiftzoneContactUsEmailContext:::::::::::::::::");
	}

	@Override
	protected BaseSiteModel getSite(final SparGiftzoneContactUsEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparGiftzoneContactUsEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparGiftzoneContactUsEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}
}