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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
public class ProductRouterTest {

    // Similar a MockMvc
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

    @Test
    void findById() {
        client.get().uri("/api/route/products/{id}", product1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .isEqualTo(product1)
                .value(product -> {
                    assertNotNull(product);
                    assertEquals(product1.getId(), product.getId());
                    assertEquals(product1.getTitle(), product.getTitle());
                });
    }

    @Test
    void findById_notFound() {
        client.get().uri("/api/route/products/{id}", 9999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void create() {
        Product product = Product.builder()
                .title("Product Test")
                .price(20.0)
                .quantity(5)
//                .active(false)
//                .creationDate(LocalDateTime.now().minusDays(5))
                .manufacturerId(manufacturer.getId())
                .manufacturer(manufacturer)
                .build();

        client.post().uri("/api/route/products")
                .bodyValue(product)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("location", location -> {
                    assertNotNull(location);
                    assertTrue(location.startsWith("/api/route/products/"));
                })
                .expectBody(Product.class)
                // .isEqualTo(product) // no lo hacemos por que el de la respuesta tiene id y el nuestro no
                .value(createdProduct -> {
                    assertNotNull(createdProduct);
                    assertNotNull(createdProduct.getId());
                    assertEquals("Product Test", createdProduct.getTitle());
                    assertTrue(createdProduct.getActive());
                    assertNotNull(createdProduct.getCreationDate());
                });
    }

    // update
    @Test
    void update() {
        product1.setQuantity(60); // 50 -> 60
        product1.setPrice(20.0); // 10.0 -> 20.0

        client.put().uri("/api/route/products/{id}", product1.getId())
                .bodyValue(product1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .value(product -> {
                    assertNotNull(product);
                    assertEquals(product1.getId(), product.getId());
                    assertEquals(product1.getQuantity(), product.getQuantity());
                    assertEquals(product1.getPrice(), product.getPrice());
                });

        var savedProduct = productRepository.findById(product1.getId()).block();
        assertNotNull(savedProduct);
        assertEquals(product1.getQuantity(), savedProduct.getQuantity());
        assertEquals(product1.getPrice(), savedProduct.getPrice());
    }

    @Test
    void createAndUpdate() {
        Product product = Product.builder()
                .title("Product Test")
                .price(20.0)
                .quantity(5)
                .manufacturerId(manufacturer.getId())
                .manufacturer(manufacturer)
                .build();

        Product createdProduct = client.post().uri("/api/route/products")
                .bodyValue(product)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .returnResult()
                .getResponseBody();

        createdProduct.setPrice(30.0);
        createdProduct.setQuantity(10);

        client.put().uri("/api/route/products/{id}", createdProduct.getId())
                .bodyValue(createdProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .value(updatedProduct -> {
                    assertNotNull(updatedProduct);
                    assertEquals(createdProduct.getId(), updatedProduct.getId());
                    assertEquals(createdProduct.getQuantity(), updatedProduct.getQuantity());
                    assertEquals(createdProduct.getPrice(), updatedProduct.getPrice());
                });

        var savedProduct = productRepository.findById(createdProduct.getId()).block();
        assertNotNull(savedProduct);
        assertEquals(createdProduct.getQuantity(), savedProduct.getQuantity());
        assertEquals(createdProduct.getPrice(), savedProduct.getPrice());
    }
    // delete

}
