package application.domain.models;

import application.domain.exceptions.InvalidProductException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A product variation, such as color, size or model. */
@Getter
@Setter
@NoArgsConstructor
public class Variant {

    private String name;
    private String value;

    public Variant(String name, String value) {
        if (name == null || name.isBlank() || name.length() > 60) {
            throw new InvalidProductException("Variant name must be provided (max 60 characters).");
        }
        if (value == null || value.isBlank() || value.length() > 120) {
            throw new InvalidProductException("Variant value must be provided (max 120 characters).");
        }
        this.name = name.trim();
        this.value = value.trim();
    }
}
