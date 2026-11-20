package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdjustStockRequestDTO {

    @NotNull @Min(0)
    private Integer newQuantity;

    @NotBlank @Size(max = 200)
    private String reason;
}
