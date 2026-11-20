package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class InventoryResponseDTO {

    private Long id;

    private Long productId;

    private Long warehouseId;

    private int stock;

    private String status;

    private int lowStockThreshold;

    private List<InventoryMovementResponseDTO> movements;
}
