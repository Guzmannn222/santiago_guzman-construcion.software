package application.domain.ports.in;

import application.domain.models.Shipment;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-11/CU-12 consult shipments. BUYER only sees shipments of its own orders. */
public interface ConsultShipmentUseCase {

    Shipment consultShipment(User actor, Shipment shipment);
    List<Shipment> consultAllShipments(User actor);
    List<Shipment> consultMyShipments(User actor);
}
