package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import application.domain.valueobjects.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates each request from its {@code Authorization: Bearer} token. The persisted user is
 * loaded on every request, so blocking a user or bumping {@code authTokenVersion} (logout,
 * password change) invalidates previously issued tokens immediately.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServicePort jwtService;
    private final MarketUserDetailsAdapter userDetailsAdapter;

    public JwtAuthenticationFilter(JwtServicePort jwtService, MarketUserDetailsAdapter userDetailsAdapter) {
        this.jwtService = jwtService;
        this.userDetailsAdapter = userDetailsAdapter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);
        if (token != null && jwtService.validateToken(token)) {
            try {
                Long userId = jwtService.extractUserId(token);
                Integer tokenVersion = jwtService.extractTokenVersion(token);
                UserDetails details = userDetailsAdapter.loadUserByUsername(String.valueOf(userId));
                User stored = ((AuthenticatedUserPrincipal) details).getUser();
                if (tokenVersion != null && isUsable(stored, tokenVersion)) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUsable(User stored, Integer tokenVersion) {
        if (!UserStatus.ACTIVE.equals(stored.getStatus())) {
            return false;
        }
        Integer current = stored.getAuthTokenVersion() != null ? stored.getAuthTokenVersion() : 1;
        return current.equals(tokenVersion);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
