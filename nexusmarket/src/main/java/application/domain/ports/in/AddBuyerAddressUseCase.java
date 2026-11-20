package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.User;

/** Input Port: CU-02 add a delivery address to the actor own buyer profile. */
public interface AddBuyerAddressUseCase {

    void addAddress(User actor, Buyer buyer, String address);
}
