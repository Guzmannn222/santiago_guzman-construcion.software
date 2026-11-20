package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String token;

    private String tokenType;

    private Long expiresInSeconds;

    private UserResponseDTO user;
}
