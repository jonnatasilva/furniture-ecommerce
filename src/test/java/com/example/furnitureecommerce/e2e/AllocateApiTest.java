package com.example.furnitureecommerce.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AllocateApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testApiReturnsAllocation() throws Exception {
        postToAddBatch("REF-1", "SMALL-FORK", 100, LocalDate.of(2011, Month.JANUARY, 2));
        postToAddBatch("REF-2", "SMALL-FORK", 100, LocalDate.of(2011, Month.JANUARY, 1));
        postToAddBatch("REF-3", "HIGHBROW-POSTER", 100, null);

        var sku = "SMALL-FORK";
        var orderId = "order1";

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson(orderId, sku, 3)))
                .andExpect(status().isCreated())
                .andExpect(content().json(getBatchRefJson("REF-2")));
    }

    @Test
    public void testAllocationsArePersisted() throws Exception {
        postToAddBatch("REF-1", "SMALL-FORK", 10, LocalDate.of(2011, Month.JANUARY, 2));
        postToAddBatch("REF-2", "SMALL-FORK", 10, LocalDate.of(2011, Month.JANUARY, 1));

        var sku = "SMALL-FORK";
        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order1", sku, 10)))
                .andExpect(status().isCreated())
                .andExpect(content().json(getBatchRefJson("REF-2")));

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order2", sku, 10)))
                .andExpect(status().isCreated())
                .andExpect(content().json(getBatchRefJson("REF-1")));
    }

    @Test
    public void test400MessageForOutOfStock() throws Exception {
        postToAddBatch("REF-2", "SMALL-FORK", 10, LocalDate.of(2011, Month.JANUARY, 1));

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order1", "SMALL-FORK", 20)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Out of stock for sku SMALL-FORK\"}"));
    }

    @Test
    public void test400MessageForInvalidSku() throws Exception {
        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order1", "unknow-sku", 20)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\": \"Invalid sku unknow-sku\"}"));
    }

    @Test
    public void testDeallocate() throws Exception {
        final var batch = randomBatchRef();
        final var sku = randomSku();

        postToAddBatch(batch, sku, 100, LocalDate.of(2011, Month.JANUARY, 2));

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order1", sku, 100)))
                .andExpect(status().isCreated())
                .andExpect(content().json(getBatchRefJson(batch)));

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order2", sku, 100)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/deallocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order1", sku, 100)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/allocate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getLineJson("order2", sku, 100)))
                .andExpect(status().isCreated())
                .andExpect(content().json(getBatchRefJson(batch)));
    }

    private String getBatchRefJson(String batch) throws JsonProcessingException {
        return toJson(Map.of("batchref", batch));
    }

    private void postToAddBatch(String ref, String sku, Integer qty, LocalDate eta) throws Exception {
        mockMvc.perform(post("/batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getBatchJson(ref, sku, qty, eta)))
                .andExpect(status().isCreated());
    }

    private String getLineJson(String orderId, String sku, Integer qty) throws JsonProcessingException {
        return toJson(Map.of("orderId", orderId, "sku", sku, "qty", qty));
    }

    private String getBatchJson(String ref, String sku, Integer qty, LocalDate eta) throws JsonProcessingException {
        if (Objects.nonNull(eta)) {
            return toJson(Map.of("ref", ref, "sku", sku, "qty", qty, "eta", eta));
        }

        return toJson(Map.of("ref", ref, "sku", sku, "qty", qty));
    }

    private String randomBatchRef() {
        return "batch-" + randomSuffix();
    }

    private String randomSku() {
        return "sku-" + randomSuffix();
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString();
    }

    private String toJson(Map<String, Object> content) throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(content);
    }
}
