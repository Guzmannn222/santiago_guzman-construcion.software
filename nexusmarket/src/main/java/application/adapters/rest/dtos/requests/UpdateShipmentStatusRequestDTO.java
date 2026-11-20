package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateShipmentStatusRequestDTO {

    @NotBlank @Pattern(regexp = "^(PENDING|PREPARING|DISPATCHED|IN_TRANSIT|DELIVERED)$", message = "status must be one of: PENDING, PREPARING, DISPATCHED, IN_TRANSIT, DELIVERED")
    private String status;
}
