package application.domain.services.shipment;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.PrepareShipmentUseCase;
import application.domain.enums.NotificationChannel;

/** PENDING -> PREPARING. */
@Service
public class PrepareShipmentService extends AuthorizedService implements PrepareShipmentUseCase {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final NotificationPort notificationPort;

    public PrepareShipmentService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ShipmentRepositoryPort shipmentRepositoryPort, OrderRepositoryPort orderRepositoryPort,
            NotificationPort notificationPort) {
        super(s, r, t);
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void prepare(User actor, Shipment shipment) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR);
        provided(shipment, "Shipment");
        Shipment persisted = found(shipmentRepositoryPort.findById(idOf(shipment.getId(), "Shipment")), "Shipment",
                shipment.getId());
        persisted.prepare();
        shipmentRepositoryPort.update(persisted);
        orderRepositoryPort.update(persisted.getOrder());
        register(actor, OperationType.SHIPMENT_PREPARATION, EntityType.SHIPMENT, persisted.getId(),
                "orderId", persisted.getOrder().getId(), "status", persisted.getStatus().getCode());
        if (false) {
            try {
                notificationPort.notifyUser(persisted.getOrder().getBuyer().getUser(),
                        "Shipment of order " + persisted.getOrder().getId() + " is now " + persisted.getStatus().getCode() + ".",
                        NotificationChannel.EMAIL);
            } catch (RuntimeException ignored) {
                // Notifications are best effort.
            }
        }
    }
}
