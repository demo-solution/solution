package com.spar.hcl.warehousing.sourcing.strategy.impl;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingContext;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.sourcing.context.PosSelectionStrategy;
import de.hybris.platform.warehousing.sourcing.strategy.impl.PickupStrategy;

public class SparPickupStrategy
extends PickupStrategy {
    private static Logger LOGGER = Logger.getLogger(SparPickupStrategy.class);
    private PosSelectionStrategy posSelectionStrategy;

    @Override
    public void source(SourcingContext sourcingContext) {
        Preconditions.checkArgument((boolean)Objects.nonNull((Object)sourcingContext), (Object)"Parameter sourcingContext cannot be null");
        Optional<AbstractOrderEntryModel> entryOptional = sourcingContext.getOrderEntries().stream().filter(orderEntry -> Objects.nonNull((Object)orderEntry.getDeliveryPointOfService())).findFirst();
        if (entryOptional.isPresent()) {
            AbstractOrderEntryModel entry = entryOptional.get();
            Optional<SourcingLocation> pickupLocation = sourcingContext.getSourcingLocations().stream().filter(location -> entry.getDeliveryPointOfService() == this.getPosSelectionStrategy().getPointOfService(entry.getOrder(), location.getWarehouse())).findFirst();
            if (pickupLocation.isPresent()) {
                this.createPickupSourcingResult(sourcingContext, pickupLocation.get());
            }
        }
        if (Objects.nonNull((Object)sourcingContext.getResult()) && !sourcingContext.getResult().getResults().isEmpty()) {
            LOGGER.debug((Object)("Total order entries sourceable using Pickup Strategy: " + ((SourcingResult)sourcingContext.getResult().getResults().iterator().next()).getAllocation().size()));
        }
    }

    protected void createPickupSourcingResult(SourcingContext sourcingContext, SourcingLocation location) {
        boolean isComplete = Boolean.TRUE;
        HashMap<AbstractOrderEntryModel, Long> allocations = new HashMap<AbstractOrderEntryModel, Long>();
        for (AbstractOrderEntryModel entry : sourcingContext.getOrderEntries()) {
            Long orderQty;
            Long stock = (Long)location.getAvailability().get((Object)entry.getProduct());
            if (stock >= entry.getQuantity()) {
                orderQty = entry.getQuantity();
            } else {
                orderQty = stock;
                isComplete = Boolean.FALSE;
                LOGGER.debug((Object)String.format("Incomplete sourcing - Insufficient stock for product [%s]: requested qty [%d], stock qty [%d]", entry.getProduct().getCode(), entry.getQuantity(), stock));
            }
            if (stock <= 0) continue;
            allocations.put(entry, orderQty);
            LOGGER.debug((Object)String.format("Created sourcing result allocation for product [%s]: requested qty [%d] at location [%s] ", entry.getProduct().getCode(), orderQty, location.getWarehouse().getCode()));
        }
        if (!allocations.isEmpty()) {
            SourcingResult result = this.getSourcingResultFactory().create(allocations, location);
            sourcingContext.getResult().getResults().add(result);
        }
        sourcingContext.getResult().setComplete(isComplete);
    }

    protected PosSelectionStrategy getPosSelectionStrategy() {
        return this.posSelectionStrategy;
    }

    @Required
    public void setPosSelectionStrategy(PosSelectionStrategy posSelectionStrategy) {
        this.posSelectionStrategy = posSelectionStrategy;
    }
}

