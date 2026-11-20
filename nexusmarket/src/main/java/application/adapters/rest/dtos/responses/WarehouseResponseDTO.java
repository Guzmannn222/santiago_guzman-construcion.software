package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class WarehouseResponseDTO {

    private Long id;

    private String location;

    private String type;

    private boolean active;

    private Long ownerSellerId;
}
