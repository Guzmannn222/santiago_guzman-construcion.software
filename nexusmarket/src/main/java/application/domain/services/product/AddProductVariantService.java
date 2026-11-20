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
import application.domain.ports.in.AddProductVariantUseCase;

@Service
public class AddProductVariantService extends AuthorizedService implements AddProductVariantUseCase {

    private final LoadManagedProductService loadManagedProductService;
    private final ProductRepositoryPort productRepositoryPort;

    public AddProductVariantService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedProductService loadManagedProductService, ProductRepositoryPort productRepositoryPort) {
        super(s, r, t);
        this.loadManagedProductService = loadManagedProductService;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void addVariant(User actor, Product product, String name, String value) {
        authorize(actor, SystemRole.SELLER);
        Product persisted = loadManagedProductService.execute(actor, product, false);
        persisted.addVariant(new Variant(name, value));
        productRepositoryPort.update(persisted);
        register(actor, OperationType.PRODUCT_VARIANT_ADDED, EntityType.PRODUCT, persisted.getId(),
                "variant", name, "value", value);
    }
}
