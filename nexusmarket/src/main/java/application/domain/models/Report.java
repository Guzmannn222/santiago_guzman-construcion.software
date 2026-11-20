package application.domain.models;

import application.domain.exceptions.InvalidReportException;
import application.domain.valueobjects.ReportType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Consolidated administrative query, generated on demand (not persisted). */
@Getter
@Setter
@NoArgsConstructor
public class Report {

    private ReportType type;
    private Map<String, Object> filters = new HashMap<>();
    /** Aggregated figures computed when the report is generated. */
    private Map<String, Object> data = new HashMap<>();
    private LocalDateTime generationDate;

    public Report(ReportType type) {
        if (type == null) {
            throw new InvalidReportException("Report type must be provided.");
        }
        this.type = type;
    }

    public void filter(Map<String, Object> filters) {
        this.filters = filters == null ? new HashMap<>() : new HashMap<>(filters);
    }

    public void generate(Map<String, Object> data) {
        this.data = data == null ? new HashMap<>() : new HashMap<>(data);
        this.generationDate = LocalDateTime.now();
    }
}
