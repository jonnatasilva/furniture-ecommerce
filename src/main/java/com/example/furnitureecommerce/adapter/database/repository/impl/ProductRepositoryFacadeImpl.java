package com.example.furnitureecommerce.adapter.database.repository.impl;

import com.example.furnitureecommerce.adapter.database.repository.ProductRepositoryFacade;
import com.example.furnitureecommerce.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ProductRepositoryFacadeImpl implements ProductRepositoryFacade {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findById(sku);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
