package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-06 discontinue an own product (role SELLER). */
public interface DiscontinueProductUseCase {

    void discontinue(User actor, Product product);
}
