package com.nexusmarket.repository;

import com.nexusmarket.model.Return;

/** Persistence for {@link Return} entities. */
public class ReturnRepository extends InMemoryRepository<Return, String> {

    public ReturnRepository() {
        super(Return::getId);
    }
}
