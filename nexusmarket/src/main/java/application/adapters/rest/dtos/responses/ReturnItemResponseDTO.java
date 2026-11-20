package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class ReturnItemResponseDTO {

    private Long productId;

    private String productName;

    private Long sellerId;

    private Integer quantity;
}
