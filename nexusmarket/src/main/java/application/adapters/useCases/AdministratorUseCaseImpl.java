package application.adapters.useCases;

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
import application.domain.ports.in.ActivateUserUseCase;
import application.domain.ports.in.AdministratorPort;
import application.domain.ports.in.BlockUserUseCase;
import application.domain.ports.in.CancelInvoiceUseCase;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.ChangeUserRoleUseCase;
import application.domain.ports.in.CompleteRefundUseCase;
import application.domain.ports.in.ConsultAuditLogsUseCase;
import application.domain.ports.in.ConsultInvoiceUseCase;
import application.domain.ports.in.ConsultOperationsUseCase;
import application.domain.ports.in.ConsultOrderUseCase;
import application.domain.ports.in.ConsultRefundUseCase;
import application.domain.ports.in.ConsultReturnUseCase;
import application.domain.ports.in.ConsultSellerUseCase;
import application.domain.ports.in.ConsultUserUseCase;
import application.domain.ports.in.ConsultWarehouseUseCase;
import application.domain.ports.in.DeactivateSellerUseCase;
import application.domain.ports.in.DeactivateWarehouseUseCase;
import application.domain.ports.in.GenerateInventoryReportUseCase;
import application.domain.ports.in.GenerateInvoiceUseCase;
import application.domain.ports.in.GenerateReturnsReportUseCase;
import application.domain.ports.in.GenerateSalesReportUseCase;
import application.domain.ports.in.IssueRefundUseCase;
import application.domain.ports.in.ProcessRefundUseCase;
import application.domain.ports.in.ReactivateSellerUseCase;
import application.domain.ports.in.RegisterSellerUseCase;
import application.domain.ports.in.RegisterUserUseCase;
import application.domain.ports.in.RegisterWarehouseUseCase;
import application.domain.ports.in.RejectRefundUseCase;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Role facade: delegates every Administrator operation to its input-port use case (each one validates status, role and ownership). */
@Service
public class AdministratorUseCaseImpl implements AdministratorPort {

    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final BlockUserUseCase blockUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final ChangeUserRoleUseCase changeUserRoleUseCase;
    private final ConsultUserUseCase consultUserUseCase;
    private final RegisterSellerUseCase registerSellerUseCase;
    private final DeactivateSellerUseCase deactivateSellerUseCase;
    private final ReactivateSellerUseCase reactivateSellerUseCase;
    private final ConsultSellerUseCase consultSellerUseCase;
    private final RegisterWarehouseUseCase registerWarehouseUseCase;
    private final DeactivateWarehouseUseCase deactivateWarehouseUseCase;
    private final ConsultWarehouseUseCase consultWarehouseUseCase;
    private final ConsultOrderUseCase consultOrderUseCase;
    private final GenerateInvoiceUseCase generateInvoiceUseCase;
    private final CancelInvoiceUseCase cancelInvoiceUseCase;
    private final ConsultInvoiceUseCase consultInvoiceUseCase;
    private final ConsultReturnUseCase consultReturnUseCase;
    private final IssueRefundUseCase issueRefundUseCase;
    private final ProcessRefundUseCase processRefundUseCase;
    private final CompleteRefundUseCase completeRefundUseCase;
    private final RejectRefundUseCase rejectRefundUseCase;
    private final ConsultRefundUseCase consultRefundUseCase;
    private final GenerateSalesReportUseCase generateSalesReportUseCase;
    private final GenerateInventoryReportUseCase generateInventoryReportUseCase;
    private final GenerateReturnsReportUseCase generateReturnsReportUseCase;
    private final ConsultAuditLogsUseCase consultAuditLogsUseCase;
    private final ConsultOperationsUseCase consultOperationsUseCase;

    public AdministratorUseCaseImpl(ChangeUserPasswordUseCase changeUserPasswordUseCase,
            RegisterUserUseCase registerUserUseCase,
            BlockUserUseCase blockUserUseCase,
            ActivateUserUseCase activateUserUseCase,
            ChangeUserRoleUseCase changeUserRoleUseCase,
            ConsultUserUseCase consultUserUseCase,
            RegisterSellerUseCase registerSellerUseCase,
            DeactivateSellerUseCase deactivateSellerUseCase,
            ReactivateSellerUseCase reactivateSellerUseCase,
            ConsultSellerUseCase consultSellerUseCase,
            RegisterWarehouseUseCase registerWarehouseUseCase,
            DeactivateWarehouseUseCase deactivateWarehouseUseCase,
            ConsultWarehouseUseCase consultWarehouseUseCase,
            ConsultOrderUseCase consultOrderUseCase,
            GenerateInvoiceUseCase generateInvoiceUseCase,
            CancelInvoiceUseCase cancelInvoiceUseCase,
            ConsultInvoiceUseCase consultInvoiceUseCase,
            ConsultReturnUseCase consultReturnUseCase,
            IssueRefundUseCase issueRefundUseCase,
            ProcessRefundUseCase processRefundUseCase,
            CompleteRefundUseCase completeRefundUseCase,
            RejectRefundUseCase rejectRefundUseCase,
            ConsultRefundUseCase consultRefundUseCase,
            GenerateSalesReportUseCase generateSalesReportUseCase,
            GenerateInventoryReportUseCase generateInventoryReportUseCase,
            GenerateReturnsReportUseCase generateReturnsReportUseCase,
            ConsultAuditLogsUseCase consultAuditLogsUseCase,
            ConsultOperationsUseCase consultOperationsUseCase) {
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.blockUserUseCase = blockUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.changeUserRoleUseCase = changeUserRoleUseCase;
        this.consultUserUseCase = consultUserUseCase;
        this.registerSellerUseCase = registerSellerUseCase;
        this.deactivateSellerUseCase = deactivateSellerUseCase;
        this.reactivateSellerUseCase = reactivateSellerUseCase;
        this.consultSellerUseCase = consultSellerUseCase;
        this.registerWarehouseUseCase = registerWarehouseUseCase;
        this.deactivateWarehouseUseCase = deactivateWarehouseUseCase;
        this.consultWarehouseUseCase = consultWarehouseUseCase;
        this.consultOrderUseCase = consultOrderUseCase;
        this.generateInvoiceUseCase = generateInvoiceUseCase;
        this.cancelInvoiceUseCase = cancelInvoiceUseCase;
        this.consultInvoiceUseCase = consultInvoiceUseCase;
        this.consultReturnUseCase = consultReturnUseCase;
        this.issueRefundUseCase = issueRefundUseCase;
        this.processRefundUseCase = processRefundUseCase;
        this.completeRefundUseCase = completeRefundUseCase;
        this.rejectRefundUseCase = rejectRefundUseCase;
        this.consultRefundUseCase = consultRefundUseCase;
        this.generateSalesReportUseCase = generateSalesReportUseCase;
        this.generateInventoryReportUseCase = generateInventoryReportUseCase;
        this.generateReturnsReportUseCase = generateReturnsReportUseCase;
        this.consultAuditLogsUseCase = consultAuditLogsUseCase;
        this.consultOperationsUseCase = consultOperationsUseCase;
    }

    @Override
    public void changeMyPassword(User admin, String currentPassword, String newPassword) {
        changeUserPasswordUseCase.changePassword(admin, currentPassword, newPassword);
    }

    @Override
    public User registerUser(User admin, User newUser) {
        return registerUserUseCase.register(admin, newUser);
    }

    @Override
    public void blockUser(User admin, User target) {
        blockUserUseCase.block(admin, target);
    }

    @Override
    public void activateUser(User admin, User target) {
        activateUserUseCase.activate(admin, target);
    }

    @Override
    public void changeUserRole(User admin, User target, SystemRole newRole) {
        changeUserRoleUseCase.changeRole(admin, target, newRole);
    }

    @Override
    public User consultUser(User admin, User target) {
        return consultUserUseCase.consultUser(admin, target);
    }

    @Override
    public List<User> consultUsers(User admin) {
        return consultUserUseCase.consultAllUsers(admin);
    }

    @Override
    public Seller registerSeller(User admin, User sellerUser, String businessInfo, String firstWarehouseLocation) {
        return registerSellerUseCase.registerSeller(admin, sellerUser, businessInfo, firstWarehouseLocation);
    }

    @Override
    public void deactivateSeller(User admin, Seller seller) {
        deactivateSellerUseCase.deactivate(admin, seller);
    }

    @Override
    public void reactivateSeller(User admin, Seller seller) {
        reactivateSellerUseCase.reactivate(admin, seller);
    }

    @Override
    public List<Seller> consultSellers(User admin) {
        return consultSellerUseCase.consultAllSellers(admin);
    }

    @Override
    public Warehouse registerWarehouse(User admin, String location, WarehouseType type, Seller owner) {
        return registerWarehouseUseCase.registerWarehouse(admin, location, type, owner);
    }

    @Override
    public void deactivateWarehouse(User admin, Warehouse warehouse) {
        deactivateWarehouseUseCase.deactivateWarehouse(admin, warehouse);
    }

    @Override
    public List<Warehouse> consultWarehouses(User admin) {
        return consultWarehouseUseCase.consultAllWarehouses(admin);
    }

    @Override
    public List<Order> consultOrders(User admin) {
        return consultOrderUseCase.consultAllOrders(admin);
    }

    @Override
    public Invoice generateInvoice(User admin, Order order, BigDecimal taxRate) {
        return generateInvoiceUseCase.generateInvoice(admin, order, taxRate);
    }

    @Override
    public void cancelInvoice(User admin, Invoice invoice) {
        cancelInvoiceUseCase.cancelInvoice(admin, invoice);
    }

    @Override
    public List<Invoice> consultInvoices(User admin) {
        return consultInvoiceUseCase.consultAllInvoices(admin);
    }

    @Override
    public List<Return> consultReturns(User admin) {
        return consultReturnUseCase.consultAllReturns(admin);
    }

    @Override
    public Refund issueRefund(User admin, Return productReturn, Money amount) {
        return issueRefundUseCase.issueRefund(admin, productReturn, amount);
    }

    @Override
    public void processRefund(User admin, Refund refund) {
        processRefundUseCase.process(admin, refund);
    }

    @Override
    public void completeRefund(User admin, Refund refund) {
        completeRefundUseCase.complete(admin, refund);
    }

    @Override
    public void rejectRefund(User admin, Refund refund) {
        rejectRefundUseCase.reject(admin, refund);
    }

    @Override
    public List<Refund> consultRefunds(User admin) {
        return consultRefundUseCase.consultAllRefunds(admin);
    }

    @Override
    public Report generateSalesReport(User admin, Map<String, Object> filters) {
        return generateSalesReportUseCase.generateSalesReport(admin, filters);
    }

    @Override
    public Report generateInventoryReport(User admin, Map<String, Object> filters) {
        return generateInventoryReportUseCase.generateInventoryReport(admin, filters);
    }

    @Override
    public Report generateReturnsReport(User admin, Map<String, Object> filters) {
        return generateReturnsReportUseCase.generateReturnsReport(admin, filters);
    }

    @Override
    public List<AuditLog> consultAuditLogsByUser(User admin, User user) {
        return consultAuditLogsUseCase.consultByUser(admin, user);
    }

    @Override
    public List<AuditLog> consultAuditLogsByEntity(User admin, EntityReference entity) {
        return consultAuditLogsUseCase.consultByEntity(admin, entity);
    }

    @Override
    public List<Operation> consultOperationsByUser(User admin, User user) {
        return consultOperationsUseCase.consultByUser(admin, user);
    }
}
