package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class CartResponseDTO {

    private Long id;

    private Long buyerId;

    private List<CartItemResponseDTO> items;

    private MoneyResponseDTO total;
}
