package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.*;
import application.adapters.rest.dtos.responses.*;
import application.adapters.rest.mappers.*;
import application.domain.models.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.ports.in.PublicAccessPort;
import org.springframework.beans.factory.annotation.Value;

/** Public endpoints (login, logout, buyer self-registration) plus password change for any authenticated role. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final PublicAccessPort publicAccessPort;
    private final ChangeUserPasswordUseCase changeUserPasswordUseCase;
    private final long expirationMs;

    public AuthRestController(PublicAccessPort publicAccessPort, ChangeUserPasswordUseCase changeUserPasswordUseCase,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.publicAccessPort = publicAccessPort;
        this.changeUserPasswordUseCase = changeUserPasswordUseCase;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthenticationResult result = publicAccessPort.login(UserRestMapper.toDomain(requestDTO));
        return ResponseEntity.ok(UserRestMapper.toLoginResponse(
                result.getToken(), result.getAuthenticatedUser(), expirationMs / 1000));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal(expression = "user") User authenticatedUser) {
        publicAccessPort.logout(authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<BuyerResponseDTO> registerBuyer(@Valid @RequestBody RegisterBuyerRequestDTO requestDTO) {
        Buyer buyer = publicAccessPort.registerBuyer(UserRestMapper.toDomain(requestDTO), requestDTO.getPrimaryAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(MarketRestMapper.buyer(buyer));
    }

    /** Any authenticated role; every token issued before the change becomes invalid. */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal(expression = "user") User authenticatedUser,
            @Valid @RequestBody ChangePasswordRequestDTO requestDTO) {
        changeUserPasswordUseCase.changePassword(authenticatedUser, requestDTO.getCurrentPassword(),
                requestDTO.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
