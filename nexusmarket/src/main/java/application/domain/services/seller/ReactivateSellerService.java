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
import application.domain.ports.in.ReactivateSellerUseCase;

/** Administrator reactivates a deactivated seller. */
@Service
public class ReactivateSellerService extends AuthorizedService implements ReactivateSellerUseCase {

    private final SellerRepositoryPort sellerRepositoryPort;

    public ReactivateSellerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public void reactivate(User actor, Seller seller) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(seller, "Seller");
        Seller persisted = found(sellerRepositoryPort.findById(idOf(seller.getId(), "Seller")), "Seller", seller.getId());
        persisted.reactivate();
        sellerRepositoryPort.update(persisted);
        register(actor, OperationType.SELLER_REACTIVATION, EntityType.SELLER, persisted.getId());
    }
}
