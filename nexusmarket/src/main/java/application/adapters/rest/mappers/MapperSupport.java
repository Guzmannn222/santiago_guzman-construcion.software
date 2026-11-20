package application.adapters.rest.mappers;

import application.domain.valueobjects.DomainCatalog;
import java.time.LocalDateTime;

/** Small null-safe helpers shared by the REST mappers. */
final class MapperSupport {

    private MapperSupport() {
    }

    static String code(DomainCatalog catalog) {
        return catalog == null ? null : catalog.getCode();
    }

    static String date(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
