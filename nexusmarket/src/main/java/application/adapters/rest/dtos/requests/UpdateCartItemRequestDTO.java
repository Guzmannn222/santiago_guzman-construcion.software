package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequestDTO {

    @NotNull @Min(1) @Max(100)
    private Integer quantity;
}
