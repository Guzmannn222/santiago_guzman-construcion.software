package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/** Every use case available to the BUYER role (CU-02, CU-08, CU-09, CU-13 and read access to the published catalog). */
public interface BuyerPort {

    Buyer registerMyProfile(User user, String primaryAddress);
    Buyer consultMyProfile(User user);
    void addMyAddress(User user, Buyer buyer, String address);
    void changeMyPassword(User user, String currentPassword, String newPassword);
    List<Product> browsePublishedCatalog(User user);
    Product consultProduct(User user, Product product);
    Cart consultMyCart(User user);
    void addToCart(User user, Cart cart, Product product, int quantity);
    void removeFromCart(User user, Cart cart, Product product);
    void updateCartItemQuantity(User user, Cart cart, Product product, int quantity);
    Order createOrder(User user, Cart cart, Warehouse warehouse);
    void confirmPayment(User user, Order order);
    void cancelOrder(User user, Order order);
    Order consultOrder(User user, Order order);
    List<Order> consultMyOrders(User user);
    List<Invoice> consultMyInvoices(User user);
    List<Shipment> consultMyShipments(User user);
    default Return requestReturn(User user, Order order, String reason) {
        return requestReturn(user, order, reason, null);
    }
    Return requestReturn(User user, Order order, String reason, java.util.List<application.domain.models.ReturnItem> items);
    List<Return> consultMyReturns(User user);
    List<Refund> consultMyRefunds(User user);
}
