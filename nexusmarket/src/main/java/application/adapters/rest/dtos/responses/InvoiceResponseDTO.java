package application.adapters.rest.dtos.responses;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InvoiceResponseDTO {

    private Long id;

    private Long orderId;

    private MoneyResponseDTO subtotal;

    private MoneyResponseDTO tax;

    private MoneyResponseDTO total;

    private BigDecimal taxRate;

    private boolean cancelled;

    private String issueDate;
}
