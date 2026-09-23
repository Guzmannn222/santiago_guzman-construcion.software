package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Buyer;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.BuyerRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for {@link Buyer} self-registration and address management (SDD, CU-02). */
public class BuyerService {

    private final BuyerRepository buyerRepository;

    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    /** Use case: UC-07 Register buyer profile. Allowed role: BUYER (self-service, right after account creation). */
    public Buyer registerBuyer(User actor, String primaryAddress) {
        AccessControl.requireRole(actor, Role.BUYER);
        Buyer buyer = new Buyer(IdGenerator.next("BUY"), actor, primaryAddress);
        return buyerRepository.save(buyer);
    }

    /** Use case: UC-08 Add a secondary delivery address. Allowed role: BUYER (owner of the profile). */
    public void addAddress(User actor, Buyer buyer, String address) {
        AccessControl.requireSelfOrRole(actor, buyer.getUser(), Role.ADMINISTRATOR);
        buyer.addAddress(address);
        buyerRepository.save(buyer);
    }
}
