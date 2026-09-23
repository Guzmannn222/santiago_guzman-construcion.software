package com.nexusmarket.service;

import java.util.Map;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Report;
import com.nexusmarket.model.User;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for consolidated administrative reports (SDD, Domain 11, CU-15). */
public class ReportService {

    /** Use case: UC-50 Generate sales report. Allowed role: SUPERVISOR. */
    public Report generateSalesReport(User actor, Map<String, Object> filters) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        return buildReport("SALES", filters);
    }

    /** Use case: UC-51 Generate inventory report. Allowed role: SUPERVISOR. */
    public Report generateInventoryReport(User actor, Map<String, Object> filters) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        return buildReport("INVENTORY", filters);
    }

    /** Use case: UC-52 Generate returns and refunds report. Allowed role: SUPERVISOR. */
    public Report generateReturnsReport(User actor, Map<String, Object> filters) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        return buildReport("RETURNS_AND_REFUNDS", filters);
    }

    /** Use case: UC-53 Generate seller performance report. Allowed role: ADMINISTRATOR. */
    public Report generateSellerPerformanceReport(User actor, Map<String, Object> filters) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        return buildReport("SELLER_PERFORMANCE", filters);
    }

    private Report buildReport(String type, Map<String, Object> filters) {
        Report report = new Report(IdGenerator.next("RPT"), type);
        report.filter(filters);
        report.generate();
        return report;
    }
}
