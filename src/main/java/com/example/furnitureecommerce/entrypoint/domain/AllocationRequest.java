package com.example.furnitureecommerce.entrypoint.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllocationRequest {

    private final String orderId;
    private final String sku;
    private final Integer qty;

    public AllocationRequest(@JsonProperty String orderId, @JsonProperty String sku, @JsonProperty Integer qty) {
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
        return qty;
    }
}
