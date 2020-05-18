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

import com.spar.hcl.core.model.process.SparPLMSManualpickOrderEmailProcessModel;


/**
 * @author madan.d
 *
 */
public class SPARPLMSManualPickOrderContext extends AbstractEmailContext<SparPLMSManualpickOrderEmailProcessModel>
{
	private static final String MAIL_TO_PLMS_MANUALPICKORDER = "_ManualPick";
	private static final String MAIL_CC_PLMS_MANUALPICKORDER = "_ManualPickCC";
	private static final String ORDER = "order";
	private static final String CUSTOMER = "customer";
	private static final String MAIL_CC_KEY = "cc_addresses";
	public static final String TOEMAILS = "toEmails";

	@Autowired
	private SiteConfigService siteConfigService;

	@Override
	public void init(final SparPLMSManualpickOrderEmailProcessModel sparPLMSManualpickOrderEmailProcessModel,
			final EmailPageModel emailPageModel)
	{
		final Logger LOG = Logger.getLogger(SPARPLMSManualPickOrderContext.class);
		super.init(sparPLMSManualpickOrderEmailProcessModel, emailPageModel);
		final OrderModel order = sparPLMSManualpickOrderEmailProcessModel.getOrder();
		final String warehousecode = order.getOrderWarehouse().getCode();
		put(DISPLAY_NAME, siteConfigService.getProperty(warehousecode + MAIL_TO_PLMS_MANUALPICKORDER));
		put(EMAIL, siteConfigService.getProperty(warehousecode + MAIL_TO_PLMS_MANUALPICKORDER));
		final String addresss = (String) get(EMAIL);
		put(TOEMAILS, Arrays.asList(addresss.split(",")));
		put(MAIL_CC_KEY, Arrays.asList(siteConfigService.getProperty(warehousecode + MAIL_CC_PLMS_MANUALPICKORDER).split(",")));
		put(ORDER, order);
		put(CUSTOMER, getCustomer(sparPLMSManualpickOrderEmailProcessModel));
		LOG.info(order.getCode() + warehousecode);
	}

	@Override
	protected BaseSiteModel getSite(final SparPLMSManualpickOrderEmailProcessModel businessProcessModel)
	{
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final SparPLMSManualpickOrderEmailProcessModel businessProcessModel)
	{
		return (CustomerModel) businessProcessModel.getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final SparPLMSManualpickOrderEmailProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
