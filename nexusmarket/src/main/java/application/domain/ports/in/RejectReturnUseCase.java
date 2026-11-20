package application.domain.ports.in;

import application.domain.models.Return;
import application.domain.models.User;

/** Input Port: CU-13 reject a return of an order with own products (role SELLER). */
public interface RejectReturnUseCase {

    void reject(User actor, Return productReturn);
}
