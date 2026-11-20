package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;

/** Input Port: CU-03 reactivate a seller (role ADMINISTRATOR). */
public interface ReactivateSellerUseCase {

    void reactivate(User actor, Seller seller);
}
