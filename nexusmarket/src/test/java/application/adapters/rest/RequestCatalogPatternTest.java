package application.adapters.rest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.adapters.rest.dtos.requests.ChangeUserRoleRequestDTO;
import application.adapters.rest.dtos.requests.CreateProductRequestDTO;
import application.adapters.rest.dtos.requests.IssueRefundRequestDTO;
import application.adapters.rest.dtos.requests.RegisterUserRequestDTO;
import application.adapters.rest.dtos.requests.RegisterWarehouseRequestDTO;
import application.adapters.rest.dtos.requests.UpdateShipmentStatusRequestDTO;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.lang.reflect.Field;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Keeps the catalog {@code @Pattern} constraints of the request DTOs in sync with the domain
 * catalogs (the Bean Validation engine itself runs in the Spring/Maven build).
 */
class RequestCatalogPatternTest {

    private static String regexp(Class<?> dto, String field) throws Exception {
        Field f = dto.getDeclaredField(field);
        Pattern pattern = f.getAnnotation(Pattern.class);
        assertNotNull(pattern, dto.getSimpleName() + "." + field + " must declare @Pattern");
        return pattern.regexp();
    }

    @Test
    @DisplayName("role patterns accept every SystemRole code and reject unknown ones")
    void rolePatterns() throws Exception {
        for (Class<?> dto : new Class<?>[] {RegisterUserRequestDTO.class, ChangeUserRoleRequestDTO.class}) {
            String regexp = regexp(dto, "role");
            for (SystemRole role : SystemRole.values()) {
                assertTrue(role.getCode().matches(regexp), role.getCode());
            }
            assertFalse("GUEST".matches(regexp));
            assertFalse("buyer".matches(regexp));
        }
    }

    @Test
    @DisplayName("currency, product type, warehouse type and shipment status patterns match their catalogs")
    void otherCatalogPatterns() throws Exception {
        String currency = regexp(CreateProductRequestDTO.class, "currency");
        for (Currency c : Currency.values()) {
            assertTrue(c.getCode().matches(currency));
        }
        assertFalse("BTC".matches(currency));
        assertTrue(regexp(IssueRefundRequestDTO.class, "currency").equals(currency));

        String type = regexp(CreateProductRequestDTO.class, "type");
        for (ProductType t : ProductType.values()) {
            assertTrue(t.getCode().matches(type));
        }
        assertFalse("SERVICE".matches(type));

        String warehouseType = regexp(RegisterWarehouseRequestDTO.class, "type");
        for (WarehouseType t : WarehouseType.values()) {
            assertTrue(t.getCode().matches(warehouseType));
        }
        assertFalse("MOON".matches(warehouseType));

        String shipment = regexp(UpdateShipmentStatusRequestDTO.class, "status");
        for (ShipmentStatus s : ShipmentStatus.values()) {
            assertTrue(s.getCode().matches(shipment));
        }
        assertFalse("LOST".matches(shipment));
    }
}
