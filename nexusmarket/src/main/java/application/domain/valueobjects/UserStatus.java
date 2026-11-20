package application.domain.valueobjects;

import java.util.List;

/**
 * Operating status of a User (independent from the commercial status of a Buyer).
 */
public final class UserStatus extends DomainCatalog {

    public static final UserStatus ACTIVE = new UserStatus(
            "ACTIVE", "Active", "User can access the system normally.");
    public static final UserStatus INACTIVE = new UserStatus(
            "INACTIVE", "Inactive", "User exists but cannot perform system operations.");
    public static final UserStatus BLOCKED = new UserStatus(
            "BLOCKED", "Blocked", "User access has been suspended by an administrator.");

    private static final List<UserStatus> VALUES = List.of(ACTIVE, INACTIVE, BLOCKED);

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<UserStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static UserStatus fromCode(String code) {
        return lookup("UserStatus", code, VALUES);
    }
}
