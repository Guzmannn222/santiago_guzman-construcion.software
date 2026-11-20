package application.adapters.useCases;

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
import application.domain.ports.in.AddBuyerAddressUseCase;
import application.domain.ports.in.AddCartItemUseCase;
import application.domain.ports.in.BuyerPort;
import application.domain.ports.in.CancelOrderUseCase;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.ConfirmOrderPaymentUseCase;
import application.domain.ports.in.ConsultBuyerUseCase;
import application.domain.ports.in.ConsultCartUseCase;
import application.domain.ports.in.ConsultInvoiceUseCase;
import application.domain.ports.in.ConsultOrderUseCase;
import application.domain.ports.in.ConsultProductUseCase;
import application.domain.ports.in.ConsultRefundUseCase;
import application.domain.ports.in.ConsultReturnUseCase;
import application.domain.ports.in.ConsultShipmentUseCase;
import application.domain.ports.in.CreateOrderUseCase;
import application.domain.ports.in.RegisterBuyerUseCase;
import application.domain.ports.in.RemoveCartItemUseCase;
import application.domain.ports.in.RequestReturnUseCase;
import application.domain.ports.in.UpdateCartItemQuantityUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/** Role facade: delegates every Buyer operation to its input-port use case (each one validates status, role and ownership). */
@Service
public class BuyerUseCaseImpl implements BuyerPort {

    private final RegisterBuyerUseCase registerBuyerUseCase;
    private final ConsultBuyerUseCase consultBuyerUseCase;
    private final AddBuyerAddressUseCase addBuyerAddressUseCase;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final ConsultProductUseCase consultProductUseCase;
    private final ConsultCartUseCase consultCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ConsultOrderUseCase consultOrderUseCase;
    private final ConsultInvoiceUseCase consultInvoiceUseCase;
    private final ConsultShipmentUseCase consultShipmentUseCase;
    private final RequestReturnUseCase requestReturnUseCase;
    private final ConsultReturnUseCase consultReturnUseCase;
    private final ConsultRefundUseCase consultRefundUseCase;

    public BuyerUseCaseImpl(RegisterBuyerUseCase registerBuyerUseCase,
            ConsultBuyerUseCase consultBuyerUseCase,
            AddBuyerAddressUseCase addBuyerAddressUseCase,
            ChangeUserPasswordUseCase changeUserPasswordUseCase,
            ConsultProductUseCase consultProductUseCase,
            ConsultCartUseCase consultCartUseCase,
            AddCartItemUseCase addCartItemUseCase,
            RemoveCartItemUseCase removeCartItemUseCase,
            UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase,
            CreateOrderUseCase createOrderUseCase,
            ConfirmOrderPaymentUseCase confirmOrderPaymentUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            ConsultOrderUseCase consultOrderUseCase,
            ConsultInvoiceUseCase consultInvoiceUseCase,
            ConsultShipmentUseCase consultShipmentUseCase,
            RequestReturnUseCase requestReturnUseCase,
            ConsultReturnUseCase consultReturnUseCase,
            ConsultRefundUseCase consultRefundUseCase) {
        this.registerBuyerUseCase = registerBuyerUseCase;
        this.consultBuyerUseCase = consultBuyerUseCase;
        this.addBuyerAddressUseCase = addBuyerAddressUseCase;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.consultProductUseCase = consultProductUseCase;
        this.consultCartUseCase = consultCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.updateCartItemQuantityUseCase = updateCartItemQuantityUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.confirmOrderPaymentUseCase = confirmOrderPaymentUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.consultOrderUseCase = consultOrderUseCase;
        this.consultInvoiceUseCase = consultInvoiceUseCase;
        this.consultShipmentUseCase = consultShipmentUseCase;
        this.requestReturnUseCase = requestReturnUseCase;
        this.consultReturnUseCase = consultReturnUseCase;
        this.consultRefundUseCase = consultRefundUseCase;
    }

    @Override
    public Buyer registerMyProfile(User user, String primaryAddress) {
        return registerBuyerUseCase.registerBuyer(user, primaryAddress);
    }

    @Override
    public Buyer consultMyProfile(User user) {
        return consultBuyerUseCase.consultMyBuyer(user);
    }

    @Override
    public void addMyAddress(User user, Buyer buyer, String address) {
        addBuyerAddressUseCase.addAddress(user, buyer, address);
    }

    @Override
    public void changeMyPassword(User user, String currentPassword, String newPassword) {
        changeUserPasswordUseCase.changePassword(user, currentPassword, newPassword);
    }

    @Override
    public List<Product> browsePublishedCatalog(User user) {
        return consultProductUseCase.consultPublishedProducts(user);
    }

    @Override
    public Product consultProduct(User user, Product product) {
        return consultProductUseCase.consultProduct(user, product);
    }

    @Override
    public Cart consultMyCart(User user) {
        return consultCartUseCase.consultMyCart(user);
    }

    @Override
    public void addToCart(User user, Cart cart, Product product, int quantity) {
        addCartItemUseCase.addProduct(user, cart, product, quantity);
    }

    @Override
    public void removeFromCart(User user, Cart cart, Product product) {
        removeCartItemUseCase.removeProduct(user, cart, product);
    }

    @Override
    public void updateCartItemQuantity(User user, Cart cart, Product product, int quantity) {
        updateCartItemQuantityUseCase.updateQuantity(user, cart, product, quantity);
    }

    @Override
    public Order createOrder(User user, Cart cart, Warehouse warehouse) {
        return createOrderUseCase.createOrder(user, cart, warehouse);
    }

    @Override
    public void confirmPayment(User user, Order order) {
        confirmOrderPaymentUseCase.confirmPayment(user, order);
    }

    @Override
    public void cancelOrder(User user, Order order) {
        cancelOrderUseCase.cancelOrder(user, order);
    }

    @Override
    public Order consultOrder(User user, Order order) {
        return consultOrderUseCase.consultOrder(user, order);
    }

    @Override
    public List<Order> consultMyOrders(User user) {
        return consultOrderUseCase.consultMyOrders(user);
    }

    @Override
    public List<Invoice> consultMyInvoices(User user) {
        return consultInvoiceUseCase.consultMyInvoices(user);
    }

    @Override
    public List<Shipment> consultMyShipments(User user) {
        return consultShipmentUseCase.consultMyShipments(user);
    }

    @Override
    public Return requestReturn(User user, Order order, String reason,
            java.util.List<application.domain.models.ReturnItem> items) {
        return requestReturnUseCase.requestReturn(user, order, reason, items);
    }

    @Override
    public List<Return> consultMyReturns(User user) {
        return consultReturnUseCase.consultMyReturns(user);
    }

    @Override
    public List<Refund> consultMyRefunds(User user) {
        return consultRefundUseCase.consultMyRefunds(user);
    }
}
