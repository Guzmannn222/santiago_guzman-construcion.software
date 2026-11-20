package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterUserRequestDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import java.util.ArrayList;
import java.util.List;

public final class UserRestMapper {

    private UserRestMapper() {
    }

    /** Credentials only: the login use case authenticates against the stored user. */
    public static User toDomain(LoginRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static User toDomain(RegisterBuyerRequestDTO dto) {
        return build(dto.getUsername(), dto.getPassword(), dto.getIdentityDocument(), dto.getName(),
                dto.getEmail(), SystemRole.BUYER);
    }

    public static User toDomain(RegisterSellerRequestDTO dto) {
        return build(dto.getUsername(), dto.getPassword(), dto.getIdentityDocument(), dto.getName(),
                dto.getEmail(), SystemRole.SELLER);
    }

    public static User toDomain(RegisterUserRequestDTO dto) {
        return build(dto.getUsername(), dto.getPassword(), dto.getIdentityDocument(), dto.getName(),
                dto.getEmail(), SystemRole.fromCode(dto.getRole()));
    }

    private static User build(String username, String password, String identityDocument, String name,
            String email, SystemRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setIdentityDocument(identityDocument);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }

    /** Never exposes the password hash. */
    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setIdentityDocument(user.getIdentityDocument());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(MapperSupport.code(user.getRole()));
        dto.setStatus(MapperSupport.code(user.getStatus()));
        return dto;
    }

    public static List<UserResponseDTO> toResponseDTOs(List<User> users) {
        List<UserResponseDTO> result = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                result.add(toResponseDTO(user));
            }
        }
        return result;
    }

    public static LoginResponseDTO toLoginResponse(String token, User user, long expiresInSeconds) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(token);
        dto.setTokenType("Bearer");
        dto.setExpiresInSeconds(expiresInSeconds);
        dto.setUser(toResponseDTO(user));
        return dto;
    }
}
