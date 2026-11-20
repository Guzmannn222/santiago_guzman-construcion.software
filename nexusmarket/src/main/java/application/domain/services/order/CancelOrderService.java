package application.domain.services.order;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.CancelOrderUseCase;

/** Cancels an unpaid or paid (not yet shipped) order, releasing stock and cancelling its invoice. */
@Service
public class CancelOrderService extends AuthorizedService implements CancelOrderUseCase {

    private final LoadOrderForBuyerService loadOrderForBuyerService;
    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final InvoiceRepositoryPort invoiceRepositoryPort;

    public CancelOrderService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadOrderForBuyerService loadOrderForBuyerService, OrderRepositoryPort orderRepositoryPort,
            InventoryRepositoryPort inventoryRepositoryPort, InvoiceRepositoryPort invoiceRepositoryPort) {
        super(s, r, t);
        this.loadOrderForBuyerService = loadOrderForBuyerService;
        this.orderRepositoryPort = orderRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    @Override
    public void cancelOrder(User actor, Order order) {
        authorize(actor, SystemRole.BUYER, SystemRole.ADMINISTRATOR);
        Order persisted = loadOrderForBuyerService.execute(actor, order);
        persisted.cancel();
        orderRepositoryPort.update(persisted);
        if (persisted.getWarehouse() != null) {
            for (OrderItem item : persisted.getItems()) {
                if (!item.getProduct().isPhysical()) {
                    continue;
                }
                inventoryRepositoryPort.findByProductAndWarehouse(item.getProduct(), persisted.getWarehouse())
                        .ifPresent(inventory -> {
                            inventory.returnStock(item.getQuantity());
                            inventory.recordMovement(new InventoryMovement(InventoryMovementType.RETURN,
                                    item.getQuantity(), "Order " + persisted.getId() + " cancelled"));
                            inventoryRepositoryPort.update(inventory);
                        });
            }
        }
        invoiceRepositoryPort.findByOrderId(persisted.getId()).ifPresent(invoice -> {
            if (!invoice.isCancelled()) {
                invoice.cancel();
                invoiceRepositoryPort.update(invoice);
            }
        });
        register(actor, OperationType.ORDER_CANCELLATION, EntityType.ORDER, persisted.getId());
    }
}
