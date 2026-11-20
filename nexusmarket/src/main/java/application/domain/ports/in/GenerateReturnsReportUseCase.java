package application.domain.ports.in;

import application.domain.models.Report;
import application.domain.models.User;
import java.util.Map;

/** Input Port: CU-15 returns and refunds report (roles SUPERVISOR, ADMINISTRATOR; read-only). */
public interface GenerateReturnsReportUseCase {

    Report generateReturnsReport(User actor, Map<String, Object> filters);
}
