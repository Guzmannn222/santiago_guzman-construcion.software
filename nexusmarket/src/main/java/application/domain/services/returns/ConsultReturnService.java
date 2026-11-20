package application.domain.services.returns;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultReturnUseCase;

/** Buyers: own returns; sellers: returns of their products; operational roles: all. */
@Service
public class ConsultReturnService extends AuthorizedService implements ConsultReturnUseCase {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultReturnService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ReturnRepositoryPort returnRepositoryPort, BuyerRepositoryPort buyerRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.returnRepositoryPort = returnRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Return consultReturn(User actor, Return productReturn) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        provided(productReturn, "Return");
        Return persisted = found(returnRepositoryPort.findById(idOf(productReturn.getId(), "Return")), "Return",
                productReturn.getId());
        if (SystemRole.BUYER.equals(actor.getRole()) && !persisted.getOrder().isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult their own returns.");
        }
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            if (!persisted.involvesProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only consult returns of their products.");
            }
        }
        return persisted;
    }

    @Override
    public List<Return> consultAllReturns(User actor) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return returnRepositoryPort.findAll();
    }

    @Override
    public List<Return> consultMyReturns(User actor) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER);
        List<Return> mine = new ArrayList<>();
        if (SystemRole.BUYER.equals(actor.getRole())) {
            Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
            for (Return candidate : returnRepositoryPort.findAll()) {
                if (candidate.getOrder().getBuyer() != null
                        && buyer.getId().equals(candidate.getOrder().getBuyer().getId())) {
                    mine.add(candidate);
                }
            }
            return mine;
        }
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        for (Return candidate : returnRepositoryPort.findAll()) {
            if (candidate.involvesProductOf(seller)) {
                mine.add(candidate);
            }
        }
        return mine;
    }
}
