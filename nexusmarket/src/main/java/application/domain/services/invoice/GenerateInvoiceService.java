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
import application.domain.ports.in.GenerateInvoiceUseCase;
import java.math.BigDecimal;

/** Administrator issues the (single active) invoice of a paid order; null tax rate = configured default. */
@Service
public class GenerateInvoiceService extends AuthorizedService implements GenerateInvoiceUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;

    public GenerateInvoiceService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            OrderRepositoryPort orderRepositoryPort, InvoiceRepositoryPort invoiceRepositoryPort,
            BusinessConfigurationPort businessConfigurationPort) {
        super(s, r, t);
        this.orderRepositoryPort = orderRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
    }

    @Override
    public Invoice generateInvoice(User actor, Order order, BigDecimal taxRate) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(order, "Order");
        Order persisted = found(orderRepositoryPort.findById(idOf(order.getId(), "Order")), "Order", order.getId());
        Optional<Invoice> existing = invoiceRepositoryPort.findByOrderId(persisted.getId());
        if (existing.isPresent() && !existing.get().isCancelled()) {
            throw new DuplicateResourceException("The order already has an active invoice.");
        }
        BigDecimal rate = taxRate != null ? taxRate : businessConfigurationPort.getDefaultTaxRate();
        Invoice invoice = new Invoice(persisted);
        invoice.generate(rate);
        Invoice saved = invoiceRepositoryPort.save(invoice);
        register(actor, OperationType.INVOICE_GENERATION, EntityType.INVOICE, saved.getId(),
                "orderId", persisted.getId(), "taxRate", rate.toPlainString(), "total", String.valueOf(saved.getTotal()));
        return saved;
    }
}
