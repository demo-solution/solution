/**
 *
 */
package com.spar.hcl.blowhorn.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.spar.hcl.blowhorn.webservices.utility.SparBlowhornOrderTransferUtility;
import com.spar.hcl.core.blowhorn.SparBlowhornService;
import com.spar.hcl.core.model.process.SparBlowhornFailedRequestOrderEmailProcessModel;
import com.spar.hcl.core.plms.SparPlmsService;
import com.spar.hcl.core.plms.dao.SparPlmsDao;
import com.spar.hcl.facades.blowhorn.SparBlowhornCancelledOrderDataResponse;
import com.spar.hcl.facades.blowhorn.SparBlowhornOrderData;
import com.spar.hcl.facades.blowhorn.SparBlowhornProductDetailData;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataHeader;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataResponse;
import com.spar.hcl.facades.blowhorn.SparCancelledOrderData;
import com.spar.hcl.facades.blowhorn.SparDeliveryAddressData;
import com.spar.hcl.facades.plms.SparOMSOrderData;
import com.spar.hcl.facades.plms.SparPLMSCancelOrderData;
import com.spar.hcl.facades.plms.SparPLMSCancelOrderDataHeader;
import com.spar.hcl.facades.plms.SparPLMSOrderData;
import com.spar.hcl.facades.plms.SparPLMSOrderDataHeader;
import com.spar.hcl.facades.plms.SparPLMSUpdateOrderData;
import com.spar.hcl.facades.plms.SparPLMSUpdateOrderDataHeader;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;
import com.spar.hcl.plms.webservices.utility.SparPLMSOrderTransferUtility;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import org.springframework.http.HttpMethod;


/**
 *This class is implemented for Blowhorn DB operation 
 * 
 * @author pavan
 *
 */
public class SparDefaultBlowhornService implements SparBlowhornService
{
	
	private static final Logger LOGGER = Logger.getLogger(SparDefaultBlowhornService.class);
	private static final String BLOWHORN_ADD_ORDER = "spar.blowhorn.addorder";
	private static final String BLOWHORN_CANCEL_ORDER_MODE = "spar.blowhorn.cancelled.order.mode";
	final String blowhornUri=Config.getString("spar.blowhorn.rooturl","spar.blowhorn.rooturl");
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;
	
	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;
	
	@Resource(name = "modelService")
	private ModelService modelService;
	
	private static final String FAILED_BLOWHORN_ORDER_EMAIL_PROCESS = "SparBlowhornFailedOrderRequestEmailProcess";
	private static final String BASESITE = "basesite.uid";
	
	/**
	 * This method is used to push the OMS data to PLMS server for inserted Order in OMS
	 *
	 * @param orderModel
	 * @return ResponseEntity<String>
	 */
	@Override
	public ResponseEntity<SparBlowhornShipAddressOrderDataResponse> addOrderToBlowhorn(final OrderModel orderModel)
	{
		LOGGER.info("Entered in sendOrderDetailToBlowhorn method");
		final String addOrderUri=Config.getString("spar.blowhorn.addorder","spar.blowhorn.addorder");
		String apiUrl = blowhornUri + addOrderUri;
		SparDeliveryAddressData sparDeliveryAddressData = new com.spar.hcl.facades.blowhorn.SparDeliveryAddressData();
		ResponseEntity<SparBlowhornShipAddressOrderDataResponse> blowhornResponse = null;
		
		List<SparBlowhornProductDetailData> list = new ArrayList<SparBlowhornProductDetailData>();
		String deliveryAddress = "";
		if(null != orderModel && null != orderModel.getDeliveryAddress()){
			if(null != orderModel.getDeliveryAddress().getLongAddress()){
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getStreetnumber())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getStreetnumber() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getBuilding())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getBuilding() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getStreetname())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getStreetname() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getAppartment())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getAppartment() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getLongAddress())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getLongAddress() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getTown())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getTown();
				}
			}else{
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getStreetnumber())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getStreetnumber() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getBuilding())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getBuilding() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getStreetname())){
					deliveryAddress = deliveryAddress + deliveryAddress + orderModel.getDeliveryAddress().getStreetname() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getAppartment())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getAppartment() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getDistrict())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getLongAddress() + ", ";
				}
				if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getDistrict())){
					deliveryAddress = deliveryAddress + orderModel.getDeliveryAddress().getTown() + ", ";
				}
			}
		}
		
		sparDeliveryAddressData.setDelivery_address(deliveryAddress);
		if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getPostalcode())){
			sparDeliveryAddressData.setDelivery_postal_code(orderModel.getDeliveryAddress().getPostalcode());
		}
		if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getMiddlename())){
			sparDeliveryAddressData.setCustomer_name(orderModel.getDeliveryAddress().getTitle().getName() + " " + orderModel.getDeliveryAddress().getFirstname() + " " + orderModel.getDeliveryAddress().getMiddlename() + " " + orderModel.getDeliveryAddress().getLastname());
		}else{
			sparDeliveryAddressData.setCustomer_name(orderModel.getDeliveryAddress().getTitle().getName() + " " + orderModel.getDeliveryAddress().getFirstname() + " " + orderModel.getDeliveryAddress().getLastname());
		}
		
		if(StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getPhone1())){
			sparDeliveryAddressData.setCustomer_mobile(orderModel.getDeliveryAddress().getPhone1());
		}
		sparDeliveryAddressData.setReference_number(orderModel.getCode());
		//sparDeliveryAddressData.setOrder_creation_time(format.format(orderModel.getCreationtime()));
		if(StringUtils.isNotEmpty(orderModel.getDeliveryDate())){
				String deliverySlot = orderModel.getDeliverySlot();
				if(null != deliverySlot && StringUtils.isNotEmpty(deliverySlot)){
					LOGGER.info("deliverySlot::::::::::::::::::::::::::"+deliverySlot);
					String time[] = null;
					if(deliverySlot.contains("To")){
						time = deliverySlot.split("To");
					}
					if(deliverySlot.contains("to")){
						time = deliverySlot.split("to");
					}
					String expectedStartTime = orderModel.getDeliveryDate() + " " + time[0];
					String expectedEndTime = orderModel.getDeliveryDate() + " " + time[1];
					String expected_deliveryDate = convertExpectedDeliveryDate(expectedStartTime);
					String expected_deliveryendDate = convertExpectedDeliveryDate(expectedEndTime);
					
					sparDeliveryAddressData.setSlot_start_date_time(expected_deliveryDate);
					sparDeliveryAddressData.setSlot_end_date_time(expected_deliveryendDate);
				}
		}
		sparDeliveryAddressData.setCustomer_email(orderModel.getDeliveryAddress().getEmail());
		if(null != orderModel.getDeliveryAddress().getLatitude() && StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getLatitude().toString())){
			sparDeliveryAddressData.setDelivery_lat(orderModel.getDeliveryAddress().getLatitude().toString());
		}
		if(null != orderModel.getDeliveryAddress().getLongitude() && StringUtils.isNotEmpty(orderModel.getDeliveryAddress().getLongitude().toString())){
			sparDeliveryAddressData.setDelivery_lon(orderModel.getDeliveryAddress().getLongitude().toString());
		}
		
		if(null != orderModel && null != orderModel.getEntries()){
			for(AbstractOrderEntryModel entryModel : orderModel.getEntries()){
				SparBlowhornProductDetailData sparBlowhornProductDetailData =  new SparBlowhornProductDetailData();
				sparBlowhornProductDetailData.setItem_name(entryModel.getProduct().getName());
				sparBlowhornProductDetailData.setItem_quantity(entryModel.getQuantity().toString());
				list.add(sparBlowhornProductDetailData);
			}
		}
				
		sparDeliveryAddressData.setItem_details(list);
		
		final String omsJsonData = SparBlowhornOrderTransferUtility.createOMSDataAsJson(sparDeliveryAddressData);		
		LOGGER.debug("Json Data for OrderId " + orderModel.getCode() + "::::::" + omsJsonData);
		
		if (StringUtils.isNotBlank(omsJsonData))
		{
			blowhornResponse = SparBlowhornOrderTransferUtility.sendOMSDataToBlowhorn(omsJsonData, apiUrl, HttpMethod.POST);
			LOGGER.info("Response of shipment API from blowhorn server ::::::::: "+blowhornResponse);
			return blowhornResponse;
		}	
		return blowhornResponse;
	}
	
	
	// Send Cancelled order to blowhorn
	@Override
	public ResponseEntity<SparBlowhornCancelledOrderDataResponse> sendCancelledOrderToBlowhorn(final OrderModel orderModel)
	{
		LOGGER.info("Entered in sendCancelledOrderToBlowhorn method");
		final String cancelledOrderUri=Config.getString("spar.blowhorn.cancelled.order","spar.blowhorn.cancelled.order");
		final String deployedEnv=Config.getString("spar.blowhorn.cancelled.order.mode","spar.blowhorn.cancelled.order.mode");
		
		String apiUrl = blowhornUri + cancelledOrderUri + "/" + orderModel.getAwb_number();
		SparCancelledOrderData sparCancelledOrderData = new com.spar.hcl.facades.blowhorn.SparCancelledOrderData();
		ResponseEntity<SparBlowhornCancelledOrderDataResponse> blowhornResponse = null;
		
		if(deployedEnv.equalsIgnoreCase("test")){
			sparCancelledOrderData.setMode(deployedEnv);
		}
		
		sparCancelledOrderData.setOrderId(orderModel.getAwb_number());
		
		final String omsJsonData = SparBlowhornOrderTransferUtility.createOMSDataAsJson(sparCancelledOrderData);		
		LOGGER.debug("Json Data for OrderId " + orderModel.getCode() + "::::::" + omsJsonData);
		
		if (StringUtils.isNotBlank(omsJsonData))
		{
			blowhornResponse = SparBlowhornOrderTransferUtility.sendCancelledOMSDataToBlowhorn(omsJsonData, apiUrl, HttpMethod.PUT);
			LOGGER.info("Response of shipment API from blowhorn server ::::::::: "+blowhornResponse);
			return blowhornResponse;
		}	
		return blowhornResponse;
	}
	
	
	private String convertExpectedDeliveryDate(String expectedDelDate){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd yyyy hh:mm aa");
        try
        {
            Date date = simpleDateFormat.parse(expectedDelDate);
			return format.format(date);
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
        return null;
	}
	
	@Override
	public void sendEmailBlowhornSendDataFailure(OrderModel orderModel)
	{
		final SparBlowhornFailedRequestOrderEmailProcessModel process=(SparBlowhornFailedRequestOrderEmailProcessModel)businessProcessService.createProcess
				(FAILED_BLOWHORN_ORDER_EMAIL_PROCESS + "-" + "-" + System.currentTimeMillis(), FAILED_BLOWHORN_ORDER_EMAIL_PROCESS);
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(configurationService.getConfiguration().getString(
				BASESITE));
		process.setOrder(orderModel);
		process.setSite(baseSite);
		process.setUser((CustomerModel)orderModel.getUser());
		process.setStore(baseSite.getStores().get(0));
		modelService.save(process);
		businessProcessService.startProcess(process);
		LOGGER.debug("sendEmailBlowhornSendDataFailure ::::: finished");
	}
	

}
