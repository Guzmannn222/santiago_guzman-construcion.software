package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Product aggregates. */
public interface ProductRepositoryPort {

    Product save(Product product);
    void update(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByStatus(ProductStatus status);
    List<Product> findBySellerId(Long sellerId);
}
