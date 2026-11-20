package application.domain.ports.in;

import application.domain.models.Operation;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import java.util.List;

/** Input Port: consult the operations log (roles SUPERVISOR, ADMINISTRATOR). */
public interface ConsultOperationsUseCase {

    List<Operation> consultByUser(User actor, User user);
    List<Operation> consultByEntity(User actor, EntityReference entity);
}
