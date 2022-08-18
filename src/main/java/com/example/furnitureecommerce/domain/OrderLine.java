package com.example.furnitureecommerce.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * It is a business concept that has data but no identify, we often choose to represent it
 * using the Value Object pattern.
 *
 * A value object is any domain object that is uniquely identified by the data it holds.
 *
 * ORM gets model definitions from it
 */
@Entity
@Table(name = "ORDER_LINES")
@Access(AccessType.FIELD)
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "QTY")
    private Integer qty;

    @ManyToOne(fetch = FetchType.LAZY)
    private Batch batch;

    public OrderLine() {}
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
