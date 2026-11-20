package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity (repository DTO) for the User aggregate.
 * Money values are stored as amount + currency columns and catalogs by their code.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt hash. */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "identity_document", nullable = false, unique = true, length = 30)
    private String identityDocument;

    /** SystemRole catalog code. */
    @Column(name = "role", nullable = false, length = 30)
    private String role;

    /** UserStatus catalog code. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** Incremented to invalidate issued tokens. */
    @Column(name = "auth_token_version", nullable = false)
    private Integer authTokenVersion;
}
