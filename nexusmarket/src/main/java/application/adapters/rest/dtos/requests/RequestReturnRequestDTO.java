package application.adapters.rest.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class RequestReturnRequestDTO {

    @NotBlank @Size(max = 500)
    private String reason;

    /** Optional: products to return. Omitted or empty means the whole order. */
    @Valid
    private List<ReturnItemRequestDTO> items;
}
