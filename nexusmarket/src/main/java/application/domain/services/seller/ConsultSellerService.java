package application.domain.services.seller;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultSellerUseCase;

/** A seller sees only their own profile; administrators and supervisors see all. */
@Service
public class ConsultSellerService extends AuthorizedService implements ConsultSellerUseCase {

    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultSellerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Seller consultSeller(User actor, Seller seller) {
        validateStatus.execute(actor);
        provided(seller, "Seller");
        Seller persisted = found(sellerRepositoryPort.findById(idOf(seller.getId(), "Seller")), "Seller", seller.getId());
        if (!persisted.isOwnedBy(actor)) {
            validateRole.execute(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        }
        return persisted;
    }

    @Override
    public Seller consultMySeller(User actor) {
        authorize(actor, SystemRole.SELLER);
        return found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
    }

    @Override
    public List<Seller> consultAllSellers(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return sellerRepositoryPort.findAll();
    }
}
