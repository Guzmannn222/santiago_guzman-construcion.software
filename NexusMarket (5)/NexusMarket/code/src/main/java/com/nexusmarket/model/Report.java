package com.nexusmarket.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Consolidated administrative query (SDD, Domain 11). */
public class Report {

    private final String id;
    private final String type;
    private Map<String, Object> filters = new HashMap<>();
    private LocalDateTime generationDate;

    public Report(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public void generate() {
        this.generationDate = LocalDateTime.now();
    }

    public void filter(Map<String, Object> filters) {
        this.filters = filters;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public Map<String, Object> getFilters() { return filters; }
    public LocalDateTime getGenerationDate() { return generationDate; }
}
