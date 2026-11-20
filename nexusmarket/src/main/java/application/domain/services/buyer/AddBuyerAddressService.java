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
import application.domain.ports.in.AddBuyerAddressUseCase;

/** A buyer adds an additional delivery address to their own profile. */
@Service
public class AddBuyerAddressService extends AuthorizedService implements AddBuyerAddressUseCase {

    private final BuyerRepositoryPort buyerRepositoryPort;

    public AddBuyerAddressService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            BuyerRepositoryPort buyerRepositoryPort) {
        super(s, r, t);
        this.buyerRepositoryPort = buyerRepositoryPort;
    }

    @Override
    public void addAddress(User actor, Buyer buyer, String address) {
        authorize(actor, SystemRole.BUYER);
        provided(buyer, "Buyer");
        Buyer persisted = found(buyerRepositoryPort.findById(idOf(buyer.getId(), "Buyer")), "Buyer", buyer.getId());
        if (!persisted.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only modify their own profile.");
        }
        persisted.addAddress(address);
        buyerRepositoryPort.update(persisted);
        register(actor, OperationType.BUYER_ADDRESS_ADDED, EntityType.BUYER, persisted.getId());
    }
}
