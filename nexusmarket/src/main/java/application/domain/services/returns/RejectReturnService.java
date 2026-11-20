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
import application.domain.ports.in.RejectReturnUseCase;

/** The seller (or admin) rejects a REQUESTED return. */
@Service
public class RejectReturnService extends AuthorizedService implements RejectReturnUseCase {

    private final LoadReturnForReviewService loadReturnForReviewService;
    private final ReturnRepositoryPort returnRepositoryPort;

    public RejectReturnService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadReturnForReviewService loadReturnForReviewService, ReturnRepositoryPort returnRepositoryPort) {
        super(s, r, t);
        this.loadReturnForReviewService = loadReturnForReviewService;
        this.returnRepositoryPort = returnRepositoryPort;
    }

    @Override
    public void reject(User actor, Return productReturn) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Return persisted = loadReturnForReviewService.execute(actor, productReturn);
        persisted.reject();
        returnRepositoryPort.update(persisted);

        register(actor, OperationType.RETURN_REJECTION, EntityType.RETURN, persisted.getId(),
                "orderId", persisted.getOrder().getId(), "status", persisted.getStatus().getCode());
    }
}
