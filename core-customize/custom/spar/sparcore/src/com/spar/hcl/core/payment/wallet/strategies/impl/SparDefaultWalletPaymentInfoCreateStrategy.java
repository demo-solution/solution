/**
 *
 */
package com.spar.hcl.core.payment.wallet.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WalletPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.spar.hcl.core.payment.wallet.strategies.SparWalletPaymentInfoCreateStrategy;


/**
 * This class is used to implement Wallet payment info Strategy
 *
 *
 */
public class SparDefaultWalletPaymentInfoCreateStrategy implements SparWalletPaymentInfoCreateStrategy
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CustomerEmailResolutionService customerEmailResolutionService;
	private UserService userService;


	/*
	 * This method is used to create Wallet Payment INFO
	 *
	 * @see com.spar.hcl.core.payment.cashondelivery.strategies.SparDefaultWalletPaymentInfoCreateStrategy#
	 * de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData,
	 * de.hybris.platform.core.model.user.AddressModel, de.hybris.platform.core.model.user.CustomerModel, boolean)
	 */
	@Override
	public WalletPaymentInfoModel createCreditCardPaymentInfo(final PaymentInfoData paymentInfo,
			final AddressModel billingAddress, final CustomerModel customerModel, final boolean saveInAccount)
	{
		validateParameterNotNull(paymentInfo, "paymentInfo cannot be null");
		validateParameterNotNull(customerModel, "customerModel cannot be null");

		final WalletPaymentInfoModel walletPaymentInfoModel = getModelService().create(WalletPaymentInfoModel.class);
		walletPaymentInfoModel.setBillingAddress(billingAddress);
		walletPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		walletPaymentInfoModel.setUser(customerModel);
		walletPaymentInfoModel.setSaved(saveInAccount);

		return walletPaymentInfoModel;
	}

	/*
	 * This method is used to save Wallet Payment Info
	 *
	 * @see
	 * com.spar.hcl.core.payment.cashondelivery.strategies.SparDefaultWalletPaymentInfoCreateStrategy#saveSubscription
	 * (de.hybris.platform.core.model.user.CustomerModel,
	 * de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData,
	 * de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData, boolean)
	 */
	@Override
	public WalletPaymentInfoModel saveWalletSubscription(final CustomerModel customerModel,
			final CustomerInfoData customerInfoData, final PaymentInfoData paymentInfoData, final boolean saveInAccount)
	{
		//validateParameterNotNull(customerInfoData, "customerInfoData cannot be null");
		validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");

		final WalletPaymentInfoModel walletPaymentInfoModel = this.createCreditCardPaymentInfo(paymentInfoData, null,
				customerModel, saveInAccount);
		getModelService().saveAll(walletPaymentInfoModel);
		getModelService().refresh(customerModel);
		final List<PaymentInfoModel> paymentInfoModels = new ArrayList<PaymentInfoModel>(customerModel.getPaymentInfos());
		customerModel.getTotalWalletAmount();
		if (!paymentInfoModels.contains(walletPaymentInfoModel))
		{
			paymentInfoModels.add(walletPaymentInfoModel);
			if (saveInAccount)
			{
				customerModel.setPaymentInfos(paymentInfoModels);
				getModelService().save(customerModel);
			}
			getModelService().save(walletPaymentInfoModel);
			getModelService().refresh(customerModel);
		}

		return walletPaymentInfoModel;
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
