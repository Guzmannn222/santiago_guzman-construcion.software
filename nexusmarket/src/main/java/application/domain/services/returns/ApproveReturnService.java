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
import application.domain.ports.in.ApproveReturnUseCase;

/** The seller (or admin) approves a REQUESTED return. */
@Service
public class ApproveReturnService extends AuthorizedService implements ApproveReturnUseCase {

    private final LoadReturnForReviewService loadReturnForReviewService;
    private final ReturnRepositoryPort returnRepositoryPort;

    public ApproveReturnService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadReturnForReviewService loadReturnForReviewService, ReturnRepositoryPort returnRepositoryPort) {
        super(s, r, t);
        this.loadReturnForReviewService = loadReturnForReviewService;
        this.returnRepositoryPort = returnRepositoryPort;
    }

    @Override
    public void approve(User actor, Return productReturn) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Return persisted = loadReturnForReviewService.execute(actor, productReturn);
        persisted.approve();
        returnRepositoryPort.update(persisted);

        register(actor, OperationType.RETURN_APPROVAL, EntityType.RETURN, persisted.getId(),
                "orderId", persisted.getOrder().getId(), "status", persisted.getStatus().getCode());
    }
}
