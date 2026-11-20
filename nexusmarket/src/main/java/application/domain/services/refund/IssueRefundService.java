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
import application.domain.ports.in.IssueRefundUseCase;

/** Issues the (single) refund of an approved/completed return; amount cannot exceed the order total. */
@Service
public class IssueRefundService extends AuthorizedService implements IssueRefundUseCase {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final RefundRepositoryPort refundRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public IssueRefundService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ReturnRepositoryPort returnRepositoryPort, RefundRepositoryPort refundRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.returnRepositoryPort = returnRepositoryPort;
        this.refundRepositoryPort = refundRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Refund issueRefund(User actor, Return productReturn, Money amount) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        provided(productReturn, "Return");
        provided(amount, "Refund amount");
        Return persisted = found(returnRepositoryPort.findById(idOf(productReturn.getId(), "Return")), "Return",
                productReturn.getId());
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            if (!persisted.involvesProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only refund returns of their own products.");
            }
        }
        Optional<Refund> existing = refundRepositoryPort.findByReturnId(persisted.getId());
        if (existing.isPresent() && !RefundStatus.REJECTED.equals(existing.get().getStatus())) {
            throw new DuplicateResourceException("The return already has a refund.");
        }
        Refund saved = refundRepositoryPort.save(new Refund(persisted, amount));
        register(actor, OperationType.REFUND_ISSUE, EntityType.REFUND, saved.getId(),
                "returnId", persisted.getId(), "amount", String.valueOf(amount));
        return saved;
    }
}
