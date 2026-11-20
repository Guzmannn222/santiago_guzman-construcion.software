package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.buyer.*;
import application.domain.services.cart.*;
import application.domain.services.inventory.*;
import application.domain.services.invoice.*;
import application.domain.services.order.*;
import application.domain.services.product.*;
import application.domain.services.refund.*;
import application.domain.services.report.*;
import application.domain.services.returns.*;
import application.domain.services.seller.*;
import application.domain.services.shipment.*;
import application.domain.services.user.*;
import application.domain.services.warehouse.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class ReturnRefundReportServicesTest {

    private Fixture fx;
    private Seller seller;
    private Buyer buyer;
    private Product product;
    private Inventory inventory;
    private Order delivered;
    private User admin;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        seller = fx.newSeller();
        buyer = fx.newBuyer();
        admin = fx.admin();
        product = fx.newProduct(seller, ProductType.PHYSICAL, "10000", true);
        inventory = fx.newInventory(product, fx.warehouseOf(seller), 10);
        delivered = new Order(buyer, fx.warehouseOf(seller));
        delivered.addItem(new OrderItem(product, 2, product.getPrice()));
        inventory.reserve(2);
        delivered.confirmPayment();
        delivered.ship();
        delivered.deliver();
        fx.orders.save(delivered);
    }

    private RequestReturnService request() {
        return new RequestReturnService(fx.validateStatus, fx.validateRole, fx.traceability, fx.orderPort, fx.returnPort);
    }

    private LoadReturnForReviewService loadReturn() { return new LoadReturnForReviewService(fx.returnPort, fx.sellerPort); }

    private ApproveReturnService approve() {
        return new ApproveReturnService(fx.validateStatus, fx.validateRole, fx.traceability, loadReturn(), fx.returnPort);
    }

    private IssueRefundService issue() {
        return new IssueRefundService(fx.validateStatus, fx.validateRole, fx.traceability, fx.returnPort, fx.refundPort, fx.sellerPort);
    }

    private LoadRefundForReviewService loadRefund() { return new LoadRefundForReviewService(fx.refundPort, fx.sellerPort); }

    @Test
    void returnRequestRules() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        assertEquals(ReturnStatus.REQUESTED, productReturn.getStatus());
        assertThrows(DuplicateResourceException.class, () -> request().requestReturn(buyer.getUser(), delivered, "Again"));
        assertThrows(UnauthorizedOperationException.class, () -> request().requestReturn(fx.newBuyer().getUser(), delivered, "x"));
        Order pending = new Order(buyer, fx.warehouseOf(seller));
        fx.orders.save(pending);
        assertThrows(InvalidReturnStatusException.class, () -> request().requestReturn(buyer.getUser(), pending, "x"));
    }

    @Test
    void rejectedReturnAllowsANewRequest() {
        RejectReturnService reject = new RejectReturnService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadReturn(), fx.returnPort);
        Return first = request().requestReturn(buyer.getUser(), delivered, "Broken");
        reject.reject(seller.getUser(), first);
        assertEquals(ReturnStatus.REJECTED, first.getStatus());
        assertThrows(InvalidReturnStatusException.class, () -> approve().approve(seller.getUser(), first));
        assertNotNull(request().requestReturn(buyer.getUser(), delivered, "Second try"));
    }

    @Test
    void onlyTheOwningSellerOrAdminReviewsReturns() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        assertThrows(UnauthorizedOperationException.class, () -> approve().approve(fx.newSeller().getUser(), productReturn));
        assertThrows(UnauthorizedOperationException.class, () -> approve().approve(buyer.getUser(), productReturn));
        approve().approve(seller.getUser(), productReturn);
        assertEquals(ReturnStatus.APPROVED, productReturn.getStatus());
    }

    @Test
    void completingAReturnRestocksPhysicalItems() {
        CompleteReturnService complete = new CompleteReturnService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadReturn(), fx.returnPort, fx.inventoryPort);
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        User logistics = fx.logistics();
        assertThrows(InvalidReturnStatusException.class, () -> complete.complete(logistics, productReturn));
        approve().approve(seller.getUser(), productReturn);
        assertEquals(8, inventory.getStock());
        complete.complete(logistics, productReturn);
        assertEquals(10, fx.inventories.findById(inventory.getId()).get().getStock());
        assertEquals(ReturnStatus.COMPLETED, productReturn.getStatus());
    }

    @Test
    void refundRules() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        assertThrows(InvalidRefundStatusException.class, () -> issue().issueRefund(seller.getUser(), productReturn, Fixture.cop("100")));
        approve().approve(seller.getUser(), productReturn);
        assertThrows(InvalidRefundException.class, () -> issue().issueRefund(seller.getUser(), productReturn, Fixture.cop("20001")));
        assertThrows(UnauthorizedOperationException.class, () -> issue().issueRefund(fx.newSeller().getUser(), productReturn, Fixture.cop("100")));
        assertThrows(UnauthorizedOperationException.class, () -> issue().issueRefund(buyer.getUser(), productReturn, Fixture.cop("100")));
        Refund refund = issue().issueRefund(seller.getUser(), productReturn, Fixture.cop("20000"));
        assertEquals(RefundStatus.PENDING, refund.getStatus());
        assertThrows(DuplicateResourceException.class, () -> issue().issueRefund(admin, productReturn, Fixture.cop("100")));
    }

    @Test
    void refundLifecycleNotifiesBuyerOnCompletion() {
        ProcessRefundService process = new ProcessRefundService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadRefund(), fx.refundPort, fx.notificationPort);
        CompleteRefundService complete = new CompleteRefundService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadRefund(), fx.refundPort, fx.notificationPort);
        RejectRefundService reject = new RejectRefundService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadRefund(), fx.refundPort, fx.notificationPort);
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        approve().approve(seller.getUser(), productReturn);
        Refund refund = issue().issueRefund(seller.getUser(), productReturn, Fixture.cop("5000"));

        assertThrows(InvalidRefundStatusException.class, () -> complete.complete(seller.getUser(), refund));
        process.process(seller.getUser(), refund);
        complete.complete(seller.getUser(), refund);
        assertEquals(RefundStatus.COMPLETED, refund.getStatus());
        assertEquals(1, fx.notifications.size());
        assertThrows(InvalidRefundStatusException.class, () -> reject.reject(seller.getUser(), refund));
    }

    @Test
    void returnAndRefundConsultVisibility() {
        ConsultReturnService consultReturn = new ConsultReturnService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.returnPort, fx.buyerPort, fx.sellerPort);
        ConsultRefundService consultRefund = new ConsultRefundService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.refundPort, fx.buyerPort, fx.sellerPort);
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Broken");
        approve().approve(seller.getUser(), productReturn);
        Refund refund = issue().issueRefund(seller.getUser(), productReturn, Fixture.cop("5000"));
        Buyer other = fx.newBuyer();
        Seller otherSeller = fx.newSeller();

        assertSame(productReturn, consultReturn.consultReturn(buyer.getUser(), productReturn));
        assertThrows(UnauthorizedOperationException.class, () -> consultReturn.consultReturn(other.getUser(), productReturn));
        assertThrows(UnauthorizedOperationException.class, () -> consultReturn.consultReturn(otherSeller.getUser(), productReturn));
        assertEquals(1, consultReturn.consultMyReturns(buyer.getUser()).size());
        assertEquals(1, consultReturn.consultMyReturns(seller.getUser()).size());
        assertEquals(0, consultReturn.consultMyReturns(otherSeller.getUser()).size());
        assertEquals(1, consultReturn.consultAllReturns(fx.logistics()).size());

        assertSame(refund, consultRefund.consultRefund(buyer.getUser(), refund));
        assertThrows(UnauthorizedOperationException.class, () -> consultRefund.consultRefund(other.getUser(), refund));
        assertEquals(1, consultRefund.consultMyRefunds(buyer.getUser()).size());
        assertEquals(1, consultRefund.consultMyRefunds(seller.getUser()).size());
        assertEquals(1, consultRefund.consultAllRefunds(fx.supervisor()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consultRefund.consultAllRefunds(buyer.getUser()));
    }

    @Test
    void reportsComputeFromDataAndValidateFilters() {
        GenerateSalesReportService sales = new GenerateSalesReportService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.orderPort, fx.sellerPort);
        GenerateInventoryReportService stock = new GenerateInventoryReportService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.inventoryPort, fx.sellerPort);
        GenerateReturnsReportService returnsReport = new GenerateReturnsReportService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.returnPort, fx.refundPort, fx.sellerPort);
        User supervisor = fx.supervisor();
        request().requestReturn(buyer.getUser(), delivered, "Broken");

        Report salesReport = sales.generateSalesReport(supervisor, null);
        assertEquals(ReportType.SALES, salesReport.getType());
        assertEquals(1, salesReport.getData().get("orders"));
        assertEquals(2, salesReport.getData().get("unitsSold"));
        assertEquals("20000.00", salesReport.getData().get("totalSales"));
        assertNotNull(salesReport.getGenerationDate());
        assertEquals("0", String.valueOf(sales.generateSalesReport(admin, Map.of("status", "FINISHED")).getData().get("orders")));
        assertThrows(InvalidReportException.class, () -> sales.generateSalesReport(admin, Map.of("bogus", "x")));
        assertThrows(InvalidReportException.class, () -> sales.generateSalesReport(admin, Map.of("status", "NOPE")));
        assertThrows(UnauthorizedOperationException.class, () -> sales.generateSalesReport(buyer.getUser(), null));

        Report stockReport = stock.generateInventoryReport(supervisor, Map.of("productId", product.getId()));
        assertEquals(8, stockReport.getData().get("totalUnits"));
        assertThrows(InvalidReportException.class, () -> stock.generateInventoryReport(admin, Map.of("productId", "abc")));

        Report returnData = returnsReport.generateReturnsReport(supervisor, Map.of("status", "REQUESTED"));
        assertEquals(1, returnData.getData().get("returns"));
        long traced = fx.audits.stream().filter(a -> a.getOperationType().equals(OperationType.REPORT_GENERATION)).count();
        assertEquals(4L, traced);
    }
}
