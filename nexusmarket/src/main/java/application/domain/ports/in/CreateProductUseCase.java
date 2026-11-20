package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;

/** Input Port: CU-05 create a product (role SELLER). {@code seller} may be null or a stub: the service resolves the actor own seller profile and rejects any other seller. */
public interface CreateProductUseCase {

    Product createProduct(User actor, Seller seller, String name, String description, Money price, ProductType type);
}
