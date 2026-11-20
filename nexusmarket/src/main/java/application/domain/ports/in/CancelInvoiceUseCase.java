package application.domain.ports.in;

import application.domain.models.Invoice;
import application.domain.models.User;

/** Input Port: CU-10 cancel an invoice (role ADMINISTRATOR). */
public interface CancelInvoiceUseCase {

    void cancelInvoice(User actor, Invoice invoice);
}
