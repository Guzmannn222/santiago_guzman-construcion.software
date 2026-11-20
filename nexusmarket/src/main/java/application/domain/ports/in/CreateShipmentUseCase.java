package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;

/** Input Port: CU-11 create the shipment of a paid order with physical items (role LOGISTICS_OPERATOR). A null address uses the buyer primary address. */
public interface CreateShipmentUseCase {

    Shipment createShipment(User actor, Order order, String address);
}
