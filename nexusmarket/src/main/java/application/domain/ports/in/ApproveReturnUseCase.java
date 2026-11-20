package application.domain.ports.in;

import application.domain.models.Return;
import application.domain.models.User;

/** Input Port: CU-13 approve a return of an order with own products (role SELLER). */
public interface ApproveReturnUseCase {

    void approve(User actor, Return productReturn);
}
