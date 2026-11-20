package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class ShipmentResponseDTO {

    private Long id;

    private Long orderId;

    private String address;

    private String status;

    private String dispatchDate;

    private String deliveryDate;
}
