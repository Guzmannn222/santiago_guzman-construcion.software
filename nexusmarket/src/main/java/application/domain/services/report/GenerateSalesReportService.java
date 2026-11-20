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
import application.domain.ports.in.GenerateSalesReportUseCase;
import java.math.BigDecimal;

/**
 * Sales summary over paid-or-later orders. Filters: status (order status code), buyerId, sellerId.
 * With a seller scope only that seller's order lines are counted, broken down by product.
 */
@Service
public class GenerateSalesReportService extends AuthorizedService implements GenerateSalesReportUseCase {

    private static final Set<String> ALLOWED = Set.of("status", "buyerId", "sellerId");

    private final OrderRepositoryPort orderRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public GenerateSalesReportService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            OrderRepositoryPort orderRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.orderRepositoryPort = orderRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Report generateSalesReport(User actor, Map<String, Object> filters) {
        authorize(actor, SystemRole.SELLER, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        Map<String, Object> clean = ReportSupport.validateFilters(filters, ALLOWED);
        Long sellerId = ReportSupport.sellerScope(actor, clean, sellerRepositoryPort);
        OrderStatus status = ReportSupport.catalogFilter(clean, "status", OrderStatus::fromCode);
        Long buyerId = ReportSupport.longFilter(clean, "buyerId");

        BigDecimal total = BigDecimal.ZERO;
        int orders = 0;
        int units = 0;
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        Map<Long, Map<String, Object>> byProduct = new TreeMap<>();
        Seller scope = null;
        if (sellerId != null) {
            scope = new Seller();
            scope.setId(sellerId);
        }
        for (Order order : orderRepositoryPort.findAll()) {
            if (!order.isPaidOrLater() || OrderStatus.CANCELLED.equals(order.getStatus())) {
                continue;
            }
            if (status != null && !status.equals(order.getStatus())) {
                continue;
            }
            if (buyerId != null && (order.getBuyer() == null || !buyerId.equals(order.getBuyer().getId()))) {
                continue;
            }
            if (scope != null && !order.containsProductOf(scope)) {
                continue;
            }
            orders++;
            if (scope == null) {
                total = total.add(order.getTotal().getAmount());
            }
            for (OrderItem item : order.getItems()) {
                if (scope != null) {
                    if (!item.getProduct().belongsTo(scope)) {
                        continue;
                    }
                    BigDecimal line = item.getUnitPrice().getAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
                    total = total.add(line);
                    Map<String, Object> row = byProduct.computeIfAbsent(item.getProduct().getId(), id -> {
                        Map<String, Object> created = new LinkedHashMap<>();
                        created.put("productId", id);
                        created.put("productName", item.getProduct().getName());
                        created.put("productType", item.getProduct().getType().getCode());
                        created.put("units", 0);
                        created.put("amount", BigDecimal.ZERO);
                        return created;
                    });
                    row.put("units", (Integer) row.get("units") + item.getQuantity());
                    row.put("amount", ((BigDecimal) row.get("amount")).add(line));
                }
                units += item.getQuantity();
            }
            byStatus.merge(order.getStatus().getCode(), 1, Integer::sum);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        if (sellerId != null) {
            data.put("sellerId", sellerId);
        }
        data.put("orders", orders);
        data.put("unitsSold", units);
        data.put("totalSales", total.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        data.put("ordersByStatus", byStatus);
        if (sellerId != null) {
            List<Map<String, Object>> products = new ArrayList<>();
            for (Map<String, Object> row : byProduct.values()) {
                row.put("amount", ReportSupport.money((BigDecimal) row.get("amount")));
                products.add(row);
            }
            data.put("byProduct", products);
        }
        Report report = ReportSupport.build(ReportType.SALES, clean, data);
        ReportSupport.trace(traceability, actor, report);
        return report;
    }
}
