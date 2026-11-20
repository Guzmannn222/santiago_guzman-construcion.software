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
import application.domain.ports.in.UpdateShipmentStatusUseCase;

/** Intermediate tracking update (DISPATCHED -> IN_TRANSIT); delivery uses ConfirmDelivery. */
@Service
public class UpdateShipmentStatusService extends AuthorizedService implements UpdateShipmentStatusUseCase {

    private final ShipmentRepositoryPort shipmentRepositoryPort;

    public UpdateShipmentStatusService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ShipmentRepositoryPort shipmentRepositoryPort) {
        super(s, r, t);
        this.shipmentRepositoryPort = shipmentRepositoryPort;
    }

    @Override
    public void updateStatus(User actor, Shipment shipment, ShipmentStatus status) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR);
        provided(shipment, "Shipment");
        provided(status, "Shipment status");
        Shipment persisted = found(shipmentRepositoryPort.findById(idOf(shipment.getId(), "Shipment")), "Shipment",
                shipment.getId());
        ShipmentStatus previous = persisted.getStatus();
        persisted.updateStatus(status);
        shipmentRepositoryPort.update(persisted);
        register(actor, OperationType.SHIPMENT_STATUS_UPDATE, EntityType.SHIPMENT, persisted.getId(),
                "from", previous.getCode(), "to", status.getCode());
    }
}
