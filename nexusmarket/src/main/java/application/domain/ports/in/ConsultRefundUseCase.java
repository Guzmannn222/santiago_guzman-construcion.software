package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-14 consult refunds. BUYER sees its own, SELLER the ones of its products. */
public interface ConsultRefundUseCase {

    Refund consultRefund(User actor, Refund refund);
    List<Refund> consultAllRefunds(User actor);
    List<Refund> consultMyRefunds(User actor);
}
