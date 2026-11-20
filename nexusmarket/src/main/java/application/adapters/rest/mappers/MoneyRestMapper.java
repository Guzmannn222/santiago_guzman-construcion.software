package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.MoneyResponseDTO;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import java.math.BigDecimal;

public final class MoneyRestMapper {

    private MoneyRestMapper() {
    }

    public static Money toDomain(BigDecimal amount, String currencyCode) {
        return Money.of(amount, Currency.fromCode(currencyCode));
    }

    public static MoneyResponseDTO toResponseDTO(Money money) {
        if (money == null) {
            return null;
        }
        MoneyResponseDTO dto = new MoneyResponseDTO();
        dto.setAmount(money.getAmount());
        dto.setCurrency(MapperSupport.code(money.getCurrency()));
        return dto;
    }
}
