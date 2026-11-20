package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Report;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AddProductVariantUseCase;
import application.domain.ports.in.AdjustStockUseCase;
import application.domain.ports.in.ApproveReturnUseCase;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.CompleteRefundUseCase;
import application.domain.ports.in.ConsultInventoryUseCase;
import application.domain.ports.in.ConsultOrderUseCase;
import application.domain.ports.in.ConsultProductUseCase;
import application.domain.ports.in.ConsultRefundUseCase;
import application.domain.ports.in.ConsultReturnUseCase;
import application.domain.ports.in.ConsultSellerUseCase;
import application.domain.ports.in.ConsultWarehouseUseCase;
import application.domain.ports.in.GenerateInventoryReportUseCase;
import application.domain.ports.in.GenerateReturnsReportUseCase;
import application.domain.ports.in.GenerateSalesReportUseCase;
import application.domain.ports.in.CreateProductUseCase;
import application.domain.ports.in.DiscontinueProductUseCase;
import application.domain.ports.in.IssueRefundUseCase;
import application.domain.ports.in.MarkInventoryDamagedUseCase;
import application.domain.ports.in.OpenInventoryUseCase;
import application.domain.ports.in.ProcessRefundUseCase;
import application.domain.ports.in.PublishProductUseCase;
import application.domain.ports.in.ReceiveStockUseCase;
import application.domain.ports.in.RejectRefundUseCase;
import application.domain.ports.in.RejectReturnUseCase;
import application.domain.ports.in.SellerPort;
import application.domain.ports.in.SuspendProductUseCase;
import application.domain.ports.in.UpdateProductPriceUseCase;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Role facade: delegates every Seller operation to its input-port use case (each one validates status, role and ownership). */
@Service
public class SellerUseCaseImpl implements SellerPort {

    private final ConsultSellerUseCase consultSellerUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final AddProductVariantUseCase addProductVariantUseCase;
    private final UpdateProductPriceUseCase updateProductPriceUseCase;
    private final PublishProductUseCase publishProductUseCase;
    private final SuspendProductUseCase suspendProductUseCase;
    private final DiscontinueProductUseCase discontinueProductUseCase;
    private final ConsultProductUseCase consultProductUseCase;
    private final OpenInventoryUseCase openInventoryUseCase;
    private final ReceiveStockUseCase receiveStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final MarkInventoryDamagedUseCase markInventoryDamagedUseCase;
    private final ConsultInventoryUseCase consultInventoryUseCase;
    private final ConsultWarehouseUseCase consultWarehouseUseCase;
    private final ConsultOrderUseCase consultOrderUseCase;
    private final ApproveReturnUseCase approveReturnUseCase;
    private final RejectReturnUseCase rejectReturnUseCase;
    private final ConsultReturnUseCase consultReturnUseCase;
    private final IssueRefundUseCase issueRefundUseCase;
    private final ProcessRefundUseCase processRefundUseCase;
    private final CompleteRefundUseCase completeRefundUseCase;
    private final RejectRefundUseCase rejectRefundUseCase;
    private final ConsultRefundUseCase consultRefundUseCase;
    private final GenerateSalesReportUseCase generateSalesReportUseCase;
    private final GenerateInventoryReportUseCase generateInventoryReportUseCase;
    private final GenerateReturnsReportUseCase generateReturnsReportUseCase;

    public SellerUseCaseImpl(ConsultSellerUseCase consultSellerUseCase,
            ChangeUserPasswordUseCase changeUserPasswordUseCase,
            CreateProductUseCase createProductUseCase,
            AddProductVariantUseCase addProductVariantUseCase,
            UpdateProductPriceUseCase updateProductPriceUseCase,
            PublishProductUseCase publishProductUseCase,
            SuspendProductUseCase suspendProductUseCase,
            DiscontinueProductUseCase discontinueProductUseCase,
            ConsultProductUseCase consultProductUseCase,
            OpenInventoryUseCase openInventoryUseCase,
            ReceiveStockUseCase receiveStockUseCase,
            AdjustStockUseCase adjustStockUseCase,
            MarkInventoryDamagedUseCase markInventoryDamagedUseCase,
            ConsultInventoryUseCase consultInventoryUseCase,
            ConsultWarehouseUseCase consultWarehouseUseCase,
            ConsultOrderUseCase consultOrderUseCase,
            ApproveReturnUseCase approveReturnUseCase,
            RejectReturnUseCase rejectReturnUseCase,
            ConsultReturnUseCase consultReturnUseCase,
            IssueRefundUseCase issueRefundUseCase,
            ProcessRefundUseCase processRefundUseCase,
            CompleteRefundUseCase completeRefundUseCase,
            RejectRefundUseCase rejectRefundUseCase,
            ConsultRefundUseCase consultRefundUseCase,
            GenerateSalesReportUseCase generateSalesReportUseCase,
            GenerateInventoryReportUseCase generateInventoryReportUseCase,
            GenerateReturnsReportUseCase generateReturnsReportUseCase) {
        this.generateSalesReportUseCase = generateSalesReportUseCase;
        this.generateInventoryReportUseCase = generateInventoryReportUseCase;
        this.generateReturnsReportUseCase = generateReturnsReportUseCase;
        this.consultSellerUseCase = consultSellerUseCase;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.createProductUseCase = createProductUseCase;
        this.addProductVariantUseCase = addProductVariantUseCase;
        this.updateProductPriceUseCase = updateProductPriceUseCase;
        this.publishProductUseCase = publishProductUseCase;
        this.suspendProductUseCase = suspendProductUseCase;
        this.discontinueProductUseCase = discontinueProductUseCase;
        this.consultProductUseCase = consultProductUseCase;
        this.openInventoryUseCase = openInventoryUseCase;
        this.receiveStockUseCase = receiveStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.markInventoryDamagedUseCase = markInventoryDamagedUseCase;
        this.consultInventoryUseCase = consultInventoryUseCase;
        this.consultWarehouseUseCase = consultWarehouseUseCase;
        this.consultOrderUseCase = consultOrderUseCase;
        this.approveReturnUseCase = approveReturnUseCase;
        this.rejectReturnUseCase = rejectReturnUseCase;
        this.consultReturnUseCase = consultReturnUseCase;
        this.issueRefundUseCase = issueRefundUseCase;
        this.processRefundUseCase = processRefundUseCase;
        this.completeRefundUseCase = completeRefundUseCase;
        this.rejectRefundUseCase = rejectRefundUseCase;
        this.consultRefundUseCase = consultRefundUseCase;
    }

    @Override
    public Seller consultMyProfile(User user) {
        return consultSellerUseCase.consultMySeller(user);
    }

    @Override
    public void changeMyPassword(User user, String currentPassword, String newPassword) {
        changeUserPasswordUseCase.changePassword(user, currentPassword, newPassword);
    }

    @Override
    public Product createProduct(User user, Seller seller, String name, String description, Money price, ProductType type) {
        return createProductUseCase.createProduct(user, seller, name, description, price, type);
    }

    @Override
    public void addProductVariant(User user, Product product, String name, String value) {
        addProductVariantUseCase.addVariant(user, product, name, value);
    }

    @Override
    public void updateProductPrice(User user, Product product, Money newPrice) {
        updateProductPriceUseCase.updatePrice(user, product, newPrice);
    }

    @Override
    public void publishProduct(User user, Product product) {
        publishProductUseCase.publish(user, product);
    }

    @Override
    public void suspendProduct(User user, Product product) {
        suspendProductUseCase.suspend(user, product);
    }

    @Override
    public void discontinueProduct(User user, Product product) {
        discontinueProductUseCase.discontinue(user, product);
    }

    @Override
    public Product consultProduct(User user, Product product) {
        return consultProductUseCase.consultProduct(user, product);
    }

    @Override
    public List<Product> consultMyProducts(User user) {
        return consultProductUseCase.consultMyProducts(user);
    }

    @Override
    public Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock) {
        return openInventoryUseCase.openInventory(user, product, warehouse, initialStock);
    }

    @Override
    public void receiveStock(User user, Inventory inventory, int quantity, String reason) {
        receiveStockUseCase.receiveStock(user, inventory, quantity, reason);
    }

    @Override
    public void adjustStock(User user, Inventory inventory, int newQuantity, String reason) {
        adjustStockUseCase.adjustStock(user, inventory, newQuantity, reason);
    }

    @Override
    public void markInventoryDamaged(User user, Inventory inventory) {
        markInventoryDamagedUseCase.markDamaged(user, inventory);
    }

    @Override
    public List<Inventory> consultMyInventory(User user) {
        return consultInventoryUseCase.consultMyInventory(user);
    }

    @Override
    public List<Warehouse> consultWarehouses(User user) {
        return consultWarehouseUseCase.consultAllWarehouses(user);
    }

    @Override
    public List<Order> consultMyOrders(User user) {
        return consultOrderUseCase.consultMyOrders(user);
    }

    @Override
    public void approveReturn(User user, Return productReturn) {
        approveReturnUseCase.approve(user, productReturn);
    }

    @Override
    public void rejectReturn(User user, Return productReturn) {
        rejectReturnUseCase.reject(user, productReturn);
    }

    @Override
    public List<Return> consultMyReturns(User user) {
        return consultReturnUseCase.consultMyReturns(user);
    }

    @Override
    public Refund issueRefund(User user, Return productReturn, Money amount) {
        return issueRefundUseCase.issueRefund(user, productReturn, amount);
    }

    @Override
    public void processRefund(User user, Refund refund) {
        processRefundUseCase.process(user, refund);
    }

    @Override
    public void completeRefund(User user, Refund refund) {
        completeRefundUseCase.complete(user, refund);
    }

    @Override
    public void rejectRefund(User user, Refund refund) {
        rejectRefundUseCase.reject(user, refund);
    }

    @Override
    public List<Refund> consultMyRefunds(User user) {
        return consultRefundUseCase.consultMyRefunds(user);
    }

    @Override
    public Report generateSalesReport(User user, Map<String, Object> filters) {
        return generateSalesReportUseCase.generateSalesReport(user, filters);
    }

    @Override
    public Report generateInventoryReport(User user, Map<String, Object> filters) {
        return generateInventoryReportUseCase.generateInventoryReport(user, filters);
    }

    @Override
    public Report generateReturnsReport(User user, Map<String, Object> filters) {
        return generateReturnsReportUseCase.generateReturnsReport(user, filters);
    }
}
