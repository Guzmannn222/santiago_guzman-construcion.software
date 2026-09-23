package com.nexusmarket.repository;

import java.util.List;

import com.nexusmarket.enums.ProductStatus;
import com.nexusmarket.model.Product;

/** Persistence for {@link Product} entities. */
public class ProductRepository extends InMemoryRepository<Product, String> {

    public ProductRepository() {
        super(Product::getId);
    }

    public List<Product> findByStatus(ProductStatus status) {
        return findAll().stream().filter(product -> product.getStatus() == status).toList();
    }
}
