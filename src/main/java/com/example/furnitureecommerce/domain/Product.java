package com.example.furnitureecommerce.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCTS")
@Access(AccessType.FIELD)
public class Product {

    @Id
    @Column(name = "SKU", nullable = false)
    private String sku;

    @Column(name = "VERSION_NUMBER", nullable = false)
    private Integer versionNumber;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SKU", referencedColumnName = "SKU")
    private List<Batch> batches;

    public Product() {
        this.batches = new ArrayList<>();
    }

    public Product(String sku) {
        this(sku, 1, new ArrayList<>());
    }

    public Product(String sku, Integer versionNumber, List<Batch> batches) {
        this.sku = sku;
        this.versionNumber = versionNumber;
        this.batches = batches;
    }

    public Batch allocate(OrderLine line) throws OutOfStock {
        var batch = batches.stream()
                .filter(b -> b.canAllocate(line))
                .sorted()
                .findFirst()
                .orElseThrow(() -> new OutOfStock(line.getSku()));

        batch.allocate(line);
        this.versionNumber += 1; //Optimistic lock

        return batch;
    }

    public void deallocate(String orderId) {
        for (Batch batch : batches) {
            final var orderLine = batch.getAllocations().stream().filter(ol -> ol.getOrderId().equals(orderId)).findFirst();

            if (orderLine.isPresent()) {
                batch.deallocate(orderLine.get());
                break;
            }
        }
    }

    public String getSku() {
        return sku;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }
}
