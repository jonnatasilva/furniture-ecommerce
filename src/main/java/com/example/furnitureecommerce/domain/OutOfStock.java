package com.example.furnitureecommerce.domain;

public class OutOfStock extends Exception {
    public OutOfStock(String sku) {
        super("Out of stock for sku " + sku);
    }
}
