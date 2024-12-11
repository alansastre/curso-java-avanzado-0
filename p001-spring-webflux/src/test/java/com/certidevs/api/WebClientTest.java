package com.certidevs.api;

import com.certidevs.dto.ProductStoreDTO;
import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

/*
Clientes HTTP para interactuar con APIs remotas

Pruebas con WebClient antes de usarlo en servicios

Para Spring Web:
* RestTemplate
* RestClient

Para Spring WebFlux
* WebClient : cliente http reactivo para usar en servicios, handlers, componentes....
* WebTestClient: cliente http para Testing de integración de controladores/routes

AQUÍ USAMOS WebClient para llamar a un API remota: fakestoreapi.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientTest {

//    private final WebClient client = WebClient.create();
//    private WebClient client = WebClient.create("/api/route/products");
    private WebClient client = WebClient.create("https://fakestoreapi.com/");
    @Autowired
    private ProductRepository productRepository;
//    private WebClient client = WebClient.builder().defaultHeader("X-microservice-id", "123456567").build();




    @Test
    void findAll() {
        Flux<ProductStoreDTO> productFlux = client.get().uri("/products")
                .retrieve()
                .bodyToFlux(ProductStoreDTO.class)
//                .filter(productStoreDTO -> productStoreDTO.price() > 10)
                .doOnNext(System.out::println);

        StepVerifier.create(productFlux)
                .expectNextCount(20)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<ProductStoreDTO> productDTOMono = client.get().uri("/products/1")
                .retrieve()
                .bodyToMono(ProductStoreDTO.class)
                .doOnNext(System.out::println);

        StepVerifier.create(productDTOMono)
                .expectNextMatches(p -> p.id().equals(1L) && p.price().equals(109.95))
                .verifyComplete();

        // mapeando a nuestro producto
        Mono<Product> productMono = client.get().uri("/products/1")
                .retrieve()
                .bodyToMono(ProductStoreDTO.class)
                .map(productStoreDTO ->
                    Product.builder()
                    .id(productStoreDTO.id())
                    .title(productStoreDTO.title())
                    .price(productStoreDTO.price())
                    .build()
                );
        StepVerifier.create(productMono)
                .expectNextMatches(p -> p.getId().equals(1L) && p.getPrice().equals(109.95))
                .verifyComplete();
    }



    @Test
    void create() {

        ProductStoreDTO product = new ProductStoreDTO(
                null, "Product Test", 44.2, "test", "test", "test", null
        );

        Mono<ProductStoreDTO> productMono = client.post().uri("/products")
                .bodyValue(product)
//                .body(Mono.just(product), ProductStoreDTO.class)
                .retrieve()
                .bodyToMono(ProductStoreDTO.class)
                .doOnNext(System.out::println)
                .onErrorResume(e -> {
                    System.out.println("Error al crear");
                    return Mono.empty();
                });

        StepVerifier.create(productMono)
                .expectNextMatches(p -> p.id() != null && p.title().equals("Product Test"))
                .verifyComplete();
    }

    @Test
    void update() {
        ProductStoreDTO productEdited = new ProductStoreDTO(
                1L, "Product Edited", 44.2, "test", "test", "test", null
        );

        Mono<ProductStoreDTO> productStoreDTOMono = client.put().uri("products/{id}", productEdited.id())
                .bodyValue(productEdited)
                .retrieve()
                .bodyToMono(ProductStoreDTO.class)
                .doOnNext(System.out::println);

        StepVerifier.create(productStoreDTOMono)
                .expectNextMatches(p -> p.id().equals(productEdited.id()) && p.title().equals(productEdited.title()))
                .verifyComplete();
    }


    // delete con bodilessEntity
    // zip
    // retryWhen
}
