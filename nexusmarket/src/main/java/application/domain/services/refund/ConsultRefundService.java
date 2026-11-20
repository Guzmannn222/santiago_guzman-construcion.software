package application.domain.services.refund;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultRefundUseCase;

/** Buyers: refunds of own orders; sellers: of their products; admin/supervisor: all. */
@Service
public class ConsultRefundService extends AuthorizedService implements ConsultRefundUseCase {

    private final RefundRepositoryPort refundRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultRefundService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            RefundRepositoryPort refundRepositoryPort, BuyerRepositoryPort buyerRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.refundRepositoryPort = refundRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Refund consultRefund(User actor, Refund refund) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        provided(refund, "Refund");
        Refund persisted = found(refundRepositoryPort.findById(idOf(refund.getId(), "Refund")), "Refund",
                refund.getId());
        Order order = persisted.getProductReturn().getOrder();
        if (SystemRole.BUYER.equals(actor.getRole()) && !order.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult their own refunds.");
        }
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            if (!persisted.getProductReturn().involvesProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only consult refunds of their products.");
            }
        }
        return persisted;
    }

    @Override
    public List<Refund> consultAllRefunds(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return refundRepositoryPort.findAll();
    }

    @Override
    public List<Refund> consultMyRefunds(User actor) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER);
        List<Refund> mine = new ArrayList<>();
        if (SystemRole.BUYER.equals(actor.getRole())) {
            Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
            for (Refund candidate : refundRepositoryPort.findAll()) {
                Buyer owner = candidate.getProductReturn().getOrder().getBuyer();
                if (owner != null && buyer.getId().equals(owner.getId())) {
                    mine.add(candidate);
                }
            }
            return mine;
        }
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        for (Refund candidate : refundRepositoryPort.findAll()) {
            if (candidate.getProductReturn().involvesProductOf(seller)) {
                mine.add(candidate);
            }
        }
        return mine;
    }
}
