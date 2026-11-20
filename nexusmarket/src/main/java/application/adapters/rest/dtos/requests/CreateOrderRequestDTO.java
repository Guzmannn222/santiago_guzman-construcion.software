package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateOrderRequestDTO {

    @Positive
    private Long warehouseId;
}
