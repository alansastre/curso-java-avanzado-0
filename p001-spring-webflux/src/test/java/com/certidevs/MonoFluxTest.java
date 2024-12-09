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
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
                .quantity(15)
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

        // Ejemplo de programación funcional operador de map de API STREAM de Java
        List.of(
                new Product(),
                new Product()
        ).stream().map(product -> {
            product.setPrice(50d);
            return product;
        }).forEach(System.out::println);

        // Opción 1:
        Mono<Product> productMono = productRepository.findById(product1.getId());
        Mono<Product> productMonoWithOfferPrice = productMono.map(product -> {
            product.setPrice(product.getPrice() - 1);
            return product;
        });
        StepVerifier.create(productMonoWithOfferPrice)
                .expectNextMatches(product -> product.getPrice().equals(9.0))
                .verifyComplete();

        // Opción 2:
        productRepository.findById(product1.getId())
                .map(product -> {
                    product.setPrice(product.getPrice() - 1);
                    return product;
                }).map(product -> {
                    product.setQuantity(product.getQuantity() - 1);
                    return product;
                }).as(StepVerifier::create)
                .expectNextMatches(product -> product.getPrice().equals(9.0) && product.getQuantity().equals(49))
                .verifyComplete();


//            Mono<Double> productMono2 = productMono.map(product -> product.getPrice());
    }


    @Test
    @DisplayName("Operador flatMap se usa cuando queremos encadenar otra operación que devuelve un Mono")
    void monoFlatMap() {

//        Mono<Mono<Product>> productMono = productRepository.findById(product1.getId())
//                .map(product -> {
//                    product.setPrice(product.getPrice() - 1);
//                    // return product;
//                    return productRepository.save(product);
//                });

        Mono<Product> productMono = productRepository.findById(product1.getId())
                .flatMap(product -> {
                    product.setPrice(product.getPrice() - 1);
                    // return product;
                    return productRepository.save(product);
                });

        StepVerifier.create(productMono)
//                .expectNextCount(1)
                .expectNextMatches(product -> product.getPrice().equals(9.0))
                .verifyComplete();


    }


    @Test
    void monoFlatMapMany() {

        // Obtener un fabricante y a partir del fabricante obtener sus productos (Flux)

        Flux<Product> productFlux = manufacturerRepository.findByName(manufacturer.getName())
                .flatMapMany(m -> productRepository.findByManufacturerId(m.getId()));

        StepVerifier.create(productFlux)
                .expectNextCount(2)
                .verifyComplete();

        // Ejemplo de comprobaciones usando la API Stream de Java
//        List<Product> products = productRepository.findByManufacturerId(manufacturer.getId()).collectList().block();
//        assertTrue(products.stream().allMatch(product -> product.getManufacturerId().equals(manufacturer.getId())));


    }

    @Test
    void fluxFilter() {
        Flux<Product> productFlux = productRepository.findAll()
//                .filter(product -> product.getActive().equals(true));
                .filter(Product::getActive);

        StepVerifier.create(productFlux)
                .expectNextCount(1)
                .verifyComplete();

        // productFlux.subscribe(products -> System.out.println(products));
        // productFlux.subscribe(System.out::println);
    }

    @Test
    void switchIfEmpty() {

        Mono<Product> productMono = productRepository.findById(9999L)
                .switchIfEmpty(Mono.just(new Product()));

        StepVerifier.create(productMono)
                .expectNextMatches(product -> product.getTitle() == null)
                .verifyComplete();

        Mono<ServerResponse> response = productRepository.findById(9999L)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());

        StepVerifier.create(response)
                .expectNextMatches(r -> r.statusCode().is4xxClientError())
                .verifyComplete();

    }

    /*
    Maneja una excepción proporcionando un mono alternativo
     */
    @Test
    void onErrorResume() {
       Product product = productRepository.findById(999L)
                .flatMap(p -> Mono.<Product>error(new RuntimeException("Error")))
                .onErrorResume(throwable -> {
                    // log.error("Error haciendo X operacion sobre un product");
                    return Mono.just(new Product());
                }).block();

   assertNotNull(product);
   assertNull(product.getTitle());
//        StepVerifier.create(productMono)
//                .expectNextMatches(product -> product.getTitle() == null)
//                .verifyComplete();
    }

    /*
    Colecciona todos los elementos de un Flux a una lista
     */
    @Test
    void collectList() {
//        Flux<Product> productFlux = productRepository.findAll();
        Mono<List<Product>> monoProductList = productRepository.findAll().collectList();

        StepVerifier.create(monoProductList)
                .expectNextMatches(products -> products.size() == 2);
    }

    @Test
    void fluxConcat() {

        Flux<Product> firstFlux = productRepository.findAll().filter(p -> p.getQuantity() > 25); // 1
        Flux<Product> secondFlux = productRepository.findAll().filter(p -> p.getQuantity() < 25); // 1

        Flux<Product> allProductsFlux = Flux.concat(firstFlux, secondFlux);

        StepVerifier.create(allProductsFlux)
                .expectNextCount(2)
                .verifyComplete();

    }

    /*
    combina de forma concurrente, no respeta ese orden secuencial que tiene concat
     */
    @Test
    void fluxMerge() {
        Flux<Product> firstFlux = productRepository.findAll().filter(p -> p.getQuantity() > 10); // 2
        Flux<Product> secondFlux = productRepository.findAll().filter(p -> p.getQuantity() < 55); // 2

        Flux<Product> allProductsFlux = Flux.merge(firstFlux, secondFlux);

//        List<Product> products = allProductsFlux.collectList().block();
//        assertEquals(4, products.size());

        StepVerifier.create(allProductsFlux)
                .expectNextCount(4)
                .verifyComplete();

        /*
        merge fusiona como el concat, pero se de forma eager en vez de secuencial,
        no reemplaza duplicados, mejor usar/combinar con distinct para eso teniendo en cuenta hashCode
         */

        // java.lang.AssertionError: expectation "expectComplete" failed (expected: onComplete(); actual: onNext(
        // Product(id=1, title=Product 1, price=10.0, quantity=50, active=true, creationDate=2024-11-29T13:02:10.082572, manufacturerId=1, manufacturer=null)))
    }


    // map
    // flatMap
    // flatMapMany
    // filter
    // switchIfEmpty
    // collectList
    // merge
    // zip
    // concat



}
