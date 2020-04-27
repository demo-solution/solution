package com.spar.hcl.facades.process.email.context;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.spar.hcl.core.model.process.SparPaymentAmountEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * @author ravindra.kr
 *
 */
public class SparPaymentAmountEmailContext extends AbstractEmailContext<SparPaymentAmountEmailProcessModel>
{
	private static final String MAIL_TO_PAYMENTAMOUNT_REPORT = "mail.to.paymentamount.report";
	private static final String ORDER = "order";
	private static final String CUSTOMER = "customer";
	private static final String MAIL_CC_PAYMENTAMOUNT_REPORT  = "mail.cc.paymentamount.report";
	private static final String MAIL_CC_KEY = "cc_addresses";
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";
	public static String ENVIORNMENT = "enviornment";

	@Autowired
	private SiteConfigService siteConfigService;

	@Override
	public void init(final SparPaymentAmountEmailProcessModel sparPaymentAmountEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		final Logger LOG = Logger.getLogger(SparPaymentAmountEmailContext.class);
		super.init(sparPaymentAmountEmailProcessModel, emailPageModel);
		final OrderModel order = sparPaymentAmountEmailProcessModel.getOrder();
		final String paymentCaptureValue = order.getPaymentCaptureValue().toString();
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_PAYMENTAMOUNT_REPORT));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_PAYMENTAMOUNT_REPORT));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_PAYMENTAMOUNT_REPORT).split(",")));
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ORDER, order);
		put(CUSTOMER, getCustomer(sparPaymentAmountEmailProcessModel));
		put(ENVIORNMENT, getEnviromentName(enviornment));
		LOG.info("paymentCaptureValue : " + order.getCode() + paymentCaptureValue);
	}

	@Override
	protected BaseSiteModel getSite(final SparPaymentAmountEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparPaymentAmountEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparPaymentAmountEmailProcessModel businessProcessModel)
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
