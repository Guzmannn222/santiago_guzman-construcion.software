package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Invoice;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.InvoiceRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for billing (SDD, Domain 8, CU-10). */
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /** Use case: UC-35 Generate invoice for a paid order. Allowed role: ADMINISTRATOR. */
    public Invoice generateInvoice(User actor, Order order, double taxRate) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        Invoice invoice = new Invoice(IdGenerator.next("INV"), order);
        invoice.generate(taxRate);
        return invoiceRepository.save(invoice);
    }

    /** Use case: UC-36 Cancel invoice. Allowed role: ADMINISTRATOR. */
    public void cancelInvoice(User actor, Invoice invoice) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        invoice.cancel();
        invoiceRepository.save(invoice);
    }
}
