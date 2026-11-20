package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequestDTO {

    @NotNull @Positive
    private Long productId;

    @NotNull @Min(1) @Max(100)
    private Integer quantity;
}
