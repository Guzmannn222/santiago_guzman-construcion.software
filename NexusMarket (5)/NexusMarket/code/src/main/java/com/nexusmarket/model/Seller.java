package com.nexusmarket.model;

/**
 * Product provider (SDD, Domain 3).
 * Business rule: sellers cannot self-register; they are onboarded by an Administrator (CU-03).
 */
public class Seller {

    private final String id;
    private final User user;
    private String businessInfo;
    private boolean active;

    public Seller(String id, User user, String businessInfo) {
        this.id = id;
        this.user = user;
        this.businessInfo = businessInfo;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public String getBusinessInfo() { return businessInfo; }
    public boolean isActive() { return active; }
}
