package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.domain.models.Invoice;
import application.domain.models.Order;

/** Bidirectional mapper between the Invoice Domain Model and its JPA entity. */
public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static InvoiceJpaEntity toEntity(Invoice domain) {
        if (domain == null) {
            return null;
        }
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrder() != null ? domain.getOrder().getId() : null);
        entity.setSubtotalAmount(MoneyJpaMapper.amount(domain.getSubtotal()));
        entity.setSubtotalCurrency(MoneyJpaMapper.currency(domain.getSubtotal()));
        entity.setTaxAmount(MoneyJpaMapper.amount(domain.getTax()));
        entity.setTaxCurrency(MoneyJpaMapper.currency(domain.getTax()));
        entity.setTotalAmount(MoneyJpaMapper.amount(domain.getTotal()));
        entity.setTotalCurrency(MoneyJpaMapper.currency(domain.getTotal()));
        entity.setTaxRate(domain.getTaxRate());
        entity.setCancelled(domain.isCancelled());
        entity.setIssueDate(domain.getIssueDate());
        return entity;
    }

    public static Invoice toDomain(InvoiceJpaEntity entity, Order order) {
        if (entity == null) {
            return null;
        }
        Invoice domain = new Invoice();
        domain.setId(entity.getId());
        domain.setOrder(order);
        domain.setSubtotal(MoneyJpaMapper.toMoney(entity.getSubtotalAmount(), entity.getSubtotalCurrency()));
        domain.setTax(MoneyJpaMapper.toMoney(entity.getTaxAmount(), entity.getTaxCurrency()));
        domain.setTotal(MoneyJpaMapper.toMoney(entity.getTotalAmount(), entity.getTotalCurrency()));
        domain.setTaxRate(entity.getTaxRate());
        domain.setCancelled(Boolean.TRUE.equals(entity.getCancelled()));
        domain.setIssueDate(entity.getIssueDate());
        return domain;
    }
}
