package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.User;
import com.nexusmarket.repository.UserRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.DuplicateEmailException;
import com.nexusmarket.util.IdGenerator;

/**
 * Business rules for {@link User} management. Every public method below is one platform service,
 * each tied to a use case and restricted to the role(s) noted in its Javadoc (SDD, CU-01).
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Use case: UC-01 Register user. Allowed role: ADMINISTRATOR. */
    public User register(User actor, String name, String email, Role role) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        userRepository.findByEmail(email).ifPresent(existing -> {
            throw new DuplicateEmailException("A user with email " + email + " already exists.");
        });
        User user = new User(IdGenerator.next("USR"), name, email, role);
        return userRepository.save(user);
    }

    /** Use case: UC-02 Authenticate user. Allowed role: any registered user (self-service login). */
    public boolean authenticate(User user) {
        return user.validateAccess();
    }

    /** Use case: UC-03 Block user. Allowed role: ADMINISTRATOR. */
    public void block(User actor, User target) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        target.block();
        userRepository.save(target);
    }

    /** Use case: UC-04 Activate user. Allowed role: ADMINISTRATOR. */
    public void activate(User actor, User target) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        target.activate();
        userRepository.save(target);
    }

    /** Use case: UC-05 Change user role. Allowed role: ADMINISTRATOR. */
    public void changeRole(User actor, User target, Role newRole) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        target.changeRole(newRole);
        userRepository.save(target);
    }

    /** Use case: UC-06 Update own profile. Allowed role: the owner of the account, or ADMINISTRATOR. */
    public void updateProfile(User actor, User target, String newName) {
        AccessControl.requireSelfOrRole(actor, target, Role.ADMINISTRATOR);
        target.updateName(newName);
        userRepository.save(target);
    }
}
