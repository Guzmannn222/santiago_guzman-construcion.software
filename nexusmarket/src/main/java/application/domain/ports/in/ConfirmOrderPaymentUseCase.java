package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.models.User;

/** Input Port: CU-09 confirm the payment of an own order (role BUYER). */
public interface ConfirmOrderPaymentUseCase {

    void confirmPayment(User actor, Order order);
}
