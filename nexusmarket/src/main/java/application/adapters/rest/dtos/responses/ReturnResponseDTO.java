package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class ReturnResponseDTO {

    private Long id;

    private Long orderId;

    private String reason;

    private String status;

    private String requestDate;

    private List<ReturnItemResponseDTO> items;
}
