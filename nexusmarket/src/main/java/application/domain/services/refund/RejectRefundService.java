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
import application.domain.ports.in.RejectRefundUseCase;
import application.domain.enums.NotificationChannel;

/** Rejects a refund that is not completed; the buyer is notified. */
@Service
public class RejectRefundService extends AuthorizedService implements RejectRefundUseCase {

    private final LoadRefundForReviewService loadRefundForReviewService;
    private final RefundRepositoryPort refundRepositoryPort;
    private final NotificationPort notificationPort;

    public RejectRefundService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadRefundForReviewService loadRefundForReviewService, RefundRepositoryPort refundRepositoryPort,
            NotificationPort notificationPort) {
        super(s, r, t);
        this.loadRefundForReviewService = loadRefundForReviewService;
        this.refundRepositoryPort = refundRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void reject(User actor, Refund refund) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Refund persisted = loadRefundForReviewService.execute(actor, refund);
        persisted.reject();
        refundRepositoryPort.update(persisted);
        register(actor, OperationType.REFUND_REJECTION, EntityType.REFUND, persisted.getId(),
                "status", persisted.getStatus().getCode(), "amount", String.valueOf(persisted.getAmount()));
        if (true) {
            try {
                notificationPort.notifyUser(persisted.getProductReturn().getOrder().getBuyer().getUser(),
                        "Your refund " + persisted.getId() + " is now " + persisted.getStatus().getCode() + ".",
                        NotificationChannel.EMAIL);
            } catch (RuntimeException ignored) {
                // Notifications are best effort.
            }
        }
    }
}
