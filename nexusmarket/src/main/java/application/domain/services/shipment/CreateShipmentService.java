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
import application.domain.ports.in.CreateShipmentUseCase;

/** Creates the shipment of a paid order with physical items; a blank address defaults to the buyer's primary one. */
@Service
public class CreateShipmentService extends AuthorizedService implements CreateShipmentUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public CreateShipmentService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            OrderRepositoryPort orderRepositoryPort, ShipmentRepositoryPort shipmentRepositoryPort) {
        super(s, r, t);
        this.orderRepositoryPort = orderRepositoryPort;
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    @Override
    public Shipment createShipment(User actor, Order order, String address) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR);
        provided(order, "Order");
        Order persisted = found(orderRepositoryPort.findById(idOf(order.getId(), "Order")), "Order", order.getId());
        if (!OrderStatus.PAID.equals(persisted.getStatus())) {
            throw new InvalidOrderStateException("A shipment can only be created for a paid order.");
        }
        if (shipmentRepositoryPort.findByOrderId(persisted.getId()).isPresent()) {
            throw new DuplicateResourceException("The order already has a shipment.");
        }
        String destination = (address == null || address.isBlank()) ? persisted.getBuyer().getPrimaryAddress() : address;
        Shipment saved = shipmentRepositoryPort.save(new Shipment(persisted, destination));
        register(actor, OperationType.SHIPMENT_CREATION, EntityType.SHIPMENT, saved.getId(),
                "orderId", persisted.getId(), "address", saved.getAddress());
        return saved;
    }
}
