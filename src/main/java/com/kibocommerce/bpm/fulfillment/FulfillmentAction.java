package com.kibocommerce.bpm.fulfillment;

public enum FulfillmentAction {
    AcceptShipment("AcceptShipment"),
    CancelShipment("CancelShipment"),
    CancelItems("CancelItems"),
    CustomerCareShipment("CustomerCareShipment"),
    CustomerCareItems("CustomerCareItems"),
    RejectShipment("RejectShipment"),
    RejectItems("RejectItems"),
    ReassignShipment("ReassignShipment"),
    ReassignItems("ReassignItems"),
    BackorderShipment("BackorderShipment"),
    BackorderItems("BackorderItems"),
    TransferShipment("TransferShipment"),
    TransferItems("TransferItems"),
    CompleteShipment("CompleteShipment"),
    FulfillItems("FulfillItems"),
    ShipTransferShipment("ShipTransferShipment"),
    ReadyForPickup("ReadyForPickup"),
    CapturePayment("CapturePayment");
    
    private final String name;
    
    FulfillmentAction(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static FulfillmentAction fromString(String name) {
        return FulfillmentAction.valueOf(name);
    }
}
