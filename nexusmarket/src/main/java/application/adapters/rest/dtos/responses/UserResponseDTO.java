package application.adapters.rest.dtos.responses;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;

    private String username;

    private String identityDocument;

    private String name;

    private String email;

    private String role;

    private String status;
}
