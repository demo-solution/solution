/**
 *
 */
package com.spar.hcl.core.payment.ipg.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl.DefaultCreditCardPaymentInfoCreateStrategy;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.spar.hcl.core.payment.ipg.strategies.SparIPGPaymentInfoCreateStrategy;


/**
 * This class is used to create the payment info
 *
 * @author rohan_c
 *
 */
public class SparIPGPaymentInfoCreateStrategyImpl extends DefaultCreditCardPaymentInfoCreateStrategy implements
		SparIPGPaymentInfoCreateStrategy
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CustomerEmailResolutionService customerEmailResolutionService;
	private UserService userService;

	/**
	 * This method is used to create IPG payment info
	 */
	@Override
	public CreditCardPaymentInfoModel createIPGCreditCardPaymentInfo(final SubscriptionInfoData subscriptionInfo,
			final PaymentInfoData paymentInfo, final CustomerModel customerModel, final boolean saveInAccount)
	{
		validateParameterNotNull(subscriptionInfo, "subscriptionInfo cannot be null");
		validateParameterNotNull(paymentInfo, "paymentInfo cannot be null");
		validateParameterNotNull(customerModel, "customerModel cannot be null");

		final CreditCardPaymentInfoModel cardPaymentInfoModel = getModelService().create(CreditCardPaymentInfoModel.class);
		cardPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		cardPaymentInfoModel.setUser(customerModel);
		cardPaymentInfoModel.setSubscriptionId(subscriptionInfo.getSubscriptionID());
		cardPaymentInfoModel.setNumber(paymentInfo.getCardAccountNumber());
		cardPaymentInfoModel.setType(CreditCardType.valueOf(paymentInfo.getCardCardType().toUpperCase()));
		cardPaymentInfoModel.setCcOwner(getCCOwner(paymentInfo, null));
		cardPaymentInfoModel.setValidFromMonth(paymentInfo.getCardStartMonth());
		cardPaymentInfoModel.setValidFromYear(paymentInfo.getCardStartYear());
		if (paymentInfo.getCardExpirationMonth().intValue() > 0)
		{
			cardPaymentInfoModel.setValidToMonth(String.valueOf(paymentInfo.getCardExpirationMonth()));
		}
		if (paymentInfo.getCardExpirationYear().intValue() > 0)
		{
			cardPaymentInfoModel.setValidToYear(String.valueOf(paymentInfo.getCardExpirationYear()));
		}
		cardPaymentInfoModel.setSubscriptionId(subscriptionInfo.getSubscriptionID());
		cardPaymentInfoModel.setSaved(saveInAccount);
		if (StringUtils.isNotBlank(paymentInfo.getCardIssueNumber()))
		{
			cardPaymentInfoModel.setIssueNumber(Integer.valueOf(paymentInfo.getCardIssueNumber()));
		}

		return cardPaymentInfoModel;
	}

	/**
	 * This method is used to IPG response in form of subscription.
	 */
	@Override
	public CreditCardPaymentInfoModel saveIPGSubscription(final CustomerModel customerModel,
			final SubscriptionInfoData subscriptionInfo, final PaymentInfoData paymentInfoData, final boolean saveInAccount)
	{
		validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");

		final CreditCardPaymentInfoModel cardPaymentInfoModel = this.createIPGCreditCardPaymentInfo(subscriptionInfo,
				paymentInfoData, customerModel, saveInAccount);

		final List<PaymentInfoModel> paymentInfoModels = new ArrayList<PaymentInfoModel>(customerModel.getPaymentInfos());
		if (!paymentInfoModels.contains(cardPaymentInfoModel))
		{
			paymentInfoModels.add(cardPaymentInfoModel);
			if (saveInAccount)
			{
				customerModel.setPaymentInfos(paymentInfoModels);
				getModelService().save(customerModel);
			}

			getModelService().save(cardPaymentInfoModel);
			getModelService().refresh(customerModel);
		}

		return cardPaymentInfoModel;
	}

	@Override
	protected String getCCOwner(final PaymentInfoData paymentInfo, final AddressModel billingAddress)
	{
		if (paymentInfo.getCardAccountHolderName() != null && !paymentInfo.getCardAccountHolderName().isEmpty())
		{
			return paymentInfo.getCardAccountHolderName();
		}
		else
		{
			return billingAddress == null ? "" : billingAddress.getFirstname() + " " + billingAddress.getLastname();
		}
	}

	@Override
	protected ModelService getModelService()
	{
		return modelService;
	}

	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Override
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	@Override
	protected CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	@Override
	@Required
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	@Override
	protected UserService getUserService()
	{
		return userService;
	}

	@Override
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
