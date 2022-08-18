package com.example.furnitureecommerce.adapter.database.repository.impl;

import com.example.furnitureecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
