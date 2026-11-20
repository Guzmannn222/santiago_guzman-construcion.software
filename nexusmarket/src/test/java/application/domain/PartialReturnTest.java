package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.refund.*;
import application.domain.services.returns.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** One order with three products from three different sellers; the buyer returns only one product. */
class PartialReturnTest {

    private Fixture fx;
    private Buyer buyer;
    private Seller sellerA;
    private Seller sellerB;
    private Seller sellerC;
    private Product a;
    private Product b;
    private Product c;
    private Inventory invA;
    private Inventory invB;
    private Inventory invC;
    private Order delivered;

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
        invA = fx.newInventory(a, shared, 10);
        invB = fx.newInventory(b, shared, 10);
        invC = fx.newInventory(c, shared, 10);
        delivered = new Order(buyer, shared);
        delivered.addItem(new OrderItem(a, 2, a.getPrice()));
        delivered.addItem(new OrderItem(b, 1, b.getPrice()));
        delivered.addItem(new OrderItem(c, 3, c.getPrice()));
        invA.reserve(2);
        invB.reserve(1);
        invC.reserve(3);
        delivered.confirmPayment();
        delivered.ship();
        delivered.deliver();
        fx.orders.save(delivered);
    }

    private RequestReturnService request() {
        return new RequestReturnService(fx.validateStatus, fx.validateRole, fx.traceability, fx.orderPort, fx.returnPort);
    }

    private LoadReturnForReviewService loadReturn() {
        return new LoadReturnForReviewService(fx.returnPort, fx.sellerPort);
    }

    private ApproveReturnService approve() {
        return new ApproveReturnService(fx.validateStatus, fx.validateRole, fx.traceability, loadReturn(), fx.returnPort);
    }

    private IssueRefundService issue() {
        return new IssueRefundService(fx.validateStatus, fx.validateRole, fx.traceability, fx.returnPort, fx.refundPort, fx.sellerPort);
    }

    private CompleteReturnService complete() {
        return new CompleteReturnService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadReturn(), fx.returnPort, fx.inventoryPort);
    }

    @Test
    @DisplayName("Returning one product restocks only that product and refunds only its value")
    void returnsOnlyTheChosenProduct() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Defective",
                List.of(new ReturnItem(b, 1)));
        assertEquals(1, productReturn.getItems().size());
        assertEquals(b.getId(), productReturn.getItems().get(0).getProduct().getId());
        assertEquals(Fixture.cop("20000"), productReturn.returnedValue());

        approve().approve(sellerB.getUser(), productReturn);
        complete().complete(fx.logistics(), productReturn);

        assertEquals(10, fx.inventories.findById(invB.getId()).get().getStock());
        assertEquals(8, fx.inventories.findById(invA.getId()).get().getStock());
        assertEquals(7, fx.inventories.findById(invC.getId()).get().getStock());

        assertThrows(InvalidRefundException.class,
                () -> issue().issueRefund(sellerB.getUser(), productReturn, Fixture.cop("20001")));
        Refund refund = issue().issueRefund(sellerB.getUser(), productReturn, Fixture.cop("20000"));
        assertEquals(RefundStatus.PENDING, refund.getStatus());
    }

    @Test
    @DisplayName("Return lines are validated against the order")
    void invalidLinesAreRejected() {
        Product foreign = fx.newProduct(sellerA, ProductType.PHYSICAL, "5000", true);
        assertThrows(InvalidReturnException.class, () -> request().requestReturn(buyer.getUser(), delivered, "x",
                List.of(new ReturnItem(foreign, 1))));
        assertThrows(InvalidReturnException.class, () -> request().requestReturn(buyer.getUser(), delivered, "x",
                List.of(new ReturnItem(b, 2))));
        assertThrows(InvalidReturnException.class, () -> new ReturnItem(b, 0));
        assertThrows(InvalidReturnException.class, () -> request().requestReturn(buyer.getUser(), delivered, "x",
                List.of(new ReturnItem(b, 1), new ReturnItem(b, 1))));
    }

    @Test
    @DisplayName("Without lines the whole order is returned, as before")
    void noLinesMeansWholeOrder() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "All broken");
        assertEquals(3, productReturn.getItems().size());
        assertEquals(Fixture.cop("130000"), productReturn.returnedValue());
    }

    @Test
    @DisplayName("Every step of a partial return leaves an audit trace")
    void partialReturnIsAudited() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Defective",
                List.of(new ReturnItem(b, 1)));
        approve().approve(sellerB.getUser(), productReturn);
        complete().complete(fx.logistics(), productReturn);
        issue().issueRefund(sellerB.getUser(), productReturn, Fixture.cop("20000"));
        Set<OperationType> types = new HashSet<>();
        for (AuditLog log : fx.audits) {
            types.add(log.getOperationType());
        }
        assertTrue(types.contains(OperationType.RETURN_REQUEST));
        assertTrue(types.contains(OperationType.RETURN_APPROVAL));
        assertTrue(types.contains(OperationType.RETURN_COMPLETION));
        assertTrue(types.contains(OperationType.REFUND_ISSUE));
    }

    @Test
    @DisplayName("A seller whose product is not in the returned lines cannot approve, refund or see the return")
    void otherSellersCannotTouchThePartialReturn() {
        Return productReturn = request().requestReturn(buyer.getUser(), delivered, "Defective",
                List.of(new ReturnItem(b, 1)));
        assertThrows(UnauthorizedOperationException.class, () -> approve().approve(sellerA.getUser(), productReturn));
        assertThrows(UnauthorizedOperationException.class,
                () -> issue().issueRefund(sellerC.getUser(), productReturn, Fixture.cop("100")));
        approve().approve(sellerB.getUser(), productReturn);
        assertEquals(ReturnStatus.APPROVED, fx.returnPort.findById(productReturn.getId()).get().getStatus());
    }
}
