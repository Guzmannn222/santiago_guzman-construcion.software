package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 check whether a quantity can be reserved (role BUYER). */
public interface CheckStockAvailabilityUseCase {

    boolean checkAvailability(User actor, Inventory inventory, int quantity);
}
