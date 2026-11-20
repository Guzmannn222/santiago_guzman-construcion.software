package application.adapters.useCases;

import application.domain.models.AuditLog;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Report;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.ConsultAuditLogsUseCase;
import application.domain.ports.in.ConsultInventoryUseCase;
import application.domain.ports.in.ConsultOperationsUseCase;
import application.domain.ports.in.ConsultOrderUseCase;
import application.domain.ports.in.ConsultRefundUseCase;
import application.domain.ports.in.ConsultReturnUseCase;
import application.domain.ports.in.GenerateInventoryReportUseCase;
import application.domain.ports.in.GenerateReturnsReportUseCase;
import application.domain.ports.in.GenerateSalesReportUseCase;
import application.domain.ports.in.SupervisorPort;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Role facade: delegates every Supervisor operation to its input-port use case (each one validates status, role and ownership). */
@Service
public class SupervisorUseCaseImpl implements SupervisorPort {

    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final GenerateSalesReportUseCase generateSalesReportUseCase;
    private final GenerateInventoryReportUseCase generateInventoryReportUseCase;
    private final GenerateReturnsReportUseCase generateReturnsReportUseCase;
    private final ConsultOrderUseCase consultOrderUseCase;
    private final ConsultInventoryUseCase consultInventoryUseCase;
    private final ConsultReturnUseCase consultReturnUseCase;
    private final ConsultRefundUseCase consultRefundUseCase;
    private final ConsultAuditLogsUseCase consultAuditLogsUseCase;
    private final ConsultOperationsUseCase consultOperationsUseCase;

    public SupervisorUseCaseImpl(ChangeUserPasswordUseCase changeUserPasswordUseCase,
            GenerateSalesReportUseCase generateSalesReportUseCase,
            GenerateInventoryReportUseCase generateInventoryReportUseCase,
            GenerateReturnsReportUseCase generateReturnsReportUseCase,
            ConsultOrderUseCase consultOrderUseCase,
            ConsultInventoryUseCase consultInventoryUseCase,
            ConsultReturnUseCase consultReturnUseCase,
            ConsultRefundUseCase consultRefundUseCase,
            ConsultAuditLogsUseCase consultAuditLogsUseCase,
            ConsultOperationsUseCase consultOperationsUseCase) {
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.generateSalesReportUseCase = generateSalesReportUseCase;
        this.generateInventoryReportUseCase = generateInventoryReportUseCase;
        this.generateReturnsReportUseCase = generateReturnsReportUseCase;
        this.consultOrderUseCase = consultOrderUseCase;
        this.consultInventoryUseCase = consultInventoryUseCase;
        this.consultReturnUseCase = consultReturnUseCase;
        this.consultRefundUseCase = consultRefundUseCase;
        this.consultAuditLogsUseCase = consultAuditLogsUseCase;
        this.consultOperationsUseCase = consultOperationsUseCase;
    }

    @Override
    public void changeMyPassword(User supervisor, String currentPassword, String newPassword) {
        changeUserPasswordUseCase.changePassword(supervisor, currentPassword, newPassword);
    }

    @Override
    public Report generateSalesReport(User supervisor, Map<String, Object> filters) {
        return generateSalesReportUseCase.generateSalesReport(supervisor, filters);
    }

    @Override
    public Report generateInventoryReport(User supervisor, Map<String, Object> filters) {
        return generateInventoryReportUseCase.generateInventoryReport(supervisor, filters);
    }

    @Override
    public Report generateReturnsReport(User supervisor, Map<String, Object> filters) {
        return generateReturnsReportUseCase.generateReturnsReport(supervisor, filters);
    }

    @Override
    public List<Order> consultOrders(User supervisor) {
        return consultOrderUseCase.consultAllOrders(supervisor);
    }

    @Override
    public List<Inventory> consultInventory(User supervisor) {
        return consultInventoryUseCase.consultAllInventory(supervisor);
    }

    @Override
    public List<Return> consultReturns(User supervisor) {
        return consultReturnUseCase.consultAllReturns(supervisor);
    }

    @Override
    public List<Refund> consultRefunds(User supervisor) {
        return consultRefundUseCase.consultAllRefunds(supervisor);
    }

    @Override
    public List<AuditLog> consultAuditLogsByUser(User supervisor, User user) {
        return consultAuditLogsUseCase.consultByUser(supervisor, user);
    }

    @Override
    public List<AuditLog> consultAuditLogsByEntity(User supervisor, EntityReference entity) {
        return consultAuditLogsUseCase.consultByEntity(supervisor, entity);
    }

    @Override
    public List<AuditLog> consultAuditLogsByOperationType(User supervisor, OperationType operationType) {
        return consultAuditLogsUseCase.consultByOperationType(supervisor, operationType);
    }

    @Override
    public List<Operation> consultOperationsByUser(User supervisor, User user) {
        return consultOperationsUseCase.consultByUser(supervisor, user);
    }

    @Override
    public List<Operation> consultOperationsByEntity(User supervisor, EntityReference entity) {
        return consultOperationsUseCase.consultByEntity(supervisor, entity);
    }
}
