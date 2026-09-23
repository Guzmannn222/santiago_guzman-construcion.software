package com.nexusmarket.model;

import com.nexusmarket.enums.RefundStatus;

/** Money reimbursement originated by an approved {@link Return} (SDD, Domain 10). */
public class Refund {

    private final String id;
    private final Return productReturn;
    private final double amount;
    private RefundStatus status;

    public Refund(String id, Return productReturn, double amount) {
        this.id = id;
        this.productReturn = productReturn;
        this.amount = amount;
        this.status = RefundStatus.PENDING;
    }

    public void process() {
        this.status = RefundStatus.PROCESSING;
    }

    public void complete() {
        this.status = RefundStatus.COMPLETED;
    }

    public void reject() {
        this.status = RefundStatus.REJECTED;
    }

    public String getId() { return id; }
    public Return getReturn() { return productReturn; }
    public double getAmount() { return amount; }
    public RefundStatus getStatus() { return status; }
}
