package com.certidevs.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction{
        private Long id;
        private Long accountId;
        private  String type; // "deposit", "withdraw"
        private  Double amount;
        private  LocalDateTime timestamp;

}
