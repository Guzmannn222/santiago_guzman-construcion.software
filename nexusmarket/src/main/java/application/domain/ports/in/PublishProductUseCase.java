package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-06 publish an own product (role SELLER). */
public interface PublishProductUseCase {

    void publish(User actor, Product product);
}
