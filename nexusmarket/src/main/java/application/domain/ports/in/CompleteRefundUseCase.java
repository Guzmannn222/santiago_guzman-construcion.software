package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.User;

/** Input Port: CU-14 complete a refund (roles ADMINISTRATOR, SELLER). */
public interface CompleteRefundUseCase {

    void complete(User actor, Refund refund);
}
