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
import application.domain.ports.in.GenerateReturnsReportUseCase;
import java.math.BigDecimal;

/** Returns and refunds summary. Filters: status (return status code). */
@Service
public class GenerateReturnsReportService extends AuthorizedService implements GenerateReturnsReportUseCase {

    private static final Set<String> ALLOWED = Set.of("status", "sellerId");

    private final ReturnRepositoryPort returnRepositoryPort;
    private final RefundRepositoryPort refundRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public GenerateReturnsReportService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ReturnRepositoryPort returnRepositoryPort, RefundRepositoryPort refundRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.returnRepositoryPort = returnRepositoryPort;
        this.refundRepositoryPort = refundRepositoryPort;
    }

    @Override
    public Report generateReturnsReport(User actor, Map<String, Object> filters) {
        authorize(actor, SystemRole.SELLER, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        Map<String, Object> clean = ReportSupport.validateFilters(filters, ALLOWED);
        Long sellerId = ReportSupport.sellerScope(actor, clean, sellerRepositoryPort);
        ReturnStatus status = ReportSupport.catalogFilter(clean, "status", ReturnStatus::fromCode);
        if (sellerId != null) {
            return sellerReport(actor, clean, sellerId, status);
        }

        int returns = 0;
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        for (Return productReturn : returnRepositoryPort.findAll()) {
            if (status != null && !status.equals(productReturn.getStatus())) {
                continue;
            }
            returns++;
            byStatus.merge(productReturn.getStatus().getCode(), 1, Integer::sum);
        }
        int refunds = 0;
        BigDecimal refunded = BigDecimal.ZERO;
        for (Refund refund : refundRepositoryPort.findAll()) {
            if (RefundStatus.COMPLETED.equals(refund.getStatus())) {
                refunds++;
                refunded = refunded.add(refund.getAmount().getAmount());
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("returns", returns);
        data.put("returnsByStatus", byStatus);
        data.put("completedRefunds", refunds);
        data.put("totalRefunded", refunded.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        Report report = ReportSupport.build(ReportType.RETURNS_AND_REFUNDS, clean, data);
        ReportSupport.trace(traceability, actor, report);
        return report;
    }

    /** Returns that include the seller's products, with only the seller's lines and the refund attached to each. */
    private Report sellerReport(User actor, Map<String, Object> clean, Long sellerId, ReturnStatus status) {
        Seller scope = new Seller();
        scope.setId(sellerId);
        int returns = 0;
        int returnedUnits = 0;
        BigDecimal returnedValue = BigDecimal.ZERO;
        BigDecimal refunded = BigDecimal.ZERO;
        BigDecimal pending = BigDecimal.ZERO;
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        Map<String, Integer> refundsByStatus = new LinkedHashMap<>();
        List<Map<String, Object>> detail = new ArrayList<>();
        List<Return> all = new ArrayList<>(returnRepositoryPort.findAll());
        all.sort(Comparator.comparing(Return::getId));
        for (Return productReturn : all) {
            if (!productReturn.involvesProductOf(scope)) {
                continue;
            }
            if (status != null && !status.equals(productReturn.getStatus())) {
                continue;
            }
            returns++;
            byStatus.merge(productReturn.getStatus().getCode(), 1, Integer::sum);
            List<Map<String, Object>> lines = new ArrayList<>();
            for (ReturnItem line : productReturn.returnedLines()) {
                if (!line.getProduct().belongsTo(scope)) {
                    continue;
                }
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("productId", line.getProduct().getId());
                row.put("productName", line.getProduct().getName());
                row.put("quantity", line.getQuantity());
                lines.add(row);
                returnedUnits += line.getQuantity();
                returnedValue = returnedValue.add(line.getProduct().getPrice().getAmount()
                        .multiply(BigDecimal.valueOf(line.getQuantity())));
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("returnId", productReturn.getId());
            entry.put("orderId", productReturn.getOrder().getId());
            entry.put("status", productReturn.getStatus().getCode());
            entry.put("reason", productReturn.getReason());
            entry.put("lines", lines);
            Optional<Refund> refund = refundRepositoryPort.findByReturnId(productReturn.getId());
            if (refund.isPresent()) {
                Map<String, Object> refundRow = new LinkedHashMap<>();
                refundRow.put("refundId", refund.get().getId());
                refundRow.put("status", refund.get().getStatus().getCode());
                refundRow.put("amount", ReportSupport.money(refund.get().getAmount().getAmount()));
                entry.put("refund", refundRow);
                refundsByStatus.merge(refund.get().getStatus().getCode(), 1, Integer::sum);
                if (RefundStatus.COMPLETED.equals(refund.get().getStatus())) {
                    refunded = refunded.add(refund.get().getAmount().getAmount());
                } else if (RefundStatus.PENDING.equals(refund.get().getStatus())
                        || RefundStatus.PROCESSING.equals(refund.get().getStatus())) {
                    pending = pending.add(refund.get().getAmount().getAmount());
                }
            }
            detail.add(entry);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sellerId", sellerId);
        data.put("returns", returns);
        data.put("returnsByStatus", byStatus);
        data.put("returnedUnits", returnedUnits);
        data.put("returnedValue", ReportSupport.money(returnedValue));
        data.put("refundsByStatus", refundsByStatus);
        data.put("totalRefunded", ReportSupport.money(refunded));
        data.put("pendingRefundAmount", ReportSupport.money(pending));
        data.put("returnDetails", detail);
        Report report = ReportSupport.build(ReportType.RETURNS_AND_REFUNDS, clean, data);
        ReportSupport.trace(traceability, actor, report);
        return report;
    }
}
