package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

/** Input Port: CU-11 dispatch a shipment (role LOGISTICS_OPERATOR). */
public interface DispatchShipmentUseCase {

    void dispatch(User actor, Shipment shipment);
}
