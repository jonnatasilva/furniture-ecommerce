package com.example.furnitureecommerce.entrypoint;

import com.example.furnitureecommerce.adapter.database.repository.ProductRepositoryFacade;
import com.example.furnitureecommerce.domain.OutOfStock;
import com.example.furnitureecommerce.entrypoint.domain.AllocationRequest;
import com.example.furnitureecommerce.entrypoint.domain.DeallocationRequest;
import com.example.furnitureecommerce.service.InvalidSku;
import com.example.furnitureecommerce.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AllocationController {

    @Autowired
    private ProductRepositoryFacade productRepositoryFacade;

    @Autowired
    private Service service;

    @PostMapping("/allocate")
    public ResponseEntity<Map<String, String>> allocate(@RequestBody AllocationRequest req) {
        try {
            var batchref = service.allocate(req.getOrderId(), req.getSku(), req.getQty(), productRepositoryFacade);

            return new ResponseEntity<>(Map.of("batchref", batchref), HttpStatus.CREATED);

        } catch (OutOfStock | InvalidSku e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/deallocate")
    public ResponseEntity<Void> deallocate(@RequestBody DeallocationRequest req) throws InvalidSku {

        service.deallocate(req.getOrderId(), req.getSku(), productRepositoryFacade);

        return ResponseEntity.ok().build();
    }
}
