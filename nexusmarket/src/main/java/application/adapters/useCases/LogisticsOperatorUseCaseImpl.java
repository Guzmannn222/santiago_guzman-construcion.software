package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdjustStockUseCase;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.CompleteReturnUseCase;
import application.domain.ports.in.ConfirmDeliveryUseCase;
import application.domain.ports.in.ConsultInventoryUseCase;
import application.domain.ports.in.ConsultOrderUseCase;
import application.domain.ports.in.ConsultReturnUseCase;
import application.domain.ports.in.ConsultShipmentUseCase;
import application.domain.ports.in.ConsultWarehouseUseCase;
import application.domain.ports.in.CreateShipmentUseCase;
import application.domain.ports.in.DeactivateWarehouseUseCase;
import application.domain.ports.in.DispatchShipmentUseCase;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.ports.in.MarkInventoryDamagedUseCase;
import application.domain.ports.in.OpenInventoryUseCase;
import application.domain.ports.in.PrepareShipmentUseCase;
import application.domain.ports.in.ReceiveStockUseCase;
import application.domain.ports.in.RegisterWarehouseUseCase;
import application.domain.ports.in.ReturnStockUseCase;
import application.domain.ports.in.UpdateShipmentStatusUseCase;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import org.springframework.stereotype.Service;

/** Role facade: delegates every LogisticsOperator operation to its input-port use case (each one validates status, role and ownership). */
@Service
public class LogisticsOperatorUseCaseImpl implements LogisticsOperatorPort {

    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final RegisterWarehouseUseCase registerWarehouseUseCase;
    private final DeactivateWarehouseUseCase deactivateWarehouseUseCase;
    private final ConsultWarehouseUseCase consultWarehouseUseCase;
    private final OpenInventoryUseCase openInventoryUseCase;
    private final ReceiveStockUseCase receiveStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final ReturnStockUseCase returnStockUseCase;
    private final MarkInventoryDamagedUseCase markInventoryDamagedUseCase;
    private final ConsultInventoryUseCase consultInventoryUseCase;
    private final ConsultOrderUseCase consultOrderUseCase;
    private final CreateShipmentUseCase createShipmentUseCase;
    private final PrepareShipmentUseCase prepareShipmentUseCase;
    private final DispatchShipmentUseCase dispatchShipmentUseCase;
    private final UpdateShipmentStatusUseCase updateShipmentStatusUseCase;
    private final ConfirmDeliveryUseCase confirmDeliveryUseCase;
    private final ConsultShipmentUseCase consultShipmentUseCase;
    private final CompleteReturnUseCase completeReturnUseCase;
    private final ConsultReturnUseCase consultReturnUseCase;

    public LogisticsOperatorUseCaseImpl(ChangeUserPasswordUseCase changeUserPasswordUseCase,
            RegisterWarehouseUseCase registerWarehouseUseCase,
            DeactivateWarehouseUseCase deactivateWarehouseUseCase,
            ConsultWarehouseUseCase consultWarehouseUseCase,
            OpenInventoryUseCase openInventoryUseCase,
            ReceiveStockUseCase receiveStockUseCase,
            AdjustStockUseCase adjustStockUseCase,
            ReturnStockUseCase returnStockUseCase,
            MarkInventoryDamagedUseCase markInventoryDamagedUseCase,
            ConsultInventoryUseCase consultInventoryUseCase,
            ConsultOrderUseCase consultOrderUseCase,
            CreateShipmentUseCase createShipmentUseCase,
            PrepareShipmentUseCase prepareShipmentUseCase,
            DispatchShipmentUseCase dispatchShipmentUseCase,
            UpdateShipmentStatusUseCase updateShipmentStatusUseCase,
            ConfirmDeliveryUseCase confirmDeliveryUseCase,
            ConsultShipmentUseCase consultShipmentUseCase,
            CompleteReturnUseCase completeReturnUseCase,
            ConsultReturnUseCase consultReturnUseCase) {
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.registerWarehouseUseCase = registerWarehouseUseCase;
        this.deactivateWarehouseUseCase = deactivateWarehouseUseCase;
        this.consultWarehouseUseCase = consultWarehouseUseCase;
        this.openInventoryUseCase = openInventoryUseCase;
        this.receiveStockUseCase = receiveStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.returnStockUseCase = returnStockUseCase;
        this.markInventoryDamagedUseCase = markInventoryDamagedUseCase;
        this.consultInventoryUseCase = consultInventoryUseCase;
        this.consultOrderUseCase = consultOrderUseCase;
        this.createShipmentUseCase = createShipmentUseCase;
        this.prepareShipmentUseCase = prepareShipmentUseCase;
        this.dispatchShipmentUseCase = dispatchShipmentUseCase;
        this.updateShipmentStatusUseCase = updateShipmentStatusUseCase;
        this.confirmDeliveryUseCase = confirmDeliveryUseCase;
        this.consultShipmentUseCase = consultShipmentUseCase;
        this.completeReturnUseCase = completeReturnUseCase;
        this.consultReturnUseCase = consultReturnUseCase;
    }

    @Override
    public void changeMyPassword(User user, String currentPassword, String newPassword) {
        changeUserPasswordUseCase.changePassword(user, currentPassword, newPassword);
    }

    @Override
    public Warehouse registerWarehouse(User user, String location, WarehouseType type, Seller owner) {
        return registerWarehouseUseCase.registerWarehouse(user, location, type, owner);
    }

    @Override
    public void deactivateWarehouse(User user, Warehouse warehouse) {
        deactivateWarehouseUseCase.deactivateWarehouse(user, warehouse);
    }

    @Override
    public List<Warehouse> consultWarehouses(User user) {
        return consultWarehouseUseCase.consultAllWarehouses(user);
    }

    @Override
    public Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock) {
        return openInventoryUseCase.openInventory(user, product, warehouse, initialStock);
    }

    @Override
    public void receiveStock(User user, Inventory inventory, int quantity, String reason) {
        receiveStockUseCase.receiveStock(user, inventory, quantity, reason);
    }

    @Override
    public void adjustStock(User user, Inventory inventory, int newQuantity, String reason) {
        adjustStockUseCase.adjustStock(user, inventory, newQuantity, reason);
    }

    @Override
    public void returnStock(User user, Inventory inventory, int quantity, String reason) {
        returnStockUseCase.returnStock(user, inventory, quantity, reason);
    }

    @Override
    public void markInventoryDamaged(User user, Inventory inventory) {
        markInventoryDamagedUseCase.markDamaged(user, inventory);
    }

    @Override
    public List<Inventory> consultInventory(User user) {
        return consultInventoryUseCase.consultAllInventory(user);
    }

    @Override
    public List<Order> consultOrders(User user) {
        return consultOrderUseCase.consultAllOrders(user);
    }

    @Override
    public Shipment createShipment(User user, Order order, String address) {
        return createShipmentUseCase.createShipment(user, order, address);
    }

    @Override
    public void prepareShipment(User user, Shipment shipment) {
        prepareShipmentUseCase.prepare(user, shipment);
    }

    @Override
    public void dispatchShipment(User user, Shipment shipment) {
        dispatchShipmentUseCase.dispatch(user, shipment);
    }

    @Override
    public void updateShipmentStatus(User user, Shipment shipment, ShipmentStatus status) {
        updateShipmentStatusUseCase.updateStatus(user, shipment, status);
    }

    @Override
    public void confirmDelivery(User user, Shipment shipment) {
        confirmDeliveryUseCase.confirmDelivery(user, shipment);
    }

    @Override
    public List<Shipment> consultShipments(User user) {
        return consultShipmentUseCase.consultAllShipments(user);
    }

    @Override
    public void completeReturn(User user, Return productReturn) {
        completeReturnUseCase.complete(user, productReturn);
    }

    @Override
    public List<Return> consultReturns(User user) {
        return consultReturnUseCase.consultAllReturns(user);
    }
}
