package com.nexusmarket.model;

import java.time.LocalDateTime;

import com.nexusmarket.enums.ShipmentStatus;

/** Physical logistics of an {@link Order} (SDD, Domain 9). */
public class Shipment {

    private final String id;
    private final Order order;
    private final String address;
    private ShipmentStatus status;
    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    public Shipment(String id, Order order, String address) {
        this.id = id;
        this.order = order;
        this.address = address;
        this.status = ShipmentStatus.PENDING;
    }

    public void prepare() {
        this.status = ShipmentStatus.PREPARING;
    }

    public void dispatch() {
        this.status = ShipmentStatus.DISPATCHED;
        this.dispatchDate = LocalDateTime.now();
        this.order.ship();
    }

    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }

    public void confirmDelivery() {
        this.status = ShipmentStatus.DELIVERED;
        this.deliveryDate = LocalDateTime.now();
        this.order.deliver();
        this.order.finish();
    }

    public String getId() { return id; }
    public Order getOrder() { return order; }
    public String getAddress() { return address; }
    public ShipmentStatus getStatus() { return status; }
    public LocalDateTime getDispatchDate() { return dispatchDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
}
