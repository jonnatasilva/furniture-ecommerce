package com.example.furnitureecommerce.service;

import com.example.furnitureecommerce.FakeProductRepository;
import com.example.furnitureecommerce.domain.Batch;
import com.example.furnitureecommerce.domain.OutOfStock;
import com.example.furnitureecommerce.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ServiceTest {

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalDate LATER = LocalDate.now().plusDays(10);

    private Service service;

    @BeforeEach
    public void setup() {
        service = new Service();
    }

    @Test
    public void testPrefersCurrentStockBatchesToShipments() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forProduct("RETRO-CLOCK", new ArrayList<>());

        service.addBatch("in-stock-batch", "RETRO-CLOCK", 100, null, repo);
        service.addBatch("shipment-batch", "RETRO-CLOCK", 100, TOMORROW, repo);

        var batchref = service.allocate("oref", "RETRO-CLOCK", 10, repo);

        assert batchref == "in-stock-batch";
    }

    @Test
    public void testPrefersEarlierBatches() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forProduct("MINIMALIST-SPOON", new ArrayList<>());

        service.addBatch("speedy-batch", "MINIMALIST-SPOON", 100, LocalDate.now(), repo);
        service.addBatch("normal-batch", "MINIMALIST-SPOON", 100, TOMORROW, repo);
        service.addBatch("slow-batch", "MINIMALIST-SPOON", 100, LATER, repo);

        var batchref = service.allocate("order1", "MINIMALIST-SPOON", 10, repo);

        assert batchref == "speedy-batch";
    }

    @Test
    public void testReturnsAllocatedBatchRef() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forProduct("HIGHBROW-POSTER", new ArrayList<>());

        service.addBatch("in-stock-batch-ref", "HIGHBROW-POSTER", 100, null, repo);
        service.addBatch("shipment-batch-ref", "HIGHBROW-POSTER", 100, TOMORROW, repo);

        var batchRef = service.allocate("oref", "HIGHBROW-POSTER", 10, repo);

        assert batchRef.equals("in-stock-batch-ref");
    }

    @Test
    public void testReturnsAllocation() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forBatch("COMPLICATED-LAMP", "b1", 100, null);

        var result = service.allocate("o1", "COMPLICATED-LAMP", 10, repo);

        assert result == "b1";
    }

    @Test
    public void testRaisesOutOfStockExceptionIfCanNotAllocate() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forBatch("SMALL-FORK", "batch1",10, LocalDate.now());

        service.allocate("order1", "SMALL-FORK", 10, repo);

        assert getBatchByRef(repo.findBySku("SMALL-FORK").get(), "batch1").getAvailableQuantity() == 0;

        assertThrowsExactly(OutOfStock.class, () -> service.allocate("order2", "SMALL-FORK", 1, repo), "SMALL-FORK");
    }

    @Test
    public void testErrorForInvalidSku() {
        var repo = FakeProductRepository.forBatch("COMPLICATED-LAMP", "b1", 100, null);

        assertThrowsExactly(InvalidSku.class, () -> service.allocate("o1", "NONEXISTENTSKU", 10, repo), "Invalid sku NONEXISTENTSKU");
    }

    @Test
    public void testDeallocateDecrementsAvailableQuantity() throws OutOfStock, InvalidSku {
        var repo = FakeProductRepository.forProduct("BLUE-PLINTH", new ArrayList<>());

        service.addBatch("b1", "BLUE-PLINTH", 100, null, repo);
        service.allocate("o1", "BLUE-PLINTH", 10, repo);

        final var batch = getBatchByRef(repo.findBySku("BLUE-PLINTH").get(), "b1");

        assert batch.getAvailableQuantity() == 90;

        service.deallocate("o1", "BLUE-PLINTH", repo);

        assert batch.getAvailableQuantity() == 100;
    }

    private Batch getBatchByRef(Product product, String ref) {
        return product.getBatches().stream().filter(b -> b.getReference().equals(ref)).findFirst().get();
    }
}
