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
import application.domain.ports.in.ConsultShipmentUseCase;

/** Buyers: shipments of own orders; sellers: containing their products; operational roles: all. */
@Service
public class ConsultShipmentService extends AuthorizedService implements ConsultShipmentUseCase {

    private final ShipmentRepositoryPort shipmentRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultShipmentService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ShipmentRepositoryPort shipmentRepositoryPort, OrderRepositoryPort orderRepositoryPort,
            BuyerRepositoryPort buyerRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.shipmentRepositoryPort = shipmentRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Shipment consultShipment(User actor, Shipment shipment) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        provided(shipment, "Shipment");
        Shipment persisted = found(shipmentRepositoryPort.findById(idOf(shipment.getId(), "Shipment")), "Shipment",
                shipment.getId());
        if (SystemRole.BUYER.equals(actor.getRole()) && !persisted.getOrder().isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult shipments of their own orders.");
        }
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            if (!persisted.getOrder().containsProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only consult shipments with their products.");
            }
        }
        return persisted;
    }

    @Override
    public List<Shipment> consultAllShipments(User actor) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return shipmentRepositoryPort.findAll();
    }

    @Override
    public List<Shipment> consultMyShipments(User actor) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER);
        List<Shipment> mine = new ArrayList<>();
        if (SystemRole.BUYER.equals(actor.getRole())) {
            Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
            for (Order order : orderRepositoryPort.findByBuyer(buyer)) {
                shipmentRepositoryPort.findByOrderId(order.getId()).ifPresent(mine::add);
            }
            return mine;
        }
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        for (Shipment candidate : shipmentRepositoryPort.findAll()) {
            if (candidate.getOrder().containsProductOf(seller)) {
                mine.add(candidate);
            }
        }
        return mine;
    }
}
