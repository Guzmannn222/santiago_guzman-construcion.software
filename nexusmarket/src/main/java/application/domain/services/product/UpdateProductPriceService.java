package application.domain.services.product;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.UpdateProductPriceUseCase;

/** The price changes only for future carts/orders; existing order lines keep their unit price. */
@Service
public class UpdateProductPriceService extends AuthorizedService implements UpdateProductPriceUseCase {

    private final LoadManagedProductService loadManagedProductService;
    private final ProductRepositoryPort productRepositoryPort;

    public UpdateProductPriceService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedProductService loadManagedProductService, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadManagedProductService = loadManagedProductService;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void updatePrice(User actor, Product product, Money newPrice) {
        authorize(actor, SystemRole.SELLER);
        Product persisted = loadManagedProductService.execute(actor, product, false);
        Money previous = persisted.getPrice();
        persisted.updatePrice(newPrice);
        productRepositoryPort.update(persisted);
        register(actor, OperationType.PRODUCT_PRICE_UPDATE, EntityType.PRODUCT, persisted.getId(),
                "previousPrice", String.valueOf(previous), "newPrice", String.valueOf(newPrice));
    }
}
