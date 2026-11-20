package application.domain.services.refund;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Refund;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal helper: reloads a refund; a SELLER may only manage refunds of orders with their products. */
@Service
public class LoadRefundForReviewService {

    private final RefundRepositoryPort refundRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public LoadRefundForReviewService(RefundRepositoryPort refundRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        this.refundRepositoryPort = refundRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    public Refund execute(User actor, Refund reference) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Refund");
        }
        Refund refund = refundRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Refund", reference.getId()));
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = sellerRepositoryPort.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", actor.getId()));
            if (!refund.getProductReturn().involvesProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only manage refunds of their own products.");
            }
        }
        return refund;
    }
}
