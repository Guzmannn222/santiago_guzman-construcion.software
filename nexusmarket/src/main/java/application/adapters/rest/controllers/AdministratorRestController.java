package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.domain.models.*;
import application.domain.valueobjects.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import application.domain.ports.in.AdministratorPort;

@RestController
@RequestMapping("/api/v1/administrator")
public class AdministratorRestController {

    private final AdministratorPort port;

    public AdministratorRestController(AdministratorPort port) {
        this.port = port;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> registerUser(@AuthenticationPrincipal(expression = "user") User user, @Valid @RequestBody RegisterUserRequestDTO requestDTO) {
        User created = port.registerUser(user, UserRestMapper.toDomain(requestDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponseDTO(created));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> consultUsers(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(UserRestMapper.toResponseDTOs(port.consultUsers(user)));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> consultUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        return ResponseEntity.ok(UserRestMapper.toResponseDTO(port.consultUser(user, EntityStubs.user(userId))));
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<Void> blockUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        port.blockUser(user, EntityStubs.user(userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/activation")
    public ResponseEntity<Void> activateUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        port.activateUser(user, EntityStubs.user(userId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> changeUserRole(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId,
            @Valid @RequestBody ChangeUserRoleRequestDTO requestDTO) {
        port.changeUserRole(user, EntityStubs.user(userId), SystemRole.fromCode(requestDTO.getRole()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sellers")
    public ResponseEntity<SellerResponseDTO> registerSeller(@AuthenticationPrincipal(expression = "user") User user, @Valid @RequestBody RegisterSellerRequestDTO requestDTO) {
        Seller seller = port.registerSeller(user, UserRestMapper.toDomain(requestDTO), requestDTO.getBusinessInfo(),
                requestDTO.getFirstWarehouseLocation());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.seller(seller));
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<SellerResponseDTO>> consultSellers(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultSellers(user), MarketRestMapper::seller));
    }

    @PostMapping("/sellers/{sellerId}/deactivation")
    public ResponseEntity<Void> deactivateSeller(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long sellerId) {
        port.deactivateSeller(user, EntityStubs.seller(sellerId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sellers/{sellerId}/reactivation")
    public ResponseEntity<Void> reactivateSeller(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long sellerId) {
        port.reactivateSeller(user, EntityStubs.seller(sellerId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponseDTO> registerWarehouse(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody RegisterWarehouseRequestDTO requestDTO) {
        Warehouse warehouse = port.registerWarehouse(user, requestDTO.getLocation(),
                WarehouseType.fromCode(requestDTO.getType()), EntityStubs.seller(requestDTO.getOwnerSellerId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.warehouse(warehouse));
    }

    @PostMapping("/warehouses/{warehouseId}/deactivation")
    public ResponseEntity<Void> deactivateWarehouse(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long warehouseId) {
        port.deactivateWarehouse(user, EntityStubs.warehouse(warehouseId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> consultWarehouses(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultWarehouses(user), MarketRestMapper::warehouse));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultOrders(user), MarketRestMapper::order));
    }

    @PostMapping("/orders/{orderId}/invoice")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId,
            @Valid @RequestBody GenerateInvoiceRequestDTO requestDTO) {
        Invoice invoice = port.generateInvoice(user, EntityStubs.order(orderId), requestDTO.getTaxRate());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.invoice(invoice));
    }

    @PostMapping("/invoices/{invoiceId}/cancellation")
    public ResponseEntity<Void> cancelInvoice(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long invoiceId) {
        port.cancelInvoice(user, EntityStubs.invoice(invoiceId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> consultInvoices(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultInvoices(user), MarketRestMapper::invoice));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultReturns(user), MarketRestMapper::productReturn));
    }

    @PostMapping("/returns/{returnId}/refund")
    public ResponseEntity<RefundResponseDTO> issueRefund(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long returnId,
            @Valid @RequestBody IssueRefundRequestDTO requestDTO) {
        Refund refund = port.issueRefund(user, EntityStubs.productReturn(returnId),
                MoneyRestMapper.toDomain(requestDTO.getAmount(), requestDTO.getCurrency()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.refund(refund));
    }

    @PostMapping("/refunds/{refundId}/processing")
    public ResponseEntity<Void> processRefund(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long refundId) {
        port.processRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refunds/{refundId}/completion")
    public ResponseEntity<Void> completeRefund(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long refundId) {
        port.completeRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refunds/{refundId}/rejection")
    public ResponseEntity<Void> rejectRefund(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long refundId) {
        port.rejectRefund(user, EntityStubs.refund(refundId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<RefundResponseDTO>> consultRefunds(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultRefunds(user), MarketRestMapper::refund));
    }

    @PostMapping("/reports/sales")
    public ResponseEntity<ReportResponseDTO> generateSalesReport(@AuthenticationPrincipal(expression = "user") User user, @RequestBody(required = false) ReportRequestDTO body) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateSalesReport(user, filters(body))));
    }

    @PostMapping("/reports/inventory")
    public ResponseEntity<ReportResponseDTO> generateInventoryReport(@AuthenticationPrincipal(expression = "user") User user, @RequestBody(required = false) ReportRequestDTO body) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateInventoryReport(user, filters(body))));
    }

    @PostMapping("/reports/returns")
    public ResponseEntity<ReportResponseDTO> generateReturnsReport(@AuthenticationPrincipal(expression = "user") User user, @RequestBody(required = false) ReportRequestDTO body) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateReturnsReport(user, filters(body))));
    }

    @GetMapping("/audit-logs/users/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditLogsByUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultAuditLogsByUser(user, EntityStubs.user(userId)), MarketRestMapper::auditLog));
    }

    @GetMapping("/audit-logs/entities")
    public ResponseEntity<List<AuditLogResponseDTO>> auditLogsByEntity(@AuthenticationPrincipal(expression = "user") User user, @RequestParam String type,
            @RequestParam String id) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultAuditLogsByEntity(user, EntityStubs.entityReference(type, id)), MarketRestMapper::auditLog));
    }

    @GetMapping("/operations/users/{userId}")
    public ResponseEntity<List<OperationResponseDTO>> operationsByUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultOperationsByUser(user, EntityStubs.user(userId)), MarketRestMapper::operation));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<ReportResponseDTO> salesReportGet(@AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(required = false) Long sellerId) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateSalesReport(user, sellerFilter(sellerId))));
    }

    @GetMapping("/reports/inventory")
    public ResponseEntity<ReportResponseDTO> inventoryReportGet(@AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(required = false) Long sellerId) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateInventoryReport(user, sellerFilter(sellerId))));
    }

    @GetMapping("/reports/returns")
    public ResponseEntity<ReportResponseDTO> returnsReportGet(@AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(required = false) Long sellerId) {
        return ResponseEntity.ok(MarketRestMapper.report(port.generateReturnsReport(user, sellerFilter(sellerId))));
    }

    private static Map<String, Object> sellerFilter(Long sellerId) {
        return sellerId == null ? Map.of() : Map.of("sellerId", sellerId);
    }

    private static Map<String, Object> filters(ReportRequestDTO body) {
        return body == null || body.getFilters() == null ? new java.util.HashMap<>() : body.getFilters();
    }
}
