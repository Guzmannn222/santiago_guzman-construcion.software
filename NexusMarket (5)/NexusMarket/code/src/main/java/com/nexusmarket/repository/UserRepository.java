package com.nexusmarket.repository;

import java.util.Optional;

import com.nexusmarket.model.User;

/** Persistence for {@link User} entities. */
public class UserRepository extends InMemoryRepository<User, String> {

    public UserRepository() {
        super(User::getId);
    }

    /** Used to enforce email uniqueness before registering a new user (SDD 31.1). */
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
