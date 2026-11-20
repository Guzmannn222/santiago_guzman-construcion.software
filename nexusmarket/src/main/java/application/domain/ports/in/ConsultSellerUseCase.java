package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-03 consult sellers. A seller only sees its own profile. */
public interface ConsultSellerUseCase {

    Seller consultSeller(User actor, Seller seller);
    Seller consultMySeller(User actor);
    List<Seller> consultAllSellers(User actor);
}
