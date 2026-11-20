package application.adapters.rest.mappers;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;

/**
 * Identity-only references built from request path/body ids. The domain services reload the
 * authoritative aggregate by id, so the adapter never trusts any other field of a stub.
 * A {@code null} id yields a {@code null} reference.
 */
public final class EntityStubs {

    private EntityStubs() {
    }

    public static User user(Long id) {
        if (id == null) {
            return null;
        }
        User value = new User();
        value.setId(id);
        return value;
    }

    public static Buyer buyer(Long id) {
        if (id == null) {
            return null;
        }
        Buyer value = new Buyer();
        value.setId(id);
        return value;
    }

    public static Seller seller(Long id) {
        if (id == null) {
            return null;
        }
        Seller value = new Seller();
        value.setId(id);
        return value;
    }

    public static Warehouse warehouse(Long id) {
        if (id == null) {
            return null;
        }
        Warehouse value = new Warehouse();
        value.setId(id);
        return value;
    }

    public static Product product(Long id) {
        if (id == null) {
            return null;
        }
        Product value = new Product();
        value.setId(id);
        return value;
    }

    public static Inventory inventory(Long id) {
        if (id == null) {
            return null;
        }
        Inventory value = new Inventory();
        value.setId(id);
        return value;
    }

    public static Cart cart(Long id) {
        if (id == null) {
            return null;
        }
        Cart value = new Cart();
        value.setId(id);
        return value;
    }

    public static Order order(Long id) {
        if (id == null) {
            return null;
        }
        Order value = new Order();
        value.setId(id);
        return value;
    }

    public static Invoice invoice(Long id) {
        if (id == null) {
            return null;
        }
        Invoice value = new Invoice();
        value.setId(id);
        return value;
    }

    public static Shipment shipment(Long id) {
        if (id == null) {
            return null;
        }
        Shipment value = new Shipment();
        value.setId(id);
        return value;
    }

    public static Return productReturn(Long id) {
        if (id == null) {
            return null;
        }
        Return value = new Return();
        value.setId(id);
        return value;
    }

    public static Refund refund(Long id) {
        if (id == null) {
            return null;
        }
        Refund value = new Refund();
        value.setId(id);
        return value;
    }

    /** Builds an audit/operation entity reference from catalog code + identifier query parameters. */
    public static EntityReference entityReference(String typeCode, String identifier) {
        return new EntityReference(EntityType.fromCode(typeCode), identifier);
    }
}
