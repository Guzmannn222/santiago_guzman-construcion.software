package application.domain.models;

import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.Setter;

/** Identity and contact data shared by every identifiable person of the platform. */
@Getter
@Setter
public abstract class Person {
    private String identityDocument;
    private String name;
    private String email;
    private SystemRole role;
}
