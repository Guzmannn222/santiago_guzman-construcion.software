package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaProductRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaProductVariantRepository;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Output persistence adapter for Product backed by Spring Data JPA (MySQL). */
@Repository
public class ProductJpaAdapter implements ProductRepositoryPort {

    private final SpringDataJpaProductRepository productRepository;
    private final SpringDataJpaProductVariantRepository variantRepository;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ProductJpaAdapter(SpringDataJpaProductRepository productRepository,
                             SpringDataJpaProductVariantRepository variantRepository,
                             SellerRepositoryPort sellerRepositoryPort) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    @Transactional
    public Product save(Product product) {
        if (product == null) {
            return null;
        }
        ProductJpaEntity saved = productRepository.save(ProductJpaMapper.toEntity(product));
        variantRepository.deleteByProductId(saved.getId());
        variantRepository.saveAll(ProductJpaMapper.toVariantEntities(saved.getId(), product.getVariants()));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void update(Product product) {
        save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return id == null ? Optional.empty() : productRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return status == null ? List.of()
                : productRepository.findByStatus(status.getCode()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {
        return sellerId == null ? List.of()
                : productRepository.findBySellerId(sellerId).stream().map(this::toDomain).toList();
    }

    private Product toDomain(ProductJpaEntity entity) {
        return ProductJpaMapper.toDomain(entity,
                sellerRepositoryPort.findById(entity.getSellerId()).orElse(null),
                variantRepository.findByProductId(entity.getId()));
    }
}
