package application.domain;

import application.domain.enums.NotificationChannel;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.ValidateRoleAuthorizationService;
import application.domain.services.authorization.ValidateUserAuthorizationStatusService;
import application.domain.services.operation.RegisterAuditLogService;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.services.operation.RegisterOperationService;
import application.domain.valueobjects.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/** In-memory fakes of every output port plus builders of ready-made business data. */
public class Fixture {

    /** Generic id-assigning store. */
    public static class Store<T> {
        public final Map<Long, T> data = new LinkedHashMap<>();
        private long seq = 0;
        private final Function<T, Long> getId;
        private final BiConsumer<T, Long> setId;

        Store(Function<T, Long> getId, BiConsumer<T, Long> setId) {
            this.getId = getId;
            this.setId = setId;
        }

        public T save(T t) {
            if (getId.apply(t) == null) {
                setId.accept(t, ++seq);
            }
            data.put(getId.apply(t), t);
            return t;
        }

        public void update(T t) {
            data.put(getId.apply(t), t);
        }

        public Optional<T> findById(Long id) {
            return Optional.ofNullable(data.get(id));
        }

        public List<T> findAll() {
            return new ArrayList<>(data.values());
        }

        public List<T> where(Predicate<T> p) {
            List<T> out = new ArrayList<>();
            for (T t : data.values()) {
                if (p.test(t)) {
                    out.add(t);
                }
            }
            return out;
        }

        public Optional<T> first(Predicate<T> p) {
            return where(p).stream().findFirst();
        }
    }

    public final Store<User> users = new Store<>(User::getId, User::setId);
    public final Store<Buyer> buyers = new Store<>(Buyer::getId, Buyer::setId);
    public final Store<Seller> sellers = new Store<>(Seller::getId, Seller::setId);
    public final Store<Warehouse> warehouses = new Store<>(Warehouse::getId, Warehouse::setId);
    public final Store<Product> products = new Store<>(Product::getId, Product::setId);
    public final Store<Inventory> inventories = new Store<>(Inventory::getId, Inventory::setId);
    public final Store<Cart> carts = new Store<>(Cart::getId, Cart::setId);
    public final Store<Order> orders = new Store<>(Order::getId, Order::setId);
    public final Store<Invoice> invoices = new Store<>(Invoice::getId, Invoice::setId);
    public final Store<Shipment> shipments = new Store<>(Shipment::getId, Shipment::setId);
    public final Store<Return> returns = new Store<>(Return::getId, Return::setId);
    public final Store<Refund> refunds = new Store<>(Refund::getId, Refund::setId);
    public final Store<Operation> operations = new Store<>(Operation::getOperationId, Operation::setOperationId);
    public final List<AuditLog> audits = new ArrayList<>();
    public final List<String> notifications = new ArrayList<>();

    public int defaultLowStock = 5;

    public final UserRepositoryPort userPort = new UserRepositoryPort() {
        public User save(User u) { return users.save(u); }
        public void update(User u) { users.update(u); }
        public Optional<User> findById(Long id) { return users.findById(id); }
        public List<User> findAll() { return users.findAll(); }
        public Optional<User> findByUsername(String n) { return users.first(u -> n != null && n.equals(u.getUsername())); }
        public Optional<User> findByEmail(String e) { return users.first(u -> e != null && e.equals(u.getEmail())); }
        public Optional<User> findByIdentityDocument(String d) {
            return users.first(u -> d != null && d.equals(u.getIdentityDocument()));
        }
    };
    public final BuyerRepositoryPort buyerPort = new BuyerRepositoryPort() {
        public Buyer save(Buyer b) { return buyers.save(b); }
        public void update(Buyer b) { buyers.update(b); }
        public Optional<Buyer> findById(Long id) { return buyers.findById(id); }
        public List<Buyer> findAll() { return buyers.findAll(); }
        public Optional<Buyer> findByUserId(Long id) { return buyers.first(b -> b.getUser().getId().equals(id)); }
    };
    public final SellerRepositoryPort sellerPort = new SellerRepositoryPort() {
        public Seller save(Seller s) { return sellers.save(s); }
        public void update(Seller s) { sellers.update(s); }
        public Optional<Seller> findById(Long id) { return sellers.findById(id); }
        public List<Seller> findAll() { return sellers.findAll(); }
        public Optional<Seller> findByUserId(Long id) { return sellers.first(s -> s.getUser().getId().equals(id)); }
    };
    public final WarehouseRepositoryPort warehousePort = new WarehouseRepositoryPort() {
        public Warehouse save(Warehouse w) { return warehouses.save(w); }
        public void update(Warehouse w) { warehouses.update(w); }
        public Optional<Warehouse> findById(Long id) { return warehouses.findById(id); }
        public List<Warehouse> findAll() { return warehouses.findAll(); }
        public List<Warehouse> findByOwnerId(Long id) {
            return warehouses.where(w -> w.getOwner() != null && w.getOwner().getId().equals(id));
        }
    };
    public final ProductRepositoryPort productPort = new ProductRepositoryPort() {
        public Product save(Product p) { return products.save(p); }
        public void update(Product p) { products.update(p); }
        public Optional<Product> findById(Long id) { return products.findById(id); }
        public List<Product> findAll() { return products.findAll(); }
        public List<Product> findByStatus(ProductStatus st) { return products.where(p -> st.equals(p.getStatus())); }
        public List<Product> findBySellerId(Long id) { return products.where(p -> p.getSeller().getId().equals(id)); }
    };
    public final InventoryRepositoryPort inventoryPort = new InventoryRepositoryPort() {
        public Inventory save(Inventory i) { return inventories.save(i); }
        public void update(Inventory i) { inventories.update(i); }
        public Optional<Inventory> findById(Long id) { return inventories.findById(id); }
        public List<Inventory> findAll() { return inventories.findAll(); }
        public Optional<Inventory> findByProductAndWarehouse(Product p, Warehouse w) {
            return inventories.first(i -> i.getProduct().getId().equals(p.getId()) && i.getWarehouse().getId().equals(w.getId()));
        }
        public List<Inventory> findByProductId(Long id) { return inventories.where(i -> i.getProduct().getId().equals(id)); }
        public List<Inventory> findByWarehouseId(Long id) { return inventories.where(i -> i.getWarehouse().getId().equals(id)); }
    };
    public final CartRepositoryPort cartPort = new CartRepositoryPort() {
        public Cart save(Cart c) { return carts.save(c); }
        public void update(Cart c) { carts.update(c); }
        public Optional<Cart> findById(Long id) { return carts.findById(id); }
        public List<Cart> findAll() { return carts.findAll(); }
        public Optional<Cart> findByBuyerId(Long id) { return carts.first(c -> c.getBuyer().getId().equals(id)); }
    };
    public final OrderRepositoryPort orderPort = new OrderRepositoryPort() {
        public Order save(Order o) { return orders.save(o); }
        public void update(Order o) { orders.update(o); }
        public Optional<Order> findById(Long id) { return orders.findById(id); }
        public List<Order> findAll() { return orders.findAll(); }
        public List<Order> findByBuyer(Buyer b) { return orders.where(o -> o.getBuyer().getId().equals(b.getId())); }
    };
    public final InvoiceRepositoryPort invoicePort = new InvoiceRepositoryPort() {
        public Invoice save(Invoice i) { return invoices.save(i); }
        public void update(Invoice i) { invoices.update(i); }
        public Optional<Invoice> findById(Long id) { return invoices.findById(id); }
        public List<Invoice> findAll() { return invoices.findAll(); }
        public Optional<Invoice> findByOrderId(Long id) { return invoices.first(i -> i.getOrder().getId().equals(id)); }
    };
    public final ShipmentRepositoryPort shipmentPort = new ShipmentRepositoryPort() {
        public Shipment save(Shipment s) { return shipments.save(s); }
        public void update(Shipment s) { shipments.update(s); }
        public Optional<Shipment> findById(Long id) { return shipments.findById(id); }
        public List<Shipment> findAll() { return shipments.findAll(); }
        public Optional<Shipment> findByOrderId(Long id) { return shipments.first(s -> s.getOrder().getId().equals(id)); }
    };
    public final ReturnRepositoryPort returnPort = new ReturnRepositoryPort() {
        public Return save(Return r) { return returns.save(r); }
        public void update(Return r) { returns.update(r); }
        public Optional<Return> findById(Long id) { return returns.findById(id); }
        public List<Return> findAll() { return returns.findAll(); }
        public List<Return> findByOrderId(Long id) { return returns.where(r -> r.getOrder().getId().equals(id)); }
    };
    public final RefundRepositoryPort refundPort = new RefundRepositoryPort() {
        public Refund save(Refund r) { return refunds.save(r); }
        public void update(Refund r) { refunds.update(r); }
        public Optional<Refund> findById(Long id) { return refunds.findById(id); }
        public List<Refund> findAll() { return refunds.findAll(); }
        public Optional<Refund> findByReturnId(Long id) { return refunds.first(r -> r.getProductReturn().getId().equals(id)); }
    };
    public final OperationRepositoryPort operationPort = new OperationRepositoryPort() {
        public Operation save(Operation o) { return operations.save(o); }
        public List<Operation> findByUserId(Long id) {
            return operations.where(o -> o.getPerformedBy() != null && id.equals(o.getPerformedBy().getId()));
        }
        public List<Operation> findByEntity(EntityReference e) {
            return operations.where(o -> e.equals(o.getAffectedEntity()));
        }
    };
    public final AuditLogRepositoryPort auditPort = new AuditLogRepositoryPort() {
        public AuditLog save(AuditLog a) { a.setAuditId("a" + (audits.size() + 1)); audits.add(a); return a; }
        public List<AuditLog> findByUserId(Long id) {
            List<AuditLog> out = new ArrayList<>();
            for (AuditLog a : audits) {
                if (a.getPerformedBy() != null && id.equals(a.getPerformedBy().getId())) out.add(a);
            }
            return out;
        }
        public List<AuditLog> findByEntity(EntityReference e) {
            List<AuditLog> out = new ArrayList<>();
            for (AuditLog a : audits) { if (e.equals(a.getAffectedEntity())) out.add(a); }
            return out;
        }
        public List<AuditLog> findByOperationType(OperationType t) {
            List<AuditLog> out = new ArrayList<>();
            for (AuditLog a : audits) { if (t.equals(a.getOperationType())) out.add(a); }
            return out;
        }
    };
    public final PasswordServicePort passwordPort = new PasswordServicePort() {
        public boolean matches(String raw, String encoded) { return encoded != null && encoded.equals("enc:" + raw); }
        public String encrypt(String raw) { return "enc:" + raw; }
    };
    public final JwtServicePort jwtPort = new JwtServicePort() {
        public String generateToken(User u) { return "token-" + u.getId() + "-" + u.getAuthTokenVersion(); }
        public boolean validateToken(String token) { return token != null && token.startsWith("token-"); }
        public Long extractUserId(String token) { return Long.valueOf(token.split("-")[1]); }
        public Integer extractTokenVersion(String token) { return Integer.valueOf(token.split("-")[2]); }
        public User reconstructUser(String token) { return users.findById(extractUserId(token)).orElse(null); }
    };
    public final NotificationPort notificationPort = new NotificationPort() {
        public void notifyUser(User user, String message, NotificationChannel channel) { notifications.add(message); }
    };
    public final BusinessConfigurationPort configPort = new BusinessConfigurationPort() {
        public BigDecimal getDefaultTaxRate() { return new BigDecimal("0.19"); }
        public int getLowStockThreshold() { return defaultLowStock; }
    };

    public final ValidateUserAuthorizationStatusService validateStatus = new ValidateUserAuthorizationStatusService();
    public final ValidateRoleAuthorizationService validateRole = new ValidateRoleAuthorizationService();
    public final RegisterOperationAndAuditService traceability = new RegisterOperationAndAuditService(
            new RegisterOperationService(operationPort), new RegisterAuditLogService(auditPort));

    // ------------------------------------------------------------------ builders

    private int counter = 0;

    public User newUser(SystemRole role) {
        int n = ++counter;
        User u = new User("user" + n, "password" + n, "Name " + n, "user" + n + "@mail.com", "DOC" + n, role);
        u.setPassword(passwordPort.encrypt("password" + n));
        return users.save(u);
    }

    public User admin() { return newUser(SystemRole.ADMINISTRATOR); }
    public User supervisor() { return newUser(SystemRole.SUPERVISOR); }
    public User logistics() { return newUser(SystemRole.LOGISTICS_OPERATOR); }

    public Buyer newBuyer() {
        Buyer b = buyers.save(new Buyer(newUser(SystemRole.BUYER), "Street 1 #2-3"));
        b.setCart(carts.save(new Cart(b)));
        return b;
    }

    public Seller newSeller() {
        Seller s = sellers.save(new Seller(newUser(SystemRole.SELLER), "Shop business"));
        Warehouse w = warehouses.save(new Warehouse("Seller depot", WarehouseType.SELLER, s));
        s.addWarehouse(w);
        return s;
    }

    public static Money cop(String amount) { return Money.of(new BigDecimal(amount), application.domain.valueobjects.Currency.COP); }

    public Product newProduct(Seller seller, ProductType type, String price, boolean published) {
        Product p = products.save(new Product(seller, "Product " + (++counter), "desc", cop(price), type));
        if (published) {
            p.publish();
            products.update(p);
        }
        return p;
    }

    public Warehouse warehouseOf(Seller seller) { return seller.getWarehouses().get(0); }

    public Inventory newInventory(Product product, Warehouse warehouse, int stock) {
        Inventory i = new Inventory(product, warehouse, stock);
        return inventories.save(i);
    }
}
