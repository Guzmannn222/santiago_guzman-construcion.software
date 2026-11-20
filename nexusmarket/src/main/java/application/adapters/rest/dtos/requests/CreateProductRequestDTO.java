package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateProductRequestDTO {

    @NotBlank @Size(max = 120)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull @DecimalMin(value = "0.01", message = "price must be greater than zero") @Digits(integer = 12, fraction = 2)
    private BigDecimal price;

    @NotBlank @Pattern(regexp = "^(COP|USD|EUR)$", message = "currency must be one of: COP, USD, EUR")
    private String currency;

    @NotBlank @Pattern(regexp = "^(PHYSICAL|DIGITAL)$", message = "type must be one of: PHYSICAL, DIGITAL")
    private String type;
}
