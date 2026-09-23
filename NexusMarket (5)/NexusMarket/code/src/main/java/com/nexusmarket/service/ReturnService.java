package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.Return;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.ReturnRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for post-sale returns (SDD, Domain 10, CU-13). */
public class ReturnService {

    private final ReturnRepository returnRepository;

    public ReturnService(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }

    /** Use case: UC-42 Request a return for a delivered order. Allowed role: BUYER. */
    public Return requestReturn(User actor, Order order, String reason) {
        AccessControl.requireRole(actor, Role.BUYER);
        Return productReturn = new Return(IdGenerator.next("RET"), order, reason);
        return returnRepository.save(productReturn);
    }

    /** Use case: UC-43 Approve return request. Allowed role: SUPERVISOR. */
    public void approve(User actor, Return productReturn) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        productReturn.approve();
        returnRepository.save(productReturn);
    }

    /** Use case: UC-44 Reject return request. Allowed role: SUPERVISOR. */
    public void reject(User actor, Return productReturn) {
        AccessControl.requireRole(actor, Role.SUPERVISOR);
        productReturn.reject();
        returnRepository.save(productReturn);
    }

    /** Use case: UC-45 Complete return (goods physically received back). Allowed role: LOGISTICS_OPERATOR. */
    public void complete(User actor, Return productReturn) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        productReturn.complete();
        returnRepository.save(productReturn);
    }
}
