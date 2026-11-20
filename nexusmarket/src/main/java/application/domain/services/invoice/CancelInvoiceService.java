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
import application.domain.ports.in.CancelInvoiceUseCase;

@Service
public class CancelInvoiceService extends AuthorizedService implements CancelInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;

    public CancelInvoiceService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            InvoiceRepositoryPort invoiceRepositoryPort) {
        super(s, r, t);
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    @Override
    public void cancelInvoice(User actor, Invoice invoice) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(invoice, "Invoice");
        Invoice persisted = found(invoiceRepositoryPort.findById(idOf(invoice.getId(), "Invoice")), "Invoice",
                invoice.getId());
        persisted.cancel();
        invoiceRepositoryPort.update(persisted);
        register(actor, OperationType.INVOICE_CANCELLATION, EntityType.INVOICE, persisted.getId());
    }
}
