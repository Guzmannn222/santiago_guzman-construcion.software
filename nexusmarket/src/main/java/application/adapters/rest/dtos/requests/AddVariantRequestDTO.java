package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddVariantRequestDTO {

    @NotBlank @Size(max = 60)
    private String name;

    @NotBlank @Size(max = 120)
    private String value;
}
