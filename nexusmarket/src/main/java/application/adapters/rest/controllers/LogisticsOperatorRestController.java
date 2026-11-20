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

import application.domain.ports.in.LogisticsOperatorPort;

@RestController
@RequestMapping("/api/v1/logistics-operator")
public class LogisticsOperatorRestController {

    private final LogisticsOperatorPort port;

    public LogisticsOperatorRestController(LogisticsOperatorPort port) {
        this.port = port;
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

    @PostMapping("/inventories")
    public ResponseEntity<InventoryResponseDTO> openInventory(@AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody OpenInventoryRequestDTO requestDTO) {
        Inventory inventory = port.openInventory(user, EntityStubs.product(requestDTO.getProductId()),
                EntityStubs.warehouse(requestDTO.getWarehouseId()), requestDTO.getInitialStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.inventory(inventory));
    }

    @GetMapping("/inventories")
    public ResponseEntity<List<InventoryResponseDTO>> consultInventory(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultInventory(user), MarketRestMapper::inventory));
    }

    @PostMapping("/inventories/{inventoryId}/receipts")
    public ResponseEntity<Void> receiveStock(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody StockQuantityRequestDTO requestDTO) {
        port.receiveStock(user, EntityStubs.inventory(inventoryId), requestDTO.getQuantity(), requestDTO.getReason());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/inventories/{inventoryId}/stock")
    public ResponseEntity<Void> adjustStock(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody AdjustStockRequestDTO requestDTO) {
        port.adjustStock(user, EntityStubs.inventory(inventoryId), requestDTO.getNewQuantity(), requestDTO.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inventories/{inventoryId}/returns")
    public ResponseEntity<Void> returnStock(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId,
            @Valid @RequestBody StockQuantityRequestDTO requestDTO) {
        port.returnStock(user, EntityStubs.inventory(inventoryId), requestDTO.getQuantity(), requestDTO.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inventories/{inventoryId}/damage")
    public ResponseEntity<Void> markDamaged(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long inventoryId) {
        port.markInventoryDamaged(user, EntityStubs.inventory(inventoryId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> consultOrders(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultOrders(user), MarketRestMapper::order));
    }

    @PostMapping("/orders/{orderId}/shipments")
    public ResponseEntity<ShipmentResponseDTO> createShipment(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long orderId,
            @Valid @RequestBody CreateShipmentRequestDTO requestDTO) {
        Shipment shipment = port.createShipment(user, EntityStubs.order(orderId), requestDTO.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.shipment(shipment));
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> consultShipments(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultShipments(user), MarketRestMapper::shipment));
    }

    @PostMapping("/shipments/{shipmentId}/preparation")
    public ResponseEntity<Void> prepareShipment(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long shipmentId) {
        port.prepareShipment(user, EntityStubs.shipment(shipmentId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shipments/{shipmentId}/dispatch")
    public ResponseEntity<Void> dispatchShipment(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long shipmentId) {
        port.dispatchShipment(user, EntityStubs.shipment(shipmentId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/shipments/{shipmentId}/status")
    public ResponseEntity<Void> updateShipmentStatus(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long shipmentId,
            @Valid @RequestBody UpdateShipmentStatusRequestDTO requestDTO) {
        port.updateShipmentStatus(user, EntityStubs.shipment(shipmentId), ShipmentStatus.fromCode(requestDTO.getStatus()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shipments/{shipmentId}/delivery")
    public ResponseEntity<Void> confirmDelivery(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long shipmentId) {
        port.confirmDelivery(user, EntityStubs.shipment(shipmentId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnResponseDTO>> consultReturns(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(MarketRestMapper.list(port.consultReturns(user), MarketRestMapper::productReturn));
    }

    @PostMapping("/returns/{returnId}/completion")
    public ResponseEntity<Void> completeReturn(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long returnId) {
        port.completeReturn(user, EntityStubs.productReturn(returnId));
        return ResponseEntity.noContent().build();
    }
}
