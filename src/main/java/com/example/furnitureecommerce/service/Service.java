package com.example.furnitureecommerce.service;

import com.example.furnitureecommerce.adapter.database.repository.ProductRepositoryFacade;
import com.example.furnitureecommerce.domain.Batch;
import com.example.furnitureecommerce.domain.OrderLine;
import com.example.furnitureecommerce.domain.OutOfStock;
import com.example.furnitureecommerce.domain.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.time.LocalDate;

/**
 * It is an application service. Its job is to handle requests from the outside world and to orchestrate and operation.
 * The service layer drives the application by following a bunch of simple steps:
 *  - Get some data from the database
 *  - Update the domain model
 *  - Persist any changes
 */
@Component
public class Service {

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String allocate(String orderId, String sku, Integer qty, ProductRepositoryFacade repo) throws OutOfStock, InvalidSku {
        try {
            var product = repo.findBySku(sku).orElseThrow(() -> new InvalidSku(sku));

            var batch = product.allocate(new OrderLine(orderId, sku, qty));

            return batch.getReference();
        } catch (OptimisticLockException e) {
            return allocate(orderId, sku, qty, repo);
        }
    }

    @Transactional
    public void deallocate(String orderId, String sku, ProductRepositoryFacade productRepository) throws InvalidSku {
        var product = productRepository.findBySku(sku).orElseThrow(() -> new InvalidSku(sku));

        product.deallocate(orderId);
    }


    @Transactional
    public Batch addBatch(String ref, String sku, Integer qty, LocalDate eta, ProductRepositoryFacade repo) {
        var product = repo.findBySku(sku).orElseGet(() -> addTheProductToRepo(sku, repo));

        final var batch = new Batch(ref, sku, qty, eta);
        product.getBatches().add(batch);

        return batch;
    }

    private Product addTheProductToRepo(String sku, ProductRepositoryFacade repo) {
        var p = new Product(sku);

        return repo.save(p);
    }
}
