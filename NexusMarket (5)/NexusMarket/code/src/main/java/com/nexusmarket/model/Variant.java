package com.nexusmarket.model;

/** A variation of a product, such as color, size or model (SDD, Domain 5). */
public class Variant {

    private final String id;
    private final String name;
    private final String value;

    public Variant(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getValue() { return value; }
}
