package application.domain.services.order;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.CreateOrderUseCase;
import java.util.stream.Collectors;

/**
 * Converts the buyer's cart into an order (PENDING_PAYMENT). Prices are re-read from the catalog,
 * physical items are reserved from one warehouse (chosen automatically when none is given) and
 * the cart is emptied. Everything is validated before any stock is touched.
 */
@Service
public class CreateOrderService extends AuthorizedService implements CreateOrderUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;

    public CreateOrderService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            CartRepositoryPort cartRepositoryPort, ProductRepositoryPort productRepositoryPort,
            WarehouseRepositoryPort warehouseRepositoryPort, InventoryRepositoryPort inventoryRepositoryPort,
            OrderRepositoryPort orderRepositoryPort) {
        super(s, r, t);
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order createOrder(User actor, Cart cart, Warehouse warehouse) {
        authorize(actor, SystemRole.BUYER);
        provided(cart, "Cart");
        Cart persistedCart = found(cartRepositoryPort.findById(idOf(cart.getId(), "Cart")), "Cart", cart.getId());
        if (!persistedCart.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only create orders from their own cart.");
        }
        Buyer buyer = persistedCart.getBuyer();
        buyer.ensureCanPlaceOrders();
        persistedCart.confirm();

        List<OrderItem> items = new ArrayList<>();
        for (CartItem line : persistedCart.getItems()) {
            Product product = found(productRepositoryPort.findById(idOf(line.getProduct().getId(), "Product")),
                    "Product", line.getProduct().getId());
            product.ensurePurchasable();
            items.add(new OrderItem(product, line.getQuantity(), product.getPrice()));
        }
        List<OrderItem> physical = items.stream().filter(i -> i.getProduct().isPhysical()).collect(Collectors.toList());

        Warehouse source = null;
        Map<Long, Inventory> reservations = new LinkedHashMap<>();
        if (!physical.isEmpty()) {
            source = warehouse != null ? resolveWarehouse(warehouse) : chooseWarehouse(physical);
            final Warehouse chosen = source;
            for (OrderItem item : physical) {
                Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(item.getProduct(), chosen)
                        .orElseThrow(() -> new InsufficientStockException("Product " + item.getProduct().getId()
                                + " has no stock in warehouse " + describe(chosen) + "."));
                if (!inventory.canFulfill(item.getQuantity())) {
                    throw new InsufficientStockException("Not enough stock for product " + item.getProduct().getId()
                            + " (requested " + item.getQuantity() + ").");
                }
                reservations.put(item.getProduct().getId(), inventory);
            }
        }

        Order order = new Order(buyer, source);
        items.forEach(order::addItem);
        Order saved = orderRepositoryPort.save(order);

        for (OrderItem item : physical) {
            Inventory inventory = reservations.get(item.getProduct().getId());
            inventory.reserve(item.getQuantity());
            inventory.recordMovement(new InventoryMovement(InventoryMovementType.RESERVATION, item.getQuantity(),
                    "Order " + saved.getId()));
            inventoryRepositoryPort.update(inventory);
        }
        persistedCart.clear();
        cartRepositoryPort.update(persistedCart);
        register(actor, OperationType.ORDER_CREATION, EntityType.ORDER, saved.getId(),
                "items", saved.getItems().size(), "total", String.valueOf(saved.getTotal()),
                "warehouseId", source == null ? null : source.getId());
        return saved;
    }

    private Warehouse resolveWarehouse(Warehouse reference) {
        Warehouse persisted = found(warehouseRepositoryPort.findById(idOf(reference.getId(), "Warehouse")),
                "Warehouse", reference.getId());
        persisted.ensureActive();
        return persisted;
    }

    private Warehouse chooseWarehouse(List<OrderItem> physical) {
        Set<Long> candidates = null;
        for (OrderItem item : physical) {
            Set<Long> fulfilling = new LinkedHashSet<>();
            for (Inventory inventory : inventoryRepositoryPort.findByProductId(item.getProduct().getId())) {
                if (inventory.getWarehouse() != null && inventory.getWarehouse().isActive()
                        && inventory.canFulfill(item.getQuantity())) {
                    fulfilling.add(inventory.getWarehouse().getId());
                }
            }
            if (candidates == null) {
                candidates = fulfilling;
            } else {
                candidates.retainAll(fulfilling);
            }
            if (candidates.isEmpty()) {
                throw new InsufficientStockException("No single warehouse can fulfil the whole order (product "
                        + item.getProduct().getId() + ").");
            }
        }
        Long chosen = candidates.iterator().next();
        return found(warehouseRepositoryPort.findById(chosen), "Warehouse", chosen);
    }

    private static String describe(Warehouse warehouse) {
        return warehouse.getId() == null ? warehouse.getLocation() : String.valueOf(warehouse.getId());
    }
}
