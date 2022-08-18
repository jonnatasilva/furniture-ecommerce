package com.example.furnitureecommerce.entrypoint.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeallocationRequest {

    private final String orderId;
    private final String sku;

    public DeallocationRequest(@JsonProperty String orderId, @JsonProperty String sku) {
        this.orderId = orderId;
        this.sku = sku;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSku() {
        return sku;
    }
}
