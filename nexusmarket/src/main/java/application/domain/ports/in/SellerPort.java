package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Report;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import java.util.List;
import java.util.Map;

/** Every use case available to the SELLER role (CU-05, CU-06, CU-07, CU-13 decisions, CU-14). */
public interface SellerPort {

    Seller consultMyProfile(User user);
    void changeMyPassword(User user, String currentPassword, String newPassword);
    Product createProduct(User user, Seller seller, String name, String description, Money price, ProductType type);
    void addProductVariant(User user, Product product, String name, String value);
    void updateProductPrice(User user, Product product, Money newPrice);
    void publishProduct(User user, Product product);
    void suspendProduct(User user, Product product);
    void discontinueProduct(User user, Product product);
    Product consultProduct(User user, Product product);
    List<Product> consultMyProducts(User user);
    Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock);
    void receiveStock(User user, Inventory inventory, int quantity, String reason);
    void adjustStock(User user, Inventory inventory, int newQuantity, String reason);
    void markInventoryDamaged(User user, Inventory inventory);
    List<Inventory> consultMyInventory(User user);
    List<Warehouse> consultWarehouses(User user);
    List<Order> consultMyOrders(User user);
    void approveReturn(User user, Return productReturn);
    void rejectReturn(User user, Return productReturn);
    List<Return> consultMyReturns(User user);
    Refund issueRefund(User user, Return productReturn, Money amount);
    void processRefund(User user, Refund refund);
    void completeRefund(User user, Refund refund);
    void rejectRefund(User user, Refund refund);
    List<Refund> consultMyRefunds(User user);
    /** Own-activity reports: the seller scope is forced, asking for another seller is rejected. */
    Report generateSalesReport(User user, Map<String, Object> filters);
    Report generateInventoryReport(User user, Map<String, Object> filters);
    Report generateReturnsReport(User user, Map<String, Object> filters);
}
