package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Refund;
import com.nexusmarket.model.Return;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.RefundRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for refunds originating from an approved {@link Return} (SDD, CU-14). */
public class RefundService {

    private final RefundRepository refundRepository;

    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    /** Use case: UC-46 Issue refund for an approved return. Allowed role: SUPERVISOR. */
    public Refund issueRefund(User actor, Return productReturn, double amount) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        Refund refund = new Refund(IdGenerator.next("RFD"), productReturn, amount);
        return refundRepository.save(refund);
    }

    /** Use case: UC-47 Process refund payment. Allowed role: SUPERVISOR. */
    public void process(User actor, Refund refund) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        refund.process();
        refundRepository.save(refund);
    }

    /** Use case: UC-48 Complete refund. Allowed role: SUPERVISOR. */
    public void complete(User actor, Refund refund) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        refund.complete();
        refundRepository.save(refund);
    }

    /** Use case: UC-49 Reject refund. Allowed role: SUPERVISOR. */
    public void reject(User actor, Refund refund) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        refund.reject();
        refundRepository.save(refund);
    }
}
