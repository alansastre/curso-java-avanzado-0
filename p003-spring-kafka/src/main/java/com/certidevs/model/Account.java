package com.certidevs.model;

import lombok.*;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account{

        private Long id;
    private String owner;
    private Double balance;
}
