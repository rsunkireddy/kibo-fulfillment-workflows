package com.kibocommerce.bpm.fulfillment.config;

import com.kibocommerce.bpm.fulfillment.service.PreAcceptShipmentService;
import org.kie.api.runtime.process.WorkItemHandler;

public class WorkflowConfig {

    public WorkItemHandler preAcceptShipmentService(PreAcceptShipmentService service) {
        return service;
    }
}
