package application.adapters.rest.dtos.responses;

import java.util.List;
import lombok.Data;

@Data
public class BuyerResponseDTO {

    private Long id;

    private UserResponseDTO user;

    private String primaryAddress;

    private List<String> additionalAddresses;

    private String tradingStatus;
}
