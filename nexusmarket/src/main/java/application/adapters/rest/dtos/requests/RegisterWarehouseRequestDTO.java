package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterWarehouseRequestDTO {

    @NotBlank @Size(max = 200)
    private String location;

    @NotBlank @Pattern(regexp = "^(MARKETPLACE|SELLER)$", message = "type must be one of: MARKETPLACE, SELLER")
    private String type;

    @Positive
    private Long ownerSellerId;
}
