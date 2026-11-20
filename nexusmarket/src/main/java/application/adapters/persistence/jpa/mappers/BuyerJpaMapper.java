package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.BuyerAddressJpaEntity;
import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.TradingStatus;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Buyer Domain Model and its JPA entities. */
public final class BuyerJpaMapper {

    private BuyerJpaMapper() {
    }

    public static BuyerJpaEntity toEntity(Buyer domain) {
        if (domain == null) {
            return null;
        }
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUser() != null ? domain.getUser().getId() : null);
        entity.setPrimaryAddress(domain.getPrimaryAddress());
        entity.setTradingStatus(domain.getTradingStatus() != null ? domain.getTradingStatus().getCode() : null);
        return entity;
    }

    public static List<BuyerAddressJpaEntity> toAddressEntities(Long buyerId, List<String> addresses) {
        List<BuyerAddressJpaEntity> result = new ArrayList<>();
        if (addresses == null) {
            return result;
        }
        int position = 0;
        for (String address : addresses) {
            BuyerAddressJpaEntity row = new BuyerAddressJpaEntity();
            row.setBuyerId(buyerId);
            row.setPosition(position++);
            row.setAddress(address);
            result.add(row);
        }
        return result;
    }

    public static Buyer toDomain(BuyerJpaEntity entity, User user, List<BuyerAddressJpaEntity> addresses) {
        if (entity == null) {
            return null;
        }
        Buyer domain = new Buyer();
        domain.setId(entity.getId());
        domain.setUser(user);
        domain.setPrimaryAddress(entity.getPrimaryAddress());
        domain.setTradingStatus(entity.getTradingStatus() != null ? TradingStatus.fromCode(entity.getTradingStatus()) : null);
        List<String> list = new ArrayList<>();
        if (addresses != null) {
            for (BuyerAddressJpaEntity row : addresses) {
                list.add(row.getAddress());
            }
        }
        domain.setAdditionalAddresses(list);
        return domain;
    }
}
