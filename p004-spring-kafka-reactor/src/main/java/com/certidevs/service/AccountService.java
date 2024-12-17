package com.certidevs.service;

import com.certidevs.entity.Account;
import com.certidevs.entity.Transaction;
import com.certidevs.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private AccountRepository accountRepository;
    private ReactiveKafkaConsumerTemplate<String, Transaction> transactionConsumer;

    public Mono<Account> create (Account account) {
        account.setBalance(0.0);
        return accountRepository.save(account);
    }

    public Mono<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @PostConstruct // cuando termine de inicializar la clase se inicia el consumer
    public void startConsumer() {
        transactionConsumer.receiveAutoAck()
                .map(record -> record.value())
                .flatMap(this::updateAccountBalance)
                .doOnError(e -> log.error("Error procesando transaction", e))
                .onErrorContinue((e, transaction) -> log.error("Continuando después de error", e))
                .subscribe(null, e -> log.error("Error en la suscripción ",e));
    }

    public Mono<Void> updateAccountBalance(Transaction transaction) {
        return Mono.defer( () -> accountRepository.findById(transaction.getAccountId())
                .flatMap(account -> {
                    log.info("Procesando transacción {} para actualizar account {}", transaction, account);
                    if ("deposit".equalsIgnoreCase(transaction.getType()))
                        account.setBalance(account.getBalance() + transaction.getAmount());
                    else if ("withdraw".equalsIgnoreCase(transaction.getType()))
                        account.setBalance(account.getBalance() - transaction.getAmount());
                    return accountRepository.save(account);
                })
                .doOnSuccess(account -> log.info("Balance actualizado {}", account))
                .then());
    }

}
