package com.nexusmarket.repository;

import com.nexusmarket.model.Shipment;

/** Persistence for {@link Shipment} entities. */
public class ShipmentRepository extends InMemoryRepository<Shipment, String> {

    public ShipmentRepository() {
        super(Shipment::getId);
    }
}
