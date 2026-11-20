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
import application.domain.ports.in.CompleteRefundUseCase;
import application.domain.enums.NotificationChannel;

/** PROCESSING -> COMPLETED; the buyer is notified. */
@Service
public class CompleteRefundService extends AuthorizedService implements CompleteRefundUseCase {

    private final LoadRefundForReviewService loadRefundForReviewService;
    private final RefundRepositoryPort refundRepositoryPort;
    private final NotificationPort notificationPort;

    public CompleteRefundService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadRefundForReviewService loadRefundForReviewService, RefundRepositoryPort refundRepositoryPort,
            NotificationPort notificationPort) {
        super(s, r, t);
        this.loadRefundForReviewService = loadRefundForReviewService;
        this.refundRepositoryPort = refundRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void complete(User actor, Refund refund) {
        authorize(actor, SystemRole.SELLER, SystemRole.ADMINISTRATOR);
        Refund persisted = loadRefundForReviewService.execute(actor, refund);
        persisted.complete();
        refundRepositoryPort.update(persisted);
        register(actor, OperationType.REFUND_COMPLETION, EntityType.REFUND, persisted.getId(),
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
