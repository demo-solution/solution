/**
 *
 */
package com.spar.hcl.core.service.interceptor.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.core.plms.dao.SparPlmsDao;
import com.spar.hcl.core.storefinder.StoreFinderServiceInterface;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;


/**
 *
 *
 */
public class SparSaveDeliverySlotsInterceptor implements PrepareInterceptor<OrderModel>
{

	@Resource(name = "storeFinderServiceInterface")
	private StoreFinderServiceInterface storeFinderServiceInterface;
	@Resource(name = "sessionService")
	SessionService sessionService;
	@Resource(name = "modelService")
	ModelService modelService;

	private SparPlmsService sparPlmsService;

	@Autowired
	private SparPlmsDao sparPlmsDao;

	final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
	private static final String UPDATE = "U";
	private static final String INSERT = "I";
	protected static final Logger LOG = Logger.getLogger(SparSaveDeliverySlotsInterceptor.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.interceptor.LoadInterceptor#onLoad(java .lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */



	@SuppressWarnings("boxing")
	@Override
	public void onPrepare(final OrderModel model, final InterceptorContext ctx) throws InterceptorException
	{
		final OrderModel orderModel = model;
		LOG.info("Entered in Instance of Save Order interceptor....");
		if (null == orderModel.getOriginalSubtotal() || 0.0 == orderModel.getOriginalSubtotal())
		{
			orderModel.setOriginalSubtotal(orderModel.getSubtotal());
		}
		if (null == orderModel.getOriginalTotalPrice() || 0.0 == orderModel.getOriginalTotalPrice())
		{
			orderModel.setOriginalTotalPrice(orderModel.getTotalPrice());
		}
		if (orderModel.getTotalPrice() <= Double.valueOf(0.0d))
		{
			orderModel.setTotalDiscounts(orderModel.getOriginalSubtotal());
			orderModel.setTotalPrice(Double.valueOf(0.0d));
		}


		if (ctx.isModified(orderModel, OrderModel.DELIVERYDATE) || ctx.isModified(orderModel, OrderModel.DELIVERYSLOT))

		{
			LOG.info("Entered when delivery slot and date is modified for Save Order interceptor....");
			final List<String> deliverySlots = new ArrayList<String>();
			final List<String> deliveryDates = new ArrayList<String>();
			if (null != model && null != model.getAllDeliverySlots() && !(model.getAllDeliverySlots().isEmpty()))
			{
				for (final String deliverySlot : model.getAllDeliverySlots())
				{
					final String[] splitText = deliverySlot.split("-");
					deliverySlots.add(splitText[1].trim().toString());
					deliveryDates.add(splitText[0].trim().toString());

				}

				try
				{
					if (deliveryDates.contains(orderModel.getDeliveryDate().trim())
							&& deliverySlots.contains(orderModel.getDeliverySlot().trim()))
					{
						LOG.debug("Saving the Delivery Model and Delivery Slot.");
					}
					else
					{
						throw new InterceptorException("Cannot Save changes. Please check the value of Delivery Slot and Delivery Date");
					}
				}
				catch (final ModelSavingException modelSavingException)
				{
					LOG.error("Cannot Save changes. Please check the value of Delivery Slot and Delivery Date");
				}
			}
		}

		 if (!ctx.isNew(orderModel) && (ctx.isModified(orderModel, OrderModel.DELIVERYDATE) ||
				 ctx.isModified(orderModel, OrderModel.DELIVERYSLOT))) 
				 { 
					 LOG.error("Entered when delivery slot and date is modified and try to send to PLMS : "+ orderModel.getCode());
					 final PlmsRetryOrdersModel plmsRetryOrdersModel = sparPlmsDao.findPlmsOrders(orderModel.getCode()); 
					 if (null == plmsRetryOrdersModel || !plmsRetryOrdersModel.getActionType().equals(INSERT)) 
					 { 
						 sendUpdateDataToPlms(orderModel, plmsRetryOrdersModel);
					 } 
				 }
	}


	/**
	 * This method is used to send the updated order from oms to plms
	 *
	 * @param orderModel
	 */
	@SuppressWarnings("boxing")
	private void sendUpdateDataToPlms(final OrderModel orderModel, final PlmsRetryOrdersModel plmsRetryOrdersModel)
	{
		  LOG.info("Entered into sendPlmsUpdateData method"); 
		   final ResponseEntity<SparPlmsOrderDataResponse> response = getSparPlmsService().plmsUpdateOrder(orderModel);
		   String status = null;
		   if (null != response)
		   { 
		   	status = response.getBody().getResult().getStatus(); 
		   	LOG.info("status::" + status + " ErrorCode::" + response.getBody().getResult().getError_code() + " ErrorFiledValue:: " +
		   			response.getBody().getResult().getError_Field_Value()); LOG.info(" error reason::" +
		   					response.getBody().getResult().getError_Reason() + " error type::" +
		   					response.getBody().getResult().getError_Type() + " message:::" + response.getBody().getResult().getMessage()); 
		   }
		   if (null == response || !status.equals("true")) 
		   { 
		   	LOG.info("Entered into sendPlmsUpdateData method"); 
		   	if (null != plmsRetryOrdersModel && !plmsRetryOrdersModel.getActionType().equalsIgnoreCase(UPDATE)) 
		   	{
		   		LOG.info("Entered into sendPlmsUpdateData method to save order PLMS Retry Table");
		   		getSparPlmsService().saveOrder(orderModel.getCode(), UPDATE); 
		   	} 
		   }
		   else
		   {
		   	if (null != plmsRetryOrdersModel)
		   	{
		   		sparPlmsService.deleteOrders(orderModel.getCode());
		   	}
		   	
		   }
		   LOG.info("Exited from sendPlmsUpdateData method");
	}

	/**
	 * @return the sparPlmsService
	 */
	public SparPlmsService getSparPlmsService()
	{
		return sparPlmsService;
	}

	/**
	 * @param sparPlmsService
	 *           the sparPlmsService to set
	 */
	public void setSparPlmsService(final SparPlmsService sparPlmsService)
	{
		this.sparPlmsService = sparPlmsService;
	}
}
