package application.domain.services.invoice;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultInvoiceUseCase;

/** Buyers see only the invoices of their own orders; administrators and supervisors see all. */
@Service
public class ConsultInvoiceService extends AuthorizedService implements ConsultInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;

    public ConsultInvoiceService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            InvoiceRepositoryPort invoiceRepositoryPort, OrderRepositoryPort orderRepositoryPort,
            BuyerRepositoryPort buyerRepositoryPort) {
        super(s, r, t);
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
    }

    @Override
    public Invoice consultInvoice(User actor, Invoice invoice) {
        authorize(actor, SystemRole.BUYER, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        provided(invoice, "Invoice");
        Invoice persisted = found(invoiceRepositoryPort.findById(idOf(invoice.getId(), "Invoice")), "Invoice",
                invoice.getId());
        if (SystemRole.BUYER.equals(actor.getRole()) && !persisted.getOrder().isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult invoices of their own orders.");
        }
        return persisted;
    }

    @Override
    public List<Invoice> consultAllInvoices(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return invoiceRepositoryPort.findAll();
    }

    @Override
    public List<Invoice> consultMyInvoices(User actor) {
        authorize(actor, SystemRole.BUYER);
        Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
        List<Invoice> mine = new ArrayList<>();
        for (Order order : orderRepositoryPort.findByBuyer(buyer)) {
            invoiceRepositoryPort.findByOrderId(order.getId()).ifPresent(mine::add);
        }
        return mine;
    }
}
