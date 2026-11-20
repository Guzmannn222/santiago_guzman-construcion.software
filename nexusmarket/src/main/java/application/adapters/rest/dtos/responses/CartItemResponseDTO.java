package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class CartItemResponseDTO {

    private Long productId;

    private String productName;
    private Long sellerId;

    private int quantity;

    private MoneyResponseDTO unitPrice;
}
