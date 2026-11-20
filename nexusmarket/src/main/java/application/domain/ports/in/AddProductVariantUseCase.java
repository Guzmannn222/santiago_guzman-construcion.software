package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;

/** Input Port: CU-05 add a variant to an own product (role SELLER). */
public interface AddProductVariantUseCase {

    void addVariant(User actor, Product product, String name, String value);
}
