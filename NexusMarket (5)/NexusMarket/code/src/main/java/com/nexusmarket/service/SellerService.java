package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Seller;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.SellerRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/**
 * Business rules for {@link Seller} onboarding and lifecycle (SDD, CU-03).
 * Business rule: sellers are onboarded exclusively by an Administrator.
 */
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /** Use case: UC-09 Onboard seller. Allowed role: ADMINISTRATOR. */
    public Seller registerSeller(User actor, User sellerUser, String businessInfo) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        Seller seller = new Seller(IdGenerator.next("SLR"), sellerUser, businessInfo);
        return sellerRepository.save(seller);
    }

    /** Use case: UC-10 Deactivate seller. Allowed role: ADMINISTRATOR. */
    public void deactivate(User actor, Seller seller) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        seller.deactivate();
        sellerRepository.save(seller);
    }

    /** Use case: UC-11 Reactivate seller. Allowed role: ADMINISTRATOR. */
    public void reactivate(User actor, Seller seller) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        seller.reactivate();
        sellerRepository.save(seller);
    }
}
