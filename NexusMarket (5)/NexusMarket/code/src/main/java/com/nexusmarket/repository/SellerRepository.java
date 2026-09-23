package com.nexusmarket.repository;

import com.nexusmarket.model.Seller;

/** Persistence for {@link Seller} entities. */
public class SellerRepository extends InMemoryRepository<Seller, String> {

    public SellerRepository() {
        super(Seller::getId);
    }
}
