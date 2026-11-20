package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OpenInventoryRequestDTO {

    @NotNull @Positive
    private Long productId;

    @NotNull @Positive
    private Long warehouseId;

    @NotNull @Min(0)
    private Integer initialStock;
}
