package com.certidevs.controller;

import com.certidevs.entity.Product;
import com.certidevs.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/*
Entrar por SWAGGER: http://localhost:8080/webjars/swagger-ui/index.html

* Enfoque basado en anotaciones:
    * @RestController, @GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping
    * Se usa más en Spring Web normal, es decir Spring MVC / REST

 * Enfoque funcional:
     * Router @Configuration @Bean
     * Handler @Component
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {


    private ProductService productService;

    // http://localhost:8080/api/products"
//    @GetMapping
//    public ResponseEntity<List<Product>> findAll() {
//        return ResponseEntity.ok(productService.findAll());
//    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> findAll() {
        return Mono.just(ResponseEntity.ok(productService.findAll()));
    }

//    @GetMapping("findall2")
//    public Flux<Product> findAll2() {
//        return productService.findAll();
//    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Product>> findById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> create(@RequestBody Product product) {
        if (product.getId() != null)
            return Mono.just(ResponseEntity.badRequest().build()); // 400

        return productService.save(product)
                .map(savedProduct -> ResponseEntity.created(URI.create("/api/products/" + savedProduct.getId())).body(product)) // 201
                .onErrorResume(e -> {
                    log.warn("Error creating new product", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()); // 409
                });
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Product>> update(@PathVariable Long id, @RequestBody Product product) {
        if (product.getId() == null)
            return Mono.just(ResponseEntity.badRequest().build()); // 400

        return productService.update(id, product)
                .map(ResponseEntity::ok) // 200
                .defaultIfEmpty(ResponseEntity.notFound().build()) // 404
                .onErrorResume(e -> {
                    log.warn("Error creating new product", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()); // 409
                });
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        // alternativa: usar existsById en lugar de findById
        return productService.findById(id)
                .flatMap(existingProduct ->
                        productService.deleteById(existingProduct.getId()).then(Mono.just(ResponseEntity.noContent().<Void>build()))) // 204
                .defaultIfEmpty(ResponseEntity.notFound().build()) // 404
                .onErrorResume(e -> {
                    log.warn("Error creating new product", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()); // 409
                });


//        return productService.deleteById(id)
//                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
//                .onErrorResume(e -> {
//                    log.warn("Error creating new product", e);
//                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build()); // 409
//                });

    }



}
