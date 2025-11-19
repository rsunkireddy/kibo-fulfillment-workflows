package com.kibocommerce.bpm.fulfillment.service;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Service for handling shipment pre-acceptance logic.
 * Processes order information and updates the shipment state to ACCEPTED_SHIPMENT.
 */
public class ShipmentPreAcceptanceService implements WorkItemHandler {
    private static final Logger LOG = LogManager.getLogger(ShipmentPreAcceptanceService.class);
    private static final String CURRENT_STATE = "currentState";
    private static final String ORDER = "order";
    private static final String ACCEPTED_SHIPMENT = "ACCEPTED_SHIPMENT";

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (workItem == null) {
            LOG.error("WorkItem cannot be null");
            throw new IllegalArgumentException("WorkItem cannot be null");
        }

        try {
            Map<String, Object> parameters = workItem.getParameters();
            String currentState = (String) parameters.get(CURRENT_STATE);
            Object order = parameters.get(ORDER);

            LOG.info("Processing shipment pre-acceptance for work item: {}", workItem.getId());
            
            if (currentState == null || currentState.trim().isEmpty()) {
                parameters.put(CURRENT_STATE, ACCEPTED_SHIPMENT);
                LOG.info("Set shipment state to: {}", ACCEPTED_SHIPMENT);
            }
            
            manager.completeWorkItem(workItem.getId(), parameters);
            LOG.debug("Successfully completed work item: {}", workItem.getId());
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to process shipment pre-acceptance for work item %s: %s", 
                                         workItem.getId(), e.getMessage());
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (workItem != null && manager != null) {
            LOG.warn("Aborting work item: {}", workItem.getId());
            manager.abortWorkItem(workItem.getId());
        }
    }

    /**
     * Determines the appropriate shipment state based on order information
     * @param order The order object (not currently used but kept for backward compatibility)
     * @return Always returns "ACCEPTED_SHIPMENT"
     * @deprecated This method is maintained for backward compatibility. The state is now handled in executeWorkItem.
     */
    @Deprecated
    public String determineShipmentState(Object order) {
        LOG.debug("Using default shipment state: {}", ACCEPTED_SHIPMENT);
        return ACCEPTED_SHIPMENT;
    }
}
