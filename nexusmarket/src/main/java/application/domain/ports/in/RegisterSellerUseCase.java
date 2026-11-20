package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;

/** Input Port: CU-03 onboard a seller together with its first warehouse (role ADMINISTRATOR). */
public interface RegisterSellerUseCase {

    Seller registerSeller(User actor, User sellerUser, String businessInfo, String firstWarehouseLocation);
}
