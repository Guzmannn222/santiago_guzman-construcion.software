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
class InvoiceShipmentServicesTest {

    private Fixture fx;
    private Seller seller;
    private Buyer buyer;
    private Order paidOrder;
    private User admin;
    private User logistics;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        seller = fx.newSeller();
        buyer = fx.newBuyer();
        admin = fx.admin();
        logistics = fx.logistics();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "10000", true);
        paidOrder = new Order(buyer, fx.warehouseOf(seller));
        paidOrder.addItem(new OrderItem(product, 2, product.getPrice()));
        paidOrder.confirmPayment();
        fx.orders.save(paidOrder);
    }

    private GenerateInvoiceService generate() {
        return new GenerateInvoiceService(fx.validateStatus, fx.validateRole, fx.traceability, fx.orderPort,
                fx.invoicePort, fx.configPort);
    }

    @Test
    void invoiceGenerationUsesDefaultTaxAndPreventsDuplicates() {
        Invoice invoice = generate().generateInvoice(admin, paidOrder, null);
        assertEquals(new BigDecimal("0.19"), invoice.getTaxRate());
        assertEquals(new BigDecimal("3800.00"), invoice.getTax().getAmount());
        assertEquals(new BigDecimal("23800.00"), invoice.getTotal().getAmount());
        assertThrows(DuplicateResourceException.class, () -> generate().generateInvoice(admin, paidOrder, null));
        assertThrows(UnauthorizedOperationException.class, () -> generate().generateInvoice(buyer.getUser(), paidOrder, null));
    }

    @Test
    void invoiceRequiresPaidOrderAndValidRate() {
        Order unpaid = new Order(buyer, fx.warehouseOf(seller));
        fx.orders.save(unpaid);
        assertThrows(InvalidInvoiceStatusException.class, () -> generate().generateInvoice(admin, unpaid, null));
        assertThrows(InvalidInvoiceException.class, () -> generate().generateInvoice(admin, paidOrder, new BigDecimal("2")));
        assertThrows(EntityNotFoundException.class, () -> generate().generateInvoice(admin, new Order(), null));
    }

    @Test
    void cancelInvoiceAllowsANewOne() {
        CancelInvoiceService cancel = new CancelInvoiceService(fx.validateStatus, fx.validateRole, fx.traceability, fx.invoicePort);
        Invoice invoice = generate().generateInvoice(admin, paidOrder, new BigDecimal("0.10"));
        cancel.cancelInvoice(admin, invoice);
        assertTrue(invoice.isCancelled());
        assertThrows(InvalidInvoiceStatusException.class, () -> cancel.cancelInvoice(admin, invoice));
        assertNotNull(generate().generateInvoice(admin, paidOrder, null));
    }

    @Test
    void invoiceConsultVisibility() {
        ConsultInvoiceService consult = new ConsultInvoiceService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.invoicePort, fx.orderPort, fx.buyerPort);
        Invoice invoice = generate().generateInvoice(admin, paidOrder, null);
        assertSame(invoice, consult.consultInvoice(buyer.getUser(), invoice));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultInvoice(fx.newBuyer().getUser(), invoice));
        assertEquals(1, consult.consultMyInvoices(buyer.getUser()).size());
        assertEquals(1, consult.consultAllInvoices(fx.supervisor()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllInvoices(buyer.getUser()));
    }

    private CreateShipmentService create() {
        return new CreateShipmentService(fx.validateStatus, fx.validateRole, fx.traceability, fx.orderPort, fx.shipmentPort);
    }

    @Test
    void shipmentCreationRules() {
        Shipment shipment = create().createShipment(logistics, paidOrder, " ");
        assertEquals(buyer.getPrimaryAddress(), shipment.getAddress());
        assertEquals(ShipmentStatus.PENDING, shipment.getStatus());
        assertThrows(DuplicateResourceException.class, () -> create().createShipment(logistics, paidOrder, "Other 1"));
        assertThrows(UnauthorizedOperationException.class, () -> create().createShipment(buyer.getUser(), paidOrder, "x y"));
        Order unpaid = new Order(buyer, fx.warehouseOf(seller));
        fx.orders.save(unpaid);
        assertThrows(InvalidOrderStateException.class, () -> create().createShipment(logistics, unpaid, "x y"));
    }

    @Test
    @DisplayName("Shipment flows PENDING -> PREPARING -> DISPATCHED -> IN_TRANSIT -> DELIVERED and drives the order")
    void shipmentLifecycle() {
        PrepareShipmentService prepare = new PrepareShipmentService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.shipmentPort, fx.orderPort, fx.notificationPort);
        DispatchShipmentService dispatch = new DispatchShipmentService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.shipmentPort, fx.orderPort, fx.notificationPort);
        UpdateShipmentStatusService update = new UpdateShipmentStatusService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.shipmentPort);
        ConfirmDeliveryService deliver = new ConfirmDeliveryService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.shipmentPort, fx.orderPort, fx.notificationPort);
        Shipment shipment = create().createShipment(logistics, paidOrder, "Calle 10 # 5-5");

        assertThrows(InvalidShipmentStatusException.class, () -> dispatch.dispatch(logistics, shipment));
        assertThrows(InvalidShipmentStatusException.class, () -> deliver.confirmDelivery(logistics, shipment));
        prepare.prepare(logistics, shipment);
        assertThrows(InvalidShipmentStatusException.class, () -> prepare.prepare(logistics, shipment));
        dispatch.dispatch(logistics, shipment);
        assertEquals(OrderStatus.SHIPPED, fx.orders.findById(paidOrder.getId()).get().getStatus());
        assertNotNull(shipment.getDispatchDate());
        update.updateStatus(logistics, shipment, ShipmentStatus.IN_TRANSIT);
        assertThrows(InvalidShipmentStatusException.class, () -> update.updateStatus(logistics, shipment, ShipmentStatus.PENDING));
        deliver.confirmDelivery(logistics, shipment);
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertEquals(OrderStatus.FINISHED, fx.orders.findById(paidOrder.getId()).get().getStatus());
        assertNotNull(shipment.getDeliveryDate());
        assertEquals(2, fx.notifications.size());
        assertThrows(UnauthorizedOperationException.class, () -> dispatch.dispatch(buyer.getUser(), shipment));
    }

    @Test
    void shipmentConsultVisibility() {
        ConsultShipmentService consult = new ConsultShipmentService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.shipmentPort, fx.orderPort, fx.buyerPort, fx.sellerPort);
        Shipment shipment = create().createShipment(logistics, paidOrder, "Calle 1 # 1-1");
        assertSame(shipment, consult.consultShipment(buyer.getUser(), shipment));
        assertSame(shipment, consult.consultShipment(seller.getUser(), shipment));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultShipment(fx.newBuyer().getUser(), shipment));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultShipment(fx.newSeller().getUser(), shipment));
        assertEquals(1, consult.consultMyShipments(buyer.getUser()).size());
        assertEquals(1, consult.consultMyShipments(seller.getUser()).size());
        assertEquals(1, consult.consultAllShipments(logistics).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllShipments(buyer.getUser()));
    }
}
