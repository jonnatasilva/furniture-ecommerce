package com.example.furnitureecommerce.service;

public class InvalidSku extends Exception {

    public InvalidSku(String sku) {
        super("Invalid sku " + sku);
    }
}
