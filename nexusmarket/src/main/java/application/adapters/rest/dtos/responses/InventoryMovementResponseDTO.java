package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class InventoryMovementResponseDTO {

    private String type;

    private int quantity;

    private String date;

    private String reason;
}
