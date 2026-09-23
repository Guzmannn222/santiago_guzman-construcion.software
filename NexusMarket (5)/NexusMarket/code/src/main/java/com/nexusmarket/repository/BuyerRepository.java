package com.nexusmarket.repository;

import com.nexusmarket.model.Buyer;

/** Persistence for {@link Buyer} entities. */
public class BuyerRepository extends InMemoryRepository<Buyer, String> {

    public BuyerRepository() {
        super(Buyer::getId);
    }
}
