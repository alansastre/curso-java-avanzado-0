package com.certidevs.repository;

import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class ProductRepositoryTest {

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
    void save() {

        Product newProduct = Product.builder()
                .title("New Product")
                .price(20.0)
                .quantity(5)
                .active(false)
                .creationDate(LocalDateTime.now().minusDays(5))
                .build();

        Mono<Product> productMono = productRepository.save(newProduct);

        StepVerifier.create(productMono)
                .expectNextMatches(product -> product.getId() != null && product.getTitle().equals("New Product"))
                .verifyComplete();

//        Product savedProduct = productRepository.save(newProduct).block();
//        assertNotNull(savedProduct);
//        assertNotNull(savedProduct.getId());
//        assertEquals("New Product", savedProduct.getTitle());

    }

    /*

    Product(id=1, title=Product 1, price=10.0, quantity=50, active=true, creationDate=2024-11-29T10:15:37.628061200, manufacturerId=null))" failed
    Product(id=1, title=Product 1, price=10.0, quantity=50, active=true, creationDate=2024-11-29T10:15:37.628061200, manufacturerId=null); actual value:
    Product(id=1, title=Product 1, price=10.0, quantity=50, active=true, creationDate=2024-11-29T10:15:37.628061, manufacturerId=null))


     */
    @Test
    void findById() {
        StepVerifier.create(productRepository.findById(product1.getId()))
                .expectNext(product1)
                .verifyComplete();

        productRepository.findById(product1.getId())
//                .as(productMono -> StepVerifier.create(productMono))
                .as(StepVerifier::create)
                .expectNext(product1)
                .verifyComplete();
    }

    @Test
    void findAll() {
        Flux<Product> productFlux = productRepository.findAll();


        StepVerifier.create(productFlux)
                .expectNext(product1, product2)
                .verifyComplete();

        StepVerifier.create(productFlux)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        StepVerifier.create(productFlux)
                .expectNextMatches(product -> product.getId().equals(product1.getId()))
                .expectNextMatches(product -> product.getId().equals(product2.getId()))
                .verifyComplete();
    }

    @Test
    void findAllByManufacturerId() {
        Flux<Product> productFlux = productRepository.findByManufacturerId(manufacturer.getId());


        StepVerifier.create(productFlux)
                .expectNext(product1, product2)
                .verifyComplete();

        StepVerifier.create(productFlux)
                .expectNext(product1)
                .expectNext(product2)
                .verifyComplete();

        StepVerifier.create(productFlux)
                .expectNextMatches(product -> product.getId().equals(product1.getId()))
                .expectNextMatches(product -> product.getId().equals(product2.getId()))
                .verifyComplete();
    }

    @Test
    void existsById() {
        StepVerifier.create(productRepository.existsById(product1.getId()))
                .expectNext(true)
                .verifyComplete();
        StepVerifier.create(productRepository.existsById(999L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void delete() {
        Mono<Void> deleteMono = productRepository.deleteById(product1.getId());
        StepVerifier.create(deleteMono).verifyComplete();

        StepVerifier.create(productRepository.findById(product1.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }


}