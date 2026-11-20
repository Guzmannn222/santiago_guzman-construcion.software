package application.domain.ports.out;

/** Output port: password hashing (BCrypt in the adapter layer). */
public interface PasswordServicePort {

    boolean matches(String rawPassword, String encodedPassword);
    String encrypt(String rawPassword);
}
