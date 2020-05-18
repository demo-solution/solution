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

import com.spar.hcl.core.model.process.SparBlowhornFailedRequestOrderEmailProcessModel;
import com.spar.hcl.facades.constants.SparFacadesConstants;


/**
 * @author madan.d
 *
 */
public class SparBlowhornFailedRequestOrderContext extends AbstractEmailContext<SparBlowhornFailedRequestOrderEmailProcessModel>
{

	private static final String ORDER = "order";
	private static final String CUSTOMER = "customer";
	private static final String MAIL_CC_KEY = "cc_addresses";
	public static final String TOEMAILS = "toEmails";
	private static final String MAIL_TO_BLOWHORN_CARE = "mail.to.blowhorn.care";
	private static final String MAIL_CC_BLOWHORN_CARE = "mail.cc.blowhorn.care";
	
	public static final String WEBSITE_ENVIORNMENT = "website.spar.http";

	public static String ENVIORNMENT = "enviornment";

	@Autowired
	private SiteConfigService siteConfigService;

	@Override
	public void init(final SparBlowhornFailedRequestOrderEmailProcessModel sparBlowhornFailedRequestOrderEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		final Logger LOG = Logger.getLogger(SparBlowhornFailedRequestOrderContext.class);
		super.init(sparBlowhornFailedRequestOrderEmailProcessModel, emailPageModel);
		final OrderModel order = sparBlowhornFailedRequestOrderEmailProcessModel.getOrder();
		final String warehousecode = order.getOrderWarehouse().getCode();
		put(DISPLAY_NAME, siteConfigService.getProperty(MAIL_TO_BLOWHORN_CARE));
		put(EMAIL, siteConfigService.getProperty(MAIL_TO_BLOWHORN_CARE));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(MAIL_CC_BLOWHORN_CARE).split(",")));
		put(ORDER, order);
		put(CUSTOMER, getCustomer(sparBlowhornFailedRequestOrderEmailProcessModel));
		final String enviornment = siteConfigService.getProperty(WEBSITE_ENVIORNMENT);
		put(ENVIORNMENT, getEnviromentName(enviornment));
		LOG.info(order.getCode() + warehousecode);
	}

	@Override
	protected BaseSiteModel getSite(final SparBlowhornFailedRequestOrderEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparBlowhornFailedRequestOrderEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparBlowhornFailedRequestOrderEmailProcessModel businessProcessModel)
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
