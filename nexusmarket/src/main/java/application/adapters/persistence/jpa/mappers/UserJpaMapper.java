package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/** Bidirectional mapper between the User Domain Model and its JPA entity. */
public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setIdentityDocument(domain.getIdentityDocument());
        entity.setRole(domain.getRole() != null ? domain.getRole().getCode() : null);
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        entity.setAuthTokenVersion(domain.getAuthTokenVersion());
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        User domain = new User();
        domain.setId(entity.getId());
        domain.setUsername(entity.getUsername());
        domain.setPassword(entity.getPassword());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setIdentityDocument(entity.getIdentityDocument());
        domain.setRole(entity.getRole() != null ? SystemRole.fromCode(entity.getRole()) : null);
        domain.setStatus(entity.getStatus() != null ? UserStatus.fromCode(entity.getStatus()) : null);
        domain.setAuthTokenVersion(entity.getAuthTokenVersion() != null ? entity.getAuthTokenVersion() : 1);
        return domain;
    }
}
