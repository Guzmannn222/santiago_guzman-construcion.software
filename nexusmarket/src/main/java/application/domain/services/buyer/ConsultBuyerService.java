package application.domain.services.buyer;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultBuyerUseCase;

/** Buyers see only their own profile; administrators and supervisors see all. */
@Service
public class ConsultBuyerService extends AuthorizedService implements ConsultBuyerUseCase {

    private final BuyerRepositoryPort buyerRepositoryPort;

    public ConsultBuyerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            BuyerRepositoryPort buyerRepositoryPort) {
        super(s, r, t);
        this.buyerRepositoryPort = buyerRepositoryPort;
    }

    @Override
    public Buyer consultBuyer(User actor, Buyer buyer) {
        validateStatus.execute(actor);
        provided(buyer, "Buyer");
        Buyer persisted = found(buyerRepositoryPort.findById(idOf(buyer.getId(), "Buyer")), "Buyer", buyer.getId());
        if (!persisted.isOwnedBy(actor)) {
            validateRole.execute(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        }
        return persisted;
    }

    @Override
    public Buyer consultMyBuyer(User actor) {
        authorize(actor, SystemRole.BUYER);
        return found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
    }

    @Override
    public List<Buyer> consultAllBuyers(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return buyerRepositoryPort.findAll();
    }
}
