package application.domain.models;

import application.domain.exceptions.InvalidUserException;
import application.domain.exceptions.InvalidUserStatusException;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * System identity used for authentication and authorization. Each user has exactly one role (RG-02).
 * identityDocument, email and username must be unique (enforced by the registration services).
 */
@Getter
@Setter
@NoArgsConstructor
public class User extends Person {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 100;

    private Long id;
    private String username;
    /** BCrypt hash once persisted; plain text only while a registration is being processed. */
    private String password;
    private UserStatus status = UserStatus.ACTIVE;
    /** Version bound to issued JWTs ({@code ver} claim); bumped to invalidate every previous token. */
    private Integer authTokenVersion = 1;

    public User(String username, String password, String name, String email, String identityDocument,
            SystemRole role) {
        this.username = username;
        this.password = password;
        setName(name);
        setEmail(email);
        setIdentityDocument(identityDocument);
        setRole(role);
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Status transition matrix: ACTIVE -> BLOCKED, ACTIVE -> INACTIVE, BLOCKED -> ACTIVE,
     * INACTIVE -> ACTIVE. Every transition invalidates previously issued tokens.
     */
    public void changeStatus(UserStatus target) {
        if (target == null) {
            throw new InvalidUserStatusException("Target user status must be provided.");
        }
        if (!isValidTransition(status, target)) {
            throw new InvalidUserStatusException("Invalid user status transition from "
                    + (status == null ? "UNKNOWN" : status.getCode()) + " to " + target.getCode() + ".");
        }
        this.status = target;
        bumpAuthTokenVersion();
    }

    public void activate() {
        changeStatus(UserStatus.ACTIVE);
    }

    public void block() {
        changeStatus(UserStatus.BLOCKED);
    }

    public void changeRole(SystemRole newRole) {
        if (newRole == null) {
            throw new InvalidUserException("The new role must be provided.");
        }
        if (newRole.equals(getRole())) {
            throw new InvalidUserException("The user already has the role " + newRole.getCode() + ".");
        }
        setRole(newRole);
        bumpAuthTokenVersion();
    }

    public void bumpAuthTokenVersion() {
        this.authTokenVersion = (this.authTokenVersion == null ? 1 : this.authTokenVersion + 1);
    }

    /** RG-01: every operation must be executed by an authenticated, ACTIVE user. */
    public boolean validateAccess() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    public boolean isSameUserAs(User other) {
        return other != null && id != null && id.equals(other.getId());
    }

    /** Guarantees a user is never registered with missing or malformed data. */
    public void ensureRegistrationDataComplete() {
        requireNonBlank(username, "username", 40);
        if (username.length() < 3 || username.chars().anyMatch(Character::isWhitespace)) {
            throw new InvalidUserException("User username must have 3-40 characters and no whitespace.");
        }
        validatePasswordFormat(password);
        requireNonBlank(getName(), "name", 120);
        requireNonBlank(getEmail(), "email", 120);
        if (!EMAIL.matcher(getEmail()).matches()) {
            throw new InvalidUserException("User email is not a valid email address.");
        }
        requireNonBlank(getIdentityDocument(), "identityDocument", 30);
        if (getRole() == null) {
            throw new InvalidUserException("User role must be provided.");
        }
    }

    public static void validatePasswordFormat(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH
                || rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidUserException("User password must have between " + MIN_PASSWORD_LENGTH
                    + " and " + MAX_PASSWORD_LENGTH + " characters.");
        }
    }

    private static void requireNonBlank(String value, String field, int max) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserException("User " + field + " must be provided.");
        }
        if (value.length() > max) {
            throw new InvalidUserException("User " + field + " must have at most " + max + " characters.");
        }
    }

    private static boolean isValidTransition(UserStatus current, UserStatus target) {
        return (UserStatus.ACTIVE.equals(current) && UserStatus.BLOCKED.equals(target))
                || (UserStatus.ACTIVE.equals(current) && UserStatus.INACTIVE.equals(target))
                || (UserStatus.BLOCKED.equals(current) && UserStatus.ACTIVE.equals(target))
                || (UserStatus.INACTIVE.equals(current) && UserStatus.ACTIVE.equals(target));
    }
}
