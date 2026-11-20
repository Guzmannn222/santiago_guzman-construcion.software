package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.ReturnItem;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-13 request the return of an own delivered order (role BUYER). */
public interface RequestReturnUseCase {

    /** Returns the whole order. */
    default Return requestReturn(User actor, Order order, String reason) {
        return requestReturn(actor, order, reason, null);
    }

    /** Returns only the given product lines (null or empty = the whole order). */
    Return requestReturn(User actor, Order order, String reason, List<ReturnItem> items);
}
