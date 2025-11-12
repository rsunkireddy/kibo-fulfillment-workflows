package com.kibocommerce.bpm.fulfillment.service;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service for handling shipment pre-acceptance logic.
 * Processes order information and updates the shipment state accordingly.
 */
public class ShipmentPreAcceptanceService implements WorkItemHandler {
    private static final Logger LOG = LogManager.getLogger(ShipmentPreAcceptanceService.class);

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            String currentState = (String) workItem.getParameter("currentState");
            Object order = workItem.getParameter("order");

            LOG.info("Processing shipment pre-acceptance for order: {}", order);
            
            if (currentState == null || currentState.isEmpty()) {
                String newState = determineShipmentState(order);
                workItem.getParameters().put("currentState", newState);
                LOG.info("Updated shipment state to: {}", newState);
            }
            
            manager.completeWorkItem(workItem.getId(), workItem.getParameters());
        } catch (Exception e) {
            String errorMsg = String.format("Failed to process shipment pre-acceptance for work item %s", 
                                         workItem != null ? workItem.getId() : "null");
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Determines the appropriate shipment state based on order information
     * @param order The order object
     * @return The determined shipment state
     */
    public String determineShipmentState(Object order) {
        String state = "ACCEPTED_SHIPMENT";
        
        if (order != null) {
            LOG.debug("Processing order: {}", order);

        }
        
        return state;
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (workItem != null) {
            LOG.warn("Work item aborted: {}", workItem.getId());
        } else {
            LOG.warn("Received null work item in abortWorkItem");
        }
    }
}
