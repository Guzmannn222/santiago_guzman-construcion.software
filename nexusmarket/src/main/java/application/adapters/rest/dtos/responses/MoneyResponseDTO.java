package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class MoneyResponseDTO {

    private BigDecimal amount;

    private String currency;
}
