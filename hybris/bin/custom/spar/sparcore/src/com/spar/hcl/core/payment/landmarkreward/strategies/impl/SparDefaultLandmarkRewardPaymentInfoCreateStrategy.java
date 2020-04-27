/**
 *
 */
package com.spar.hcl.core.payment.landmarkreward.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.payment.LandmarkPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.spar.hcl.core.payment.landmarkreward.strategies.SparLandmarkRewardPaymentInfoCreateStrategy;


/**
 * This class is used to implement Landmark Reward payment info Strategy
 *
 *
 */
public class SparDefaultLandmarkRewardPaymentInfoCreateStrategy implements SparLandmarkRewardPaymentInfoCreateStrategy
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private CustomerEmailResolutionService customerEmailResolutionService;
	private UserService userService;

	@Override
	public LandmarkPaymentInfoModel createCreditCardPaymentInfo(final PaymentInfoData paymentInfo,
			final AddressModel billingAddress, final CustomerModel customerModel, final boolean saveInAccount)
	{
		validateParameterNotNull(paymentInfo, "paymentInfo cannot be null");
		validateParameterNotNull(customerModel, "customerModel cannot be null");

		final LandmarkPaymentInfoModel landmarkPaymentInfoModel = getModelService().create(LandmarkPaymentInfoModel.class);
		landmarkPaymentInfoModel.setBillingAddress(billingAddress);
		landmarkPaymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		landmarkPaymentInfoModel.setUser(customerModel);
		landmarkPaymentInfoModel.setSaved(saveInAccount);

		return landmarkPaymentInfoModel;
	}

	@Override
	public LandmarkPaymentInfoModel savelandmarkRewardSubscription(final CustomerModel customerModel,
			final CustomerInfoData customerInfoData, final PaymentInfoData paymentInfoData, final boolean saveInAccount)
	{
		//validateParameterNotNull(customerInfoData, "customerInfoData cannot be null");
		validateParameterNotNull(paymentInfoData, "paymentInfoData cannot be null");

		final LandmarkPaymentInfoModel landmarkPaymentInfoModel = this.createCreditCardPaymentInfo(paymentInfoData, null,
				customerModel, saveInAccount);
		getModelService().saveAll(landmarkPaymentInfoModel);
		getModelService().refresh(customerModel);
		final List<PaymentInfoModel> paymentInfoModels = new ArrayList<PaymentInfoModel>(customerModel.getPaymentInfos());
		//customerModel.getTotalWalletAmount();
		if (!paymentInfoModels.contains(landmarkPaymentInfoModel))
		{
			paymentInfoModels.add(landmarkPaymentInfoModel);
			if (saveInAccount)
			{
				customerModel.setPaymentInfos(paymentInfoModels);
				getModelService().save(customerModel);
			}
			getModelService().save(landmarkPaymentInfoModel);
			getModelService().refresh(customerModel);
		}

		return landmarkPaymentInfoModel;
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
