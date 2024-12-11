package com.certidevs.handler;

import com.certidevs.entity.Product;
import com.certidevs.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Handler funcional para controladores de Spring WebFlux basados en el enfoque funcional de Router y Handler
 * donde se separa las rutas de los métodos que manejan las rutas
 *
 * ServerRequest
 * ServerResponse
 */
@AllArgsConstructor
@Component
public class ProductHandler {

    private ProductService productService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().body(productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.findById(id)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(product -> productService.save(product))
                .flatMap(product -> ServerResponse.created(URI.create("/api/route/products/" + product.getId())).bodyValue(product))
                .onErrorResume(e -> {
                    return ServerResponse.status(HttpStatus.CONFLICT).build(); // 409
                });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Product.class)
                .flatMap(product -> productService.update(id, product))
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> {
                    return ServerResponse.status(HttpStatus.CONFLICT).build(); // 409
                });
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.deleteById(id)
                .then(ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> {
                    return ServerResponse.status(HttpStatus.CONFLICT).build(); // 409
                });
    }

    public Mono<ServerResponse> increasePriceOfActiveProducts(ServerRequest request) {
        Double percentage = Double.valueOf(request.pathVariable("percentage"));
        return ServerResponse.ok().body(productService.increasePriceOfActiveProducts(percentage), Product.class);

    }

    public Mono<ServerResponse> reduceQuantity(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Integer amount = Integer.valueOf(request.queryParam("amount").orElse("0"));
        return productService.reduceQuantity(id, amount)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

}
