package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.User;

/** Input Port: CU-14 reject a refund (roles ADMINISTRATOR, SELLER). */
public interface RejectRefundUseCase {

    void reject(User actor, Refund refund);
}
