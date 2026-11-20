package application.adapters.security.mappers;

import application.adapters.security.dtos.JwtClaimsDTO;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Bidirectional mapping between the domain {@link User} and JWT claims. It works on plain maps /
 * lookup functions (not on jjwt types) so it can be unit tested without the JWT library.
 */
public final class JwtClaimsMapper {

    public static final String VERSION = "ver";
    public static final String USERNAME = "username";
    public static final String STATUS = "status";
    public static final String ROLE = "role";
    public static final String IDENTITY_DOCUMENT = "identityDocument";
    public static final String NAME = "name";
    public static final String EMAIL = "email";

    private JwtClaimsMapper() {
    }

    public static JwtClaimsDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        JwtClaimsDTO dto = new JwtClaimsDTO();
        dto.setVersion(user.getAuthTokenVersion() != null ? user.getAuthTokenVersion() : 1);
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus() != null ? user.getStatus().getCode() : null);
        dto.setRole(user.getRole() != null ? user.getRole().getCode() : null);
        dto.setIdentityDocument(user.getIdentityDocument());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static Map<String, Object> toClaimsMap(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        JwtClaimsDTO dto = toDto(user);
        if (dto == null) {
            return claims;
        }
        claims.put(VERSION, dto.getVersion());
        claims.put(USERNAME, dto.getUsername());
        claims.put(STATUS, dto.getStatus());
        claims.put(ROLE, dto.getRole());
        claims.put(IDENTITY_DOCUMENT, dto.getIdentityDocument());
        claims.put(NAME, dto.getName());
        claims.put(EMAIL, dto.getEmail());
        return claims;
    }

    public static JwtClaimsDTO toDto(Function<String, Object> claim) {
        JwtClaimsDTO dto = new JwtClaimsDTO();
        dto.setVersion(asInteger(claim.apply(VERSION)));
        dto.setUsername(asString(claim.apply(USERNAME)));
        dto.setStatus(asString(claim.apply(STATUS)));
        dto.setRole(asString(claim.apply(ROLE)));
        dto.setIdentityDocument(asString(claim.apply(IDENTITY_DOCUMENT)));
        dto.setName(asString(claim.apply(NAME)));
        dto.setEmail(asString(claim.apply(EMAIL)));
        return dto;
    }

    public static User toDomain(JwtClaimsDTO dto, Long userId) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        user.setAuthTokenVersion(dto.getVersion());
        user.setUsername(dto.getUsername());
        user.setStatus(dto.getStatus() != null ? UserStatus.fromCode(dto.getStatus()) : null);
        user.setRole(dto.getRole() != null ? SystemRole.fromCode(dto.getRole()) : null);
        user.setIdentityDocument(dto.getIdentityDocument());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static User toDomain(Function<String, Object> claim, Long userId) {
        return toDomain(toDto(claim), userId);
    }

    public static Long parseSubject(String subject) {
        try {
            return subject == null ? null : Long.valueOf(subject);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer asInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
