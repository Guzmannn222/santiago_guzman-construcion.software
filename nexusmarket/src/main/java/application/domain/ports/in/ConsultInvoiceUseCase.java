package application.domain.ports.in;

import application.domain.models.Invoice;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-10 consult invoices. BUYER only sees invoices of its own orders. */
public interface ConsultInvoiceUseCase {

    Invoice consultInvoice(User actor, Invoice invoice);
    List<Invoice> consultAllInvoices(User actor);
    List<Invoice> consultMyInvoices(User actor);
}
