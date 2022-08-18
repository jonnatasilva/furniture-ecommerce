package com.example.furnitureecommerce.adapter.database.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/*@Entity
@Table(name = "ORDER_LINES")*/
public class OrderLineEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "QTY")
    private Integer qty;

    @Column(name = "ORDER_ID")
    private String orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineEntity that = (OrderLineEntity) o;
        return sku.equals(that.sku) &&
                qty.equals(that.qty) &&
                orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, qty, orderId);
    }
}