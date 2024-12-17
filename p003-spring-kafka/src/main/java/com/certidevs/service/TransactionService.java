package com.certidevs.service;

import com.certidevs.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

    // private TransactionRepository transactionRepository;
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    public Transaction create(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());
        // transactionRepository.save(transaction);
        // Producer: enviar transacción a kafka

        log.info("Producer: enviando transacción {}", transaction);
        // kafkaTemplate.sendDefault(transaction);
        // kafkaTemplate.send("topic-transactions", transaction);
        // kafkaTemplate.send("topic-transactions", transaction.getId().toString(), transaction);

        var response = kafkaTemplate.sendDefault(transaction);

        response.thenAccept(result -> {
            log.info("Record metadata {}", result.getRecordMetadata());

        }).exceptionally(e -> {
            log.error("Error sending transaction", e);
            return null;
        });

        return transaction;
    }


}
