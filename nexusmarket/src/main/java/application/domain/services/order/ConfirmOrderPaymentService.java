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
import application.domain.ports.in.ConfirmOrderPaymentUseCase;
import application.domain.enums.NotificationChannel;

/** Marks the order as PAID; orders made only of digital products are delivered immediately. */
@Service
public class ConfirmOrderPaymentService extends AuthorizedService implements ConfirmOrderPaymentUseCase {

    private final LoadOrderForBuyerService loadOrderForBuyerService;
    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;

    public ConfirmOrderPaymentService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadOrderForBuyerService loadOrderForBuyerService, OrderRepositoryPort orderRepositoryPort,
            NotificationPort notificationPort) {
        super(s, r, t);
        this.loadOrderForBuyerService = loadOrderForBuyerService;
        this.orderRepositoryPort = orderRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void confirmPayment(User actor, Order order) {
        authorize(actor, SystemRole.BUYER, SystemRole.ADMINISTRATOR);
        Order persisted = loadOrderForBuyerService.execute(actor, order);
        persisted.confirmPayment();
        if (!persisted.hasPhysicalItems()) {
            persisted.deliverDigital();
        }
        orderRepositoryPort.update(persisted);
        register(actor, OperationType.ORDER_PAYMENT, EntityType.ORDER, persisted.getId(),
                "total", String.valueOf(persisted.getTotal()), "status", persisted.getStatus().getCode());
        try {
            notificationPort.notifyUser(persisted.getBuyer().getUser(),
                    "Payment received for order " + persisted.getId() + ".", NotificationChannel.EMAIL);
        } catch (RuntimeException ignored) {
            // A notification failure must never undo a confirmed payment.
        }
    }
}
