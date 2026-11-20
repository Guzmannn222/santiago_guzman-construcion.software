package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.buyer.*;
import application.domain.services.cart.*;
import application.domain.services.inventory.*;
import application.domain.services.invoice.*;
import application.domain.services.order.*;
import application.domain.services.product.*;
import application.domain.services.refund.*;
import application.domain.services.report.*;
import application.domain.services.returns.*;
import application.domain.services.seller.*;
import application.domain.services.shipment.*;
import application.domain.services.user.*;
import application.domain.services.warehouse.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class UserServicesTest {

    private Fixture fx;
    private CreateUserAccountService create;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        create = new CreateUserAccountService(fx.userPort, fx.passwordPort);
    }

    private User draft(String username, String email, String doc, SystemRole role) {
        return new User(username, "password123", "Some Name", email, doc, role);
    }

    private RegisterUserService registerService() {
        return new RegisterUserService(fx.validateStatus, fx.validateRole, fx.traceability, create);
    }

    @Test
    @DisplayName("Admin registers a user: password is encrypted and the operation is traced")
    void adminRegistersUser() {
        User admin = fx.admin();
        User saved = registerService().register(admin, draft("maria", "maria@mail.com", "CC1", SystemRole.SELLER));
        assertNotNull(saved.getId());
        assertEquals("enc:password123", saved.getPassword());
        assertEquals(1, fx.operations.findAll().size());
        assertEquals(1, fx.audits.size());
        assertEquals(OperationType.USER_REGISTRATION, fx.audits.get(0).getOperationType());
        assertEquals(SystemRole.ADMINISTRATOR, fx.audits.get(0).getUserRole());
    }

    @Test
    void nonAdminCannotRegisterUsers() {
        User buyer = fx.newBuyer().getUser();
        assertThrows(UnauthorizedOperationException.class,
                () -> registerService().register(buyer, draft("maria", "maria@mail.com", "CC1", SystemRole.SELLER)));
        assertTrue(fx.operations.findAll().isEmpty());
    }

    @Test
    void blockedActorCannotOperate() {
        User admin = fx.admin();
        admin.block();
        assertThrows(UnauthorizedOperationException.class,
                () -> registerService().register(admin, draft("maria", "maria@mail.com", "CC1", SystemRole.SELLER)));
    }

    @Test
    void duplicatesAreRejected() {
        User admin = fx.admin();
        registerService().register(admin, draft("maria", "maria@mail.com", "CC1", SystemRole.SELLER));
        assertThrows(DuplicateUsernameException.class,
                () -> registerService().register(admin, draft("maria", "other@mail.com", "CC2", SystemRole.SELLER)));
        assertThrows(DuplicateEmailException.class,
                () -> registerService().register(admin, draft("maria2", "maria@mail.com", "CC2", SystemRole.SELLER)));
        assertThrows(DuplicateIdentityDocumentException.class,
                () -> registerService().register(admin, draft("maria3", "m3@mail.com", "CC1", SystemRole.SELLER)));
    }

    @Test
    void invalidDataIsRejected() {
        User admin = fx.admin();
        assertThrows(InvalidUserException.class,
                () -> registerService().register(admin, draft("maria", "not-an-email", "CC1", SystemRole.SELLER)));
        User shortPassword = draft("maria", "maria@mail.com", "CC1", SystemRole.SELLER);
        shortPassword.setPassword("short");
        assertThrows(InvalidUserException.class, () -> registerService().register(admin, shortPassword));
        assertThrows(InvalidOperationException.class, () -> registerService().register(admin, null));
    }

    @Test
    void loginIssuesTokenAndRejectsBadCredentials() {
        LoginService login = new LoginService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort,
                fx.passwordPort, fx.jwtPort);
        User user = fx.newUser(SystemRole.BUYER);
        AuthenticationResult ok = login.login(new User(user.getUsername(), "password" + user.getId(), null, null, null, null));
        assertEquals("token-" + user.getId() + "-1", ok.getToken());
        assertThrows(InvalidCredentialsException.class,
                () -> login.login(new User(user.getUsername(), "wrong-password", null, null, null, null)));
        assertThrows(InvalidCredentialsException.class,
                () -> login.login(new User("ghost", "password1", null, null, null, null)));
        assertThrows(InvalidCredentialsException.class, () -> login.login(null));
        user.block();
        assertThrows(UnauthorizedOperationException.class,
                () -> login.login(new User(user.getUsername(), "password" + user.getId(), null, null, null, null)));
    }

    @Test
    void logoutInvalidatesTokens() {
        LogoutService logout = new LogoutService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        User user = fx.newUser(SystemRole.BUYER);
        int before = user.getAuthTokenVersion().intValue();
        logout.logout(user);
        assertEquals(before + 1, fx.users.findById(user.getId()).get().getAuthTokenVersion().intValue());
    }

    @Test
    void changePasswordChecksCurrentPasswordAndFormat() {
        ChangeUserPasswordService service = new ChangeUserPasswordService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.userPort, fx.passwordPort);
        User user = fx.newUser(SystemRole.BUYER);
        String current = "password" + user.getId();
        assertThrows(InvalidCredentialsException.class, () -> service.changePassword(user, "nope", "new-password-1"));
        assertThrows(InvalidUserException.class, () -> service.changePassword(user, current, "short"));
        assertThrows(InvalidUserException.class, () -> service.changePassword(user, current, current));
        service.changePassword(user, current, "new-password-1");
        assertEquals("enc:new-password-1", fx.users.findById(user.getId()).get().getPassword());
    }

    @Test
    void blockActivateAndRoleChangeRules() {
        BlockUserService block = new BlockUserService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        ActivateUserService activate = new ActivateUserService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        ChangeUserRoleService role = new ChangeUserRoleService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        User admin = fx.admin();
        User target = fx.newUser(SystemRole.BUYER);

        assertThrows(UnauthorizedOperationException.class, () -> block.block(admin, admin));
        assertThrows(UnauthorizedOperationException.class, () -> role.changeRole(admin, admin, SystemRole.SELLER));
        assertThrows(UnauthorizedOperationException.class, () -> block.block(target, target));
        assertThrows(EntityNotFoundException.class, () -> block.block(admin, new User()));

        block.block(admin, target);
        assertFalse(fx.users.findById(target.getId()).get().validateAccess());
        activate.activate(admin, target);
        assertTrue(fx.users.findById(target.getId()).get().validateAccess());
        role.changeRole(admin, target, SystemRole.LOGISTICS_OPERATOR);
        assertEquals(SystemRole.LOGISTICS_OPERATOR, fx.users.findById(target.getId()).get().getRole());
        assertThrows(InvalidUserException.class, () -> role.changeRole(admin, target, SystemRole.LOGISTICS_OPERATOR));
    }

    @Test
    void consultUserRules() {
        ConsultUserService consult = new ConsultUserService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        User admin = fx.admin();
        User buyer = fx.newUser(SystemRole.BUYER);
        User other = fx.newUser(SystemRole.BUYER);
        assertSame(buyer, consult.consultUser(buyer, buyer));
        assertSame(buyer, consult.consultUser(admin, buyer));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultUser(buyer, other));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllUsers(buyer));
        assertEquals(3, consult.consultAllUsers(admin).size());
    }
}
