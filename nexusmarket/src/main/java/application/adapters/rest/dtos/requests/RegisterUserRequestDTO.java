package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequestDTO {

    @NotBlank @Size(min = 3, max = 80)
    private String username;

    @NotBlank @Size(min = 8, max = 100)
    private String password;

    @NotBlank @Size(max = 30)
    private String identityDocument;

    @NotBlank @Size(max = 120)
    private String name;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @NotBlank @Pattern(regexp = "^(BUYER|SELLER|LOGISTICS_OPERATOR|ADMINISTRATOR|SUPERVISOR)$", message = "role must be one of: BUYER, SELLER, LOGISTICS_OPERATOR, ADMINISTRATOR, SUPERVISOR")
    private String role;
}
