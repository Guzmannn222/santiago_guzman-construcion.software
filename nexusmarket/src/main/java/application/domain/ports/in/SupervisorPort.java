package application.domain.ports.in;

import application.domain.models.AuditLog;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Report;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.OperationType;
import java.util.List;
import java.util.Map;

/** Every use case available to the SUPERVISOR role (CU-15): read-only monitoring, it never mutates orders, inventory or refunds. */
public interface SupervisorPort {

    void changeMyPassword(User supervisor, String currentPassword, String newPassword);
    Report generateSalesReport(User supervisor, Map<String, Object> filters);
    Report generateInventoryReport(User supervisor, Map<String, Object> filters);
    Report generateReturnsReport(User supervisor, Map<String, Object> filters);
    List<Order> consultOrders(User supervisor);
    List<Inventory> consultInventory(User supervisor);
    List<Return> consultReturns(User supervisor);
    List<Refund> consultRefunds(User supervisor);
    List<AuditLog> consultAuditLogsByUser(User supervisor, User user);
    List<AuditLog> consultAuditLogsByEntity(User supervisor, EntityReference entity);
    List<AuditLog> consultAuditLogsByOperationType(User supervisor, OperationType operationType);
    List<Operation> consultOperationsByUser(User supervisor, User user);
    List<Operation> consultOperationsByEntity(User supervisor, EntityReference entity);
}
