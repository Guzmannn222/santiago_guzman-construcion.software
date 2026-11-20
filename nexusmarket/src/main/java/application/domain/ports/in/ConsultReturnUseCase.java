package application.domain.ports.in;

import application.domain.models.Return;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-13 consult returns. BUYER sees its own, SELLER the ones of its products. */
public interface ConsultReturnUseCase {

    Return consultReturn(User actor, Return productReturn);
    List<Return> consultAllReturns(User actor);
    List<Return> consultMyReturns(User actor);
}
