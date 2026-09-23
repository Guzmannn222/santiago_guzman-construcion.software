package com.nexusmarket.repository;

import com.nexusmarket.model.Invoice;

/** Persistence for {@link Invoice} entities. */
public class InvoiceRepository extends InMemoryRepository<Invoice, String> {

    public InvoiceRepository() {
        super(Invoice::getId);
    }
}
