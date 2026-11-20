package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-05/CU-06 consult products. Published products are visible to any authenticated user. */
public interface ConsultProductUseCase {

    Product consultProduct(User actor, Product product);
    List<Product> consultAllProducts(User actor);
    List<Product> consultMyProducts(User actor);
    List<Product> consultPublishedProducts(User actor);
}
