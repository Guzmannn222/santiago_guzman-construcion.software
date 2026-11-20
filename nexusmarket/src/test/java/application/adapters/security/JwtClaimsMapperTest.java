package application.adapters.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.adapters.security.mappers.JwtClaimsMapper;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtClaimsMapperTest {

    private User user() {
        User user = new User();
        user.setId(42L);
        user.setUsername("admin");
        user.setIdentityDocument("1");
        user.setName("Admin");
        user.setEmail("admin@example.com");
        user.setRole(SystemRole.ADMINISTRATOR);
        user.setStatus(UserStatus.ACTIVE);
        user.setAuthTokenVersion(3);
        return user;
    }

    @Test
    @DisplayName("user snapshot survives the claims round trip")
    void roundTrip() {
        Map<String, Object> claims = JwtClaimsMapper.toClaimsMap(user());
        assertEquals(3, claims.get("ver"));
        assertEquals("ADMINISTRATOR", claims.get("role"));
        assertEquals(false, claims.containsKey("password"));

        User rebuilt = JwtClaimsMapper.toDomain(claims::get, 42L);
        assertEquals(Long.valueOf(42L), rebuilt.getId());
        assertEquals("admin", rebuilt.getUsername());
        assertEquals(SystemRole.ADMINISTRATOR, rebuilt.getRole());
        assertEquals(UserStatus.ACTIVE, rebuilt.getStatus());
        assertEquals(Integer.valueOf(3), rebuilt.getAuthTokenVersion());
        assertNull(rebuilt.getPassword());
    }

    @Test
    @DisplayName("subject and version parsing are tolerant")
    void parsing() {
        assertEquals(Long.valueOf(42L), JwtClaimsMapper.parseSubject("42"));
        assertNull(JwtClaimsMapper.parseSubject("abc"));
        assertNull(JwtClaimsMapper.parseSubject(null));
        assertEquals(Integer.valueOf(5), JwtClaimsMapper.asInteger(5L));
        assertEquals(Integer.valueOf(6), JwtClaimsMapper.asInteger("6"));
        assertNull(JwtClaimsMapper.asInteger("x"));
        assertNull(JwtClaimsMapper.asInteger(null));
    }

    @Test
    @DisplayName("principal exposes one role authority and reflects status")
    void principal() {
        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(user());
        assertEquals(1, principal.getAuthorities().size());
        assertEquals(true, principal.isEnabled());
        assertEquals(true, principal.isAccountNonLocked());
        User blocked = user();
        blocked.setStatus(UserStatus.BLOCKED);
        AuthenticatedUserPrincipal blockedPrincipal = new AuthenticatedUserPrincipal(blocked);
        assertEquals(false, blockedPrincipal.isEnabled());
        assertEquals(false, blockedPrincipal.isAccountNonLocked());
    }
}
