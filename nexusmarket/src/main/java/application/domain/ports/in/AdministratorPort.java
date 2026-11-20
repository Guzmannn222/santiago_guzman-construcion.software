package application.domain.ports.in;

import application.domain.models.AuditLog;
import application.domain.models.Invoice;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Report;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Every use case available to the ADMINISTRATOR role (CU-01, CU-03, CU-04, CU-10, CU-14, CU-15 and audit). */
public interface AdministratorPort {

    void changeMyPassword(User admin, String currentPassword, String newPassword);
    User registerUser(User admin, User newUser);
    void blockUser(User admin, User target);
    void activateUser(User admin, User target);
    void changeUserRole(User admin, User target, SystemRole newRole);
    User consultUser(User admin, User target);
    List<User> consultUsers(User admin);
    Seller registerSeller(User admin, User sellerUser, String businessInfo, String firstWarehouseLocation);
    void deactivateSeller(User admin, Seller seller);
    void reactivateSeller(User admin, Seller seller);
    List<Seller> consultSellers(User admin);
    Warehouse registerWarehouse(User admin, String location, WarehouseType type, Seller owner);
    void deactivateWarehouse(User admin, Warehouse warehouse);
    List<Warehouse> consultWarehouses(User admin);
    List<Order> consultOrders(User admin);
    Invoice generateInvoice(User admin, Order order, BigDecimal taxRate);
    void cancelInvoice(User admin, Invoice invoice);
    List<Invoice> consultInvoices(User admin);
    List<Return> consultReturns(User admin);
    Refund issueRefund(User admin, Return productReturn, Money amount);
    void processRefund(User admin, Refund refund);
    void completeRefund(User admin, Refund refund);
    void rejectRefund(User admin, Refund refund);
    List<Refund> consultRefunds(User admin);
    Report generateSalesReport(User admin, Map<String, Object> filters);
    Report generateInventoryReport(User admin, Map<String, Object> filters);
    Report generateReturnsReport(User admin, Map<String, Object> filters);
    List<AuditLog> consultAuditLogsByUser(User admin, User user);
    List<AuditLog> consultAuditLogsByEntity(User admin, EntityReference entity);
    List<Operation> consultOperationsByUser(User admin, User user);
}
