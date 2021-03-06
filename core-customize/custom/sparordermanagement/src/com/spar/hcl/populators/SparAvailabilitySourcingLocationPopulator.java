/**
 *
 */
package com.spar.hcl.populators;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;
import de.hybris.platform.warehousing.sourcing.context.populator.SourcingLocationPopulator;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;

public class SparAvailabilitySourcingLocationPopulator implements SourcingLocationPopulator {
   private WarehouseStockService warehouseStockService;

   public void populate(WarehouseModel source, SourcingLocation target) {
         Preconditions.checkArgument(source != null, "Point of service model (source) cannot be null.");
         Preconditions.checkArgument(target != null, "Sourcing location (target) cannot be null.");
         Map<ProductModel, Long> availability = new HashMap();
         target.getContext().getOrderEntries().forEach((entry) -> {
               this.setAvailability(source, entry, availability);
         });
         target.setAvailability(availability);
   }

   protected void setAvailability(WarehouseModel source, AbstractOrderEntryModel entry,
               Map<ProductModel, Long> availability) {
         ProductModel product = entry.getProduct();
         if (availability.get(product) == null) {
              /* Long stock = this.getWarehouseStockService().getStockLevelForProductAndWarehouse(product, source);
               if (stock == null) {
                     stock = 0L;
               }*/
        	   Long stock = 1000L;
               availability.put(product, stock);
         }

   }

   protected WarehouseStockService getWarehouseStockService() {
         return this.warehouseStockService;
   }

   @Required
   public void setWarehouseStockService(WarehouseStockService warehouseStockService) {
         this.warehouseStockService = warehouseStockService;
   }
}
