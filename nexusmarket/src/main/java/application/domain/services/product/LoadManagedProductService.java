package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal helper: reloads a product and verifies the actor (a SELLER) owns it. */
@Service
public class LoadManagedProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public LoadManagedProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product execute(User actor, Product reference, boolean administratorAllowed) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Product");
        }
        Product product = productRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product", reference.getId()));
        boolean admin = administratorAllowed && SystemRole.ADMINISTRATOR.equals(actor.getRole());
        if (!admin && (product.getSeller() == null || !product.getSeller().isOwnedBy(actor))) {
            throw new UnauthorizedOperationException("A seller can only manage their own products.");
        }
        return product;
    }
}
