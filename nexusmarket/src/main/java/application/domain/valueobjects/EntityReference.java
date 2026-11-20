package application.domain.valueobjects;

import application.domain.exceptions.InvalidOperationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value Object identifying the domain entity affected by an Operation or recorded in an AuditLog
 * (the marketplace equivalent of the reference project's polymorphic "affected product").
 */
@Getter
@EqualsAndHashCode
public final class EntityReference {

    private final EntityType type;
    private final String identifier;

    public EntityReference(EntityType type, String identifier) {
        if (type == null) {
            throw new InvalidOperationException("Entity reference type must be provided.");
        }
        if (identifier == null || identifier.isBlank()) {
            throw new InvalidOperationException("Entity reference identifier must be provided.");
        }
        this.type = type;
        this.identifier = identifier;
    }

    public static EntityReference of(EntityType type, Long id) {
        if (id == null) {
            throw new InvalidOperationException("Entity reference identifier must be provided.");
        }
        return new EntityReference(type, String.valueOf(id));
    }

    @Override
    public String toString() {
        return type.getCode() + ":" + identifier;
    }
}
