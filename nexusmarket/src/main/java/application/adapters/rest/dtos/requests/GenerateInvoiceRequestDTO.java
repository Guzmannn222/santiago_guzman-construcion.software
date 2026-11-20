package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class GenerateInvoiceRequestDTO {

    @DecimalMin("0.0") @DecimalMax("1.0")
    private BigDecimal taxRate;
}
