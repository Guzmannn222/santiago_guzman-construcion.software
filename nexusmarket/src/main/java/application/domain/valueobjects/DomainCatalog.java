package application.domain.valueobjects;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Base class of every closed catalog (status, type, role...). A catalog entry is identified by
 * its {@code code}; {@code name} and {@code description} are descriptive only. Catalogs replace
 * open {@code String} fields in the domain model (no "primitive obsession").
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class DomainCatalog {

    @EqualsAndHashCode.Include
    private final String code;
    private final String name;
    private final String description;

    protected DomainCatalog(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * Shared lookup used by every concrete catalog's {@code fromCode}.
     *
     * @return the entry whose code equals {@code code}, or {@code null} when {@code code} is null
     * @throws IllegalArgumentException when no entry has that code
     */
    protected static <T extends DomainCatalog> T lookup(String catalogName, String code, List<T> values) {
        if (code == null) {
            return null;
        }
        for (T value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown " + catalogName + " code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}
