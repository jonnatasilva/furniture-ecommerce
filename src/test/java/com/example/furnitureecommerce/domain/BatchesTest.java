package com.example.furnitureecommerce.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class BatchesTest {

    @Test
    public void testAllocatingToABatchReducesTheAvailableQuantity() {
        var batch = createBatch("SMALL-TABLE", 20);
        var line = createOrderLine("SMALL-TABLE", 2);

        batch.allocate(line);

        assert batch.getAvailableQuantity() == 18;
    }

    @Test
    public void testCanAllocateIfAvailableGreaterThanRequired() {
        var largeBatch = createBatch("SMALL-TABLE", 20);
        var smallLine = createOrderLine("SMALL-TABLE", 2);

        assert largeBatch.canAllocate(smallLine);
    }

    @Test
    public void testCannotAllocateIfAvailableSmallThanRequired() {
        var smallBatch = createBatch("SMALL-TABLE", 2);
        var largeLine = createOrderLine("SMALL-TABLE", 20);

        assert smallBatch.canAllocate(largeLine) == false;
    }

    @Test
    public void testCanAllocateIfAvailableEqualToRequired() {
        var batch = createBatch("SMALL-TABLE", 2);
        var line = createOrderLine("SMALL-TABLE", 1);

        assert batch.canAllocate(line);
    }

    @Test
    public void testCannotAllocateIfSkusDoNotMatch() {
        var batch = new Batch("batch-001", "UNCOMFORTABLE-CHAIR", 100, null);
        var line = new OrderLine("order-123", "EXPENSIVE-TOASTER", 10);

        assert batch.canAllocate(line) == false;
    }

    @Test
    public void testCanOnlyDeallocateAllocatedLines() {
        var batch = createBatch("DECORATIVE-TRINKET", 20);
        var unallocatedLine = createOrderLine("DECORATIVE-TRINKET", 2);

        batch.deallocate(unallocatedLine);

        assert batch.getAvailableQuantity() == 20;
    }

    @Test
    public void testAllocationIsIdempotent() {
        var batch = createBatch("ANGULAR-DESK", 20);
        var line = createOrderLine("ANGULAR-DESK", 2);
        var line2 = createOrderLine("ANGULAR-DESK", 2);

        batch.allocate(line);
        batch.allocate(line2);

        assert batch.getAvailableQuantity() == 18;
    }

    private Batch createBatch(String sku, int availableQuantity) {
        return new Batch("batch-001", sku, availableQuantity, LocalDate.now());
    }

    private OrderLine createOrderLine(String sku, int qty) {
        return new OrderLine("order-ref", sku, qty);
    }

}
