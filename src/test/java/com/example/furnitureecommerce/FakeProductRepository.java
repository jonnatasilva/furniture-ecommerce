package com.example.furnitureecommerce;

import com.example.furnitureecommerce.adapter.database.repository.ProductRepositoryFacade;
import com.example.furnitureecommerce.domain.Batch;
import com.example.furnitureecommerce.domain.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeProductRepository implements ProductRepositoryFacade {

    private final List<Product> products;

    public FakeProductRepository(List<Product> products) {
        this.products = products;
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return products.stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        this.products.add(product);

        return product;
    }

    public static FakeProductRepository forProduct(String sku, List<Batch> batches) {
        ArrayList<Product> products = new ArrayList<>(List.of(new Product(sku, 1, batches)));

        return new FakeProductRepository(products);
    }

    public static FakeProductRepository forBatch(String sku, String ref, Integer qty, LocalDate eta) {
        List<Batch> batches = List.of(new Batch(ref, sku, qty, eta));

        return new FakeProductRepository(new ArrayList<>(List.of(new Product(sku, 1, batches))));
    }
}
