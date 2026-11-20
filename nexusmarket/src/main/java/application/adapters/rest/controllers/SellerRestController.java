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

import application.domain.ports.in.SellerPort;

@RestController
@RequestMapping("/api/v1/seller")
public class SellerRestController {

    private final SellerPort sellerPort;

    public SellerRestController(SellerPort sellerPort) {
        this.sellerPort = sellerPort;
    }

    @GetMapping("/profile")
    public ResponseEntity<SellerResponseDTO> consultMyProfile(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.seller(sellerPort.consultMyProfile(user)));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody CreateProductRequestDTO requestDTO) {
        Seller seller = sellerPort.consultMyProfile(user);
        Product product = sellerPort.createProduct(user, seller, requestDTO.getName(), requestDTO.getDescription(),
                MoneyRestMapper.toDomain(requestDTO.getPrice(), requestDTO.getCurrency()),
                application.domain.valueobjects.ProductType.fromCode(requestDTO.getType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.product(product));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> consultMyProducts(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultMyProducts(user), MarketRestMapper::product));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDTO> consultProduct(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        return ResponseEntity.ok(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<ProductResponseDTO> addVariant(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId, @Valid @RequestBody AddVariantRequestDTO requestDTO) {
        sellerPort.addProductVariant(user, EntityStubs.product(productId), requestDTO.getName(), requestDTO.getValue());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PutMapping("/products/{productId}/price")
    public ResponseEntity<ProductResponseDTO> updatePrice(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId, @Valid @RequestBody UpdatePriceRequestDTO requestDTO) {
        sellerPort.updateProductPrice(user, EntityStubs.product(productId),
                MoneyRestMapper.toDomain(requestDTO.getPrice(), requestDTO.getCurrency()));
        return ResponseEntity.ok(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PostMapping("/products/{productId}/publication")
    public ResponseEntity<ProductResponseDTO> publish(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        sellerPort.publishProduct(user, EntityStubs.product(productId));
        return ResponseEntity.ok(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PostMapping("/products/{productId}/suspension")
    public ResponseEntity<ProductResponseDTO> suspend(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        sellerPort.suspendProduct(user, EntityStubs.product(productId));
        return ResponseEntity.ok(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PostMapping("/products/{productId}/discontinuation")
    public ResponseEntity<ProductResponseDTO> discontinue(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long productId) {
        sellerPort.discontinueProduct(user, EntityStubs.product(productId));
        return ResponseEntity.ok(MarketRestMapper.product(sellerPort.consultProduct(user, EntityStubs.product(productId))));
    }

    @PostMapping("/inventories")
    public ResponseEntity<InventoryResponseDTO> openInventory(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody OpenInventoryRequestDTO requestDTO) {
        Inventory inventory = sellerPort.openInventory(user, EntityStubs.product(requestDTO.getProductId()),
                EntityStubs.warehouse(requestDTO.getWarehouseId()), requestDTO.getInitialStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.inventory(inventory));
    }

    @GetMapping("/inventories")
    public ResponseEntity<List<InventoryResponseDTO>> consultMyInventory(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultMyInventory(user), MarketRestMapper::inventory));
    }

    @PostMapping("/inventories/{inventoryId}/receipts")
    public ResponseEntity<Void> receiveStock(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long inventoryId, @Valid @RequestBody StockQuantityRequestDTO requestDTO) {
        sellerPort.receiveStock(user, EntityStubs.inventory(inventoryId), requestDTO.getQuantity(), requestDTO.getReason());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/inventories/{inventoryId}/stock")
    public ResponseEntity<Void> adjustStock(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long inventoryId, @Valid @RequestBody AdjustStockRequestDTO requestDTO) {
        sellerPort.adjustStock(user, EntityStubs.inventory(inventoryId), requestDTO.getNewQuantity(), requestDTO.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inventories/{inventoryId}/damage")
    public ResponseEntity<Void> markDamaged(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long inventoryId) {
        sellerPort.markInventoryDamaged(user, EntityStubs.inventory(inventoryId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> consultWarehouses(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultWarehouses(user), MarketRestMapper::warehouse));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultMyOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultMyOrders(user), MarketRestMapper::order));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnResponseDTO>> consultMyReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultMyReturns(user), MarketRestMapper::productReturn));
    }

    @PostMapping("/returns/{returnId}/approval")
    public ResponseEntity<Void> approveReturn(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long returnId) {
        sellerPort.approveReturn(user, EntityStubs.productReturn(returnId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/returns/{returnId}/rejection")
    public ResponseEntity<Void> rejectReturn(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long returnId) {
        sellerPort.rejectReturn(user, EntityStubs.productReturn(returnId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/returns/{returnId}/refund")
    public ResponseEntity<RefundResponseDTO> issueRefund(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long returnId, @Valid @RequestBody IssueRefundRequestDTO requestDTO) {
        Refund refund = sellerPort.issueRefund(user, EntityStubs.productReturn(returnId),
                MoneyRestMapper.toDomain(requestDTO.getAmount(), requestDTO.getCurrency()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.refund(refund));
    }

    @PostMapping("/refunds/{refundId}/processing")
    public ResponseEntity<Void> processRefund(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long refundId) {
        sellerPort.processRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refunds/{refundId}/completion")
    public ResponseEntity<Void> completeRefund(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long refundId) {
        sellerPort.completeRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refunds/{refundId}/rejection")
    public ResponseEntity<Void> rejectRefund(@AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long refundId) {
        sellerPort.rejectRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<RefundResponseDTO>> consultMyRefunds(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(sellerPort.consultMyRefunds(user), MarketRestMapper::refund));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<ReportResponseDTO> salesReport(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.report(sellerPort.generateSalesReport(user, Map.of())));
    }

    @GetMapping("/reports/inventory")
    public ResponseEntity<ReportResponseDTO> inventoryReport(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.report(sellerPort.generateInventoryReport(user, Map.of())));
    }

    @GetMapping("/reports/returns")
    public ResponseEntity<ReportResponseDTO> returnsReport(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.report(sellerPort.generateReturnsReport(user, Map.of())));
    }
}
