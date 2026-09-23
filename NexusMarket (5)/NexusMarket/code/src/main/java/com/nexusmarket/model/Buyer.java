package com.nexusmarket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Buyer user and their delivery addresses (SDD, Domain 2).
 * Business rule: a buyer never manages another buyer's information nor any inventory data.
 */
public class Buyer {

    private final String id;
    private final User user;
    private String primaryAddress;
    private final List<String> additionalAddresses = new ArrayList<>();

    public Buyer(String id, User user, String primaryAddress) {
        this.id = id;
        this.user = user;
        this.primaryAddress = primaryAddress;
    }

    /** Adds an optional secondary delivery address. */
    public void addAddress(String address) {
        this.additionalAddresses.add(address);
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public String getPrimaryAddress() { return primaryAddress; }
    public List<String> getAdditionalAddresses() { return additionalAddresses; }
}
