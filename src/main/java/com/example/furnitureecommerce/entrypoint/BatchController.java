package com.example.furnitureecommerce.entrypoint;

import com.example.furnitureecommerce.adapter.database.repository.ProductRepositoryFacade;
import com.example.furnitureecommerce.domain.Batch;
import com.example.furnitureecommerce.entrypoint.domain.AddBatchRequest;
import com.example.furnitureecommerce.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {

    @Autowired
    private ProductRepositoryFacade productRepositoryFacade;

    @Autowired
    private Service service;

    @PostMapping("/batches")
    public ResponseEntity<Batch> addBatch(@RequestBody AddBatchRequest req) {
        var batch = service.addBatch(req.getRef(), req.getSku(), req.getQty(), req.getEta(), productRepositoryFacade);

        return new ResponseEntity<>(batch, HttpStatus.CREATED);
    }
}