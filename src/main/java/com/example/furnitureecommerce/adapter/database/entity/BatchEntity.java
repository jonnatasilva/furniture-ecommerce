package com.example.furnitureecommerce.adapter.database.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/*@Entity
@Table(name = "BATCHES")*/
public class BatchEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "PURCHASED_QUANTITY")
    private Integer purchasedQuantity;

    @Column(name = "ETA")
    private LocalDate eta;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
            name = "BATCH_ORDER_LINES",
            joinColumns = @JoinColumn(name = "REFERENCE"),
            inverseJoinColumns = @JoinColumn(name = "ID")
    )
    private Set<OrderLineEntity> allocations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getPurchasedQuantity() {
        return purchasedQuantity;
    }

    public void setPurchasedQuantity(Integer purchasedQuantity) {
        this.purchasedQuantity = purchasedQuantity;
    }

    public LocalDate getEta() {
        return eta;
    }

    public void setEta(LocalDate eta) {
        this.eta = eta;
    }

    public Set<OrderLineEntity> getAllocations() {
        return allocations;
    }

    public void setAllocations(Set<OrderLineEntity> allocations) {
        this.allocations = allocations;
    }
}
