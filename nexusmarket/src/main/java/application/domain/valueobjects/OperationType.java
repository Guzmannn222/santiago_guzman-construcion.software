package application.domain.valueobjects;

import java.util.List;

/**
 * Significant business action recorded in the operations log and in the audit trail.
 */
public final class OperationType extends DomainCatalog {

    public static final OperationType USER_REGISTRATION = new OperationType(
            "USER_REGISTRATION", "User Registration", "Registration of a new user.");
    public static final OperationType USER_LOGIN = new OperationType(
            "USER_LOGIN", "User Login", "Successful authentication of a user.");
    public static final OperationType USER_LOGOUT = new OperationType(
            "USER_LOGOUT", "User Logout", "Logout (all issued tokens invalidated).");
    public static final OperationType USER_PASSWORD_CHANGE = new OperationType(
            "USER_PASSWORD_CHANGE", "User Password Change", "Password change by the user.");
    public static final OperationType USER_BLOCKING = new OperationType(
            "USER_BLOCKING", "User Blocking", "Blocking of a user.");
    public static final OperationType USER_ACTIVATION = new OperationType(
            "USER_ACTIVATION", "User Activation", "Activation of a user.");
    public static final OperationType USER_ROLE_CHANGE = new OperationType(
            "USER_ROLE_CHANGE", "User Role Change", "Change of the role of a user.");
    public static final OperationType BUYER_REGISTRATION = new OperationType(
            "BUYER_REGISTRATION", "Buyer Registration", "Registration of a buyer profile and its cart.");
    public static final OperationType BUYER_ADDRESS_ADDED = new OperationType(
            "BUYER_ADDRESS_ADDED", "Buyer Address Added", "A delivery address was added to a buyer.");
    public static final OperationType SELLER_REGISTRATION = new OperationType(
            "SELLER_REGISTRATION", "Seller Registration", "Onboarding of a seller with its first warehouse.");
    public static final OperationType SELLER_DEACTIVATION = new OperationType(
            "SELLER_DEACTIVATION", "Seller Deactivation", "Deactivation of a seller.");
    public static final OperationType SELLER_REACTIVATION = new OperationType(
            "SELLER_REACTIVATION", "Seller Reactivation", "Reactivation of a seller.");
    public static final OperationType WAREHOUSE_REGISTRATION = new OperationType(
            "WAREHOUSE_REGISTRATION", "Warehouse Registration", "Registration of a warehouse.");
    public static final OperationType WAREHOUSE_DEACTIVATION = new OperationType(
            "WAREHOUSE_DEACTIVATION", "Warehouse Deactivation", "Deactivation of a warehouse.");
    public static final OperationType PRODUCT_CREATION = new OperationType(
            "PRODUCT_CREATION", "Product Creation", "Creation of a product.");
    public static final OperationType PRODUCT_VARIANT_ADDED = new OperationType(
            "PRODUCT_VARIANT_ADDED", "Product Variant Added", "A variant was added to a product.");
    public static final OperationType PRODUCT_PRICE_UPDATE = new OperationType(
            "PRODUCT_PRICE_UPDATE", "Product Price Update", "Update of a product price.");
    public static final OperationType PRODUCT_PUBLICATION = new OperationType(
            "PRODUCT_PUBLICATION", "Product Publication", "Publication of a product.");
    public static final OperationType PRODUCT_SUSPENSION = new OperationType(
            "PRODUCT_SUSPENSION", "Product Suspension", "Suspension of a product.");
    public static final OperationType PRODUCT_DISCONTINUATION = new OperationType(
            "PRODUCT_DISCONTINUATION", "Product Discontinuation", "Discontinuation of a product.");
    public static final OperationType INVENTORY_OPENING = new OperationType(
            "INVENTORY_OPENING", "Inventory Opening", "Opening of an inventory record.");
    public static final OperationType STOCK_RECEIVED = new OperationType(
            "STOCK_RECEIVED", "Stock Received", "Stock received into a warehouse.");
    public static final OperationType STOCK_RESERVED = new OperationType(
            "STOCK_RESERVED", "Stock Reserved", "Stock reserved for an order.");
    public static final OperationType STOCK_ADJUSTED = new OperationType(
            "STOCK_ADJUSTED", "Stock Adjusted", "Stock adjusted after a physical count.");
    public static final OperationType STOCK_RETURNED = new OperationType(
            "STOCK_RETURNED", "Stock Returned", "Stock returned to a warehouse.");
    public static final OperationType INVENTORY_DAMAGED = new OperationType(
            "INVENTORY_DAMAGED", "Inventory Damaged", "Inventory marked as damaged.");
    public static final OperationType CART_ITEM_ADDED = new OperationType(
            "CART_ITEM_ADDED", "Cart Item Added", "A product was added to a cart.");
    public static final OperationType CART_ITEM_REMOVED = new OperationType(
            "CART_ITEM_REMOVED", "Cart Item Removed", "A product was removed from a cart.");
    public static final OperationType CART_ITEM_UPDATED = new OperationType(
            "CART_ITEM_UPDATED", "Cart Item Updated", "The quantity of a cart item was updated.");
    public static final OperationType ORDER_CREATION = new OperationType(
            "ORDER_CREATION", "Order Creation", "Creation of an order from a cart.");
    public static final OperationType ORDER_PAYMENT = new OperationType(
            "ORDER_PAYMENT", "Order Payment", "Payment confirmation of an order.");
    public static final OperationType ORDER_CANCELLATION = new OperationType(
            "ORDER_CANCELLATION", "Order Cancellation", "Cancellation of an order.");
    public static final OperationType INVOICE_GENERATION = new OperationType(
            "INVOICE_GENERATION", "Invoice Generation", "Generation of an invoice.");
    public static final OperationType INVOICE_CANCELLATION = new OperationType(
            "INVOICE_CANCELLATION", "Invoice Cancellation", "Cancellation of an invoice.");
    public static final OperationType SHIPMENT_CREATION = new OperationType(
            "SHIPMENT_CREATION", "Shipment Creation", "Creation of a shipment.");
    public static final OperationType SHIPMENT_PREPARATION = new OperationType(
            "SHIPMENT_PREPARATION", "Shipment Preparation", "A shipment was prepared.");
    public static final OperationType SHIPMENT_DISPATCH = new OperationType(
            "SHIPMENT_DISPATCH", "Shipment Dispatch", "A shipment was dispatched.");
    public static final OperationType SHIPMENT_STATUS_UPDATE = new OperationType(
            "SHIPMENT_STATUS_UPDATE", "Shipment Status Update", "Tracking status of a shipment was updated.");
    public static final OperationType DELIVERY_CONFIRMATION = new OperationType(
            "DELIVERY_CONFIRMATION", "Delivery Confirmation", "Delivery of a shipment was confirmed.");
    public static final OperationType RETURN_REQUEST = new OperationType(
            "RETURN_REQUEST", "Return Request", "A return was requested.");
    public static final OperationType RETURN_APPROVAL = new OperationType(
            "RETURN_APPROVAL", "Return Approval", "A return was approved.");
    public static final OperationType RETURN_REJECTION = new OperationType(
            "RETURN_REJECTION", "Return Rejection", "A return was rejected.");
    public static final OperationType RETURN_COMPLETION = new OperationType(
            "RETURN_COMPLETION", "Return Completion", "A return was received back.");
    public static final OperationType REFUND_ISSUE = new OperationType(
            "REFUND_ISSUE", "Refund Issue", "A refund was issued.");
    public static final OperationType REFUND_PROCESSING = new OperationType(
            "REFUND_PROCESSING", "Refund Processing", "A refund started processing.");
    public static final OperationType REFUND_COMPLETION = new OperationType(
            "REFUND_COMPLETION", "Refund Completion", "A refund was completed.");
    public static final OperationType REFUND_REJECTION = new OperationType(
            "REFUND_REJECTION", "Refund Rejection", "A refund was rejected.");
    public static final OperationType REPORT_GENERATION = new OperationType(
            "REPORT_GENERATION", "Report Generation", "An administrative report was generated.");

    private static final List<OperationType> VALUES = List.of(USER_REGISTRATION, USER_LOGIN, USER_LOGOUT, USER_PASSWORD_CHANGE, USER_BLOCKING, USER_ACTIVATION, USER_ROLE_CHANGE, BUYER_REGISTRATION, BUYER_ADDRESS_ADDED, SELLER_REGISTRATION, SELLER_DEACTIVATION, SELLER_REACTIVATION, WAREHOUSE_REGISTRATION, WAREHOUSE_DEACTIVATION, PRODUCT_CREATION, PRODUCT_VARIANT_ADDED, PRODUCT_PRICE_UPDATE, PRODUCT_PUBLICATION, PRODUCT_SUSPENSION, PRODUCT_DISCONTINUATION, INVENTORY_OPENING, STOCK_RECEIVED, STOCK_RESERVED, STOCK_ADJUSTED, STOCK_RETURNED, INVENTORY_DAMAGED, CART_ITEM_ADDED, CART_ITEM_REMOVED, CART_ITEM_UPDATED, ORDER_CREATION, ORDER_PAYMENT, ORDER_CANCELLATION, INVOICE_GENERATION, INVOICE_CANCELLATION, SHIPMENT_CREATION, SHIPMENT_PREPARATION, SHIPMENT_DISPATCH, SHIPMENT_STATUS_UPDATE, DELIVERY_CONFIRMATION, RETURN_REQUEST, RETURN_APPROVAL, RETURN_REJECTION, RETURN_COMPLETION, REFUND_ISSUE, REFUND_PROCESSING, REFUND_COMPLETION, REFUND_REJECTION, REPORT_GENERATION);

    private OperationType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OperationType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static OperationType fromCode(String code) {
        return lookup("OperationType", code, VALUES);
    }
}
