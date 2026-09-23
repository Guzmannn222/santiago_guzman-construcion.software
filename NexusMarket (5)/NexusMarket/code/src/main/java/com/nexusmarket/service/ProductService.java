package com.nexusmarket.service;

import java.util.List;

import com.nexusmarket.enums.ProductStatus;
import com.nexusmarket.enums.ProductType;
import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.Seller;
import com.nexusmarket.model.User;
import com.nexusmarket.model.Variant;
import com.nexusmarket.repository.ProductRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for catalog management (SDD, Domain 5, CU-05 / CU-06). */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** Use case: UC-14 Create product. Allowed role: SELLER. */
    public Product createProduct(User actor, Seller seller, String name, String description, double price,
            ProductType type) {
        AccessControl.requireRole(actor, Role.SELLER);
        Product product = new Product(IdGenerator.next("PRD"), seller, name, description, price, type);
        return productRepository.save(product);
    }

    /** Use case: UC-15 Add product variant. Allowed role: SELLER. */
    public void addVariant(User actor, Product product, String name, String value) {
        AccessControl.requireRole(actor, Role.SELLER);
        product.addVariant(new Variant(IdGenerator.next("VAR"), name, value));
        productRepository.save(product);
    }

    /** Use case: UC-16 Publish product. Allowed role: SELLER. */
    public void publish(User actor, Product product) {
        AccessControl.requireRole(actor, Role.SELLER);
        product.publish();
        productRepository.save(product);
    }

    /** Use case: UC-17 Suspend product. Allowed role: SELLER. */
    public void suspend(User actor, Product product) {
        AccessControl.requireRole(actor, Role.SELLER);
        product.suspend();
        productRepository.save(product);
    }

    /** Use case: UC-18 Discontinue product. Allowed role: SELLER. */
    public void discontinue(User actor, Product product) {
        AccessControl.requireRole(actor, Role.SELLER);
        product.discontinue();
        productRepository.save(product);
    }

    /** Use case: UC-19 Update product price. Allowed role: SELLER. */
    public void updatePrice(User actor, Product product, double newPrice) {
        AccessControl.requireRole(actor, Role.SELLER);
        product.updatePrice(newPrice);
        productRepository.save(product);
    }

    /** Use case: UC-20 Browse published catalog. Allowed role: BUYER. */
    public List<Product> findPublished(User actor) {
        AccessControl.requireRole(actor, Role.BUYER);
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }
}
