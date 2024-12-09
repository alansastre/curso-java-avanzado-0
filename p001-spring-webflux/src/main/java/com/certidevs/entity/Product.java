package com.certidevs.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("product")
public class Product {

    @Id
    private Long id;
    private String title;
    private Double price;
    private Integer quantity;
    private Boolean active;
    private LocalDateTime creationDate;
    private Long manufacturerId;

    // @Transient
    // private Manufacturer manufacturer;

}
