package application.domain.ports.in;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import java.math.BigDecimal;

/** Input Port: CU-10 generate the invoice of a paid order (role ADMINISTRATOR). A null tax rate uses the configured default. */
public interface GenerateInvoiceUseCase {

    Invoice generateInvoice(User actor, Order order, BigDecimal taxRate);
}
