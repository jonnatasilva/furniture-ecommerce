package com.example.furnitureecommerce.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * It is a business concept that has a long-lived identity, whe use the term entity to describe it.
 *
 * ORM gets model definitions from it
 */
@Entity
@Table(name = "BATCHES")
@Access(AccessType.FIELD)
public class Batch implements Comparable<Batch> {

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

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    /*@JoinTable(
            name = "BATCH_ORDER_LINES",
            joinColumns = @JoinColumn(name = "REFERENCE"),
            inverseJoinColumns = @JoinColumn(name = "ID")
    )*/
    @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID")
    private Set<OrderLine> allocations;

    public Batch() {}

    public Batch(String ref, String sku, Integer qty, LocalDate eta) {
        this.reference = ref;
        this.sku = sku;
        this.purchasedQuantity = qty;
        this.eta = eta;
        this.allocations = new HashSet<>();
    }

    public void allocate(OrderLine line) {
        if(canAllocate(line)) {
            this.allocations.add(line);
        }
    }

    public boolean canAllocate(OrderLine orderLine) {
        return this.sku.equals(orderLine.getSku()) && orderLine.getQty() <= this.getAvailableQuantity();
    }

    public void deallocate(OrderLine line) {
        if (allocations.contains(line)) {
            allocations.remove(line);
        }
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getSku() {
        return sku;
    }

    public int getPurchasedQuantity() {
        return this.purchasedQuantity;
    }

    public int getAvailableQuantity() {
        return this.purchasedQuantity - this.getAllocatedQuantity();
    }

    public Integer getAllocatedQuantity() {
        return this.allocations.stream()
                .map(OrderLine::getQty)
                .reduce(0, Integer::sum);
    }

    public LocalDate getEta() {
        return eta;
    }

    public Set<OrderLine> getAllocations() {
        return Collections.unmodifiableSet(allocations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return reference.equals(batch.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public int compareTo(Batch o) {
        return Comparator
                .comparing(Batch::getEta, Comparator.nullsFirst(Comparator.naturalOrder()))
                .compare(this, o);
    }
}
