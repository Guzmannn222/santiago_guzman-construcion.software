package application.domain.ports.in;

import application.domain.models.Report;
import application.domain.models.User;
import java.util.Map;

/** Input Port: CU-15 inventory report (roles SUPERVISOR, ADMINISTRATOR; read-only). */
public interface GenerateInventoryReportUseCase {

    Report generateInventoryReport(User actor, Map<String, Object> filters);
}
