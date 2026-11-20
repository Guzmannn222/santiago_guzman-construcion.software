package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.User;

/** Input Port: CU-02 create the buyer profile (and cart) of the authenticated BUYER user. */
public interface RegisterBuyerUseCase {

    Buyer registerBuyer(User actor, String primaryAddress);
}
