package application.adapters.security;

import application.adapters.security.mappers.JwtClaimsMapper;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Output adapter that issues and validates JWTs (JJWT 0.12.x, HS256+). The token carries the user
 * id ({@code sub}), a unique id ({@code jti}), the token version ({@code ver}) and a snapshot of
 * the user so it can be rebuilt without touching the database.
 */
@Component
public class JwtServiceAdapter implements JwtServicePort {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtServiceAdapter(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("A persisted user must be provided to issue a token.");
        }
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)));
        JwtClaimsMapper.toClaimsMap(user).forEach(builder::claim);
        return builder.signWith(signingKey).compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long extractUserId(String token) {
        try {
            return JwtClaimsMapper.parseSubject(parse(token).getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Integer extractTokenVersion(String token) {
        try {
            return JwtClaimsMapper.asInteger(parse(token).get(JwtClaimsMapper.VERSION));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User reconstructUser(String token) {
        Claims claims = parse(token);
        return JwtClaimsMapper.toDomain(claims::get, JwtClaimsMapper.parseSubject(claims.getSubject()));
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
