package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;

/** Input Port: CU-12 confirm the delivery of a shipment, finishing its order (role LOGISTICS_OPERATOR). */
public interface ConfirmDeliveryUseCase {

    void confirmDelivery(User actor, Shipment shipment);
}
