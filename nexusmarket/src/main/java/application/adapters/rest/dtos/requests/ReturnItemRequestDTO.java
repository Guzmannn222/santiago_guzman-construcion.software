package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnItemRequestDTO {

    @NotNull
    private Long productId;

    @NotNull @Min(1)
    private Integer quantity;
}
