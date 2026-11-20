package application.domain.models;

import application.domain.exceptions.InvalidPriceException;
import application.domain.exceptions.InvalidProductException;
import application.domain.exceptions.InvalidProductStatusException;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Physical or digital good offered in the catalog.
 * Status transitions: SUSPENDED <-> PUBLISHED, and either one -> DISCONTINUED (terminal).
 */
@Getter
@Setter
@NoArgsConstructor
public class Product {

    private Long id;
    private Seller seller;
    private String name;
    private String description;
    private Money price;
    private ProductType type;
    private ProductStatus status = ProductStatus.SUSPENDED;
    private List<Variant> variants = new ArrayList<>();

    public Product(Seller seller, String name, String description, Money price, ProductType type) {
        if (seller == null) {
            throw new InvalidProductException("Product seller must be provided.");
        }
        if (name == null || name.isBlank() || name.length() > 120) {
            throw new InvalidProductException("Product name must be provided (max 120 characters).");
        }
        if (description != null && description.length() > 1000) {
            throw new InvalidProductException("Product description must have at most 1000 characters.");
        }
        if (type == null) {
            throw new InvalidProductException("Product type must be provided.");
        }
        validatePrice(price);
        this.seller = seller;
        this.name = name.trim();
        this.description = description;
        this.price = price;
        this.type = type;
        this.status = ProductStatus.SUSPENDED;
    }

    public void addVariant(Variant variant) {
        if (variant == null) {
            throw new InvalidProductException("Variant must be provided.");
        }
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidProductStatusException("A discontinued product cannot be modified.");
        }
        boolean duplicated = variants.stream().anyMatch(
                v -> v.getName().equalsIgnoreCase(variant.getName()) && v.getValue().equalsIgnoreCase(variant.getValue()));
        if (duplicated) {
            throw new InvalidProductException("The variant " + variant.getName() + "=" + variant.getValue()
                    + " already exists for this product.");
        }
        this.variants.add(variant);
    }

    public void publish() {
        changeStatus(ProductStatus.PUBLISHED);
    }

    public void suspend() {
        changeStatus(ProductStatus.SUSPENDED);
    }

    public void discontinue() {
        changeStatus(ProductStatus.DISCONTINUED);
    }

    public void changeStatus(ProductStatus target) {
        if (target == null) {
            throw new InvalidProductStatusException("Target product status must be provided.");
        }
        boolean valid = (ProductStatus.SUSPENDED.equals(status) && ProductStatus.PUBLISHED.equals(target))
                || (ProductStatus.PUBLISHED.equals(status) && ProductStatus.SUSPENDED.equals(target))
                || ((ProductStatus.SUSPENDED.equals(status) || ProductStatus.PUBLISHED.equals(status))
                        && ProductStatus.DISCONTINUED.equals(target));
        if (!valid) {
            throw new InvalidProductStatusException("Invalid product status transition from "
                    + (status == null ? "UNKNOWN" : status.getCode()) + " to " + target.getCode() + ".");
        }
        this.status = target;
    }

    public void updatePrice(Money newPrice) {
        if (ProductStatus.DISCONTINUED.equals(status)) {
            throw new InvalidProductStatusException("The price of a discontinued product cannot be updated.");
        }
        validatePrice(newPrice);
        if (price != null && !price.hasSameCurrency(newPrice)) {
            throw new InvalidPriceException("The new price must use the product currency " + price.getCurrency().getCode() + ".");
        }
        this.price = newPrice;
    }

    public boolean isPublished() {
        return ProductStatus.PUBLISHED.equals(status);
    }

    public boolean isPhysical() {
        return ProductType.PHYSICAL.equals(type);
    }

    /** A product can only be bought while it is PUBLISHED. */
    public void ensurePurchasable() {
        if (!isPublished()) {
            throw new InvalidProductStatusException(
                    "Product " + id + " is not published and cannot be purchased.");
        }
    }

    public boolean belongsTo(Seller candidate) {
        return candidate != null && seller != null && seller.getId() != null && seller.getId().equals(candidate.getId());
    }

    private static void validatePrice(Money value) {
        if (value == null || !value.isPositive()) {
            throw new InvalidPriceException("Price must be strictly greater than zero.");
        }
    }
}
