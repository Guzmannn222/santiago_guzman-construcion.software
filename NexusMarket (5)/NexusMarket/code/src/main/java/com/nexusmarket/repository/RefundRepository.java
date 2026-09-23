package com.nexusmarket.repository;

import com.nexusmarket.model.Refund;

/** Persistence for {@link Refund} entities. */
public class RefundRepository extends InMemoryRepository<Refund, String> {

    public RefundRepository() {
        super(Refund::getId);
    }
}
