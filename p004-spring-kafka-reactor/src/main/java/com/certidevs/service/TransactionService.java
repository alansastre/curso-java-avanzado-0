package com.certidevs.service;

import com.certidevs.entity.Transaction;
import com.certidevs.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    // Kafka normal
    // private KafkaTemplate<String, Transaction> kafkaTemplate;

    // KafkaSender proporcionado por reactor-kafka como producer reactivo
    // private KafkaSender<String, Transaction> sender;

    // ReactiveKafkaProducerTemplate: envoltorio de KafkaSender, template reactivo producer para enviar datos a kafka
    private ReactiveKafkaProducerTemplate<String, Transaction> transactionProducer;

    public Mono<Transaction> create(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());
        // transactionRepository.save(transaction);
        // Producer: enviar transacción a kafka

        log.info("Producer: enviando transacción {}", transaction);
        // kafkaTemplate.sendDefault(transaction);
        // kafkaTemplate.send("topic-transactions", transaction);
        // kafkaTemplate.send("topic-transactions", transaction.getId().toString(), transaction);

        return transactionRepository.save(transaction)
                .flatMap(savedTransaction ->
                        transactionProducer.send("spring-topic", savedTransaction.getId().toString(), savedTransaction).thenReturn(savedTransaction)
                );


//        // enviando directament el mono
//        Mono<Transaction> transactionMono = transactionRepository.save(transaction);
//
//        transactionProducer.send("spring-topic", String.valueOf(transactionMono.map(Transaction::getId).map(Object::toString)), transactionMono).subscribe();
//

    }


}
