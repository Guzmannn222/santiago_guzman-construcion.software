package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.refund.*;
import application.domain.services.report.*;
import application.domain.services.returns.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Three sellers, one delivered order with their three products, one partial return and its refund. */
class SellerReportsTest {

    private Fixture fx;
    private Buyer buyer;
    private Seller sellerA;
    private Seller sellerB;
    private Seller sellerC;
    private Product a;
    private Product b;
    private Product c;
    private Order delivered;
    private Return productReturn;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        buyer = fx.newBuyer();
        sellerA = fx.newSeller();
        sellerB = fx.newSeller();
        sellerC = fx.newSeller();
        Warehouse shared = fx.warehouseOf(sellerA);
        a = fx.newProduct(sellerA, ProductType.PHYSICAL, "10000", true);
        b = fx.newProduct(sellerB, ProductType.PHYSICAL, "20000", true);
        c = fx.newProduct(sellerC, ProductType.PHYSICAL, "30000", true);
        Inventory invA = fx.newInventory(a, shared, 10);
        Inventory invB = fx.newInventory(b, shared, 10);
        Inventory invC = fx.newInventory(c, shared, 10);
        delivered = new Order(buyer, shared);
        delivered.addItem(new OrderItem(a, 2, a.getPrice()));
        delivered.addItem(new OrderItem(b, 1, b.getPrice()));
        delivered.addItem(new OrderItem(c, 3, c.getPrice()));
        invA.reserve(2);
        invA.recordMovement(new InventoryMovement(InventoryMovementType.RESERVATION, 2, "Order"));
        invB.reserve(1);
        invB.recordMovement(new InventoryMovement(InventoryMovementType.RESERVATION, 1, "Order"));
        invC.reserve(3);
        invC.recordMovement(new InventoryMovement(InventoryMovementType.RESERVATION, 3, "Order"));
        delivered.confirmPayment();
        delivered.ship();
        delivered.deliver();
        fx.orders.save(delivered);
        RequestReturnService request = new RequestReturnService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.orderPort, fx.returnPort);
        productReturn = request.requestReturn(buyer.getUser(), delivered, "Defective", List.of(new ReturnItem(b, 1)));
    }

    private GenerateSalesReportService sales() {
        return new GenerateSalesReportService(fx.validateStatus, fx.validateRole, fx.traceability, fx.orderPort, fx.sellerPort);
    }

    private GenerateInventoryReportService stock() {
        return new GenerateInventoryReportService(fx.validateStatus, fx.validateRole, fx.traceability, fx.inventoryPort, fx.sellerPort);
    }

    private GenerateReturnsReportService returnsReport() {
        return new GenerateReturnsReportService(fx.validateStatus, fx.validateRole, fx.traceability, fx.returnPort,
                fx.refundPort, fx.sellerPort);
    }

    @Test
    @DisplayName("Each seller sees only their own sales, by product")
    void salesReportIsScopedToTheSeller() {
        Map<String, Object> dataB = sales().generateSalesReport(sellerB.getUser(), null).getData();
        assertEquals(sellerB.getId(), dataB.get("sellerId"));
        assertEquals(1, dataB.get("orders"));
        assertEquals(1, dataB.get("unitsSold"));
        assertEquals("20000.00", dataB.get("totalSales"));
        List<?> products = (List<?>) dataB.get("byProduct");
        assertEquals(1, products.size());

        Map<String, Object> dataC = sales().generateSalesReport(sellerC.getUser(), null).getData();
        assertEquals(3, dataC.get("unitsSold"));
        assertEquals("90000.00", dataC.get("totalSales"));

        Map<String, Object> viaAdmin = sales().generateSalesReport(fx.admin(), Map.of("sellerId", sellerA.getId())).getData();
        assertEquals(2, viaAdmin.get("unitsSold"));
        assertEquals("20000.00", viaAdmin.get("totalSales"));
    }

    @Test
    @DisplayName("A seller cannot ask for another seller's report, and buyers cannot ask for any")
    void reportAccessIsRestricted() {
        assertThrows(UnauthorizedOperationException.class,
                () -> sales().generateSalesReport(sellerA.getUser(), Map.of("sellerId", sellerB.getId())));
        assertThrows(UnauthorizedOperationException.class,
                () -> stock().generateInventoryReport(sellerA.getUser(), Map.of("sellerId", sellerC.getId())));
        assertThrows(UnauthorizedOperationException.class,
                () -> returnsReport().generateReturnsReport(sellerA.getUser(), Map.of("sellerId", sellerB.getId())));
        assertThrows(UnauthorizedOperationException.class, () -> sales().generateSalesReport(buyer.getUser(), null));
        assertThrows(UnauthorizedOperationException.class, () -> stock().generateInventoryReport(fx.logistics(), null));
        assertThrows(EntityNotFoundException.class, () -> sales().generateSalesReport(fx.admin(), Map.of("sellerId", 99999)));
    }

    @Test
    @DisplayName("The inventory report shows stock and the movements caused by purchases")
    void inventoryReportShowsMovements() {
        Map<String, Object> data = stock().generateInventoryReport(sellerB.getUser(), null).getData();
        assertEquals(1, data.get("inventoryRecords"));
        assertEquals(9, data.get("totalUnits"));
        List<?> rows = (List<?>) data.get("inventories");
        Map<?, ?> row = (Map<?, ?>) rows.get(0);
        assertEquals(b.getId(), row.get("productId"));
        assertEquals(9, row.get("stock"));
        Map<?, ?> movements = (Map<?, ?>) row.get("movementUnitsByType");
        assertEquals(1, movements.get("RESERVATION"));
    }

    @Test
    @DisplayName("The returns report follows the return and its refund, only for the owner of the returned product")
    void returnsReportFollowsReturnAndRefund() {
        LoadReturnForReviewService load = new LoadReturnForReviewService(fx.returnPort, fx.sellerPort);
        new ApproveReturnService(fx.validateStatus, fx.validateRole, fx.traceability, load, fx.returnPort)
                .approve(sellerB.getUser(), productReturn);
        IssueRefundService issue = new IssueRefundService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.returnPort, fx.refundPort, fx.sellerPort);
        Refund refund = issue.issueRefund(sellerB.getUser(), productReturn, Fixture.cop("20000"));

        Map<String, Object> dataB = returnsReport().generateReturnsReport(sellerB.getUser(), null).getData();
        assertEquals(1, dataB.get("returns"));
        assertEquals(1, dataB.get("returnedUnits"));
        assertEquals("20000.00", dataB.get("returnedValue"));
        assertEquals("0.00", dataB.get("totalRefunded"));
        assertEquals("20000.00", dataB.get("pendingRefundAmount"));
        List<?> details = (List<?>) dataB.get("returnDetails");
        Map<?, ?> first = (Map<?, ?>) details.get(0);
        assertEquals(productReturn.getId(), first.get("returnId"));
        assertEquals(1, ((List<?>) first.get("lines")).size());
        assertNotNull(first.get("refund"));

        Map<String, Object> dataA = returnsReport().generateReturnsReport(sellerA.getUser(), null).getData();
        assertEquals(0, dataA.get("returns"));
        assertEquals("0.00", dataA.get("returnedValue"));
        assertEquals(refund.getId(), ((Map<?, ?>) first.get("refund")).get("refundId"));
    }

    @Test
    @DisplayName("The same data gives exactly the same report every time")
    void reportsAreDeterministic() {
        Map<String, Object> first = sales().generateSalesReport(fx.admin(), Map.of("sellerId", sellerC.getId())).getData();
        Map<String, Object> second = sales().generateSalesReport(fx.supervisor(), Map.of("sellerId", sellerC.getId())).getData();
        assertEquals(first, second);
    }
}
