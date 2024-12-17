package com.certidevs.controller;

import com.certidevs.entity.Transaction;
import com.certidevs.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;

    @PostMapping
    public Mono<ResponseEntity<Transaction>> create(@RequestBody Transaction transaction) {
        return transactionService.create(transaction).map(ResponseEntity::ok);
    }
}
