package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaUserRepository;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for User backed by Spring Data JPA (MySQL). */
@Repository
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository userRepository;

    public UserJpaAdapter(SpringDataJpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }
        return UserJpaMapper.toDomain(userRepository.save(UserJpaMapper.toEntity(user)));
    }

    @Override
    public void update(User user) {
        if (user != null) {
            userRepository.save(UserJpaMapper.toEntity(user));
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return id == null ? Optional.empty() : userRepository.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().map(UserJpaMapper::toDomain).toList();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return username == null ? Optional.empty() : userRepository.findByUsername(username).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return email == null ? Optional.empty() : userRepository.findByEmail(email).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByIdentityDocument(String identityDocument) {
        return identityDocument == null ? Optional.empty()
                : userRepository.findByIdentityDocument(identityDocument).map(UserJpaMapper::toDomain);
    }
}
