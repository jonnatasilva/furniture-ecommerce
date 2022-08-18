package com.example.furnitureecommerce.adapters.repository.impl;

import com.example.furnitureecommerce.adapter.database.repository.impl.ProductRepository;
import com.example.furnitureecommerce.domain.Batch;
import com.example.furnitureecommerce.domain.OrderLine;
import com.example.furnitureecommerce.domain.OutOfStock;
import com.example.furnitureecommerce.domain.Product;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=update",
        "show.sql=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductRepositoryTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void contextLoads() {
        assertThat(productRepository).isNotNull();
    }

    @Test
    public void testRepositoryCanSaveABatch() {
        productRepository.save(createProduct("RUSTY-SOAPDISH", 1));

        var products = productRepository.findAll();

        assertThat(products).hasSize(1);
        var batches = products.get(0).getBatches();
        assertThat(batches).hasSize(1);

        assertThat(batches.get(0).getReference()).isEqualTo("batch1");
        assertThat(batches.get(0).getSku()).isEqualTo("RUSTY-SOAPDISH");
        assertThat(batches.get(0).getPurchasedQuantity()).isEqualTo(100);
        assertThat(batches.get(0).getEta()).isNull();
    }

    @Test
    public void testRepositoryCanRetrieveABatchWithAllocations() throws OutOfStock {
        var product = createProduct("RUSTY-SOAPDISH", 1);
        var orderLine = createOrderLine();

        product.allocate(orderLine);

        var sku = productRepository.save(product).getSku();

        var batchWithAllocations = productRepository.findById(sku).get().getBatches().get(0);

        assertThat(batchWithAllocations.getSku()).isEqualTo("RUSTY-SOAPDISH");
        assertThat(batchWithAllocations.getPurchasedQuantity()).isEqualTo(100);
        assertThat(batchWithAllocations.getAllocations()).hasSize(1);
        assertThat(batchWithAllocations.getAllocations().stream().findFirst().get()).isEqualTo(orderLine);
    }

    @Test
    public void testConcurrentUpdateToVersionAreNotAllowed() throws InterruptedException {
        final var sku = "RUSTY-SOAPDISH";
        final int versionNumber = 1;
        productRepository.save(createProduct(sku, versionNumber));

        List<Exception> exceptions = new ArrayList<>();
        final Runnable tryToAllocateOrder1 = () -> tryToAllocate("order1", sku, exceptions);
        final Runnable tryToAllocateOrder2 = () -> tryToAllocate("order2", sku, exceptions);

        var t1 = new Thread(tryToAllocateOrder1);
        var t2 = new Thread(tryToAllocateOrder2);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        var em = entityManagerFactory.createEntityManager();
        Integer result = (Integer) em
                .createNativeQuery("SELECT VERSION_NUMBER FROM PRODUCTS WHERE SKU = ?")
                .setParameter(1, sku)
                .getSingleResult();

        assertThat(result).isEqualTo(2);

        assertThat(exceptions).hasSize(1);
        assertThat(exceptions.get(0).getCause()).isInstanceOf(OptimisticLockException.class);

        var allocations = em.createQuery("SELECT o FROM OrderLine o JOIN o.batch b WHERE b.sku = ?1", OrderLine.class)
                .setParameter(1, sku)
                .getResultList();

        assertThat(allocations).hasSize(1);
    }

    public void tryToAllocate(String orderId, String sku, List<Exception> exceptions) {
        final var line = new OrderLine(orderId, sku, 10);

        final var em = entityManagerFactory.createEntityManager();
        final var tr = em.getTransaction();
        tr.begin();
        Session session = em.unwrap(Session.class);
        session.doWork(conn -> {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        });
        try {
            final var product = em
                    .createQuery("SELECT P FROM Product P WHERE SKU = ?1", Product.class)
                    .setParameter(1, sku)
                    .getSingleResult();


            product.allocate(line);
            Thread.sleep(100);
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
            exceptions.add(e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private Product createProduct(String sku, int versionNumber) {
        return new Product(sku, versionNumber, List.of(createBatch(sku)));
    }

    private Batch createBatch(String sku) {
        return new Batch("batch1", sku, 100, null);
    }

    private OrderLine createOrderLine() {
        var orderLine = new OrderLine("order1", "RUSTY-SOAPDISH", 10);

        return orderLine;
    }
}
