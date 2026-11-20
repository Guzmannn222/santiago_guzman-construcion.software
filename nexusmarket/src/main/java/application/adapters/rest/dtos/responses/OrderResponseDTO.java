package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class OrderResponseDTO {

    private Long id;

    private Long buyerId;

    private Long warehouseId;

    private List<OrderItemResponseDTO> items;

    private MoneyResponseDTO total;

    private String status;

    private String date;
}
