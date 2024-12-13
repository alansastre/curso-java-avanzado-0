package com.certidevs.handler;

import com.certidevs.dto.PaginatedProductResponse;
import com.certidevs.dto.PaginatedResponse;
import com.certidevs.dto.PaginatedResponseLombok;
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

    // http://localhost:8080/api/route/products?page=2&size=10
    public Mono<ServerResponse> findAllPaginated(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        int offset = (page - 1) * size;

        // Lo óptimo sería hacer la paginación a nivel de repositorio directamente con SQL para extraer los datos mínimos

        return ServerResponse.ok().body(
                productService.count().flatMap(total ->
                        productService.findAll()
                                .skip(offset)
                                .take(size)
                                .collectList()
                                .map(products -> new PaginatedProductResponse(
                                        products,
                                        page,
                                        size,
                                        total
                                ))
                        )

                , PaginatedProductResponse.class);


    }

    public Mono<ServerResponse> findAllPaginatedWithGeneric(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        int offset = (page - 1) * size;


//        return ServerResponse.ok().body(
//                productService.count().flatMap(total ->
//                        productService.findAll()
//                                .skip(offset)
//                                .take(size)
//                                .collectList()
//                                .map(products -> new PaginatedResponse<Product>(
//                                        products,
//                                        page,
//                                        size,
//                                        total
//                                ))
//                )
//
//                , PaginatedResponse.class);

        return ServerResponse.ok().body(
                productService.count().flatMap(total ->
                        productService.findAll()
                                .skip(offset)
                                .take(size)
                                .collectList()
                                .map(products -> PaginatedResponseLombok.<Product>builder()
                                        .items(products)
                                        .page(page)
                                        .size(size)
                                        .total(total)
                                        .build())
                )

                , PaginatedResponseLombok.class);


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

       return productService.findById(id)
                .flatMap(existingProduct ->
                        productService.deleteById(existingProduct.getId()).then(ServerResponse.noContent().build())) // 204
                .switchIfEmpty(ServerResponse.notFound().build()) // 404
                .onErrorResume(e -> ServerResponse.status(HttpStatus.CONFLICT).build());


//        return productService.deleteById(id)
//                .then(ServerResponse.noContent().build())
//                .switchIfEmpty(ServerResponse.notFound().build())
//                .onErrorResume(e -> {
//                    return ServerResponse.status(HttpStatus.CONFLICT).build(); // 409
//                });
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
