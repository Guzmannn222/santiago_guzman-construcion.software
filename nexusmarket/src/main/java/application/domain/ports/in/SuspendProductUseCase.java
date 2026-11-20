package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-06 suspend an own product (role SELLER). */
public interface SuspendProductUseCase {

    void suspend(User actor, Product product);
}
