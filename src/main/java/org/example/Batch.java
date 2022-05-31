package org.example;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Batch {

    private final String ref;
    private final String sku;
    private Integer purchasedQuantity;
    private final LocalDate eta;
    private final Set<OrderLine> allocations;

    public Batch(String ref, String sku, Integer qty, LocalDate eta) {
        this.ref = ref;
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
        return this.sku == orderLine.getSku() && orderLine.getQty() <= this.purchasedQuantity;
    }

    public void deallocate(OrderLine line) {
        if (allocations.contains(line)) {
            allocations.remove(line);
        }
    }

    public String getRef() {
        return ref;
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

}
