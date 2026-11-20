package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;

/** Input Port: CU-03 deactivate a seller (role ADMINISTRATOR). */
public interface DeactivateSellerUseCase {

    void deactivate(User actor, Seller seller);
}
