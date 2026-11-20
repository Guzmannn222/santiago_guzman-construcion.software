package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.User;
import java.util.List;

/** Input Port: CU-02 consult buyers. A buyer only sees its own profile. */
public interface ConsultBuyerUseCase {

    Buyer consultBuyer(User actor, Buyer buyer);
    Buyer consultMyBuyer(User actor);
    List<Buyer> consultAllBuyers(User actor);
}
