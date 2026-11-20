package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.domain.models.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import application.domain.ports.in.BuyerPort;

@RestController
@RequestMapping("/api/v1/buyer")
public class BuyerRestController {

    private final BuyerPort buyerPort;

    public BuyerRestController(BuyerPort buyerPort) {
        this.buyerPort = buyerPort;
    }

    @PostMapping("/profile")
    public ResponseEntity<BuyerResponseDTO> registerMyProfile(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterMyBuyerProfileRequestDTO requestDTO) {
        Buyer buyer = buyerPort.registerMyProfile(user, requestDTO.getPrimaryAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.buyer(buyer));
    }

    @GetMapping("/profile")
    public ResponseEntity<BuyerResponseDTO> consultMyProfile(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.buyer(buyerPort.consultMyProfile(user)));
    }

    @PostMapping("/addresses")
    public ResponseEntity<BuyerResponseDTO> addAddress(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody AddressRequestDTO requestDTO) {
        Buyer buyer = buyerPort.consultMyProfile(user);
        buyerPort.addMyAddress(user, buyer, requestDTO.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.buyer(buyerPort.consultMyProfile(user)));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> browseCatalog(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.browsePublishedCatalog(user), MarketRestMapper::product));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> consultProduct(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        return ResponseEntity.ok(MarketRestMapper.product(buyerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDTO> consultMyCart(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.cart(buyerPort.consultMyCart(user)));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponseDTO> addToCart(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody CartItemRequestDTO requestDTO) {
        Cart cart = buyerPort.consultMyCart(user);
        buyerPort.addToCart(user, cart, EntityStubs.product(requestDTO.getProductId()), requestDTO.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.cart(buyerPort.consultMyCart(user)));
    }

    @PutMapping("/cart/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId, @Valid @RequestBody UpdateCartItemRequestDTO requestDTO) {
        Cart cart = buyerPort.consultMyCart(user);
        buyerPort.updateCartItemQuantity(user, cart, EntityStubs.product(productId), requestDTO.getQuantity());
        return ResponseEntity.ok(MarketRestMapper.cart(buyerPort.consultMyCart(user)));
    }

    @DeleteMapping("/cart/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        Cart cart = buyerPort.consultMyCart(user);
        buyerPort.removeFromCart(user, cart, EntityStubs.product(productId));
        return ResponseEntity.ok(MarketRestMapper.cart(buyerPort.consultMyCart(user)));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody CreateOrderRequestDTO requestDTO) {
        Cart cart = buyerPort.consultMyCart(user);
        Order order = buyerPort.createOrder(user, cart, EntityStubs.warehouse(requestDTO.getWarehouseId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.order(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultMyOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.consultMyOrders(user), MarketRestMapper::order));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> consultOrder(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(MarketRestMapper.order(buyerPort.consultOrder(user, EntityStubs.order(orderId))));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponseDTO> confirmPayment(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long orderId) {
        buyerPort.confirmPayment(user, EntityStubs.order(orderId));
        return ResponseEntity.ok(MarketRestMapper.order(buyerPort.consultOrder(user, EntityStubs.order(orderId))));
    }

    @PostMapping("/orders/{orderId}/cancellation")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long orderId) {
        buyerPort.cancelOrder(user, EntityStubs.order(orderId));
        return ResponseEntity.ok(MarketRestMapper.order(buyerPort.consultOrder(user, EntityStubs.order(orderId))));
    }

    @PostMapping("/orders/{orderId}/returns")
    public ResponseEntity<ReturnResponseDTO> requestReturn(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long orderId, @Valid @RequestBody RequestReturnRequestDTO requestDTO) {
        java.util.List<application.domain.models.ReturnItem> items = new java.util.ArrayList<>();
        if (requestDTO.getItems() != null) {
            for (application.adapters.rest.dtos.requests.ReturnItemRequestDTO line : requestDTO.getItems()) {
                items.add(new application.domain.models.ReturnItem(
                        EntityStubs.product(line.getProductId()), line.getQuantity()));
            }
        }
        Return productReturn = buyerPort.requestReturn(user, EntityStubs.order(orderId), requestDTO.getReason(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.productReturn(productReturn));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> consultMyInvoices(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.consultMyInvoices(user), MarketRestMapper::invoice));
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> consultMyShipments(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.consultMyShipments(user), MarketRestMapper::shipment));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnResponseDTO>> consultMyReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.consultMyReturns(user), MarketRestMapper::productReturn));
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<RefundResponseDTO>> consultMyRefunds(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(buyerPort.consultMyRefunds(user), MarketRestMapper::refund));
    }
}
