package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.valueobjects.RefundStatus;

/** Bidirectional mapper between the Refund Domain Model and its JPA entity. */
public final class RefundJpaMapper {

    private RefundJpaMapper() {
    }

    public static RefundJpaEntity toEntity(Refund domain) {
        if (domain == null) {
            return null;
        }
        RefundJpaEntity entity = new RefundJpaEntity();
        entity.setId(domain.getId());
        entity.setReturnId(domain.getProductReturn() != null ? domain.getProductReturn().getId() : null);
        entity.setAmountAmount(MoneyJpaMapper.amount(domain.getAmount()));
        entity.setAmountCurrency(MoneyJpaMapper.currency(domain.getAmount()));
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        return entity;
    }

    public static Refund toDomain(RefundJpaEntity entity, Return productReturn) {
        if (entity == null) {
            return null;
        }
        Refund domain = new Refund();
        domain.setId(entity.getId());
        domain.setProductReturn(productReturn);
        domain.setAmount(MoneyJpaMapper.toMoney(entity.getAmountAmount(), entity.getAmountCurrency()));
        domain.setStatus(entity.getStatus() != null ? RefundStatus.fromCode(entity.getStatus()) : null);
        return domain;
    }
}
