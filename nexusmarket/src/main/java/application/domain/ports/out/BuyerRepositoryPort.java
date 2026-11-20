package application.domain.ports.out;

import application.domain.models.Buyer;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Buyer aggregates. */
public interface BuyerRepositoryPort {

    Buyer save(Buyer buyer);
    void update(Buyer buyer);
    Optional<Buyer> findById(Long id);
    List<Buyer> findAll();
    Optional<Buyer> findByUserId(Long userId);
}
