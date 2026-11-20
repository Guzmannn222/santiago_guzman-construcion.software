package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.Variant;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Product Domain Model and its JPA entities. */
public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductJpaEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(domain.getId());
        entity.setSellerId(domain.getSeller() != null ? domain.getSeller().getId() : null);
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPriceAmount(MoneyJpaMapper.amount(domain.getPrice()));
        entity.setPriceCurrency(MoneyJpaMapper.currency(domain.getPrice()));
        entity.setType(domain.getType() != null ? domain.getType().getCode() : null);
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        return entity;
    }

    public static List<ProductVariantJpaEntity> toVariantEntities(Long productId, List<Variant> variants) {
        List<ProductVariantJpaEntity> result = new ArrayList<>();
        if (variants == null) {
            return result;
        }
        for (Variant variant : variants) {
            ProductVariantJpaEntity row = new ProductVariantJpaEntity();
            row.setProductId(productId);
            row.setName(variant.getName());
            row.setValue(variant.getValue());
            result.add(row);
        }
        return result;
    }

    public static Product toDomain(ProductJpaEntity entity, Seller seller, List<ProductVariantJpaEntity> variants) {
        if (entity == null) {
            return null;
        }
        Product domain = new Product();
        domain.setId(entity.getId());
        domain.setSeller(seller);
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setPrice(MoneyJpaMapper.toMoney(entity.getPriceAmount(), entity.getPriceCurrency()));
        domain.setType(entity.getType() != null ? ProductType.fromCode(entity.getType()) : null);
        domain.setStatus(entity.getStatus() != null ? ProductStatus.fromCode(entity.getStatus()) : null);
        List<Variant> list = new ArrayList<>();
        if (variants != null) {
            for (ProductVariantJpaEntity row : variants) {
                list.add(new Variant(row.getName(), row.getValue()));
            }
        }
        domain.setVariants(list);
        return domain;
    }
}
