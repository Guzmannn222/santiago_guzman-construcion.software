package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangeUserRoleRequestDTO {

    @NotBlank @Pattern(regexp = "^(BUYER|SELLER|LOGISTICS_OPERATOR|ADMINISTRATOR|SUPERVISOR)$", message = "role must be one of: BUYER, SELLER, LOGISTICS_OPERATOR, ADMINISTRATOR, SUPERVISOR")
    private String role;
}
