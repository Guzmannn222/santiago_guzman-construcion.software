package com.nexusmarket.model;

import com.nexusmarket.enums.ReturnStatus;

/** Post-sale request associated with an {@link Order} (SDD, Domain 10). */
public class Return {

    private final String id;
    private final Order order;
    private final String reason;
    private ReturnStatus status;

    public Return(String id, Order order, String reason) {
        this.id = id;
        this.order = order;
        this.reason = reason;
        this.status = ReturnStatus.REQUESTED;
    }

    public void approve() {
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        this.status = ReturnStatus.REJECTED;
    }

    public void complete() {
        this.status = ReturnStatus.COMPLETED;
    }

    public String getId() { return id; }
    public Order getOrder() { return order; }
    public String getReason() { return reason; }
    public ReturnStatus getStatus() { return status; }
}
