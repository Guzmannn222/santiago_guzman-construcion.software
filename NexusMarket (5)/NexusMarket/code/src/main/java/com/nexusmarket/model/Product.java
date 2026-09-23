package com.nexusmarket.model;

import java.util.ArrayList;
import java.util.List;

import com.nexusmarket.enums.ProductStatus;
import com.nexusmarket.enums.ProductType;

/** Physical or digital good offered in the catalog (SDD, Domain 5). */
public class Product {

    private final String id;
    private final Seller seller;
    private String name;
    private String description;
    private double price;
    private final ProductType type;
    private ProductStatus status;
    private final List<Variant> variants = new ArrayList<>();

    public Product(String id, Seller seller, String name, String description, double price, ProductType type) {
        this.id = id;
        this.seller = seller;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.status = ProductStatus.SUSPENDED;
    }

    public void addVariant(Variant variant) {
        this.variants.add(variant);
    }

    public void publish() {
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void updatePrice(double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = newPrice;
    }

    public String getId() { return id; }
    public Seller getSeller() { return seller; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public ProductType getType() { return type; }
    public ProductStatus getStatus() { return status; }
    public List<Variant> getVariants() { return variants; }
}
