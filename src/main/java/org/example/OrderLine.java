package org.example;

import java.util.Objects;

public class OrderLine {

    private final String orderId;
    private final String sku;
    private final Integer qty;

    public OrderLine(String orderId, String sku, Integer qty) {
        this.orderId = orderId;
        this.sku = sku;
        this.qty = qty;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSku() {
        return sku;
    }

    public Integer getQty() {
        return this.qty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return orderId.equals(orderLine.orderId) &&
                sku.equals(orderLine.sku) &&
                qty.equals(orderLine.qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, sku, qty);
    }
}
