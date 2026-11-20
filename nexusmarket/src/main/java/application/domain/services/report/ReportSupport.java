package application.domain.services.report;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidReportException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Report;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.valueobjects.SystemRole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import application.domain.services.operation.RegisterOperationAndAuditService;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReportType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Shared filter validation and traceability for the report services. */
final class ReportSupport {

    private ReportSupport() {
    }

    static Map<String, Object> validateFilters(Map<String, Object> filters, Set<String> allowedKeys) {
        Map<String, Object> clean = new LinkedHashMap<>();
        if (filters == null) {
            return clean;
        }
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            if (!allowedKeys.contains(entry.getKey())) {
                throw new InvalidReportException("Unsupported report filter '" + entry.getKey()
                        + "'. Allowed filters: " + allowedKeys + ".");
            }
            if (entry.getValue() != null) {
                clean.put(entry.getKey(), entry.getValue());
            }
        }
        return clean;
    }

    static <T> T catalogFilter(Map<String, Object> filters, String key, java.util.function.Function<String, T> parser) {
        Object value = filters.get(key);
        if (value == null) {
            return null;
        }
        try {
            return parser.apply(String.valueOf(value).trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidReportException("Filter '" + key + "' has an unknown value '" + value + "'.");
        }
    }

    static Long longFilter(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null) {
            return null;
        }
        try {
            return value instanceof Number ? ((Number) value).longValue() : Long.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            throw new InvalidReportException("Filter '" + key + "' must be a number.");
        }
    }

    /**
     * Seller scope of a report. A SELLER always gets their own scope (asking for another seller is
     * rejected); SUPERVISOR/ADMINISTRATOR may ask for one seller with the sellerId filter, or null for the global report.
     */
    static Long sellerScope(User actor, Map<String, Object> clean, SellerRepositoryPort sellerRepositoryPort) {
        Long requested = longFilter(clean, "sellerId");
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller own = sellerRepositoryPort.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", actor.getId()));
            if (requested != null && !requested.equals(own.getId())) {
                throw new UnauthorizedOperationException("A seller can only generate reports about their own activity.");
            }
            clean.put("sellerId", own.getId());
            return own.getId();
        }
        if (requested != null) {
            sellerRepositoryPort.findById(requested).orElseThrow(() -> new EntityNotFoundException("Seller", requested));
        }
        return requested;
    }

    static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    static void trace(RegisterOperationAndAuditService traceability, User actor, Report report) {
        traceability.execute(actor, OperationType.REPORT_GENERATION,
                new EntityReference(EntityType.REPORT, report.getType().getCode() + "-" + System.currentTimeMillis()),
                Map.of("reportType", report.getType().getCode(), "filters", String.valueOf(report.getFilters())));
    }

    static Report build(ReportType type, Map<String, Object> filters, Map<String, Object> data) {
        Report report = new Report(type);
        report.filter(filters);
        report.generate(data);
        return report;
    }
}
