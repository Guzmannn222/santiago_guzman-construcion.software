package com.nexusmarket.util;

import java.util.Arrays;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.User;

/**
 * Cross-cutting authorization helper used by every service in the {@code service} package to
 * enforce "who can do what" (RG-01: only an active user can operate the system; RG-02: every
 * service is restricted to one or more specific roles). Centralizing it here avoids duplicating
 * the same validation in 40+ service methods.
 */
public final class AccessControl {

    private AccessControl() {
    }

    /** Requires the actor to be active and to hold one of the given roles. */
    public static void requireRole(User actor, Role... allowedRoles) {
        requireActive(actor);
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(role -> role == actor.getRole());
        if (!allowed) {
            throw new AccessDeniedException("User " + actor.getId() + " with role " + actor.getRole()
                    + " is not authorized to execute this service. Allowed roles: " + Arrays.toString(allowedRoles));
        }
    }

    /** Requires the actor to be the owner of the resource (same user) or to hold an override role. */
    public static void requireSelfOrRole(User actor, User owner, Role... overrideRoles) {
        requireActive(actor);
        boolean isOwner = actor.getId().equals(owner.getId());
        boolean hasOverride = Arrays.stream(overrideRoles).anyMatch(role -> role == actor.getRole());
        if (!isOwner && !hasOverride) {
            throw new AccessDeniedException("User " + actor.getId() + " is not authorized to operate on user "
                    + owner.getId() + "'s resource.");
        }
    }

    private static void requireActive(User actor) {
        if (!actor.validateAccess()) {
            throw new AccessDeniedException("User " + actor.getId() + " is not active and cannot use the system.");
        }
    }
}
