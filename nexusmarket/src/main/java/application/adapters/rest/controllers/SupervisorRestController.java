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

import application.domain.ports.in.SupervisorPort;

@RestController
@RequestMapping("/api/v1/supervisor")
public class SupervisorRestController {

    private final SupervisorPort port;

    public SupervisorRestController(SupervisorPort port) {
        this.port = port;
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

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultOrders(user), MarketRestMapper::order));
    }

    @GetMapping("/inventories")
    public ResponseEntity<List<InventoryResponseDTO>> consultInventory(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultInventory(user), MarketRestMapper::inventory));
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultReturns(user), MarketRestMapper::productReturn));
    }

    @GetMapping("/refunds")
    public ResponseEntity<List<RefundResponseDTO>> consultRefunds(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultRefunds(user), MarketRestMapper::refund));
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

    @GetMapping("/audit-logs/operation-types/{operationType}")
    public ResponseEntity<List<AuditLogResponseDTO>> auditLogsByOperationType(@AuthenticationPrincipal(expression = "user") User user, @PathVariable String operationType) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultAuditLogsByOperationType(user, OperationType.fromCode(operationType)), MarketRestMapper::auditLog));
    }

    @GetMapping("/operations/users/{userId}")
    public ResponseEntity<List<OperationResponseDTO>> operationsByUser(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long userId) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultOperationsByUser(user, EntityStubs.user(userId)), MarketRestMapper::operation));
    }

    @GetMapping("/operations/entities")
    public ResponseEntity<List<OperationResponseDTO>> operationsByEntity(@AuthenticationPrincipal(expression = "user") User user, @RequestParam String type,
            @RequestParam String id) {
        return ResponseEntity.ok(MarketRestMapper.list(
                port.consultOperationsByEntity(user, EntityStubs.entityReference(type, id)), MarketRestMapper::operation));
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
