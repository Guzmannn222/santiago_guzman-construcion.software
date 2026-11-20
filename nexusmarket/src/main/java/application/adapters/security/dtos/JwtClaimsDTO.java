package application.adapters.security.dtos;

import lombok.Data;

/**
 * Snapshot of the {@code User} that travels as signed claims inside the JWT. It is the contract
 * of the security adapter: the domain never sees claims, only this adapter converts
 * User &lt;-&gt; DTO &lt;-&gt; claims.
 */
@Data
public class JwtClaimsDTO {

    private Integer version;
    private String username;
    private String status;
    private String role;
    private String identityDocument;
    private String name;
    private String email;
}
