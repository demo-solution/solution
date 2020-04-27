/**
 *
 */
package com.spar.hcl.core.blowhorn;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.spar.hcl.facades.blowhorn.SparBlowhornCancelledOrderDataResponse;
import com.spar.hcl.facades.blowhorn.SparBlowhornShipAddressOrderDataResponse;
import com.spar.hcl.facades.plms.SparPlmsOrderDataResponse;
import com.spar.hcl.model.cms.PlmsRetryOrdersModel;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * This interface is used for sending the  orders details to Blowhorn
 * 
 * @author pavan
 *
 */
public interface SparBlowhornService
{
	ResponseEntity<SparBlowhornShipAddressOrderDataResponse> addOrderToBlowhorn(final OrderModel orderModel);
	
	ResponseEntity<SparBlowhornCancelledOrderDataResponse> sendCancelledOrderToBlowhorn(final OrderModel orderModel);

	void sendEmailBlowhornSendDataFailure(OrderModel orderModel);

}
