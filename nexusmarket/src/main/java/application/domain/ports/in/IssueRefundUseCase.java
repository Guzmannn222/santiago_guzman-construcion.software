package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.valueobjects.Money;

/** Input Port: CU-14 issue the refund of a refundable return (roles ADMINISTRATOR, SELLER). */
public interface IssueRefundUseCase {

    Refund issueRefund(User actor, Return productReturn, Money amount);
}
