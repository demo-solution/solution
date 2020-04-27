/**
 *
 */
package com.spar.hcl.core.plms;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * This interface is used for sending the  orders details to PLMS
 * 
 * @author ravindra
 *
 */
public interface SparPlmsService
{
	boolean saveOrder(final String orderCode, final String actionType);
	List<PlmsRetryOrdersModel> readOrders();
	boolean deleteOrders(final String orderCode);
	ResponseEntity<SparPlmsOrderDataResponse> plmsInsertOrder(final OrderModel orderModel);
	ResponseEntity<SparPlmsOrderDataResponse> plmsUpdateOrder(final OrderModel orderModel);
	ResponseEntity<SparPlmsOrderDataResponse> plmsCancelOrder(final OrderModel orderModel);
	OrderModel findOrderData(final String orderCode);
	void updatePlmsOrders(final String orderCode,final String actionType);
}
