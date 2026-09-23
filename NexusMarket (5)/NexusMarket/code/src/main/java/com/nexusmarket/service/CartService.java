package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Buyer;
import com.nexusmarket.model.Cart;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.CartRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for the shopping cart (SDD, Domain 7, CU-08). */
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /** Use case: UC-28 Create cart. Allowed role: BUYER. */
    public Cart createCart(User actor, Buyer buyer) {
        AccessControl.requireRole(actor, Role.BUYER);
        Cart cart = new Cart(IdGenerator.next("CRT"), buyer);
        return cartRepository.save(cart);
    }

    /** Use case: UC-29 Add product to cart. Allowed role: BUYER. */
    public void addProduct(User actor, Cart cart, Product product, int quantity) {
        AccessControl.requireRole(actor, Role.BUYER);
        cart.addProduct(product, quantity);
        cartRepository.save(cart);
    }

    /** Use case: UC-30 Remove product from cart. Allowed role: BUYER. */
    public void removeProduct(User actor, Cart cart, Product product) {
        AccessControl.requireRole(actor, Role.BUYER);
        cart.removeProduct(product);
        cartRepository.save(cart);
    }

    /** Use case: UC-31 Update cart item quantity. Allowed role: BUYER. */
    public void updateQuantity(User actor, Cart cart, Product product, int quantity) {
        AccessControl.requireRole(actor, Role.BUYER);
        cart.updateQuantity(product, quantity);
        cartRepository.save(cart);
    }
}
