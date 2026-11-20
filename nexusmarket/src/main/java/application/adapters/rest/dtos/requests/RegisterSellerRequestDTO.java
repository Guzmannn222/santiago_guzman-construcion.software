package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterSellerRequestDTO {

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

    @NotBlank @Size(max = 300)
    private String businessInfo;

    @NotBlank @Size(max = 200)
    private String firstWarehouseLocation;
}
