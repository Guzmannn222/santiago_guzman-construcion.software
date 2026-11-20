package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.User;

/** Input Port: CU-14 start processing a refund (roles ADMINISTRATOR, SELLER). */
public interface ProcessRefundUseCase {

    void process(User actor, Refund refund);
}
