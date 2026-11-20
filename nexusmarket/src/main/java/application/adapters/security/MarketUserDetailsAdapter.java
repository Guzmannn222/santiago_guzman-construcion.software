package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.adapters.security.mappers.JwtClaimsMapper;
import application.domain.exceptions.DomainException;
import application.domain.models.User;
import application.domain.services.user.LoadAuthenticatedUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges Spring Security to the domain: parses the {@code sub} claim, loads the authoritative
 * persisted user through the domain service and wraps it in the principal DTO.
 */
@Service
public class MarketUserDetailsAdapter implements UserDetailsService {

    private final LoadAuthenticatedUserService loadAuthenticatedUserService;

    public MarketUserDetailsAdapter(LoadAuthenticatedUserService loadAuthenticatedUserService) {
        this.loadAuthenticatedUserService = loadAuthenticatedUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String subject) throws UsernameNotFoundException {
        Long userId = JwtClaimsMapper.parseSubject(subject);
        if (userId == null) {
            throw new UsernameNotFoundException("Invalid token subject.");
        }
        try {
            User stored = loadAuthenticatedUserService.loadById(userId);
            return new AuthenticatedUserPrincipal(stored);
        } catch (DomainException e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }
}
