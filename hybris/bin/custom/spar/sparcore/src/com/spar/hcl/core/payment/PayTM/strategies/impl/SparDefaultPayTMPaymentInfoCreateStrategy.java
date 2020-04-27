/**
 *
 */
package com.spar.hcl.core.payment.PayTM.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.payment.PayTMPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.spar.hcl.core.payment.PayTM.strategies.SparPayTMPaymentInfoCreateStrategy;

/**
 * This class is used to implement Landmark Reward payment info Strategy
 *
 *
 */
public class SparDefaultPayTMPaymentInfoCreateStrategy implements SparPayTMPaymentInfoCreateStrategy
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CustomerEmailResolutionService customerEmailResolutionService;
	private UserService userService;

	@Override
	public PayTMPaymentInfoModel createPayTMPaymentInfo(final PaymentInfoData paymentInfo,
			final AddressModel billingAddress, final CustomerModel customerModel, final boolean saveInAccount)
	{
		validateParameterNotNull(paymentInfo, "paymentInfo cannot be null");
		validateParameterNotNull(customerModel, "customerModel cannot be null");

		final PayTMPaymentInfoModel payTMPaymentInfoModel = getModelService().create(PayTMPaymentInfoModel.class);
		payTMPaymentInfoModel.setBillingAddress(billingAddress);
		payTMPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		payTMPaymentInfoModel.setUser(customerModel);
		payTMPaymentInfoModel.setSaved(saveInAccount);

		return payTMPaymentInfoModel;
	}

	@Override
	public PayTMPaymentInfoModel savePayTMSubscription(final CustomerModel customerModel,
			final CustomerInfoData customerInfoData, final PaymentInfoData paymentInfoData, final boolean saveInAccount)
	{
		//validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");
		validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");

		final PayTMPaymentInfoModel payTMPaymentInfoModel = this.createPayTMPaymentInfo(paymentInfoData, null,
				customerModel, saveInAccount);
		getModelService().saveAll(payTMPaymentInfoModel);
		getModelService().refresh(customerModel);
		final List<PaymentInfoModel> paymentInfoModels = new ArrayList<PaymentInfoModel>(customerModel.getPaymentInfos());
		if (!paymentInfoModels.contains(payTMPaymentInfoModel))
		{
			paymentInfoModels.add(payTMPaymentInfoModel);
			if (saveInAccount)
			{
				customerModel.setPaymentInfos(paymentInfoModels);
				getModelService().save(customerModel);
			}
			getModelService().save(payTMPaymentInfoModel);
			getModelService().refresh(customerModel);
		}

		return payTMPaymentInfoModel;
	}

	/**
	 * Getter
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Setter
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Getter
	 *
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Setter
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Getter
	 *
	 * @return the customerEmailResolutionService
	 */
	public CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	/**
	 * Setter
	 *
	 * @param customerEmailResolutionService
	 *           the customerEmailResolutionService to set
	 */
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	/**
	 * Getter
	 *
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * Setter
	 *
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


}
