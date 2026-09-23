package com.nexusmarket.repository;

import com.nexusmarket.model.Cart;

/** Persistence for {@link Cart} entities. */
public class CartRepository extends InMemoryRepository<Cart, String> {

    public CartRepository() {
        super(Cart::getId);
    }
}
