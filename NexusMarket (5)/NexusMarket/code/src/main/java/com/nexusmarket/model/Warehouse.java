package com.nexusmarket.model;

import com.nexusmarket.enums.WarehouseType;

/** Physical storage space, either owned by the Marketplace or by a Seller (SDD, Domain 4). */
public class Warehouse {

    private final String id;
    private String location;
    private final WarehouseType type;
    private boolean active;

    public Warehouse(String id, String location, WarehouseType type) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getId() { return id; }
    public String getLocation() { return location; }
    public WarehouseType getType() { return type; }
    public boolean isActive() { return active; }
}
