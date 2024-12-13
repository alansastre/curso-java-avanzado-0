package com.certidevs.api;

import com.certidevs.dto.PaginatedProductResponse;
import com.certidevs.dto.PaginatedResponse;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientTest {

//    private final WebClient client = WebClient.create();
//    private WebClient client = WebClient.create("/api/route/products");
    private WebClient client = WebClient.create("https://fakestoreapi.com/");
//    @Autowired
//    private ProductRepository productRepository;
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

    @Test
    void createAndUpdate() {

        ProductStoreDTO product = new ProductStoreDTO(
                null, "Product Test", 44.2, "test", "test", "test", null
        );

        // post + put combinados con flatMap
        Mono<ProductStoreDTO> productMono = client.post().uri("/products")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(ProductStoreDTO.class)
                .flatMap(p -> client.put()
                        .uri("/products/{id}", p.id())
                        .bodyValue(new ProductStoreDTO(
                                p.id(), p.title() + " editado", p.price(), null, null, null, null
                        ))
                        .retrieve()
                        .bodyToMono(ProductStoreDTO.class)
                );

        StepVerifier.create(productMono)
                .expectNextMatches(p -> p.title().equals("Product Test editado"))
                .verifyComplete();
    }

    @Test
    void delete() {

        // opción 1: si no devuelve nada el tipo será Void
//        Mono<Void> mono = client.delete().uri("/products/1")
//                .retrieve()
//                .bodyToMono(Void.class)
//                .onErrorResume(e -> Mono.empty());


        // Opción 2: bodilessEntity
//        ResponseEntity<Void> response = client.delete().uri("/products/1")
//                .retrieve()
//                .toBodilessEntity()
//                .block();
//
//        assertTrue(response.getStatusCode().is2xxSuccessful());


        // Opción 3: exchanteToMono y devolver un Mono personalizado
        client.delete().uri("/products/1")
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful())
                        // return response.bodyToMono(ProductStoreDTO.class);
                        return Mono.empty();
                    else
                        // return response.createError();
                        return Mono.error(new RuntimeException("Error deleting"));
                });

    }

    @Test
    void findAllPaginated() {

        client.get()
//                .uri("/api/route/products?page=2&size=10")
                .uri(uriBuilder ->
                        uriBuilder.path("/api/route/products")
                                .queryParam("page", 2)
                                .queryParam("size", 10)
                                .build()
                ).retrieve()
                .bodyToMono(PaginatedProductResponse.class)
                .doOnNext(res -> {
                    System.out.println(res.size());
                    System.out.println(res.page());
                    System.out.println(res.total());
                    System.out.println(res.products());
                });
    }

    @Test
    void findAllPaginatedWithGenerics() {

        var typeRef = new ParameterizedTypeReference<PaginatedResponse<Product>>() {};

        Mono<PaginatedResponse<Product>> mono = client.get().uri(uriBuilder ->
                uriBuilder.path("/api/route/products")
                        .queryParam("page", 2)
                        .queryParam("size", 10)
                        .build()
        ).retrieve()
//        .bodyToMono(PaginatedResponse.class)
        .bodyToMono(new ParameterizedTypeReference<PaginatedResponse<Product>>() {})
        .doOnNext(res -> {
            System.out.println(res.size());
            System.out.println(res.page());
            System.out.println(res.total());
            System.out.println(res.items());
        });
    }
}
