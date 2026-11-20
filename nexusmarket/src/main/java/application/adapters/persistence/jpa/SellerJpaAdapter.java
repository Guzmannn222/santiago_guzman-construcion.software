package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.SellerJpaEntity;
import application.adapters.persistence.jpa.mappers.SellerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaSellerRepository;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Output persistence adapter for Seller backed by Spring Data JPA (MySQL). */
@Repository
public class SellerJpaAdapter implements SellerRepositoryPort {

    private final SpringDataJpaSellerRepository sellerRepository;
    private final UserRepositoryPort userRepositoryPort;

    public SellerJpaAdapter(SpringDataJpaSellerRepository sellerRepository, UserRepositoryPort userRepositoryPort) {
        this.sellerRepository = sellerRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Seller save(Seller seller) {
        if (seller == null) {
            return null;
        }
        return toDomain(sellerRepository.save(SellerJpaMapper.toEntity(seller)));
    }

    @Override
    public void update(Seller seller) {
        if (seller != null) {
            sellerRepository.save(SellerJpaMapper.toEntity(seller));
        }
    }

    @Override
    public Optional<Seller> findById(Long id) {
        return id == null ? Optional.empty() : sellerRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Seller> findAll() {
        return sellerRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Seller> findByUserId(Long userId) {
        return userId == null ? Optional.empty() : sellerRepository.findByUserId(userId).map(this::toDomain);
    }

    private Seller toDomain(SellerJpaEntity entity) {
        return SellerJpaMapper.toDomain(entity, userRepositoryPort.findById(entity.getUserId()).orElse(null));
    }
}
