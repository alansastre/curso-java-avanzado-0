package com.certidevs.api;

import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWebTestClient
public class ProductRouterTest {

    // MockMvc
    @Autowired
    private WebTestClient client;

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
    void findAll() {
        // probar con /api/products debe funciona igual
        client.get().uri("/api/route/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(2)
                .contains(product1, product2)
                .consumeWith( response -> {
                    var products = response.getResponseBody();
                    assertNotNull(products);
                    assertEquals("PRODUCT 1", products.getFirst().getTitle());
                    assertEquals("PRODUCT 2", products.getLast().getTitle());
                });

//        client.get().uri("/api/route/products")
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .jsonPath("$[0].title").isEqualTo("PRODUCT 1");
    }

}
