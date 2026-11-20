package application.domain.ports.out;

import application.domain.models.User;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of User aggregates. */
public interface UserRepositoryPort {

    User save(User user);
    void update(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdentityDocument(String identityDocument);
}
