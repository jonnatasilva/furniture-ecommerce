package com.example.furnitureecommerce.domain;

import com.example.furnitureecommerce.service.InvalidSku;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AllocateTest {

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalDate LATER = LocalDate.now().plusDays(10);

    @Test
    public void testPrefersCurrentStockBatchesToShipments() throws OutOfStock, InvalidSku {
        var inStockBatch = new Batch("in-stock-batch", "RETRO-CLOCK", 100, null);
        var shipmentBatch = new Batch("shipment-batch", "RETRO-CLOCK", 100, TOMORROW);

        Allocate.allocate(new OrderLine("oref", "RETRO-CLOCK", 10), new ArrayList<>(List.of(inStockBatch, shipmentBatch)));

        assert inStockBatch.getAvailableQuantity() == 90;
        assert shipmentBatch.getAvailableQuantity() == 100;
    }

    @Test
    public void testPrefersEarlierBatches() throws OutOfStock, InvalidSku {
        var earliest = new Batch("speedy-batch", "MINIMALIST-SPOON", 100, LocalDate.now());
        var medium = new Batch("normal-batch", "MINIMALIST-SPOON", 100, TOMORROW);
        var latest = new Batch("slow-batch", "MINIMALIST-SPOON", 100, LATER);

        Allocate.allocate(new OrderLine("order1", "MINIMALIST-SPOON", 10), new ArrayList<>(List.of(medium, earliest, latest)));

        assert earliest.getAvailableQuantity() == 90;
        assert medium.getAvailableQuantity() == 100;
        assert latest.getAvailableQuantity() == 100;
    }
}