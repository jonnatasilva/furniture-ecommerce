package com.example.furnitureecommerce.domain;

import java.util.List;

/**
 * It is a domain service. Its belongs in the domain model but doesn't sit naturally inside a stateful entity or value object.
 */
public class Allocate {

    public static Batch allocate(OrderLine line, List<Batch> batches) throws OutOfStock {
        var batch = batches.stream()
                .filter(b -> b.canAllocate(line))
                .sorted()
                .findFirst()
                .orElseThrow(() -> new OutOfStock(line.getSku()));

        batch.allocate(line);
        return batch;
    }
}
