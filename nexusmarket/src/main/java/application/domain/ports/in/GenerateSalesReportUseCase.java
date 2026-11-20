package application.domain.ports.in;

import application.domain.models.Report;
import application.domain.models.User;
import java.util.Map;

/** Input Port: CU-15 sales report (roles SUPERVISOR, ADMINISTRATOR; read-only). */
public interface GenerateSalesReportUseCase {

    Report generateSalesReport(User actor, Map<String, Object> filters);
}
