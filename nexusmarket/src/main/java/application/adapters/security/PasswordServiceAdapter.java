package application.adapters.security;

import application.domain.ports.out.PasswordServicePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** BCrypt implementation of the password hashing output port. */
@Component
public class PasswordServiceAdapter implements PasswordServicePort {

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encrypt(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
