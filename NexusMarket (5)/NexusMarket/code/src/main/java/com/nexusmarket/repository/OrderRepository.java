package com.nexusmarket.repository;

import java.util.List;

import com.nexusmarket.model.Buyer;
import com.nexusmarket.model.Order;

/** Persistence for {@link Order} entities. */
public class OrderRepository extends InMemoryRepository<Order, String> {

    public OrderRepository() {
        super(Order::getId);
    }

    public List<Order> findByBuyer(Buyer buyer) {
        return findAll().stream().filter(order -> order.getBuyer().equals(buyer)).toList();
    }
}
