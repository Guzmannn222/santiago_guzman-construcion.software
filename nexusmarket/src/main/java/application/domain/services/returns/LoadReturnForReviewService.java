package application.domain.services.returns;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal helper: reloads a return; a SELLER may only review returns of orders containing their products. */
@Service
public class LoadReturnForReviewService {

    private final ReturnRepositoryPort returnRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public LoadReturnForReviewService(ReturnRepositoryPort returnRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        this.returnRepositoryPort = returnRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    public Return execute(User actor, Return reference) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Return");
        }
        Return productReturn = returnRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Return", reference.getId()));
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = sellerRepositoryPort.findByUserId(actor.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller", actor.getId()));
            if (!productReturn.involvesProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only review returns of their own products.");
            }
        }
        return productReturn;
    }
}
