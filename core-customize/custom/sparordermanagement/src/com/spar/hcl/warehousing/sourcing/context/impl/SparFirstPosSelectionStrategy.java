package com.spar.hcl.warehousing.sourcing.context.impl;

import java.util.Iterator;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.sourcing.context.impl.FirstPosSelectionStrategy;

public class SparFirstPosSelectionStrategy extends FirstPosSelectionStrategy {

	@Override
	public PointOfServiceModel getPointOfService(AbstractOrderModel orderModel,
			WarehouseModel warehouse) {
		Iterator<PointOfServiceModel> iterator= warehouse.getPointsOfService().iterator();
		while (iterator.hasNext()){
			PointOfServiceModel orderPOS=orderModel.getOrderPointOfService();
			PointOfServiceModel pos=iterator.next();
			if(null!= orderPOS && orderPOS.getName().equals(pos.getName())){
				return pos;
			}
		}
		return ((PointOfServiceModel) warehouse.getPointsOfService().iterator()
				.next());
	}

}
