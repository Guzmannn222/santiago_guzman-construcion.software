package application.domain.services.seller;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.RegisterSellerUseCase;
import application.domain.services.user.CreateUserAccountService;

/** Administrator registers a seller (new SELLER account or existing user) and its first warehouse. */
@Service
public class RegisterSellerService extends AuthorizedService implements RegisterSellerUseCase {

    private final CreateUserAccountService createUserAccountService;
    private final UserRepositoryPort userRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public RegisterSellerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            CreateUserAccountService createUserAccountService, UserRepositoryPort userRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort) {
        super(s, r, t);
        this.createUserAccountService = createUserAccountService;
        this.userRepositoryPort = userRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    @Override
    public Seller registerSeller(User actor, User sellerUser, String businessInfo, String firstWarehouseLocation) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(sellerUser, "Seller user");
        Seller draft = new Seller(sellerUser, businessInfo);
        Warehouse firstWarehouse = new Warehouse(firstWarehouseLocation, WarehouseType.SELLER, draft);
        User user;
        if (sellerUser.getId() == null) {
            user = createUserAccountService.execute(sellerUser, SystemRole.SELLER);
        } else {
            user = found(userRepositoryPort.findById(sellerUser.getId()), "User", sellerUser.getId());
            if (!SystemRole.SELLER.equals(user.getRole())) {
                throw new InvalidSellerException("The user must have the SELLER role to be registered as a seller.");
            }
            if (sellerRepositoryPort.findByUserId(user.getId()).isPresent()) {
                throw new DuplicateResourceException("The user is already registered as a seller.");
            }
        }
        draft.setUser(user);
        Seller seller = sellerRepositoryPort.save(draft);
        firstWarehouse.setOwner(seller);
        Warehouse warehouse = warehouseRepositoryPort.save(firstWarehouse);
        seller.addWarehouse(warehouse);
        register(actor, OperationType.SELLER_REGISTRATION, EntityType.SELLER, seller.getId(), "userId", user.getId());
        register(actor, OperationType.WAREHOUSE_REGISTRATION, EntityType.WAREHOUSE, warehouse.getId(),
                "location", warehouse.getLocation());
        return seller;
    }
}
