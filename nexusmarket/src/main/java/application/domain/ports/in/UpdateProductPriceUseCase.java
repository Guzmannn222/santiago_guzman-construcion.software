package application.domain.ports.in;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.Money;

/** Input Port: CU-05 update the price of an own product (role SELLER). */
public interface UpdateProductPriceUseCase {

    void updatePrice(User actor, Product product, Money newPrice);
}
