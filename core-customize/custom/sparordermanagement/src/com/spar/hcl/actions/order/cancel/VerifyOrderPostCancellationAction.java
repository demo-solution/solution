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
package com.spar.hcl.actions.order.cancel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.util.Config;

import com.spar.hcl.actions.consignment.VerifyConsignmentCompletionAction;
import com.spar.hcl.core.blowhorn.SparBlowhornService;
import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.core.plms.dao.SparPlmsDao;
import com.spar.hcl.facades.blowhorn.SparBlowhornCancelledOrderDataResponse;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataResponse;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;
import com.spar.hcl.plms.webservices.utility.SparPLMSServerConnectionUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


/**
 * Check if cancellation is done.
 */
public class VerifyOrderPostCancellationAction extends AbstractAction<OrderProcessModel>
{
	private static Logger LOGGER = LoggerFactory.getLogger(VerifyConsignmentCompletionAction.class);
	private static final String CANCEL = "C";
	private static final String UPDATE = "U";
	private SparPlmsService sparPlmsService;
	private SparBlowhornService sparBlowhornService;
		private static final String BLOWHORN_SYSTEM_ACTIVE = "spar.blowhorn.isActive";
	@Autowired
	private SparPlmsDao sparPlmsDao;

	protected enum Transition
	{
		OK, NOK, WAIT;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final OrderProcessModel orderProcessModel) throws Exception
	{
		LOGGER.info("Process: " + orderProcessModel.getCode() + " in step " + getClass().getSimpleName());
		String transition;

		if (orderProcessModel.getOrder().getEntries().stream().anyMatch(entry -> ((OrderEntryModel)entry).getQuantityPending() > 0))
		{
			transition = Transition.WAIT.toString();
		}
		else if(orderProcessModel.getOrder().getConsignments().stream().anyMatch(consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED)))
		{
			transition = Transition.NOK.toString();
		}
		else
		{
			transition = transitionOK(orderProcessModel);
			sendSMSToUser(((CustomerModel)(orderProcessModel.getOrder().getUser())).getCustPrimaryMobNumber(), orderProcessModel.getOrder().getCode());
			final PlmsRetryOrdersModel plmsRetryOrdersModel = sparPlmsDao.findPlmsOrders(orderProcessModel.getOrder().getCode());
			if (null == plmsRetryOrdersModel || plmsRetryOrdersModel.getActionType().equals(CANCEL) || plmsRetryOrdersModel.getActionType().equals(UPDATE))
			{
				sendCancelDataToPlms(orderProcessModel);
			}
			else
			{
				sparPlmsService.deleteOrders(orderProcessModel.getOrder().getCode());
			}
			sendCancelledOrderToBlowhorn(orderProcessModel);
		}
		return transition;
	}
	
	

	/**
	 * This method is used to send the cancel order data to plms server
	 * 
	 * @param orderProcessModel
	 */
	protected void sendCancelDataToPlms(final OrderProcessModel orderProcessModel)
	{
		LOGGER.info("Entred into sendCancelDataToPlms");
		ResponseEntity<SparPlmsOrderDataResponse> response=getSparPlmsService().plmsCancelOrder(orderProcessModel.getOrder());
		String status =null;
		String errorCode="";
		if(null != response)
		{
			status = response.getBody().getResult().getStatus();
			errorCode = response.getBody().getResult().getError_code();
			LOGGER.info("status::"+status+" ErrorCode::"+response.getBody().getResult().getError_code()+" ErrorFiledValue:: "+response.getBody().getResult().getError_Field_Value());
			LOGGER.info(" error reason::"+response.getBody().getResult().getError_Reason()+" error type::"+response.getBody().getResult().getError_Type()+" message:::"+response.getBody().getResult().getMessage());
		}
		if(null == response || (response != null && !status.equals("true") && !errorCode.equals("-65")))
		{
			getSparPlmsService().saveOrder(orderProcessModel.getOrder().getCode(), CANCEL);
		}
		else
		{
		  	sparPlmsService.deleteOrders(orderProcessModel.getOrder().getCode());
		}
		LOGGER.info("Exited from sendCancelDataToPlms : "+ response);
	}


	private void sendCancelledOrderToBlowhorn(final OrderProcessModel orderProcessModel)
	{
		LOGGER.info("Entred into sendOderDataToBlowHorn method");
		boolean activeFlage = Config.getBoolean(BLOWHORN_SYSTEM_ACTIVE, false);
		if(activeFlage){
		try {
			ResponseEntity<SparBlowhornCancelledOrderDataResponse> response=getSparBlowhornService().sendCancelledOrderToBlowhorn(orderProcessModel.getOrder());
			int status = 0;
			if(null != response)
			{
				status = response.getStatusCode().value();
				LOGGER.info("status::::::::::"+status);
				LOGGER.info("Cancelled Order successfully shared with blowhorn ::::::::::"+orderProcessModel.getOrder().getCode());
			}
			if(null == response || response.getStatusCode().value() != 200)
			{
				LOGGER.info("inside the if condition to send the data in table");
				//Send email on failure case
				sparBlowhornService.sendEmailBlowhornSendDataFailure(orderProcessModel.getOrder());
			}
		}catch(Exception e){
			LOGGER.error("inside catch block of  sendCancelledOrderToBlowhorn ::::::"+e.getMessage());
			e.printStackTrace();
		}
		}
		LOGGER.info("Exited from sendOderDataToBlowHorn method");
	}
	
	
	protected String transitionOK(final OrderProcessModel orderProcessModel)
	{

		final OrderModel order = orderProcessModel.getOrder();
		order.setStatus(OrderStatus.CANCELLED);

		getModelService().save(order);

		return Transition.OK.toString();
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	
	/**
	 * @return the sparPlmsService
	 */
	public SparPlmsService getSparPlmsService() 
	{
		return sparPlmsService;
	}

	/**
	 * @param sparPlmsService the sparPlmsService to set
	 */
	public void setSparPlmsService(SparPlmsService sparPlmsService) 
	{
		this.sparPlmsService = sparPlmsService;
	}
	
	private void sendSMSToUser(final String mobilenum, final String orderId) throws IOException, DuplicateUidException
	{
		URL url = null;
		final String uname = Config.getString("mGage.user.name", "mGage.user.message");
		final String pass = Config.getString("mGage.user.password", "mGage.user.password");
		String msg = Config.getString("mGage.user.cancelOrder", "mGage.user.cancelOrder");

		msg = msg.replaceAll("XXXX", orderId);
		
		final String destmobnum = mobilenum;
		if (StringUtils.isNotBlank(mobilenum) || null != mobilenum)
		{
			final AddressModel newAddress = new AddressModel();
			newAddress.setPhone1(mobilenum);
			newAddress.setIsOTPValidate(Boolean.FALSE);
		}
		final String mgageURL = Config.getString("mGage.user.url", "mGage.user.url");

		final String mGageURL = mgageURL + "&pin=" + pass + "&mnumber=91" + destmobnum + "&signature=" + uname + "&message="
				+ URLEncoder.encode(msg, "UTF-8");

		LOGGER.info(" Change Password Message *********** " + mGageURL);

		url = new URL(mGageURL);

		sendSMS(url);
	}


	private void sendSMS(final URL url)
	{
		try
		{
			final String USER_AGENT = "Mozilla/5.0";
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			//urlConnection.connect();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			final int resCode = urlConnection.getResponseCode();
			final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			final StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			LOGGER.info("response messege for Request" + response);
			LOGGER.info(" resCode Message *********** " + resCode + "Message" + urlConnection.getResponseMessage());
			urlConnection.disconnect();
		}
		catch (final Exception ex)
		{
			ex.printStackTrace();
			LOGGER.error("Connection Failed", ex);
		}
	}
	
	
	
	/**
	 * @return the sparBlowhornService
	 */
	public SparBlowhornService getSparBlowhornService() {
		return sparBlowhornService;
	}



	/**
	 * @param sparBlowhornService the sparBlowhornService to set
	 */
	public void setSparBlowhornService(SparBlowhornService sparBlowhornService) {
		this.sparBlowhornService = sparBlowhornService;
	}

}
