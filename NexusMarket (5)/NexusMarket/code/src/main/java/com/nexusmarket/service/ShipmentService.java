package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.enums.ShipmentStatus;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.Shipment;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.ShipmentRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for shipment management (SDD, Domain 9, CU-11 / CU-12). */
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    /** Use case: UC-37 Create shipment for a paid order. Allowed role: LOGISTICS_OPERATOR. */
    public Shipment createShipment(User actor, Order order, String address) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        Shipment shipment = new Shipment(IdGenerator.next("SHP"), order, address);
        return shipmentRepository.save(shipment);
    }

    /** Use case: UC-38 Prepare shipment (packing). Allowed role: LOGISTICS_OPERATOR. */
    public void prepare(User actor, Shipment shipment) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        shipment.prepare();
        shipmentRepository.save(shipment);
    }

    /** Use case: UC-39 Dispatch shipment. Allowed role: LOGISTICS_OPERATOR. */
    public void dispatch(User actor, Shipment shipment) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        shipment.dispatch();
        shipmentRepository.save(shipment);
    }

    /** Use case: UC-40 Update shipment tracking status. Allowed role: LOGISTICS_OPERATOR. */
    public void updateStatus(User actor, Shipment shipment, ShipmentStatus status) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        shipment.updateStatus(status);
        shipmentRepository.save(shipment);
    }

    /** Use case: UC-41 Confirm delivery (finalizes the associated order). Allowed role: LOGISTICS_OPERATOR. */
    public void confirmDelivery(User actor, Shipment shipment) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        shipment.confirmDelivery();
        shipmentRepository.save(shipment);
    }
}
