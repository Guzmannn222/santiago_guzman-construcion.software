package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class RefundResponseDTO {

    private Long id;

    private Long returnId;

    private MoneyResponseDTO amount;

    private String status;
}
