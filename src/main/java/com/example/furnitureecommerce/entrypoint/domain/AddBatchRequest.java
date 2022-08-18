package com.example.furnitureecommerce.entrypoint.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class AddBatchRequest {

    private final String ref;
    private final String sku;
    private final Integer qty;
    private final LocalDate eta;

    public AddBatchRequest(@JsonProperty String ref, @JsonProperty String sku, @JsonProperty Integer qty, @JsonProperty LocalDate eta) {
        this.ref = ref;
        this.sku = sku;
        this.qty = qty;
        this.eta = eta;
    }

    public String getRef() {
        return ref;
    }

    public String getSku() {
        return sku;
    }

    public Integer getQty() {
        return qty;
    }

    public LocalDate getEta() {
        return eta;
    }
}
