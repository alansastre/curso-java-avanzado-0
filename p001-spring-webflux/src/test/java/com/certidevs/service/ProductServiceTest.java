package com.certidevs.service;

import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Test de integración
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;


    Manufacturer manufacturer;
    Product product1;
    Product product2;
    @Autowired
    private ProductService productService;

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
    void increasePriceOfActiveProducts() {
        Flux<Product> updatedProductsFlux = productService.increasePriceOfActiveProducts(10d);

        StepVerifier.create(updatedProductsFlux)
                .expectNextMatches(p1 -> Double.compare(p1.getPrice(), 11.0) == 0)
                .verifyComplete();
    }

    @Test
    void increasePriceOfActiveProductsVoid() {
       var productsMono = productService.increasePriceOfActiveProductsVoid(10d);

        StepVerifier.create(productsMono)
                .expectNextCount(0)
                .verifyComplete();

        var products = productService.findAll().collectList().block();
        assertEquals(11.0, products.getFirst().getPrice());
    }

    @Test
    void reduceQuantity () {

        productService.reduceQuantity(product1.getId(), 10)
                .as(StepVerifier::create)
                .expectNextMatches(p -> p.getQuantity().equals(40))
                .verifyComplete();
    }

    @Test
    void reduceQuantityError() {

        productService.reduceQuantity(product1.getId(), 60)
                .as(StepVerifier::create)
                .expectError(IllegalArgumentException.class)
                .verify();

        productService.reduceQuantity(product1.getId(), 60)
                .as(StepVerifier::create)
                .expectErrorMatches(e ->
                        e instanceof IllegalArgumentException && e.getMessage().equals("Cantidad insuficiente")
                )
                .verify();


    }
}