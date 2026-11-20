package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.WarehouseType;
import java.util.List;

/** Every use case available to the LOGISTICS_OPERATOR role (CU-04, CU-07, CU-11, CU-12, CU-13 goods reception). */
public interface LogisticsOperatorPort {

    void changeMyPassword(User user, String currentPassword, String newPassword);
    Warehouse registerWarehouse(User user, String location, WarehouseType type, Seller owner);
    void deactivateWarehouse(User user, Warehouse warehouse);
    List<Warehouse> consultWarehouses(User user);
    Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock);
    void receiveStock(User user, Inventory inventory, int quantity, String reason);
    void adjustStock(User user, Inventory inventory, int newQuantity, String reason);
    void returnStock(User user, Inventory inventory, int quantity, String reason);
    void markInventoryDamaged(User user, Inventory inventory);
    List<Inventory> consultInventory(User user);
    List<Order> consultOrders(User user);
    Shipment createShipment(User user, Order order, String address);
    void prepareShipment(User user, Shipment shipment);
    void dispatchShipment(User user, Shipment shipment);
    void updateShipmentStatus(User user, Shipment shipment, ShipmentStatus status);
    void confirmDelivery(User user, Shipment shipment);
    List<Shipment> consultShipments(User user);
    void completeReturn(User user, Return productReturn);
    List<Return> consultReturns(User user);
}
