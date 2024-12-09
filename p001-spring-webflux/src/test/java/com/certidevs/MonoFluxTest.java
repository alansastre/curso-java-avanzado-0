package com.certidevs;

import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;


@DataR2dbcTest
public class MonoFluxTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;


    Manufacturer manufacturer;
    Product product1;
    Product product2;

    @BeforeEach
    void setUp() {

        manufacturer = manufacturerRepository.save(Manufacturer.builder()
                .name("Test fabricante")
                .foundationYear(2000)
                .country("Spain")
                .build()).block();

        product1 = Product.builder()
                .title("Product 1")
                .price(10.0)
                .quantity(50)
                .active(true)
                .creationDate(LocalDateTime.now().minusDays(10))
                .manufacturerId(manufacturer.getId())
                .manufacturer(manufacturer)
                .build();

        product2 = Product.builder()
                .title("Product 2")
                .price(20.0)
                .quantity(5)
                .active(false)
                .creationDate(LocalDateTime.now().minusDays(5))
                .manufacturerId(manufacturer.getId())
                .manufacturer(manufacturer)
                .build();

//        productRepository.save(product1).block();
        productRepository.saveAll(List.of(product1, product2)).collectList().block();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll().then(manufacturerRepository.deleteAll()).block();
    }

    @Test
    @DisplayName("Test para el operador map de Mono - Transforma el producto cambiando su precio")
    void monoMap() {
            Mono<Product> productMono = productRepository.findById(product1.getId());

//            Mono<Double> productMono2 = productMono.map(product -> product.getPrice());

        Mono<Product> productMonoWithOfferPrice = productMono.map(product -> {
            product.setPrice(product.getPrice() - 1);
            return product;
        });

        StepVerifier.create(productMonoWithOfferPrice)
                .expectNextMatches(product -> product.getPrice().equals(9.0))
                .verifyComplete();


    }

}
