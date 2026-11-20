package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    private MoneyResponseDTO price;

    private String type;

    private String status;

    private List<VariantResponseDTO> variants;
}
