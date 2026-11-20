package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateShipmentRequestDTO {

    @NotBlank @Size(max = 200)
    private String address;
}
