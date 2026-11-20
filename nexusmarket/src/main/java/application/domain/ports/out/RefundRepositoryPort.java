package application.domain.ports.out;

import application.domain.models.Refund;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Refund aggregates. */
public interface RefundRepositoryPort {

    Refund save(Refund refund);
    void update(Refund refund);
    Optional<Refund> findById(Long id);
    List<Refund> findAll();
    Optional<Refund> findByReturnId(Long returnId);
}
