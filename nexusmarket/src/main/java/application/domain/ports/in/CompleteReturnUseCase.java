package application.domain.ports.in;

import application.domain.models.Return;
import application.domain.models.User;

/** Input Port: CU-13 complete a return once goods are received back, restocking physical items (role LOGISTICS_OPERATOR). */
public interface CompleteReturnUseCase {

    void complete(User actor, Return productReturn);
}
