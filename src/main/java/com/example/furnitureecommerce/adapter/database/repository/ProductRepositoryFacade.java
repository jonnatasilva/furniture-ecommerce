package com.example.furnitureecommerce.adapter.database.repository;

import com.example.furnitureecommerce.domain.Product;

import java.util.Optional;

public interface ProductRepositoryFacade {

    Optional<Product> findBySku(String sku);
    Product save(Product product);
}
