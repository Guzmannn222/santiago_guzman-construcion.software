package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

/** Input Port: CU-11 prepare a shipment (role LOGISTICS_OPERATOR). */
public interface PrepareShipmentUseCase {

    void prepare(User actor, Shipment shipment);
}
