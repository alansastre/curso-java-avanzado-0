package com.certidevs.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("transaction")
public class Transaction {

        @Id
        private Long id;
        private Long accountId;
        private  String type; // "deposit", "withdraw"
        private  Double amount;
        private  LocalDateTime timestamp;

}
