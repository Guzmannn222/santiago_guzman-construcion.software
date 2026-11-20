package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.*;
import application.domain.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Domain -&gt; response DTO mappings for the marketplace aggregates (null-safe, never leaks passwords). */
public final class MarketRestMapper {

    private MarketRestMapper() {
    }

    public static <S, T> List<T> list(List<S> source, Function<S, T> mapper) {
        List<T> result = new ArrayList<>();
        if (source != null) {
            for (S item : source) {
                result.add(mapper.apply(item));
            }
        }
        return result;
    }

    public static BuyerResponseDTO buyer(Buyer buyer) {
        if (buyer == null) {
            return null;
        }
        BuyerResponseDTO dto = new BuyerResponseDTO();
        dto.setId(buyer.getId());
        dto.setUser(UserRestMapper.toResponseDTO(buyer.getUser()));
        dto.setPrimaryAddress(buyer.getPrimaryAddress());
        dto.setAdditionalAddresses(buyer.getAdditionalAddresses() == null
                ? new ArrayList<>() : new ArrayList<>(buyer.getAdditionalAddresses()));
        dto.setTradingStatus(MapperSupport.code(buyer.getTradingStatus()));
        return dto;
    }

    public static WarehouseResponseDTO warehouse(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setLocation(warehouse.getLocation());
        dto.setType(MapperSupport.code(warehouse.getType()));
        dto.setActive(warehouse.isActive());
        dto.setOwnerSellerId(warehouse.getOwner() == null ? null : warehouse.getOwner().getId());
        return dto;
    }

    public static SellerResponseDTO seller(Seller seller) {
        if (seller == null) {
            return null;
        }
        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(seller.getId());
        dto.setUser(UserRestMapper.toResponseDTO(seller.getUser()));
        dto.setBusinessInfo(seller.getBusinessInfo());
        dto.setActive(seller.isActive());
        dto.setWarehouses(list(seller.getWarehouses(), MarketRestMapper::warehouse));
        return dto;
    }

    public static ProductResponseDTO product(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setSellerId(product.getSeller() == null ? null : product.getSeller().getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(MoneyRestMapper.toResponseDTO(product.getPrice()));
        dto.setType(MapperSupport.code(product.getType()));
        dto.setStatus(MapperSupport.code(product.getStatus()));
        dto.setVariants(list(product.getVariants(), variant -> {
            VariantResponseDTO v = new VariantResponseDTO();
            v.setName(variant.getName());
            v.setValue(variant.getValue());
            return v;
        }));
        return dto;
    }

    public static InventoryResponseDTO inventory(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProduct() == null ? null : inventory.getProduct().getId());
        dto.setWarehouseId(inventory.getWarehouse() == null ? null : inventory.getWarehouse().getId());
        dto.setStock(inventory.getStock());
        dto.setStatus(MapperSupport.code(inventory.getStatus()));
        dto.setLowStockThreshold(inventory.getLowStockThreshold());
        dto.setMovements(list(inventory.getMovements(), movement -> {
            InventoryMovementResponseDTO m = new InventoryMovementResponseDTO();
            m.setType(MapperSupport.code(movement.getType()));
            m.setQuantity(movement.getQuantity());
            m.setDate(MapperSupport.date(movement.getDate()));
            m.setReason(movement.getReason());
            return m;
        }));
        return dto;
    }

    public static CartResponseDTO cart(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setBuyerId(cart.getBuyer() == null ? null : cart.getBuyer().getId());
        dto.setTotal(MoneyRestMapper.toResponseDTO(cart.getTotal()));
        dto.setItems(list(cart.getItems(), item -> {
            CartItemResponseDTO i = new CartItemResponseDTO();
            i.setProductId(item.getProduct() == null ? null : item.getProduct().getId());
            i.setProductName(item.getProduct() == null ? null : item.getProduct().getName());
            i.setSellerId(item.getProduct() == null || item.getProduct().getSeller() == null ? null
                    : item.getProduct().getSeller().getId());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(MoneyRestMapper.toResponseDTO(item.getUnitPrice()));
            return i;
        }));
        return dto;
    }

    public static OrderResponseDTO order(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyer() == null ? null : order.getBuyer().getId());
        dto.setWarehouseId(order.getWarehouse() == null ? null : order.getWarehouse().getId());
        dto.setTotal(MoneyRestMapper.toResponseDTO(order.getTotal()));
        dto.setStatus(MapperSupport.code(order.getStatus()));
        dto.setDate(MapperSupport.date(order.getDate()));
        dto.setItems(list(order.getItems(), item -> {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(item.getProduct() == null ? null : item.getProduct().getId());
            i.setProductName(item.getProduct() == null ? null : item.getProduct().getName());
            i.setSellerId(item.getProduct() == null || item.getProduct().getSeller() == null ? null
                    : item.getProduct().getSeller().getId());
            i.setQuantity(item.getQuantity());
            i.setUnitPrice(MoneyRestMapper.toResponseDTO(item.getUnitPrice()));
            i.setSubtotal(MoneyRestMapper.toResponseDTO(item.getSubtotal()));
            return i;
        }));
        return dto;
    }

    public static InvoiceResponseDTO invoice(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setOrderId(invoice.getOrder() == null ? null : invoice.getOrder().getId());
        dto.setSubtotal(MoneyRestMapper.toResponseDTO(invoice.getSubtotal()));
        dto.setTax(MoneyRestMapper.toResponseDTO(invoice.getTax()));
        dto.setTotal(MoneyRestMapper.toResponseDTO(invoice.getTotal()));
        dto.setTaxRate(invoice.getTaxRate());
        dto.setCancelled(invoice.isCancelled());
        dto.setIssueDate(MapperSupport.date(invoice.getIssueDate()));
        return dto;
    }

    public static ShipmentResponseDTO shipment(Shipment shipment) {
        if (shipment == null) {
            return null;
        }
        ShipmentResponseDTO dto = new ShipmentResponseDTO();
        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrder() == null ? null : shipment.getOrder().getId());
        dto.setAddress(shipment.getAddress());
        dto.setStatus(MapperSupport.code(shipment.getStatus()));
        dto.setDispatchDate(MapperSupport.date(shipment.getDispatchDate()));
        dto.setDeliveryDate(MapperSupport.date(shipment.getDeliveryDate()));
        return dto;
    }

    public static ReturnResponseDTO productReturn(Return productReturn) {
        if (productReturn == null) {
            return null;
        }
        ReturnResponseDTO dto = new ReturnResponseDTO();
        dto.setId(productReturn.getId());
        dto.setOrderId(productReturn.getOrder() == null ? null : productReturn.getOrder().getId());
        dto.setReason(productReturn.getReason());
        dto.setStatus(MapperSupport.code(productReturn.getStatus()));
        dto.setRequestDate(MapperSupport.date(productReturn.getRequestDate()));
        java.util.List<ReturnItemResponseDTO> lines = new java.util.ArrayList<>();
        for (application.domain.models.ReturnItem item : productReturn.returnedLines()) {
            ReturnItemResponseDTO line = new ReturnItemResponseDTO();
            line.setProductId(item.getProduct() == null ? null : item.getProduct().getId());
            line.setProductName(item.getProduct() == null ? null : item.getProduct().getName());
            line.setSellerId(item.getProduct() == null || item.getProduct().getSeller() == null ? null
                    : item.getProduct().getSeller().getId());
            line.setQuantity(item.getQuantity());
            lines.add(line);
        }
        dto.setItems(lines);
        return dto;
    }

    public static RefundResponseDTO refund(Refund refund) {
        if (refund == null) {
            return null;
        }
        RefundResponseDTO dto = new RefundResponseDTO();
        dto.setId(refund.getId());
        dto.setReturnId(refund.getProductReturn() == null ? null : refund.getProductReturn().getId());
        dto.setAmount(MoneyRestMapper.toResponseDTO(refund.getAmount()));
        dto.setStatus(MapperSupport.code(refund.getStatus()));
        return dto;
    }

    public static ReportResponseDTO report(Report report) {
        if (report == null) {
            return null;
        }
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setType(MapperSupport.code(report.getType()));
        dto.setFilters(report.getFilters());
        dto.setData(report.getData());
        dto.setGenerationDate(MapperSupport.date(report.getGenerationDate()));
        return dto;
    }

    public static OperationResponseDTO operation(Operation operation) {
        if (operation == null) {
            return null;
        }
        OperationResponseDTO dto = new OperationResponseDTO();
        dto.setOperationId(operation.getOperationId());
        dto.setOperationType(MapperSupport.code(operation.getOperationType()));
        dto.setExecutionDate(MapperSupport.date(operation.getExecutionDate()));
        if (operation.getPerformedBy() != null) {
            dto.setPerformedByUserId(operation.getPerformedBy().getId());
            dto.setPerformedByUsername(operation.getPerformedBy().getUsername());
        }
        if (operation.getAffectedEntity() != null) {
            dto.setEntityType(MapperSupport.code(operation.getAffectedEntity().getType()));
            dto.setEntityId(operation.getAffectedEntity().getIdentifier());
        }
        return dto;
    }

    public static AuditLogResponseDTO auditLog(AuditLog log) {
        if (log == null) {
            return null;
        }
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setAuditId(log.getAuditId());
        dto.setOperationType(MapperSupport.code(log.getOperationType()));
        dto.setOperationDate(MapperSupport.date(log.getOperationDate()));
        if (log.getPerformedBy() != null) {
            dto.setPerformedByUserId(log.getPerformedBy().getId());
            dto.setPerformedByUsername(log.getPerformedBy().getUsername());
        }
        dto.setUserRole(MapperSupport.code(log.getUserRole()));
        if (log.getAffectedEntity() != null) {
            dto.setEntityType(MapperSupport.code(log.getAffectedEntity().getType()));
            dto.setEntityId(log.getAffectedEntity().getIdentifier());
        }
        dto.setDetails(log.getDetails());
        return dto;
    }
}
