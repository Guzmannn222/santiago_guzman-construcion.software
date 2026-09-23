package com.nexusmarket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Buyer;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.User;
import com.nexusmarket.util.InvalidOrderStateException;

/**
 * Unit test for the critical business rule: "a finished order can never be modified"
 * (SDD section 16.2).
 */
class OrderTest {

    @Test
    void shouldNotAllowChangingStatusOfAFinishedOrder() {
        User buyerUser = new User("U1", "Beatriz Buyer", "beatriz@nexusmarket.com", Role.BUYER);
        Buyer buyer = new Buyer("B1", buyerUser, "Main St 123");
        Order order = new Order("O1", buyer);

        order.finish();

        assertFalse(order.canModify());
        assertThrows(InvalidOrderStateException.class, order::confirmPayment);
    }
}
