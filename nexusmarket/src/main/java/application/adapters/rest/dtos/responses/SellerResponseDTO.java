package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class SellerResponseDTO {

    private Long id;

    private UserResponseDTO user;

    private String businessInfo;

    private boolean active;

    private List<WarehouseResponseDTO> warehouses;
}
