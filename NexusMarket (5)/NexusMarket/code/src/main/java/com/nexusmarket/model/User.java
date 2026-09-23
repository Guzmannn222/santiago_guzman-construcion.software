package com.nexusmarket.model;

import com.nexusmarket.enums.Role;
import com.nexusmarket.enums.UserStatus;

/**
 * Represents the identity and role of a platform participant (SDD, Domain 1).
 * Main attributes: id, name, email, role, status.
 */
public class User {

    private final String id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;

    public User(String id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    /** Activates the user (UserStatus.ACTIVE). */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /** Blocks the user (UserStatus.BLOCKED). */
    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    /** Changes the user's role (RG-02: a single role at a time). */
    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    /** Validates that the user is authenticated and active (RG-01). */
    public boolean validateAccess() {
        return this.status == UserStatus.ACTIVE;
    }

    /** Updates the display name of the account. */
    public void updateName(String newName) {
        this.name = newName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
}
