package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaBuyerAddressRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaBuyerRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Buyer backed by Spring Data JPA (MySQL). */
@Repository
public class BuyerJpaAdapter implements BuyerRepositoryPort {

    private final SpringDataJpaBuyerRepository buyerRepository;
    private final SpringDataJpaBuyerAddressRepository addressRepository;
    private final UserRepositoryPort userRepositoryPort;

    public BuyerJpaAdapter(SpringDataJpaBuyerRepository buyerRepository,
                           SpringDataJpaBuyerAddressRepository addressRepository,
                           UserRepositoryPort userRepositoryPort) {
        this.buyerRepository = buyerRepository;
        this.addressRepository = addressRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public Buyer save(Buyer buyer) {
        if (buyer == null) {
            return null;
        }
        BuyerJpaEntity saved = buyerRepository.save(BuyerJpaMapper.toEntity(buyer));
        addressRepository.deleteByBuyerId(saved.getId());
        addressRepository.saveAll(BuyerJpaMapper.toAddressEntities(saved.getId(), buyer.getAdditionalAddresses()));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Buyer buyer) {
        save(buyer);
    }

    @Override
    public Optional<Buyer> findById(Long id) {
        return id == null ? Optional.empty() : buyerRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Buyer> findAll() {
        return buyerRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Buyer> findByUserId(Long userId) {
        return userId == null ? Optional.empty() : buyerRepository.findByUserId(userId).map(this::toDomain);
    }

    private Buyer toDomain(BuyerJpaEntity entity) {
        return BuyerJpaMapper.toDomain(entity,
                userRepositoryPort.findById(entity.getUserId()).orElse(null),
                addressRepository.findByBuyerIdOrderByPositionAsc(entity.getId()));
    }
}
