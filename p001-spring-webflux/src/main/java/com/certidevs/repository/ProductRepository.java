package com.certidevs.repository;

import com.certidevs.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByActive(Boolean active);
    Flux<Product> findByActiveTrue();
    Flux<Product> findByActiveFalse();

    Flux<Product> findByPriceBetween(Double priceStart, Double priceEnd);

    Mono<Product> findByTitle(String title);

    @Query("""
            SELECT * FROM product WHERE quantity < :quantity
            """)
    Flux<Product> findProductsWithQuantityLessThan(Integer quantity);

    Flux<Product> findByManufacturerId(Long manufacturerId);

    Flux<Product> findByCreationDateBefore(LocalDateTime creationDate);




}
