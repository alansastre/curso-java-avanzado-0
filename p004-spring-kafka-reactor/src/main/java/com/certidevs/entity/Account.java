package com.certidevs.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("account")
public class Account{

    @Id
    private Long id;
    private String owner;
    private Double balance;
}
