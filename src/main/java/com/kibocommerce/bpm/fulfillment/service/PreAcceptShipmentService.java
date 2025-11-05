package com.kibocommerce.bpm.fulfillment.service;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class to handle Pre-Accept Shipment operations including REST API calls.
 */
@Component("preAcceptShipmentService")
public class PreAcceptShipmentService implements WorkItemHandler {



    private static final Logger logger = LoggerFactory.getLogger(PreAcceptShipmentService.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {

            System.out.println("Pre-Accept Shipment Service called");
             Map<String, Object> results = new HashMap<>();
            String orderId = (String) workItem.getParameter("orderId");
            
            String apiUrl = "";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "/" + orderId,
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                logger.info("Pre-Accept Shipment API response: {}", responseBody);
                
                results.put("apiResponse", responseBody);
                results.put("isValid", true);

                
            } else {
                logger.error("Failed to call Pre-Accept Shipment API. Status code: {}", response.getStatusCode());
                results.put("isValid", false);
                results.put("errorMessage", "Failed to validate shipment: " + response.getStatusCode());
            }
            
            manager.completeWorkItem(workItem.getId(), results);
            
        } catch (Exception e) {
            logger.error("Error in PreAcceptShipmentService: ", e);
            Map<String, Object> errorResults = new HashMap<>();
            errorResults.put("isValid", false);
            errorResults.put("errorMessage", "Error processing shipment: " + e.getMessage());
            manager.completeWorkItem(workItem.getId(), errorResults);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        logger.warn("PreAcceptShipmentService work item aborted: {}", workItem.getId());
        manager.abortWorkItem(workItem.getId());
    }
}
