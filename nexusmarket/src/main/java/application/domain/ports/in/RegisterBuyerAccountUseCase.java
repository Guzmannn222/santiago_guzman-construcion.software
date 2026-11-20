package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.User;

/** Input Port: CU-02 public self-registration: creates a BUYER user, its buyer profile and its cart. */
public interface RegisterBuyerAccountUseCase {

    Buyer registerBuyerAccount(User newUser, String primaryAddress);
}
