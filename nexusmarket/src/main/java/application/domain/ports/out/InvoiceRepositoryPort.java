package application.domain.ports.out;

import application.domain.models.Invoice;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Invoice aggregates. */
public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);
    void update(Invoice invoice);
    Optional<Invoice> findById(Long id);
    List<Invoice> findAll();
    Optional<Invoice> findByOrderId(Long orderId);
}
