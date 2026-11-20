package application.domain.services.report;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.GenerateInventoryReportUseCase;

/** Stock summary. Filters: warehouseId, productId. */
@Service
public class GenerateInventoryReportService extends AuthorizedService implements GenerateInventoryReportUseCase {

    private static final Set<String> ALLOWED = Set.of("warehouseId", "productId", "sellerId");

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public GenerateInventoryReportService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            InventoryRepositoryPort inventoryRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Report generateInventoryReport(User actor, Map<String, Object> filters) {
        authorize(actor, SystemRole.SELLER, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        Map<String, Object> clean = ReportSupport.validateFilters(filters, ALLOWED);
        Long sellerId = ReportSupport.sellerScope(actor, clean, sellerRepositoryPort);
        Long warehouseId = ReportSupport.longFilter(clean, "warehouseId");
        Long productId = ReportSupport.longFilter(clean, "productId");

        int records = 0;
        int units = 0;
        int low = 0;
        int out = 0;
        int damaged = 0;
        List<Map<String, Object>> detail = new ArrayList<>();
        Seller scope = null;
        if (sellerId != null) {
            scope = new Seller();
            scope.setId(sellerId);
        }
        for (Inventory inventory : inventoryRepositoryPort.findAll()) {
            if (scope != null && !inventory.getProduct().belongsTo(scope)) {
                continue;
            }
            if (warehouseId != null && !warehouseId.equals(inventory.getWarehouse().getId())) {
                continue;
            }
            if (productId != null && !productId.equals(inventory.getProduct().getId())) {
                continue;
            }
            records++;
            units += inventory.getStock();
            if (scope != null) {
                Map<String, Integer> movements = new TreeMap<>();
                if (inventory.getMovements() != null) {
                    for (InventoryMovement movement : inventory.getMovements()) {
                        movements.merge(movement.getType().getCode(), movement.getQuantity(), Integer::sum);
                    }
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("inventoryId", inventory.getId());
                row.put("productId", inventory.getProduct().getId());
                row.put("productName", inventory.getProduct().getName());
                row.put("warehouseId", inventory.getWarehouse().getId());
                row.put("stock", inventory.getStock());
                row.put("status", inventory.getStatus().getCode());
                row.put("movementUnitsByType", movements);
                detail.add(row);
            }
            if (InventoryStatus.LOW_STOCK.equals(inventory.getStatus())) {
                low++;
            } else if (InventoryStatus.OUT_OF_STOCK.equals(inventory.getStatus())) {
                out++;
            } else if (InventoryStatus.DAMAGED.equals(inventory.getStatus())) {
                damaged++;
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        if (sellerId != null) {
            data.put("sellerId", sellerId);
        }
        data.put("inventoryRecords", records);
        data.put("totalUnits", units);
        data.put("lowStock", low);
        data.put("outOfStock", out);
        data.put("damaged", damaged);
        if (sellerId != null) {
            data.put("inventories", detail);
        }
        Report report = ReportSupport.build(ReportType.INVENTORY, clean, data);
        ReportSupport.trace(traceability, actor, report);
        return report;
    }
}
