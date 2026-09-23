package com.nexusmarket;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.UserRepository;
import com.nexusmarket.service.UserService;
import com.nexusmarket.util.AccessDeniedException;
import com.nexusmarket.util.DuplicateEmailException;

/**
 * Unit tests for {@link UserService}: email uniqueness (SDD section 31.1) and role-based
 * authorization (every service is restricted to the roles allowed to execute it).
 */
class UserServiceTest {

    @Test
    void shouldNotAllowRegisteringTwoUsersWithTheSameEmail() {
        UserService userService = new UserService(new UserRepository());
        User admin = new User("U-ADMIN", "Ana Admin", "ana.admin@nexusmarket.com", Role.ADMINISTRATOR);

        userService.register(admin, "Ana", "ana@nexusmarket.com", Role.ADMINISTRATOR);

        assertThrows(DuplicateEmailException.class,
                () -> userService.register(admin, "Ana Duplicate", "ana@nexusmarket.com", Role.BUYER));
    }

    @Test
    void shouldNotAllowANonAdministratorToRegisterUsers() {
        UserService userService = new UserService(new UserRepository());
        User buyer = new User("U-BUYER", "Beatriz Buyer", "beatriz@nexusmarket.com", Role.BUYER);

        assertThrows(AccessDeniedException.class,
                () -> userService.register(buyer, "Someone Else", "someone@nexusmarket.com", Role.BUYER));
    }
}
