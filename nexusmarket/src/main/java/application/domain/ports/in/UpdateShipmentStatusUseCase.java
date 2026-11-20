package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.valueobjects.ShipmentStatus;

/** Input Port: CU-11 update the tracking status of a shipment (role LOGISTICS_OPERATOR). */
public interface UpdateShipmentStatusUseCase {

    void updateStatus(User actor, Shipment shipment, ShipmentStatus status);
}
