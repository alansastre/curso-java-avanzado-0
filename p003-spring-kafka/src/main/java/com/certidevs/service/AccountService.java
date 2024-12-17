package com.certidevs.service;

import com.certidevs.model.Account;
import com.certidevs.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    // private AccountRepository accountRepository;

    @KafkaListener(topics = "spring-topic", groupId = "spring-group")
    public void updateAccountBalance(Transaction transaction) {
        log.info("Consumer: mensaje de transacción {}", transaction);

        // Optional<Account> account = accountRepository.findById(transaction.getAccountId());

        // sacar el account de base de datos

        // actualizar el balance del account en base a los datos del transaction

        // guardar el account

        // opcional: enviar a kafka otro evento de cuenta actualiza para notificar al usuario por correo, sms, ....
    }
}
