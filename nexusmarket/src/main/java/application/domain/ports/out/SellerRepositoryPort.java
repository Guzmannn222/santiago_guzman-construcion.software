package application.domain.ports.out;

import application.domain.models.Seller;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Seller aggregates. */
public interface SellerRepositoryPort {

    Seller save(Seller seller);
    void update(Seller seller);
    Optional<Seller> findById(Long id);
    List<Seller> findAll();
    Optional<Seller> findByUserId(Long userId);
}
