package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {

    @NotBlank @Size(max = 100)
    private String currentPassword;

    @NotBlank @Size(min = 8, max = 100)
    private String newPassword;
}
